import Controller.MessageControllerAirborne;
import fr.dgac.ivy.IvyException;

/**
 * @author Malik Willemy
 */
public class Main {

    /**
     * @param args the command line arguments. Index 0 is related to the Flight Identification number
     */
    public static void main(String[] args) {

        MessageControllerAirborne messageController = new MessageControllerAirborne("456");
        //stop Ivy bus when application stopped
        Runtime.getRuntime().addShutdownHook(new Thread(messageController::stopBus, "Shutdown-thread"));
        try{
            messageController.startBus();
            messageController.sendOrderToRejeu();
            messageController.sendEventToGroundSystem();
            messageController.sendDemandToGroundSystem();
            messageController.sendPeriodicToGroundSystem();
            messageController.bindingContractFromGroundSystem();
        }catch(IvyException e){
            System.err.println("An error has occurred : " + e.getMessage() + ". The bus has stopped");
        }
    }
}
