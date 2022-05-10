package org.cip4.tools.alces.service.jmfmessage;

import org.cip4.jdflib.jmf.JMFBuilder;
import org.cip4.jdflib.jmf.JMFBuilderFactory;
import org.cip4.tools.alces.service.file.FileService;
import org.cip4.tools.alces.service.settings.SettingsService;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

public class IntegrationUtils {

    private final JMFBuilder jmfBuilder;

    private final SettingsService settingsService;
    private final FileService fileService;
    private final Component component;

    /**
     * Custom constructor.
     */
    public IntegrationUtils(SettingsService settingsService, FileService fileService, Component component) {
        this.settingsService = settingsService;
        this.fileService = fileService;
        this.component = component;

        // create and configure JMF Builder
        jmfBuilder = JMFBuilderFactory.getJMFBuilder(null);
        jmfBuilder.setAgentName(settingsService.getAgentName());
        jmfBuilder.setAgentVersion(settingsService.getAgentVersion());
        jmfBuilder.setSenderID(settingsService.getSenderId());
    }

    /**
     * Returns a pre-configured instance of the JDFLibJ JmfBuilder.
     * @return The JMF Builder
     */
    public JMFBuilder getJmfBuilder() {
        return jmfBuilder;
    }

    /**
     * Returns Alces' subscriber URL.
     * @return The subscriber URL of Alces.
     */
    public String getSubscriberUrl() {
        return settingsService.getBaseUrl() + "/alces/jmf/";
    }

    /**
     * Returns Alces' return URL.
     * @return The return URL of Alces.
     */
    public String getReturnUrl() {
        return getSubscriberUrl();
    }

    /**
     * Publish a file to external services by an URL.
     * @param file The file to be published
     * @return The files public URL.
     */
    public String publishFile(File file) {

        // publish file
        String filename = fileService.publishFile(file);

        // craete and return public URL
        return settingsService.getBaseUrl() + "/alces/file/" + filename;
    }

    /**
     * Select a file from the file system using a dialog.
     * @param title The dialogs title.
     * @param fileFilter The file filter.
     * @return The selected file.
     */
    public File selectFile(String title, FileFilter fileFilter) {

        // create JFileChooser dialog
        JFileChooser fileChooser = new JFileChooser(settingsService.getLastSelectedDir());
        fileChooser.addChoosableFileFilter(fileFilter);
        fileChooser.setDialogTitle(title);

        int returnValue = fileChooser.showOpenDialog(this.component);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            settingsService.setLastSelectedDir(fileChooser.getCurrentDirectory().getAbsolutePath());
            return fileChooser.getSelectedFile();
        } else {
            return null;
        }
    }
}
