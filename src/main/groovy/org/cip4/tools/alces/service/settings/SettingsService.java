package org.cip4.tools.alces.service.settings;

/**
 * Business interface for applications settings.
 */
public interface SettingsService {

    /**
     * Returns Alces configured base url.
     * @return Alces' base URL a String.
     */
    String getBaseUrl();

    /**
     * Updates the base url's ip address.
     * @param ip The new ip address
     */
    void updateBaseUrlIp(String ip);

    /**
     * Returns the current address history.
     * @return The address history as a list of strings.
     */
    String[] getAddressHistory();

    /**
     * Append an address to history.
     * @param address The address to be added.
     */
    void appendAddress(String address);

    /**
     * Returns the last selected directory.
     * @return The last selected directory.
     */
    String getLastSelectedDir();

    /**
     * Sets the last selected dir.
     * @param lastSelectedDir The last selected dir.
     */
    void setLastSelectedDir(String lastSelectedDir);

    /**
     * Returns the Alces' dialog's height.
     * @return The Alces' dialog's height.
     */
    int getAlcesDialogHeight();

    /**
     * Sets the Alces' dialog's height.
     * @param alcesDialogHeight  The Alces' dialog's height.
     */
    void setAlcesDialogHeight(int alcesDialogHeight);

    /**
     * Returns the Alces' dialog's width.
     * @return The Alces' dialog's width.
     */
    int getAlcesDialogWidth();

    /**
     * Sets the Alces' dialog's width.
     * @param alcesDialogWidth The Alces' dialog's width.
     */
    void setAlcesDialogWidth(int alcesDialogWidth);

    /**
     * Returns the main pane height.
     * @return The main pane height
     */
    int getMainPaneHeight();

    /**
     * Sets the main pane height.
     * @param mainPaneHeight The main pane height
     */
    void setMainPaneHeight(int mainPaneHeight);


    /**
     * Returns the test pane width.
     * @return The test pane width
     */
    int getTestPaneWidth();

    /**
     * Sets the test pane width.
     * @param testPaneWidth The test pane width
     */
    void setTestPaneWidth(int testPaneWidth);

    /**
     * Returns the device pane width.
     * @return The device pane width
     */
    int getDevicePaneWidth();

    /**
     * Sets the device pane width.
     * @param devicePaneWidth The device pane width.
     */
    void setDevicePaneWidth(int devicePaneWidth);
}
