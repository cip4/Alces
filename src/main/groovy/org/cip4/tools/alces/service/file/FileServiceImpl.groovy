package org.cip4.tools.alces.service.file

import groovy.xml.XmlSlurper
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang.RandomStringUtils
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.SystemUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

import java.nio.file.Files
import java.nio.file.Path;

@Service
class FileServiceImpl implements FileService {

    private static Logger log = LoggerFactory.getLogger(FileServiceImpl.class)

    private static final String JDF_SCHEMA_URL = "https://schema.cip4.org/jdfschema_1_7/"
    private static final String JDF_SCHEMA_FOLDER = "jdf-schema"
    private static final String JDF_SCHEMA_FILENAME = "JDF.xsd"

    private static final String SETTINGS_FILE_NAME = "alces.conf"

    private static final String CACHE_FOLDER = "cache"

    private static final String PUBLIC_FOLDER = "public"

    private final Path rootDir

    /**
     * Default constructor.
     */
    FileServiceImpl() {
        rootDir = new File(SystemUtils.getUserHome(), ".alces").toPath()
    }

    @Autowired
    private RestTemplate restTemplate

    /**
     * Helper method to define the cache folder.
     * @return A path object pointing to the cache folders location.
     */
    private Path getCacheDir() {
        return rootDir.resolve(CACHE_FOLDER)
    }

    /**
     * Helper method to define the public folder.
     * @return A path object pointing to the public folders location.
     */
    private Path getPublicDir() {
        return getCacheDir().resolve(PUBLIC_FOLDER)
    }

    /**
     * Helper method to define the cache folder.
     * @return A path object pointing to the cache folders location.
     */
    private Path getJdfSchemaDir() {
        return getCacheDir().resolve(JDF_SCHEMA_FOLDER)
    }

    /**
     * Returns the path to the JDF Schema.
     * @return The path to JDF Schema as path object.
     */
    @Override
    Path getAlcesSettingsFile() {
        return rootDir.resolve(SETTINGS_FILE_NAME)
    }

    @Override
    Path getJdfSchema() {
        return getJdfSchemaDir().resolve(JDF_SCHEMA_FILENAME)
    }

    @Override
    String publishFile(File file) {

        // create folder if not exists
        getPublicDir().toFile().mkdirs()

        // build new filename
        String extension = FilenameUtils.getExtension(file.getName()).toLowerCase()
        String filename = RandomStringUtils.randomAlphanumeric(16)

        if(StringUtils.isNotEmpty(extension)) {
            filename += "." + extension
        }

        // copy file to public folder
        Path target = getPublicDir().resolve(filename)
        Files.write(target, file.readBytes())

        // return filename
        return filename
    }

    @Override
    File getPublishedFile(String filename) {
        return getPublicDir().resolve(filename).toFile()
    }

    /**
     * Helper method to reload the JDF Schema from website.
     */
    @EventListener(ApplicationReadyEvent.class)
    private void reloadJdfSchema() {
        log.info("Check JDF Schema version...")

        // get current version n ca
        String localSchemaVersion

        if(getJdfSchema().toFile().exists()) {
            localSchemaVersion = new XmlSlurper().parse(getJdfSchema().toFile()).@version.toString()
        } else {
            localSchemaVersion = "n. a."
        }

        // download JDF.xsd
        byte[] jdfXsdBytes = restTemplate.getForEntity(JDF_SCHEMA_URL + JDF_SCHEMA_FILENAME, byte[].class).getBody()

        // check if version is current
        String remoteSchemaVersion = new XmlSlurper().parseText(new String(jdfXsdBytes)).@version.toString()

        if(!Objects.equals(localSchemaVersion, remoteSchemaVersion)) {

            getJdfSchemaDir().toFile().mkdirs()
            Files.write(getJdfSchemaDir().resolve(JDF_SCHEMA_FILENAME), jdfXsdBytes)

            // download referenced files
            def jdfXsd = new XmlSlurper().parse(new ByteArrayInputStream(jdfXsdBytes))

            jdfXsd.include.each { it ->
                String fileName = it.@schemaLocation.toString()
                byte[] xsdBytes = restTemplate.getForEntity(JDF_SCHEMA_URL + fileName, byte[].class).getBody()
                Files.write(getJdfSchemaDir().resolve(fileName), xsdBytes)
            }

            jdfXsd.import.each { it ->
                String fileName = it.@schemaLocation.toString()
                byte[] xsdBytes = restTemplate.getForEntity(JDF_SCHEMA_URL + fileName, byte[].class).getBody()
                Files.write(getJdfSchemaDir().resolve(fileName), xsdBytes)
            }

            log.info("JDF Schema has been updated.")
        } else {
            log.info("JDF Schema is up to date.")
        }
    }
}
