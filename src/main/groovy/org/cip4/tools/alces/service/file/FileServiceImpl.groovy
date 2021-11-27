package org.cip4.tools.alces.service.file

import groovy.xml.XmlSlurper
import org.apache.commons.lang.SystemUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

import javax.annotation.PostConstruct
import java.nio.file.Files
import java.nio.file.Path;

@Service
class FileServiceImpl implements FileService {

    private static Logger log = LoggerFactory.getLogger(FileServiceImpl.class)

    private static final String JDF_SCHEMA_URL = "https://schema.cip4.org/jdfschema_1_7/"

    private static final String JDF_SCHEMA_FOLDER = "jdf-schema"

    private final Path rootDir

    private final Path cacheDir

    /**
     * Default constructor.
     */
    FileServiceImpl() {
        rootDir = new File(SystemUtils.getUserHome(), ".alces").toPath()
        cacheDir = rootDir.resolve("cache")
    }

    /**
     * Init file service.
     */
    @PostConstruct
    init() {
        rootDir.toFile().mkdirs()
        cacheDir.toFile().mkdirs()
    }

    @Autowired
    private RestTemplate restTemplate

    /**
     * Event is called after applications start up.
     */
    @EventListener(ApplicationReadyEvent.class)
    void onStartUp() {
        reloadJdfSchema();
    }

    /**
     * Return the JDF Schema directory.
     * @return The JDF Schema directory as path.
     */
    Path getJdfSchemaDir() {

        Path jdfSchemaDir = cacheDir.resolve(JDF_SCHEMA_FOLDER)

        if(!jdfSchemaDir.toFile().exists()) {
            jdfSchemaDir.toFile().mkdirs()
        }

        return cacheDir.resolve(JDF_SCHEMA_FOLDER)
    }

    /**
     * Helper method to reload the JDF Schema from website.
     */
    private void reloadJdfSchema() {
        log.info("Download JDF Schema...")

        // download JDF.xsd xsd
        String JDF_XSD = "JDF.xsd"
        byte[] jdfXsdBytes = restTemplate.getForEntity(JDF_SCHEMA_URL + JDF_XSD, byte[].class).getBody()
        Files.write(getJdfSchemaDir().resolve(JDF_XSD), jdfXsdBytes)

        // download referenced files
        def jdfXsd = new XmlSlurper().parse(new ByteArrayInputStream(jdfXsdBytes))

        jdfXsd.include.each{ it ->
            String fileName = it.@schemaLocation.toString()
            byte[] xsdBytes = restTemplate.getForEntity(JDF_SCHEMA_URL + fileName, byte[].class).getBody()
            Files.write(getJdfSchemaDir().resolve(fileName), xsdBytes)
        }

        jdfXsd.import.each{ it ->
            String fileName = it.@schemaLocation.toString()
            byte[] xsdBytes = restTemplate.getForEntity(JDF_SCHEMA_URL + fileName, byte[].class).getBody()
            Files.write(getJdfSchemaDir().resolve(fileName), xsdBytes)
        }

        log.info("JDF Schema has been updated.")
    }
}
