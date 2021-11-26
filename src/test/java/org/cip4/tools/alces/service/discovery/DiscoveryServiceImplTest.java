package org.cip4.tools.alces.service.discovery;

import org.cip4.jdflib.core.JDFConstants;
import org.cip4.tools.alces.service.discovery.model.JdfController;
import org.cip4.tools.alces.service.discovery.model.JdfDevice;
import org.cip4.tools.alces.service.discovery.model.JdfMessageService;
import org.cip4.tools.alces.service.jmfmessage.JmfMessageService;
import org.cip4.tools.alces.service.testrunner.TestRunnerService;
import org.cip4.tools.alces.service.testrunner.model.IncomingJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.TestSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

/**
 * JUnit test case for DiscoveryServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
public class DiscoveryServiceImplTest {

    private static final String RES_ROOT = "/org/cip4/tools/alces/service/discovery/";

    @Mock
    private JmfMessageService jmfMessageServiceMock;

    @Mock
    private TestRunnerService testRunnerServiceMock;

    @InjectMocks
    private DiscoveryServiceImpl discoveryService;

    @Test
    public void discover() throws Exception {

        // arrange
        doReturn("KNOWN_DEVICES").when(jmfMessageServiceMock).createKnownDevicesQuery();
        doReturn("KNOWN_MESSAGES").when(jmfMessageServiceMock).createKnownMessagesQuery();
        // doReturn("KNOWN_QUEUE_STATUS").when(jmfMessageServiceMock).createQueryQueueStatus();

        byte[] jmfKnownDevices = DiscoveryServiceImplTest.class.getResourceAsStream(RES_ROOT + "bambi-response-knowndevices.jmf").readAllBytes();
        byte[] jmfKnownMessages = DiscoveryServiceImplTest.class.getResourceAsStream(RES_ROOT + "bambi-response-knownmessages.jmf").readAllBytes();
        // byte[] jmfQueueStatus = DiscoveryServiceImplTest.class.getResourceAsStream(RES_ROOT + "bambi-response-queuestatus.jmf").readAllBytes();

        doReturn(createJmfResponse(jmfKnownDevices)).when(testRunnerServiceMock).startTestSession("KNOWN_DEVICES", "JMF_URL");
        doReturn(createJmfResponse(jmfKnownMessages)).when(testRunnerServiceMock).startTestSession("KNOWN_MESSAGES", "JMF_URL");
        // doReturn(createJmfResponse(jmfQueueStatus)).when(testRunnerServiceMock).startTestSession("KNOWN_QUEUE_STATUS", "JMF_URL");

        // act
        JdfController jdfController = discoveryService.discover("JMF_URL");

        // assert
        assertNotNull(jdfController, "Jdf Controller object is null.");

        assertEquals(6, jdfController.getJdfDevices().size(), "Number of JDF Devices is wrong..");
        assertEquals(20, jdfController.getJdfMessageServices().size(), "Number of message services is wrong..");
    }

    @Test
    public void processKnownDevices_1() throws Exception {

        // arrange
        doReturn("KNOWN_DEVICES").when(jmfMessageServiceMock).createKnownDevicesQuery();
        byte[] jmfKnownDevices = DiscoveryServiceImplTest.class.getResourceAsStream(RES_ROOT + "bambi-response-knowndevices.jmf").readAllBytes();
        doReturn(createJmfResponse(jmfKnownDevices)).when(testRunnerServiceMock).startTestSession("KNOWN_DEVICES", "JMF_URL");

        // act
        List<JdfDevice> jdfDevices = ReflectionTestUtils.invokeMethod(discoveryService, "processKnownDevices", "JMF_URL");

        // assert
        assertNotNull(jdfDevices, "JDF Devices are null.");
        assertEquals(6, jdfDevices.size(), "Number of JDF Devices is wrong.");

        JdfDevice jdfDevice = jdfDevices.get(1);
        assertEquals("simWF", jdfDevice.getDeviceId(), "DeviceId is wrong.");
        assertEquals("simWF", jdfDevice.getJmfSenderId(), "JMFSenderID is wrong.");
        assertEquals("http://localhost:8080/SimWorker/jmf/simWF", jdfDevice.getJmfUrl(), "JMFURL is wrong.");
        assertEquals("1.3", jdfDevice.getJdfVersions(), "JDFVersions is wrong.");
        assertEquals("Bambi Push Simulation Wide Format Device", jdfDevice.getDescriptiveName(), "DescriptiveName is wrong.");
        assertEquals("Bambi Push Simulation Wide Format Device", jdfDevice.getDeviceType(), "DeviceType is wrong.");
    }

    @Test
    public void processKnownMessages_1() throws Exception {

        // arrange
        doReturn("KNOWN_MESSAGES").when(jmfMessageServiceMock).createKnownMessagesQuery();
        byte[] jmfKnownMessages = DiscoveryServiceImplTest.class.getResourceAsStream(RES_ROOT + "bambi-response-knownmessages.jmf").readAllBytes();
        doReturn(createJmfResponse(jmfKnownMessages)).when(testRunnerServiceMock).startTestSession("KNOWN_MESSAGES", "JMF_URL");

        // act
        List<JdfMessageService> jdfMessageServices = ReflectionTestUtils.invokeMethod(discoveryService, "processKnownMessages", "JMF_URL");

        // assert
        assertNotNull(jdfMessageServices, "JDF Devices are null.");
        assertEquals(20, jdfMessageServices.size(), "Number of JDF Devices is wrong.");

        JdfMessageService jdfMessageService = jdfMessageServices.get(1);
        assertEquals("HoldQueue", jdfMessageService.getType(), "Type is wrong.");
        assertEquals("http", jdfMessageService.getUrlSchemes(), "URLSchemes is wrong.");
    }

    /**
     * Helper method to create an incoming message from a body.
     * @param body The message body as byte array.
     * @return The IncomingJmfMessage object.
     */
    private TestSession createJmfResponse(byte[] body) {

        // prepare incoming message
        IncomingJmfMessage incomingJmfMessage = new IncomingJmfMessage(
                JDFConstants.MIME_JMF,
                "HEADER",
                new String(body),
                true
        );

        // create and return containing test session
        TestSession testSession = new TestSession(null, null);
        testSession.getIncomingJmfMessages().add(incomingJmfMessage);
        return testSession;
    }
}