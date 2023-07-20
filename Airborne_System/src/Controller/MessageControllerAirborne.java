package Controller;

import Contract.Contract;
import Contract.ContractType;
import Contract.IContract;
import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyException;

import java.util.concurrent.TimeUnit;


/**
 * Controller of the messages coming from Ivy, and the message that will be sent to the communication bus
 * Each time a message is received from anywhere, we need to verify the Flight_Code to avoid an order destined to another flight
 * There is two types of connection, one for order acknowledgement messages CPDLC and the other one for situation awareness of the Aircraft
 * AAR messages can be different.
 * It can be on periodic (TrackedMoveEvent), on demand (Pln), or on event (BeaconEvent).
 * Each of those messages must be authorized under the shape of contracts
 * @see RegexMessage
 * @see IContract
 */
public class MessageControllerAirborne implements RegexMessage {

    public final Ivy bus;
    private final String APPLICATION_NAME;
    private final String FLIGHT_CODE;
    private final String DOMAIN_NETWORK;
    private final ContractController contractController;


    public MessageControllerAirborne(String flightCode) {
        contractController = new ContractController();
        FLIGHT_CODE = flightCode;
        DOMAIN_NETWORK = "127.255.255.255:2010";
        APPLICATION_NAME = "MANIRAVI_Airborne_System";
        bus = new Ivy(APPLICATION_NAME, FLIGHT_CODE + " MANIRAVI_Airborne_System ready", null);
    }

    public void startBus() throws IvyException {
        bus.start(DOMAIN_NETWORK);
        System.out.println(bus.getIvyClients());
    }

    public void stopBus() {
        bus.stop();
    }

    /**
     * Send messages to the bus
     * @param message Content of the message that will be posted on the bus
     */
    public void sendMessage(String message) {
        try {
            bus.sendMsg(message);
        } catch (IvyException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Binding of the messages received from the Ground System to Rejeu. Those messages are CPDLC form (=orders)
     */
    public void sendOrderToRejeu() {
        try {
            //Defining the binding of "CLIMB TO" messages
            bus.bindMsg(FLIGHT + " CLIMB TO " + FLIGHT_LEVEL, (ivyClient, strings) -> {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (strings[0].equals(FLIGHT_CODE)) {
                    String preparedMessage = RejeuController.convertClimbMessageForRejeu(strings);
                    sendMessage(preparedMessage);
                } else try {
                    throw new IvyException("ERROR : Flight code not recognized by the Airborne System");
                } catch (IvyException e) {
                    throw new RuntimeException(e);
                }
            });

            //Defining the binding of "HEADING TO" messages
            bus.bindMsg(FLIGHT + " HEAD TO " + POSITION, (ivyClient, strings) -> {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (strings[0].equals(FLIGHT_CODE)) {
                    String preparedMessage = RejeuController.convertHeadingMessageForRejeu(strings);
                    sendMessage(preparedMessage);
                } else try {
                    throw new IvyException("ERROR : Flight code not recognized by the Airborne System");
                } catch (IvyException e) {
                    throw new RuntimeException(e);
                }
            });

            //Defining the binding of "Get PLN" messages
            bus.bindMsg(FLIGHT + " GET PLN", (ivyClient, strings) -> {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (strings[0].equals(FLIGHT_CODE)) {
                    String preparedMessage = RejeuController.convertGetPlnMessageForRejeu(strings[0]);
                    sendMessage(preparedMessage);
                } else try {
                    throw new IvyException("ERROR : Flight code not recognized by the Airborne System");
                } catch (IvyException e) {
                    throw new RuntimeException(e);
                }
            });

            //Defining the binding of "Direct to" messages
            bus.bindMsg(FLIGHT + " DIRECT TO " + BEACON, (ivyClient, strings) -> {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (strings[0].equals(FLIGHT_CODE)) {
                    String preparedMessage = RejeuController.convertDirectMessageForRejeu(strings);
                    System.out.println(preparedMessage);
                    sendMessage(preparedMessage);
                } else try {
                    throw new IvyException("ERROR : Flight code not recognized by the Airborne System");
                } catch (IvyException e) {
                    throw new RuntimeException(e);
                }
            });

            //Defining the binding of "RESUME PLN" messages
            bus.bindMsg(FLIGHT + " RESUME PLN", (ivyClient, strings) -> {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (strings[0].equals(FLIGHT_CODE)) {
                    String preparedMessage = RejeuController.convertResumePlnMessageForRejeu(strings[0]);
                    sendMessage(preparedMessage);
                } else try {
                    throw new IvyException("ERROR : Flight code not recognized by the Airborne System");
                } catch (IvyException e) {
                    throw new RuntimeException(e);
                }
            });

            //Defining the binding of "STOP" messages
            bus.bindMsg(FLIGHT + " STOP", (ivyClient, strings) -> {
                if (strings[0].equals(FLIGHT_CODE)) {
                    stopBus();
                } else try {
                    throw new IvyException("ERROR : Flight code not recognized by the Airborne System");
                } catch (IvyException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IvyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Binding of the messages coming from Rejeu to the Ground System. Those messages can be sent if the contract AAR "on Event" is initialized
     */
    public void sendEventToGroundSystem(){
        try {
            bus.bindMsg("PlnEvent Flight=" + FLIGHT + " Time=" + TIME + " CallSign=" + CALL_SIGN + " AircraftType=" + AIRCRAFT +
                    " Ssr=" + SSR + " Speed=" + SPEED + " Rfl=" + FLIGHT_LEVEL + " Dep=" + DEP_ARR + " Arr=" + DEP_ARR +
                    " Rvsm=TRUE Tcas=TA_RA Adsb=NO List=" + MESSAGE_AS_LIST, (ivyClient, strings) -> {
                if (strings[0].equals(FLIGHT_CODE)) {
                    if (contractController.isContractPresent(ContractType.DEMAND))
                        sendMessage(RejeuController.convertGetPlnEventMessageToController(strings));
                    else {
                        System.err.println("Contract AAR \"Event\" may not be initialized or can be revoked");
                        sendMessage(FLIGHT_CODE + " UNABLE EVENT_Not_Initialized");
                    }
                }
                else throw new RuntimeException("Flight code not recognized");
            });

            //Binding of the Beacon Event message
            bus.bindMsg("BeaconEvent Flight=" + FLIGHT + " Beacon=" + BEACON + " Fl=" + FLIGHT_LEVEL +
                    " Mode=" + MODE + " Time=" + TIME, (ivyClient, strings) -> {
                if (strings[0].equals(FLIGHT_CODE)) {
                    if (contractController.isContractPresent(ContractType.DEMAND)){
                        System.out.println("BEACON PASSED"+RejeuController.convertBeaconMessageToController(strings));
                        sendMessage(RejeuController.convertBeaconMessageToController(strings));}
                    else {
                        System.err.println("Contract AAR \"Event\" may not be initialized or can be revoked");
                        sendMessage(FLIGHT_CODE + " UNABLE EVENT_Not_Initialized");
                    }
                }
                else throw new RuntimeException("Flight code not recognized");
            });
        } catch (IvyException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Binding of the messages coming from Rejeu to the Ground System. Those messages can be sent if the contract AAR "on Demand" is initialized
     */
    public void sendDemandToGroundSystem() {
        try {
            //Defining the binding of "ReportEvent" messages
            bus.bindMsg("ReportEvent " + APPLICATION_NAME + " Result=" + RESULT + " Info=" + INFO + " Order=" + ORDER, (ivyClient, strings) -> {
                String gotFlightCode = strings[2].split("\\|")[1];
                if (gotFlightCode.equals(FLIGHT_CODE)) {
                    if(contractController.isContractPresent(ContractType.DEMAND)) {
                        sendMessage(RejeuController.convertReportMessageForController(strings, gotFlightCode));
                        System.out.println("Message sent!");
                    }
                    else {
                        System.err.println("Contract AAR \"Demand\" may not be initialized or can be revoked");
                        sendMessage(FLIGHT_CODE + " UNABLE DEMAND_Not_Initialized");
                    }
                }
                else throw new RuntimeException("Flight code not recognized");
            });

            //Defining the binding of "Pln"
            //It will be useful when the Ground System will connect the first time, and afterward if there is any disconnection
            bus.bindMsg("Pln " + MESSAGE + " Flight=" + FLIGHT + " Time=" + TIME + " CallSign=" + CALL_SIGN + " AircraftType=" + AIRCRAFT +
                    " Ssr=" + SSR + " Speed=" + SPEED + " Rfl=" + FLIGHT_LEVEL + " Dep=" + DEP_ARR + " Arr=" + DEP_ARR +
                    " Rvsm=TRUE Tcas=TA_RA Adsb=NO List=" + MESSAGE_AS_LIST, (ivyClient, strings) -> {
                if (strings[1].equals(FLIGHT_CODE)) {
                    if(contractController.isContractPresent(ContractType.DEMAND))
                        sendMessage(RejeuController.convertPlnDemandMessageToController(strings) + " ");
                    else {
                        System.err.println("Contract AAR \"Demand\" may not be initialized or can be revoked");
                        sendMessage(FLIGHT_CODE + " UNABLE DEMAND_Not_Initialized");
                    }
                } else throw new RuntimeException("Flight code not recognized");
            });
        } catch (IvyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Binding of the event messages coming from Rejeu to the Ground System. Those messages can be sent if the contract AAR "Periodic" is initialized.
     */
    public void sendPeriodicToGroundSystem(){
        try {
            //Defining the binding of "TrackMovedEvent" messages
            bus.bindMsg("TrackMovedEvent Flight=" + FLIGHT + " CallSign=" + CALL_SIGN + " Ssr=" + SSR +
                    " Sector=" + SECTOR + " Layers=" + LAYERS + " X=" + X_Y + " Y=" + X_Y + " Vx=" + VX_VY +
                    " Vy=" + VX_VY + " Afl=" + FLIGHT_LEVEL + " Rate=" + RATED + " Heading=" + HEADING +
                    " GroundSpeed=" + GROUND_SPEED + " Tendency=" + TENDENCY +
                    " Time=" + TIME, (ivyClient, strings) -> {
                if (strings[0].equals(FLIGHT_CODE)) {
                    if (contractController.isContractPresent(ContractType.PERIODIC)) {
                        sendMessage(RejeuController.convertTrackMessageForController(strings));
                    } else {
                        System.err.println("Contract AAR \"Periodic\" may not be initialized or can be revoked");
                        sendMessage(FLIGHT_CODE + " UNABLE PERIODIC_Not_Initialized");
                    }
                }
                else throw new RuntimeException("Flight code not recognized");
            });
        } catch (IvyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Binding of the demands of AAR contract from the Ground System. It can be on demand, on event, or periodic
     * See documentation of ADS-C
     * @see IContract
     * @see ContractType
     * @see ContractController
     */
    public void bindingContractFromGroundSystem(){
        try {
            //Defining the binding of Contract AAR messages
            bus.bindMsg(FLIGHT + " AAR " + CONTRACT, ((ivyClient, strings) -> {
                if (strings[0].equals(FLIGHT_CODE)) {
                    IContract contract = new Contract();

                    switch (strings[1]) {
                        case "EVENT":
                            contract.initializeContract(ContractType.EVENT);
                            if(contractController.addContract(contract))
                                sendMessage(strings[0] + " AAR " + strings[1] + " WILCO ");
                            else
                                sendMessage(strings[0] + " AAR " + strings[1] + " UNABLE EVENT_Contract_Already_Exists");
                            break;
                        case "DEMAND":
                            contract.initializeContract(ContractType.DEMAND);
                            if(contractController.addContract(contract))
                                sendMessage(strings[0] + " AAR " + strings[1] + " WILCO ");
                            else
                                sendMessage(strings[0] + " AAR " + strings[1] + " UNABLE DEMAND_Already_Exists");
                            break;
                        case "PERIODIC":
                            contract.initializeContract(ContractType.PERIODIC);
                            if(contractController.addContract(contract))
                                sendMessage(strings[0] + " AAR " + strings[1] + " WILCO ");
                            else
                                sendMessage(strings[0] + " AAR " + strings[1] + " UNABLE PERIODIC_Already_Exists");
                            break;
                        default:
                            sendMessage(strings[0] + " AAR " + strings[1] + " UNABLE Type_Of_Contract_Does_Not_Exists");
                            break;
                    }
                }
                else throw new RuntimeException("Flight code not recognized");
            }));

            //Defining the binding of Revoke AAR contract
            bus.bindMsg(FLIGHT + " AAR " + CONTRACT + " REVOKE", ((ivyClient, strings) -> {
                if (strings[0].equals(FLIGHT_CODE)) {
                    switch (strings[1]) {
                        case "EVENT":
                            if (contractController.revokeContractFromList(ContractType.EVENT))
                                sendMessage(strings[0] + " AAR REVOKE " + strings[1] + " WILCO ");
                            else
                                sendMessage(strings[0] + " AAR REVOKE " + strings[1] + " UNABLE EVENT_Contract_Does_Not_Exists");
                            break;
                        case "DEMAND":
                            if (contractController.revokeContractFromList(ContractType.DEMAND))
                                sendMessage(strings[0] + " AAR REVOKE " + strings[1] + " WILCO ");
                            else
                                sendMessage(strings[0] + " AAR REVOKE " + strings[1] + " UNABLE UNABLE DEMAND_Contract_Does_Not_Exists");
                            break;
                        case "PERIODIC":
                            if (contractController.revokeContractFromList(ContractType.PERIODIC))
                                sendMessage(strings[0] + " AAR REVOKE " + strings[1] + " WILCO ");
                            else
                                sendMessage(strings[0] + " AAR REVOKE " + strings[1] + " UNABLE PERIODIC_Contract_Does_Not_Exists");
                            break;
                        default:
                            sendMessage(strings[0] + " AAR REVOKE " + strings[1] + " UNABLE Type_Of_Contract_Does_Not_Exists");
                            break;
                    }
                }
                else throw new RuntimeException("Flight code not recognized");
            }));
        } catch (IvyException e) {
            throw new RuntimeException(e);
        }
    }
}
