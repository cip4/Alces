package org.cip4.tools.alces.service.settings;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.cip4.tools.alces.service.file.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Implementation of the settings service.
 */
@Service
public class SettingsServiceImpl implements SettingsService {

	private static Logger log = LoggerFactory.getLogger(SettingsServiceImpl.class);

	@Autowired
	private FileService fileService;

	@Value("${server.port}")
	private String port;

	private final static String KEY_BASEURL = "base-url";
	private final static String KEY_LAST_SELECTED_DIR = "last-selected-dir";
	private final static String KEY_ADDRESS_HISTORY = "address-history";

	private final static String KEY_ALCES_DIALOG_HEIGHT = "alces-dialog-height";
	private final static String KEY_ALCES_DIALOG_WIDTH = "alces-dialog-width";

	private final static String KEY_MAIN_PANE_HEIGHT = "main-pane-height";
	private final static String KEY_TEST_PANE_WIDTH = "test-pane-width";
	private final static String KEY_DEVICE_PANE_WIDTH = "device-pane-width";

	private Properties properties;

	@PostConstruct
	public void init() {
		this.properties = new Properties();

		if(fileService.getAlcesSettingsFile().toFile().exists()) {
			try(InputStream inputStream = Files.newInputStream(fileService.getAlcesSettingsFile())) {
				this.properties.load(inputStream);
				log.info("Config file has been read successfully.");

			} catch (IOException e) {
				log.error("Error reading config file.");
			}

		} else {
			log.info("No config file has been found. Load default values.");
		}
	}

	/**
	 * Save the properties state to file.
	 */
	private void saveState() {
		try (OutputStream outputStream = Files.newOutputStream(fileService.getAlcesSettingsFile())) {
			this.properties.store(outputStream, null);
		} catch (IOException e) {
			log.error("Error writing settings.");
		}
	}

	@Override
	public String getBaseUrl() {
		return properties.getProperty(KEY_BASEURL, "http://localhost:9090");
	}

	@Override
	public void updateBaseUrlIp(String ip) {

		String baseUrl = String.format("http://%s:%s", ip, port);

		properties.setProperty(KEY_BASEURL, baseUrl);
		saveState();
	}

	@Override
	public String[] getAddressHistory() {
		String addressHistory = properties.getProperty(KEY_ADDRESS_HISTORY, "");

		return StringUtils.isEmpty(addressHistory) ? new String[]{} : addressHistory.split(";");
	}

	@Override
	public void appendAddress(String address) {
		List<String> addressHistory = new ArrayList<>(Arrays.asList(getAddressHistory()));
		addressHistory = new ArrayList<>(addressHistory.stream().filter(s -> !s.equalsIgnoreCase(address)).toList());
		addressHistory.add(0, address);

		properties.setProperty(KEY_ADDRESS_HISTORY, String.join(";", addressHistory));
		saveState();
	}

	@Override
	public String getLastSelectedDir() {
		return properties.getProperty(KEY_LAST_SELECTED_DIR, FileUtils.getUserDirectoryPath());
	}

	@Override
	public void setLastSelectedDir(String lastSelectedDir) {
		properties.setProperty(KEY_LAST_SELECTED_DIR, lastSelectedDir);
		saveState();
	}

	@Override
	public int getAlcesDialogHeight() {
		return Integer.parseInt(properties.getProperty(KEY_ALCES_DIALOG_HEIGHT, "906"));
	}

	@Override
	public void setAlcesDialogHeight(int alcesDialogHeight) {
		properties.setProperty(KEY_ALCES_DIALOG_HEIGHT, Integer.toString(alcesDialogHeight));
		saveState();
	}

	@Override
	public int getAlcesDialogWidth() {
		return Integer.parseInt(properties.getProperty(KEY_ALCES_DIALOG_WIDTH, "1373"));
	}

	@Override
	public void setAlcesDialogWidth(int alcesDialogWidth) {
		properties.setProperty(KEY_ALCES_DIALOG_WIDTH, Integer.toString(alcesDialogWidth));
		saveState();
	}

	@Override
	public int getMainPaneHeight() {
		return Integer.parseInt(properties.getProperty(KEY_MAIN_PANE_HEIGHT, "564"));
	}

	@Override
	public void setMainPaneHeight(int mainPaneHeight) {
		properties.setProperty(KEY_MAIN_PANE_HEIGHT, Integer.toString(mainPaneHeight));
		saveState();
	}

	@Override
	public int getTestPaneWidth() {
		return Integer.parseInt(properties.getProperty(KEY_TEST_PANE_WIDTH, "432"));
	}

	@Override
	public void setTestPaneWidth(int testPaneWidth) {
		properties.setProperty(KEY_TEST_PANE_WIDTH, Integer.toString(testPaneWidth));
		saveState();
	}

	@Override
	public int getDevicePaneWidth() {
		return Integer.parseInt(properties.getProperty(KEY_DEVICE_PANE_WIDTH, "235"));
	}

	@Override
	public void setDevicePaneWidth(int devicePaneWidth) {
		properties.setProperty(KEY_DEVICE_PANE_WIDTH, Integer.toString(devicePaneWidth));
		saveState();
	}
}
