package org.cip4.tools.alces.service.discovery;

import org.cip4.jdflib.core.JDFConstants;
import org.cip4.tools.alces.service.discovery.model.*;
import org.cip4.tools.alces.service.jmfmessage.JmfMessageService;
import org.cip4.tools.alces.service.testrunner.TestRunnerService;
import org.cip4.tools.alces.service.testrunner.model.IncomingJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.TestSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.Future;

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

        byte[] jmfKnownDevices = DiscoveryServiceImplTest.class.getResourceAsStream(RES_ROOT + "bambi-response-knowndevices.jmf").readAllBytes();
        byte[] jmfKnownMessages = DiscoveryServiceImplTest.class.getResourceAsStream(RES_ROOT + "bambi-response-knownmessages.jmf").readAllBytes();

        Future futureMock_1 = Mockito.mock(Future.class);
        doReturn(createJmfResponse(jmfKnownDevices)).when(futureMock_1).get();
        doReturn(futureMock_1).when(testRunnerServiceMock).startTestSession("KNOWN_DEVICES", "JMF_URL");

        Future futureMock_2 = Mockito.mock(Future.class);
        doReturn(createJmfResponse(jmfKnownMessages)).when(futureMock_2).get();
        doReturn(futureMock_2).when(testRunnerServiceMock).startTestSession("KNOWN_MESSAGES", "JMF_URL");

        // act
        discoveryService.discover("JMF_URL");

        // assert
//        assertNotNull(jdfController, "Jdf Controller object is null.");
//
//        assertEquals(6, jdfController.getJdfDevices().size(), "Number of JDF Devices is wrong..");
//        assertEquals(20, jdfController.getJdfMessageServices().size(), "Number of message services is wrong..");
    }

    @Test
    public void processKnownDevices_1() throws Exception {

        // arrange
        doReturn("KNOWN_DEVICES").when(jmfMessageServiceMock).createKnownDevicesQuery();
        byte[] jmfKnownDevices = DiscoveryServiceImplTest.class.getResourceAsStream(RES_ROOT + "bambi-response-knowndevices.jmf").readAllBytes();

        Future futureMock = Mockito.mock(Future.class);
        doReturn(createJmfResponse(jmfKnownDevices)).when(futureMock).get();
        doReturn(futureMock).when(testRunnerServiceMock).startTestSession("KNOWN_DEVICES", "JMF_URL");

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

        Future futureMock = Mockito.mock(Future.class);
        doReturn(createJmfResponse(jmfKnownMessages)).when(futureMock).get();
        doReturn(futureMock).when(testRunnerServiceMock).startTestSession("KNOWN_MESSAGES", "JMF_URL");

        // act
        List<MessageService> messageServices = ReflectionTestUtils.invokeMethod(discoveryService, "processKnownMessages", "JMF_URL");

        // assert
        assertNotNull(messageServices, "JDF Devices are null.");
        assertEquals(20, messageServices.size(), "Number of JDF Devices is wrong.");

        MessageService messageService = messageServices.get(1);
        assertEquals("HoldQueue", messageService.getType(), "Type is wrong.");
        assertEquals("http", messageService.getUrlSchemes(), "URLSchemes is wrong.");
    }

    @Test
    public void processQueueStatus_1() throws Exception {

        // arrange
        doReturn("QUEUE_STATUS").when(jmfMessageServiceMock).createQueueStatusQuery();
        byte[] jmfQueueStatus = DiscoveryServiceImplTest.class.getResourceAsStream(RES_ROOT + "bambi-response-queuestatus.jmf").readAllBytes();

        Future futureMock = Mockito.mock(Future.class);
        doReturn(createJmfResponse(jmfQueueStatus)).when(futureMock).get();
        doReturn(futureMock).when(testRunnerServiceMock).startTestSession("QUEUE_STATUS", "JMF_URL");

        // act
        Queue queue = ReflectionTestUtils.invokeMethod(discoveryService, "processQueueStatus", "JMF_URL");

        // assert
        assertNotNull(queue, "Queue is null.");
        assertEquals("sim003", queue.getDeviceId(), "DeviceID si wrong.");
        assertEquals("Held", queue.getStatus(), "Queue Status is wrong.");
        assertEquals(2, queue.getQueueEntries().size(), "Number of Queue Entries is wrong.");

        QueueEntry queueEntry = queue.getQueueEntries().get(1);
        assertEquals("qe_211129_144457383_041440", queueEntry.getQueueEntryId(), "QueueEntryId is wrong.");
        assertEquals("2893", queueEntry.getJobId(), "JobID is wrong.");
        assertEquals("2", queueEntry.getJobPartId(), "JobPartID is wrong.");
        assertEquals(1, queueEntry.getPriority(), "Priority is wrong.");
        assertEquals("Running", queueEntry.getStatus(), "Status is wrong.");
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
                new String(body)
        );

        // create and return containing test session
        TestSession testSession = new TestSession(null, null);
        testSession.getIncomingJmfMessages().add(incomingJmfMessage);
        return testSession;
    }
}