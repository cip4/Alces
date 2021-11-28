package org.cip4.tools.alces.service.jmfmessage

import groovy.xml.XmlSlurper
import org.cip4.tools.alces.service.file.FileService
import org.cip4.tools.alces.service.settings.SettingsService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.mockito.Mockito.doReturn

/**
 * JUnit test case for JmfMessageServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class JmfMessageServiceImplTest {

    private static final String RES_ROOT = "/org/cip4/tools/alces/service/jmfmessage/"

    @Mock
    private SettingsService settingsServiceMock

    @Mock
    private FileService fileServiceMock

    @InjectMocks
    private JmfMessageServiceImpl jmfMessageService

    @Test
    void createSubmitQueueEntry() throws Exception {

        // arrange
        doReturn("http://www.example.org").when(settingsServiceMock).getBaseUrl()

        File file = new File(JmfMessageServiceImpl.class.getResource(RES_ROOT + "test.jdf").toURI())
        doReturn("abc.jdf").when(fileServiceMock).publishFile(file)

        // act
        String strJmf = jmfMessageService.createSubmitQueueEntry(file)

        // assert
        System.out.println(strJmf)

        def jmf = new XmlSlurper().parseText(strJmf)
        assertEquals("http://www.example.org/alces/jmf/", jmf.Command.QueueSubmissionParams.@ReturnJMF.toString(), "ReturnJMF Attribute is wrong.")
        assertEquals("http://www.example.org/alces/file/abc.jdf", jmf.Command.QueueSubmissionParams.@URL.toString(), "URL Attribute is wrong.")
    }

    @Test
    void createStopPersistentChannelCommand() throws Exception {

        // arrange
        doReturn("http://www.example.org").when(settingsServiceMock).getBaseUrl()

        // act
        String strJmf = jmfMessageService.createStopPersistentChannelCommand()

        // assert
        System.out.println(strJmf)

        def jmf = new XmlSlurper().parseText(strJmf)
        assertEquals("http://www.example.org/alces/jmf/", jmf.Command.StopPersChParams.@URL.toString(), "ReturnJMF Attribute is wrong.")
    }

    @Test
    void createStatusSubscription() throws Exception {

        // arrange
        doReturn("http://www.example.org").when(settingsServiceMock).getBaseUrl()

        // act
        String strJmf = jmfMessageService.createStatusSubscription()

        // assert
        System.out.println(strJmf)

        def jmf = new XmlSlurper().parseText(strJmf)
        assertEquals("http://www.example.org/alces/jmf/", jmf.Query.Subscription.@URL.toString(), "ReturnJMF Attribute is wrong.")
    }
}