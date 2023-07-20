import Controller.MessageControllerAirborne;
import fr.dgac.ivy.IvyException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class MessageControllerAirborneTest {

    //If contract does not exist send ContractException error
    @Test
    void testSendEventToGroundSystem() throws IvyException {
        MessageControllerAirborne messageControllerAirborne = new MessageControllerAirborne("456");
        messageControllerAirborne.bus.sendToSelf(true);
        messageControllerAirborne.startBus();

        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bs);
        System.setErr(ps);

        messageControllerAirborne.sendEventToGroundSystem();
        messageControllerAirborne.sendMessage("PlnEvent Flight=456 Time=07:00:12 CallSign=CRX766 AircraftType=SB20 Ssr=6744 Speed=360 Rfl=190 Dep=LFSB Arr=LFBO Rvsm=TRUE Tcas=TA_RA Adsb=NO List=OLRAK V 06:53 190 NARAK A 07:02 190 NOPTA A 07:09 190");

        //Remove new line character at the end of the printStream
        String s = bs.toString().trim();

        assertEquals("Contract AAR \"Event\" may not be initialized or can be revoked", s);

    }
}
