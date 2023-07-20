import Controller.MessageControllerGround;
import fr.dgac.ivy.IvyException;
import fr.enac.sita.visuradar.data.cartoxanthane.CartographyManagerXanthane;
import fr.enac.sita.visuradar.data.param.VisualParameters;
import fr.enac.sita.visuradar.data.param.VisualParametersManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Airspace;
import model.FlightData;
import view.ViewConstructor;

/**
 * MANIRAVI GROUND SYSTEM
 * Software part of the Maniravi System Build, Remote Pilot System
 * This software is not meant to be used as a standalone, for it to work, the MANIRAVI AIRBORNE SYSTEM must be used.
 * In the scope of the project, the standalone software REJEU(ENAC) needs to be used. Otherwise, the system cannot display the movement or send data
 * @author MANIRAVI TEAM - Group 2
 */
public class Main extends Application {

    MessageControllerGround messageControllerGround;
    CartographyManagerXanthane cartographyManager;
    Airspace airspace;
    FlightData flightData;
    VisualParameters visualParameters;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IvyException {
        generateData();
        messageControllerGround = new MessageControllerGround(flightData, visualParameters);

        String fxmlDocPath = "view/fxml/MANIRAVI_HMI.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlDocPath));

        ViewConstructor root = new ViewConstructor(loader, flightData, messageControllerGround, visualParameters);

        generateMessageController();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        //primaryStage.setMaximized(true);
        primaryStage.setTitle("MANIRAVI");
        primaryStage.show();
    }

    /**
     * Start and bing the Ivy Client
     * @throws IvyException Exception on the Ivy bus
     */
    private void generateMessageController() throws IvyException {
        Runtime.getRuntime().addShutdownHook(new Thread(messageControllerGround::stopBus, "Shutdown-thread"));

        // We bind the different messages possible to our IVY client
        messageControllerGround.startBus();
        messageControllerGround.connectionAirborneSystem();
        messageControllerGround.receiveDemandMessage();
        messageControllerGround.receiveEventMessage();
        messageControllerGround.receiveReportMessage();
        messageControllerGround.receivePeriodicMessage();
    }

    /**
     * Generate the necessary data for the program to work properly
     */
    private void generateData(){
        //Data generation
        cartographyManager = new CartographyManagerXanthane();
        airspace = new Airspace(cartographyManager);
        flightData = new FlightData("456", airspace);
        visualParameters = VisualParametersManager.load();
    }
}