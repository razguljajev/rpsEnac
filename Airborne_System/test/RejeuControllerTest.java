import Controller.RejeuController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class RejeuControllerTest {

    /**Testing if convertClimbMessageForRejeu return correct response while correct data has been sent**/
    @Test
    void testConvertClimbMessageForRejeu() {
        String[] climb = {"Fl280", "340", " 2000"};
        String expected = "AircraftLevel Flight=Fl280 Fl=340 Rate=2000";
        String result = RejeuController.convertClimbMessageForRejeu(climb);
        assertEquals(expected, result);
    }

    /**Testing if convertHeadingMessageForRejeu return correct response while correct data has been sent**/
    @Test
    void testConvertHeadingMessageForRejeu(){
        String[] hdg = {"50", "320"};
        String expected = "AircraftHeading Flight=50 To=320";
        String result = RejeuController.convertHeadingMessageForRejeu(hdg);
        assertEquals(expected, result);
    }

    /**Testing if convertDirectMessageForRejeu return correct response while correct data has been sent**/
    @Test
    void  testConvertDirectMessageForRejeu(){
        String[] directTo = {"FL320", "SOREM"};
        String expected = "AircraftDirect Flight=FL320 " + "Beacon=SOREM";
        String result = RejeuController.convertDirectMessageForRejeu(directTo);
        assertEquals(expected, result);
    }

    /**Testing if convertGetPlanMessageForRejeu return correct response while correct data has been sent**/
    @Test
    void testConvertGetPlanMessageForRejeu(){
        String flPln ="456";
        String expected = "GetPln MsgName=PlnEvent Flight=456 From=now";
        String result = RejeuController.convertGetPlnMessageForRejeu(flPln);
        assertEquals(expected, result);
    }

    /**Testing if convertReportPlanMessageForRejeu with Result = ERROR return error while a particular flight level has been sent**/
    @Test
    void testConvertReportMessageForController(){
        String[] report = {"ERROR", "Info", "AircraftLevel|456|Fl370"};
        String gotFlightCode = "456";
        String expected = "456 REPORT EVENT CLIMB TO Fl370 ERROR Info";
        String result = RejeuController.convertReportMessageForController(report, gotFlightCode);
        assertEquals(expected, result);
    }

    @Test
    void testConvertReportHdgMessageForController(){
        String[] report = {"ERROR", "Info", "AircraftHeading|456|Fl370"};
        String gotFlightCode = "456";
        String expected = "456 REPORT EVENT HEAD TO Fl370 ERROR Info";
        String result = RejeuController.convertReportMessageForController(report, gotFlightCode);
        assertEquals(expected, result);
    }

    /**Testing if convertExtractBeaconListForRejeu return correct response while correct data has been requested **/
    @Test
    void testBeaconMessageToController(){
        String[] wayPoints = {"456", "POLLY", "Fl290", "11:15:00"};
        String expected = "456 BEACON EVENT POLLY Fl290 11:15:00" ;
        String result = RejeuController.convertBeaconMessageToController(wayPoints);
        assertEquals(expected, result);
    }

    @Test
    void testConvertTrackMessageForController(){
        String[] track = {"456", "AAF312Q ", "4315 ",  "--", "I", "42.92", "25.75", "234", "445", "255", "-2039", "28", "503", "-1", "11:03:54"};
        String expected = "456 TRACKED MOVED EVENT AAF312Q  4315  -- I 42.92 25.75 234 445 255 -2039 28 503 -1 11:03:54";
        String result = RejeuController.convertTrackMessageForController(track);
        assertEquals(expected, result);
    }

    @Test
    void testConvertPlnDemandMessageToController(){
        String[] parameters = {"MESSAGE", "456", "11:15:00", "AAF312Q", "A319", "4315", "436", "380", "LPPR", "LFPO"};
        String expected = "456 PLAN DEMAND 11:15:00 AAF312Q A319 4315 436 380 LPPR LFPO ";
        String result = RejeuController.convertPlnDemandMessageToController(parameters);
        assertEquals(expected, result);
    }

    @Test
    void testConvertGetPlnEventMessageToController(){
        String[] track = {"456", "11:03:53", "AAF312Q", "A319", "4315", "436", "380", "LPPR", "LFPO"};
        String expected = "456 PLAN EVENT 11:03:53 AAF312Q A319 4315 436 380 LPPR LFPO ";
        String result = RejeuController.convertGetPlnEventMessageToController(track);
        assertEquals(expected, result);
    }
}
