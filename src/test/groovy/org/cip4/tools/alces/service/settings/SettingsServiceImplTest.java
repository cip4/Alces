package org.cip4.tools.alces.service.settings;

import org.cip4.tools.alces.service.file.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

/**
 * JUnit test case for SettingsServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class SettingsServiceImplTest {

    @TempDir
    private Path workDir;

    @Mock
    private FileService fileServiceMock;

    @InjectMocks
    private SettingsServiceImpl settingsService;

    @Test
    public void readDefaultValue() throws Exception {

        // arrange
        Path confFile = workDir.resolve("alces.conf");

        doReturn(confFile).when(fileServiceMock).getAlcesSettingsFile();

        // act
        settingsService.init();
        String baseUrl = settingsService.getBaseUrl();

        // assert
        assertEquals("http://localhost:9090", baseUrl, "BaseUrl is wrong.");
        assertFalse(confFile.toFile().exists(), "Conf file does exist.");
    }

    @Test
    public void changeValue() throws Exception {

        // arrange
        Path confFile = workDir.resolve("alces.conf");

        doReturn(confFile).when(fileServiceMock).getAlcesSettingsFile();

        // act
        settingsService.init();
        settingsService.setDevicePaneWidth(42);

        // assert
        assertEquals(42, settingsService.getDevicePaneWidth(), "DevicePaneWidth is wrong.");
        assertTrue(confFile.toFile().exists(), "Conf file does exist.");
    }

    @Test
    public void getAddressHistory_empty() throws Exception {

        // arrange
        Properties properties = new Properties();
        ReflectionTestUtils.setField(settingsService, "properties", properties);

        // act
        String[] addressHistory = settingsService.getAddressHistory();

        // assert
        assertNotNull(addressHistory, "Address History is wrong.");
        assertEquals(0, addressHistory.length, "Address History is wrong.");
    }

    @Test
    public void getAddressHistory_single_value() throws Exception {

        // arrange
        Properties properties = new Properties();
        properties.setProperty("address-history", "foo");

        ReflectionTestUtils.setField(settingsService, "properties", properties);

        // act
        String[] addressHistory = settingsService.getAddressHistory();

        // assert
        assertNotNull(addressHistory, "Address History is wrong.");
        assertEquals(1, addressHistory.length, "Address History is wrong.");
        assertEquals("foo", addressHistory[0], "Address History is wrong.");
    }

    @Test
    public void getAddressHistory_two_values() throws Exception {

        // arrange
        Properties properties = new Properties();
        properties.setProperty("address-history", "foo;bar");

        ReflectionTestUtils.setField(settingsService, "properties", properties);

        // act
        String[] addressHistory = settingsService.getAddressHistory();

        // assert
        assertNotNull(addressHistory, "Address History is wrong.");
        assertEquals(2, addressHistory.length, "Address History is wrong.");
        assertEquals("foo", addressHistory[0], "Address History is wrong.");
        assertEquals("bar", addressHistory[1], "Address History is wrong.");
    }

    @Test
    public void appendAddress_new_value() throws Exception {

        // arrange
        Path confFile = workDir.resolve("alces.conf");
        doReturn(confFile).when(fileServiceMock).getAlcesSettingsFile();

        Properties properties = new Properties();
        properties.setProperty("address-history", "1;2;3");

        ReflectionTestUtils.setField(settingsService, "properties", properties);

        // act
        settingsService.appendAddress("4");

        // assert
        String[] addressHistory = settingsService.getAddressHistory();

        assertEquals(4, addressHistory.length, "Size Address History is wrong.");
        assertEquals("4", addressHistory[0], "Value is wrong.");
        assertEquals("1", addressHistory[1], "Value is wrong.");
        assertEquals("2", addressHistory[2], "Value is wrong.");
        assertEquals("3", addressHistory[3], "Value is wrong.");
    }

    @Test
    public void appendAddress_existing_value() throws Exception {

        // arrange
        Path confFile = workDir.resolve("alces.conf");
        doReturn(confFile).when(fileServiceMock).getAlcesSettingsFile();

        Properties properties = new Properties();
        properties.setProperty("address-history", "1;2;3");

        ReflectionTestUtils.setField(settingsService, "properties", properties);

        // act
        settingsService.appendAddress("2");

        // assert
        String[] addressHistory = settingsService.getAddressHistory();

        assertEquals(3, addressHistory.length, "Size Address History is wrong.");
        assertEquals("2", addressHistory[0], "Value is wrong.");
        assertEquals("1", addressHistory[1], "Value is wrong.");
        assertEquals("3", addressHistory[2], "Value is wrong.");
    }

    @Test
    public void updateBaseUrlIp_1() throws Exception {

        // arrange
        Path confFile = workDir.resolve("alces.conf");
        doReturn(confFile).when(fileServiceMock).getAlcesSettingsFile();

        Properties properties = new Properties();

        ReflectionTestUtils.setField(settingsService, "properties", properties);
        ReflectionTestUtils.setField(settingsService, "port", "9192");

        // act
        settingsService.updateBaseUrlIp("127.0.0.1");

        // assert
        String baseUrl = properties.getProperty("base-url");

        assertEquals("http://127.0.0.1:9192", baseUrl, "BaseUrl is wrong.");
    }

    @Test
    public void updateBaseUrlIp_2() throws Exception {

        // arrange
        Path confFile = workDir.resolve("alces.conf");
        doReturn(confFile).when(fileServiceMock).getAlcesSettingsFile();

        Properties properties = new Properties();

        ReflectionTestUtils.setField(settingsService, "properties", properties);
        ReflectionTestUtils.setField(settingsService, "port", "9090");

        // act
        settingsService.updateBaseUrlIp("localhost");

        // assert
        String baseUrl = properties.getProperty("base-url");

        assertEquals("http://localhost:9090", baseUrl, "BaseUrl is wrong.");
    }
}