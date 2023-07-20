package Controller;

import java.util.Arrays;
import java.util.Objects;

/**
 * Controller of Rejeu messages, convert the messages received to something Rejeu will understand, and do the opposite
 */
public class RejeuController {
    public static String  convertClimbMessageForRejeu(String[] args){
        String preparedMessage =  "AircraftLevel Flight=" + args[0] + " Fl=" + args[1];
        if(args.length == 3) preparedMessage += " Rate=" + args[2].substring(1);
        return preparedMessage;
    }

    public static String convertHeadingMessageForRejeu(String[] args){
        return "AircraftHeading Flight=" + args[0] + " To=" + args[1];
    }

    public static String convertDirectMessageForRejeu(String[] args) {
        return "AircraftDirect Flight=" + args[0] + " Beacon=" + args[1];
    }
    

    public static String convertResumePlnMessageForRejeu(String flight) {
        return "AircraftDirect Flight=" + flight + " Beacon=RESUME_PLN";
    }

    public static String convertGetPlnMessageForRejeu(String flight){
        return "GetPln MsgName=PlnEvent Flight=" + flight + " From=now";
    }

    public static String convertReportMessageForController(String[] args, String gotFlightCode){
        String gotOrder = args[2].split("\\|")[0];
        String gotParameter = args[2].split("\\|")[2];
        String gotResult = args[0];
        String gotInfo = args[1];
        String preparedMessage = gotFlightCode + " REPORT EVENT ";
        switch (gotOrder) {
            case "AircraftHeading":
                preparedMessage += "HEAD TO " + gotParameter + " " + gotResult;
                break;
            case "AircraftLevel":
                preparedMessage += "CLIMB TO " + gotParameter + " " + gotResult;
                break;
            case "AircraftDirect":
                preparedMessage += "DIRECT TO " + gotParameter + " " + gotResult;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + gotOrder);
        }
        if(gotResult.equals("ERROR") || gotResult.equals("WARNING")) preparedMessage += " " + gotInfo;
        return preparedMessage;
    }

    /**
     *
     * @param args List of arguments from TrackedMoveEvent message
     * @return Message for controller ex: 456 TRACKED MOVED EVENT AAF312Q 4315 -- I 42.92 25.75 234 445 255 -2039 28 503 -1 11:03:54
     */
    public static String convertTrackMessageForController(String[] args){
        String gotFlightCode = args[0];
        String gotCallSign = args[1];
        String gotSsr = args[2];
        String gotSector = args[3];
        String gotLayers = args[4];
        String gotX = args[5];
        String gotY = args[6];
        String gotVx = args[7];
        String gotVy = args[8];
        String gotFlightLevel = args[9];
        String gotRate = args[10];
        String gotHeading = args[11];
        String gotGroundSpeed = args[12];
        String gotTendency = args[13];
        String gotTime = args[14];
        return gotFlightCode + " TRACKED MOVED EVENT " + gotCallSign + " " + gotSsr + " " + gotSector + " " +
                gotLayers + " " + gotX + " " + gotY + " " + gotVx + " " + gotVy + " " + gotFlightLevel + " " +
                gotRate + " " + gotHeading + " " + gotGroundSpeed + " " + gotTendency + " " + gotTime;
    }

    /**
     *
     * @param args List of arguments from Pln Messages
     * @return Message for Controller ex: 456 PLAN EVENT 11:03:53 AAF312Q A319 4315 436 380 LPPR LFPO [AMB V 11:03 255, TIRET V 11:07 240, POLLY V 11:13 240, LUREN V 11:18 240, EPL V 11:29 240, POGOL V 11:32 240, SOREM V 11:34 240, STR V 11:36 240]
     */
    public static String convertPlnDemandMessageToController(String[] args) {
        String gotFlightCode = args[1];
        String gotTime = args[2];
        String gotCallSign = args[3];
        String gotAircraft = args[4];
        String gotSsr = args[5];
        String gotSpeed = args[6];
        String gotRfl = args[7];
        String gotDep = args[8];
        String gotArr = args[9];
        try {
            if (!Objects.equals(args[10], " ")) {
                String[] gotList = args[10].split(" ");

                String wayPoints = extractBeaconList(gotList);
                return gotFlightCode + " PLAN DEMAND " + gotTime + " " + gotCallSign + " " + gotAircraft + " " +
                        gotSsr + " " + gotSpeed + " " + gotRfl + " " + gotDep + " " + gotArr + " " + wayPoints;
            } else
                return gotFlightCode + " PLAN DEMAND " + gotTime + " " + gotCallSign + " " + gotAircraft + " " +
                        gotSsr + " " + gotSpeed + " " + gotRfl + " " + gotDep + " " + gotArr;
        } catch (IndexOutOfBoundsException e) {
            return gotFlightCode + " PLAN DEMAND " + gotTime + " " + gotCallSign + " " + gotAircraft + " " +
                    gotSsr + " " + gotSpeed + " " + gotRfl + " " + gotDep + " " + gotArr + " ";
        }
    }

    public static String convertGetPlnEventMessageToController(String[] args) {
        String gotFlightCode = args[0];
        String gotTime = args[1];
        String gotCallSign = args[2];
        String gotAircraft = args[3];
        String gotSsr = args[4];
        String gotSpeed = args[5];
        String gotRfl = args[6];
        String gotDep = args[7];
        String gotArr = args[8];
        try {
            if (!Objects.equals(args[9], " ")) {
                String[] gotList = args[9].split(" ");
                String wayPoints = extractBeaconList(gotList);
                return gotFlightCode + " PLAN EVENT " + gotTime + " " + gotCallSign + " " + gotAircraft + " " +
                        gotSsr + " " + gotSpeed + " " + gotRfl + " " + gotDep + " " + gotArr + " " + wayPoints;
            } else
                return gotFlightCode + " PLAN EVENT " + gotTime + " " + gotCallSign + " " + gotAircraft + " " +
                        gotSsr + " " + gotSpeed + " " + gotRfl + " " + gotDep + " " + gotArr;
        } catch (IndexOutOfBoundsException e) {
            return gotFlightCode + " PLAN EVENT " + gotTime + " " + gotCallSign + " " + gotAircraft + " " +
                    gotSsr + " " + gotSpeed + " " + gotRfl + " " + gotDep + " " + gotArr + " ";
        }
    }

    private static String extractBeaconList(String[] gotList) {
        String[] gotWaypoints = new String[gotList.length / 4];
        for (int i = 0; i < gotList.length; i += 4) {
            gotWaypoints[i / 4] = gotList[i] + " " + gotList[i + 1] + " " + gotList[i + 2] + " " + gotList[i + 3];
        }
        return Arrays.toString(gotWaypoints).replace(",", "").replace("[", "").replace("]", "");
    }

    /**
     *
     * @param args List of arguments from Pln Messages
     * @return Message for Controller ex: 456 BEACON EVENT RAMEN 320 11:03:53
     */
    public static String convertBeaconMessageToController(String[] args) {
        String gotFlightCode = args[0];
        String gotBeacon = args[1];
        String gotFlightLevel = args[2];
        String gotTime = args[3];
        return gotFlightCode + " BEACON EVENT " + gotBeacon + " " + gotFlightLevel + " " + gotTime;
    }
}
