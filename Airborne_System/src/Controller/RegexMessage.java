package Controller;

/**
 * @author Malik Willemy
 * This interface is used for the different REGEX recognition.
 */
public interface RegexMessage {
    String FLIGHT = "([0-9]{3,4})";
    String FLIGHT_LEVEL = "([0-9]+)";
    String RATE = "( \\-?[0-9]+)*";
    String POSITION = "([0-9]{1,3}|[A-Z]{2,5})";
    String RESULT = "(ERROR|OK|WARNING)";
    String INFO = "(.*)";
    String ORDER = "(.*)";
    String CALL_SIGN = "(Unknown|.*)";
    String SSR = "([0-9]{4})";
    String SECTOR = "(\\-\\-|[A-Z]{2})";
    String LAYERS = "([A-Z])";
    String X_Y = "(\\-?[0-9]{1,3}.[0-9]{2})";
    String VX_VY = "(\\-?[0-9]{1,3})";
    String RATED = "(\\-?[0-9]+)";
    String HEADING = "([0-9]{1,3})";
    String GROUND_SPEED = "([0-9]+)";
    String TENDENCY = "(0|1|\\-1|\\-2)";
    String TIME = "(.*)";
    String AIRCRAFT = "(.*)";
    String SPEED = "([0-9]+)";
    String DEP_ARR = "([A-Z]{4})";
    String LIST = "( |([A-Z]{3,5}\\s[A-Z]+\\s\\d{2}:\\d{2}\\s+\\d{3} )+)";
    String MESSAGE = "(.*)";
    String DLINK_Optional = " Dlink=FALSE?";
    String MESSAGE_AS_LIST = "(.*) ?";
    String CONTRACT = "(PERIODIC|EVENT|DEMAND)";
    String BEACON = "([A-Z]+)";
    String MODE = "(Vertical|Horizontal)";
}
