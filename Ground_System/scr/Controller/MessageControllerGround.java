package Controller;

import Contract.ContractController;
import Contract.ContractType;
import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.enac.sita.visuradar.data.param.VisualParameters;
import model.FlightData;
import model.Order;
import model.enumeration.Status;
import model.enumeration.TypeOfOrder;
import view.SceneController;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageControllerGround implements RegexMessage {
    private final Ivy bus;
    private final String FLIGHT_CODE;
    private final String DOMAIN_NETWORK;
    private final FlightData flightData;
    private final ContractController contractController;

    public MessageControllerGround(FlightData flightData, VisualParameters visualParameters) {
        contractController = new ContractController();
        this.flightData = flightData;
        FLIGHT_CODE = flightData.getSelfTraffic().getFlightID();
        DOMAIN_NETWORK = visualParameters.getIvyBus();
        String appName = "MANIRAVI_Ground_System";
        bus = new Ivy(appName, FLIGHT + " MANIRAVI_Airborne_System ready", null);
    }

    public void startBus() throws IvyException {
        bus.start(DOMAIN_NETWORK);
    }
    public void stopBus() {
        bus.stop();
    }

    public void sendOrder(String order) {
        try {
            System.out.println("Order sent : " + order);
            bus.sendMsg(order);
        } catch (IvyException e) {
            System.err.println(e.getMessage());
        }
    }

    private void getPlan() {
        sendOrder(FLIGHT_CODE + " GET PLN");
    }

    /**
     * Bind a special message to see if the Airborne system is getting connected.
     * If that's the case, we need to instantiate the contract.
     * @throws IvyException Error on the bus communication of Ivy
     */
    public void connectionAirborneSystem() throws IvyException {
        bus.bindMsg(FLIGHT + " MANIRAVI_Airborne_System ready", (ivyClient, strings) -> {
            if(strings[0].equals(FLIGHT_CODE)) {
                try {
                    sendContracts();
                } catch (IvyException e) {
                    throw new RuntimeException(e);
                }
                new SceneController();
            }
        });
    }

    /**
     * Report messages correspond to the result of an order performed such as HDG, FL...
     * To help the user to know where its order is, we modify here the status of the said order,
     * changing its statue by PASS or FAIL depending on the report received.
     * HEAD: Change the heading of the flight
     * CLIMB: Change the flight altitude of the flight
     * DIRECT: Change the data of the flight plan to adjust it to follow one beacon
     * @see FlightData
     * @see Order
     * @throws IvyException Error on the IVY bus
     */
    public void receiveReportMessage() throws IvyException {
        bus.bindMsg(FLIGHT + " REPORT EVENT " + MESSAGE, (ivyClient, strings) -> {
            if(contractController.isContractPresent(ContractType.DEMAND)) {
                if (strings[0].equals(FLIGHT_CODE)) {
                    System.out.println("Received report message : " + strings[1]);
                    String[] reportEventMessage = strings[1].split(" ");
                    Status status = Objects.equals(reportEventMessage[3], "OK") ? Status.PASS : Status.FAIL;

                    // Depending on the type of report we receive, different actions are done
                    TypeOfOrder typeOfOrder;
                    System.out.println("Type of order : " + reportEventMessage[0]);
                    switch (reportEventMessage[0]) {
                        case "HEAD":
                            typeOfOrder = TypeOfOrder.HEADING;
                            flightData.getSelfTraffic().getIsFollowingRoute().set(false);
                            break;
                        case "CLIMB":
                            typeOfOrder = TypeOfOrder.FL;
                            break;
                        case "DIRECT":
                            typeOfOrder = TypeOfOrder.DIRECT;
                            flightData.clearRoute();
                            if(reportEventMessage[2].equals("RESUME_PLN")) {
                                flightData.getSelfTraffic().getIsFollowingRoute().set(true);
                            } else {
                                flightData.addBeaconToRoute(reportEventMessage[2]);

                                flightData.getSelfTraffic().getIsFollowingRoute().set(false);
                            }
                            break;
                        default:
                            typeOfOrder = null;
                    }

                    // We search for the concerned order and validate it
                    for (Order o : flightData.getOrderList()) {
                        if (o.getStatus() == Status.IN_PROCESS && o.getTypeOfOrder() == typeOfOrder) {
                            o.setStatus(status);
                            o.setOrderMessage();
                            break;
                        }
                    }
                } else {
                    initiateContract(ContractType.DEMAND);
                }
            }
        });
    }

    /**
     * Add a new Contract Type on our contract list if it does not exist.
     * @param contractType New Contract to add
     * @return if the contract was well initialized, it returns true, else false
     */
    private boolean initiateContract(ContractType contractType) {
        boolean status;
        System.out.println("Contract AAR \""+ contractType.toString()+ "\" may not be initialized or can be revoked, revoking contracts...");
        try {
            status = revokeContract(contractType);

            status = sendContractType(contractType);
        } catch (IvyException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return status;
    }

    /**
     * This method concerns all the Periodic Messages Airborne can send.
     * When we receive a message of the type TRACK MOVE EVENT, it means that the Aircraft has been moved.
     * We receive so the different new data concerning the flight
     * @throws IvyException Exception on the IVY bus
     */
    public void receivePeriodicMessage() throws IvyException {
        bus.bindMsg(FLIGHT + " TRACKED MOVED EVENT "+ CALL_SIGN + " " + SSR + " " + SECTOR + " " + LAYERS + " " + X_Y + " "
                + X_Y + " " + VX_VY + " " + VX_VY + " " + FLIGHT_LEVEL + " " + RATED + " " + HEADING + " " + GROUND_SPEED
                + " " + TENDENCY + " " + TIME, (ivyClient, strings) -> {
            if(contractController.isContractPresent(ContractType.PERIODIC)) {
                if (strings[0].equals(FLIGHT_CODE)) {
                    System.out.println("Movement tracked");
                    // Update the data of the aircraft
                    // Position: Y is set to negative to follow the plan conversion between real -> JavaFX
                    flightData.getSelfTraffic().updatePosition(Double.parseDouble(strings[5]), -Double.parseDouble(strings[6]));
                    // Heading
                    flightData.getSelfTraffic().updateHeading(Double.parseDouble(strings[11]));
                    // Altitude
                    flightData.getSelfTraffic().updateAltitude(Double.parseDouble(strings[9]));
                }

                if(flightData.getSelfTraffic().getRoute().isEmpty()) {
                    getPlan();
                }
          } else {
                initiateContract(ContractType.PERIODIC);
            }
        });
    }

    /**
     * This method concerns all the Demand Messages Airborne can send.
     * When we receive a message of the type PLAN DEMAND,
     * it means that we asked for this plan (when we resume, for instance)
     * We receive the flight plan and the different beacons the flight has to follow.
     * @throws IvyException Exception on the IVY bus
     */
    public void receiveDemandMessage() throws IvyException {
        bus.bindMsg(FLIGHT + " PLAN DEMAND " + TIME + " " + CALL_SIGN + " " + AIRCRAFT + " " + SSR + " " + SPEED +
                " " + FLIGHT_LEVEL + " " + DEP_ARR + " " + DEP_ARR + " " + MESSAGE, (ivyClient, strings) -> {
            if(contractController.isContractPresent(ContractType.DEMAND)) {
                SceneController sc = new SceneController();
                sc.resetBeaconList();
                if (strings[0].equals(FLIGHT_CODE)) {
                    System.out.println("Received plan message!");

                    //Time
                    System.out.println("Time: " + strings[1]);
                    flightData.getSelfTraffic().setTime(strings[1]);

                    //CallSign
                    System.out.println("Callsign: " + strings[2]);
                    flightData.getSelfTraffic().setAircraftType(strings[2]);

                    //Aircraft Type
                    System.out.println("AircraftType: " + strings[3]);
                    flightData.getSelfTraffic().setAircraftType(strings[3]);

                    //Speed
                    System.out.println("Speed: " + strings[5]);
                    flightData.getSelfTraffic().setSpeed(Double.parseDouble(strings[5]));

                    //Arrival, departure and Beacon list
                    System.out.println("List: " + strings[9]);
                    try {
                        String[] route = addBeacons(strings[9]);
                        flightData.getSelfTraffic().getFlightPlan().set(strings[7], strings[8], route);
                    } catch (IndexOutOfBoundsException e) {
                        System.err.println("Reached end of the route! Stopping Airborne System");
                        sendOrder(FLIGHT_CODE + " STOP");
                    }
                }
            } else {
                initiateContract(ContractType.DEMAND);
            }
        });
    }

    /**
     * This method concerns all the Event Messages Airborne can send.
     * When the flight reaches a beacon of the flight plan, a Beacon Event is emitted,
     * meaning that this beacon is now useless for the flight, so we delete it from our list of beacons.
     * We receive here the passed beacon
     * @throws IvyException Exception on the IVY bus
     */
    public void receiveEventMessage() throws IvyException {
        bus.bindMsg(FLIGHT + " BEACON EVENT " + BEACON + " " + FLIGHT_LEVEL + " " + TIME, (ivyClient, strings) -> {
            if(contractController.isContractPresent(ContractType.DEMAND)) {
                if (strings[0].equals(FLIGHT_CODE)) {
                    System.out.println("Beacon passed : " + strings[1]);
                    getPlan();
                    flightData.getSelfTraffic().deleteBeaconFromRoute(strings[1]);
                }
            } else {
                initiateContract(ContractType.DEMAND);
            }
        });
    }

    /**
     * If the contract is not valid anymore (because the Airborne System had to be restarted, for instance),
     * we revoke it by giving its type, because we can have only one contract per type of contract.
     * @param contractType Contract to withdraw from the contract list
     * @return The status of the revocation
     * @throws IvyException Exception on IVY bus
     */
    public boolean revokeContract(ContractType contractType) throws IvyException{
        switch(contractType) {
            case EVENT:
                sendOrder(FLIGHT_CODE + " AAR EVENT REVOKE");
                break;
            case DEMAND:
                sendOrder(FLIGHT_CODE + " AAR DEMAND REVOKE");
                break;
            case PERIODIC:
                sendOrder(FLIGHT_CODE + " AAR PERIODIC REVOKE");
                break;
        }
        AtomicBoolean status = new AtomicBoolean(false);

        bus.bindMsg(FLIGHT + " AAR REVOKE " + CONTRACT + " " + CONTRACT_STATUS + CONTRACT_RESPONSE, ((ivyClient, strings) -> {
            if(strings[0].equals(FLIGHT_CODE)) {
                if(strings[2].equals("WILCO")) {
                    status.set(true);
                } else {
                        System.out.println("Could not revoke contract: " + strings[1]);
                    }
            }else {
                System.out.println("Could not revoke contracts!");
            }
        }));
        return status.get();
    }

    private String[] addBeacons(String beacons) {
        flightData.clearRoute();
        String regex = "([A-Z]{3,5})\\sV\\s(\\d{2}:\\d{2})\\s(\\d{3})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(beacons);
        String[] route = new String[matcher.groupCount()];
        System.out.println(route.length);
        while (matcher.find()) {
            String code = matcher.group(1);
            String time = matcher.group(2);
            String level = matcher.group(3);

            flightData.addBeaconToRoute(code, time, level);
        }
        return route;
    }

    /**
     * To accept the messages from the Airborne System,
     * we need to instantiate contracts depending on the type of message.
     * This method is called when we notify that there is no valid contract for a said message.
     * @param contractType Contract type to be initiated
     * @return Status on if the contract was initiated
     * @throws InterruptedException Exception on contract validation
     */
    public boolean sendContractType(ContractType contractType) throws InterruptedException {
        AtomicBoolean status = new AtomicBoolean(false);
        sendOrder(FLIGHT_CODE + " AAR " + contractType);
        try {
            bus.bindMsg(FLIGHT + " AAR " + CONTRACT + " " + CONTRACT_STATUS + " ", (ivyClient, strings) -> {
            if(strings[2].equals("WILCO")) {
                if(strings[1].equalsIgnoreCase(contractType.toString())) {
                    contractController.addContract(contractType);
                    status.set(true);
                }
            } else if(strings[2].equals("UNABLE")) {
                System.out.println("Unable to instantiate the contract " + strings[1]);
                status.set(false);
            }
            });
        } catch (IvyException e) {
            throw new RuntimeException(e);
        }
        return status.get();
    }

    /**
     * Ask the Airborne System to instantiate its own contract at the same time we create Ground's one
     * @return Status on if the contract has been instantiated
     * @throws IvyException Exception on IVY bus
     */
    public boolean sendContracts() throws IvyException {
        boolean status = false;
        Collection<IvyClient> clients = bus.getIvyClients();
        for (IvyClient client : clients) {
            if (client.getApplicationName().equals("MANIRAVI_Airborne_System")) {
                status = true;
                break;
            }
        }
        if(status) {
            try {
                status = sendContractType(ContractType.PERIODIC);
                status = sendContractType(ContractType.EVENT);
                status = sendContractType(ContractType.DEMAND);
                sendOrder(FLIGHT_CODE + " GET PLN");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return status;
    }
}
