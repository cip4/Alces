package org.cip4.tools.alces.service.file;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

/**
 * JUnit test case for FileServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    private static final String RES_ROOT = "/org/cip4/tools/alces/service/file/";

    @Mock
    private RestTemplate restTemplateMock;

    @InjectMocks
    private FileServiceImpl fileService;

    @Test
    public void reloadJdfSchema_1() throws Exception {

        // arrange
        byte[] jdfBytes = FileServiceImplTest.class.getResourceAsStream(RES_ROOT + "jdf.xsd").readAllBytes();
        byte[] jdfCoreBytes = FileServiceImplTest.class.getResourceAsStream(RES_ROOT + "jdfcapability.xsd").readAllBytes();
        byte[] jdfCapabilityBytes = FileServiceImplTest.class.getResourceAsStream(RES_ROOT + "jdfcore.xsd").readAllBytes();
        byte[] jdfTypesBytes = FileServiceImplTest.class.getResourceAsStream(RES_ROOT + "jdftypes.xsd").readAllBytes();

        doReturn(new ResponseEntity<>(jdfBytes, HttpStatus.OK)).when(restTemplateMock)
                .getForEntity(eq("https://schema.cip4.org/jdfschema_1_7/JDF.xsd"), eq(byte[].class));
        doReturn(new ResponseEntity<>(jdfCoreBytes, HttpStatus.OK)).when(restTemplateMock)
                .getForEntity(eq("https://schema.cip4.org/jdfschema_1_7/JDFCore.xsd"), eq(byte[].class));
        doReturn(new ResponseEntity<>(jdfCapabilityBytes, HttpStatus.OK)).when(restTemplateMock)
                .getForEntity(eq("https://schema.cip4.org/jdfschema_1_7/JDFCapability.xsd"), eq(byte[].class));
        doReturn(new ResponseEntity<>(jdfTypesBytes, HttpStatus.OK)).when(restTemplateMock)
                .getForEntity(eq("https://schema.cip4.org/jdfschema_1_7/JDFTypes.xsd"), eq(byte[].class));

        Path testDir = Files.createTempDirectory("alces-test-files-");
        assertTrue(testDir.toFile().exists(), "Test Dir does not exist.");

        ReflectionTestUtils.setField(fileService, "rootDir", testDir);

        // act
        ReflectionTestUtils.invokeMethod(fileService, "reloadJdfSchema");

        // assert
        assertTrue(testDir.resolve("cache/jdf-schema/JDF.xsd").toFile().exists(), "JDF.xsd does not exist.");
        assertTrue(testDir.resolve("cache/jdf-schema/JDFCore.xsd").toFile().exists(), "JDFCore.xsd does not exist.");
        assertTrue(testDir.resolve("cache/jdf-schema/JDFCapability.xsd").toFile().exists(), "JDFCapability.xsd does not exist.");
        assertTrue(testDir.resolve("cache/jdf-schema/JDFTypes.xsd").toFile().exists(), "JDFTypes.xsd does not exist.");

        FileUtils.forceDelete(testDir.toFile());
        assertFalse(testDir.toFile().exists(), "Test Dir does still exist.");
    }

    @Test
    public void reloadJdfSchema_2() throws Exception {

        // arrange
        byte[] jdfBytes = FileServiceImplTest.class.getResourceAsStream(RES_ROOT + "jdf.xsd").readAllBytes();
        byte[] jdfCoreBytes = FileServiceImplTest.class.getResourceAsStream(RES_ROOT + "jdfcapability.xsd").readAllBytes();
        byte[] jdfCapabilityBytes = FileServiceImplTest.class.getResourceAsStream(RES_ROOT + "jdfcore.xsd").readAllBytes();
        byte[] jdfTypesBytes = FileServiceImplTest.class.getResourceAsStream(RES_ROOT + "jdftypes.xsd").readAllBytes();

        doReturn(new ResponseEntity<>(jdfBytes, HttpStatus.OK)).when(restTemplateMock)
                .getForEntity(eq("https://schema.cip4.org/jdfschema_1_7/JDF.xsd"), eq(byte[].class));
        doReturn(new ResponseEntity<>(jdfCoreBytes, HttpStatus.OK)).when(restTemplateMock)
                .getForEntity(eq("https://schema.cip4.org/jdfschema_1_7/JDFCore.xsd"), eq(byte[].class));
        doReturn(new ResponseEntity<>(jdfCapabilityBytes, HttpStatus.OK)).when(restTemplateMock)
                .getForEntity(eq("https://schema.cip4.org/jdfschema_1_7/JDFCapability.xsd"), eq(byte[].class));
        doReturn(new ResponseEntity<>(jdfTypesBytes, HttpStatus.OK)).when(restTemplateMock)
                .getForEntity(eq("https://schema.cip4.org/jdfschema_1_7/JDFTypes.xsd"), eq(byte[].class));

        Path testDir = Files.createTempDirectory("alces-test-files-");
        testDir.resolve("cache/jdf-schema").toFile().mkdirs();

        Files.write(testDir.resolve("cache/jdf-schema/JDF.xsd"), "FILE_EXISTS".getBytes());

        assertTrue(testDir.toFile().exists(), "Test Dir does not exist.");
        assertTrue(testDir.resolve("cache/jdf-schema/JDF.xsd").toFile().exists(), "Test JDF File does not exist.");

        ReflectionTestUtils.setField(fileService, "rootDir", testDir);

        // act
        ReflectionTestUtils.invokeMethod(fileService, "reloadJdfSchema");

        // assert
        assertTrue(testDir.resolve("cache/jdf-schema/JDF.xsd").toFile().exists(), "JDF.xsd does not exist.");
        assertTrue(testDir.resolve("cache/jdf-schema/JDFCore.xsd").toFile().exists(), "JDFCore.xsd does not exist.");
        assertTrue(testDir.resolve("cache/jdf-schema/JDFCapability.xsd").toFile().exists(), "JDFCapability.xsd does not exist.");
        assertTrue(testDir.resolve("cache/jdf-schema/JDFTypes.xsd").toFile().exists(), "JDFTypes.xsd does not exist.");

        FileUtils.forceDelete(testDir.toFile());
        assertFalse(testDir.toFile().exists(), "Test Dir does still exist.");
    }

    @Test
    public void getJdfSchema_0() throws Exception {

        // arrange
        Path testDir = Files.createTempDirectory("alces-test-dir-");
        ReflectionTestUtils.setField(fileService, "rootDir", testDir);

        assertTrue(testDir.toFile().exists(), "Folder not does exist.");

        // act
        Path jdfSchema = fileService.getJdfSchema();

        // assert
        System.out.println(jdfSchema.toString());
        assertTrue(jdfSchema.toString().contains("jdf-schema"), "Path is wrong.");
        assertTrue(jdfSchema.toString().endsWith("JDF.xsd"), "Path is wrong.");

        FileUtils.forceDelete(testDir.toFile());
        assertFalse(testDir.toFile().exists(), "Test Dir does still exist.");
    }

    @Test
    public void publishFile_1() throws Exception {

        // arrange
        Path testDir = Files.createTempDirectory("alces-test-dir-");
        ReflectionTestUtils.setField(fileService, "rootDir", testDir);

        File file = new File(FileServiceImplTest.class.getResource(RES_ROOT + "test.jdf").toURI());

        // act
        String filename = fileService.publishFile(file);

        // assert
        System.out.println("Filename: " + filename);
        assertTrue(filename.endsWith(".jdf"), "Extension is wrong.");
        assertTrue(testDir.resolve("cache/public").resolve(filename).toFile().exists(), "File is missing.");

        FileUtils.forceDelete(testDir.toFile());
        assertFalse(testDir.toFile().exists(), "Test Dir does still exist.");
    }

    @Test
    public void publishFile_2() throws Exception {

        // arrange
        Path testDir = Files.createTempDirectory("alces-test-dir-");
        ReflectionTestUtils.setField(fileService, "rootDir", testDir);

        File file = new File(FileServiceImplTest.class.getResource(RES_ROOT + "test-2.jDf").toURI());

        // act
        String filename = fileService.publishFile(file);

        // assert
        System.out.println("Filename: " + filename);
        assertTrue(filename.endsWith(".jdf"), "Extension is wrong.");
        assertTrue(testDir.resolve("cache/public").resolve(filename).toFile().exists(), "File is missing.");

        FileUtils.forceDelete(testDir.toFile());
        assertFalse(testDir.toFile().exists(), "Test Dir does still exist.");
    }

    @Test
    public void publishFile_3() throws Exception {

        // arrange
        Path testDir = Files.createTempDirectory("alces-test-dir-");
        ReflectionTestUtils.setField(fileService, "rootDir", testDir);

        File file = new File(FileServiceImplTest.class.getResource(RES_ROOT + "test-3").toURI());

        // act
        String filename = fileService.publishFile(file);

        // assert
        System.out.println("Filename: " + filename);
        assertFalse(filename.contains("."), "Extension is wrong.");
        assertTrue(testDir.resolve("cache/public").resolve(filename).toFile().exists(), "File is missing.");

        FileUtils.forceDelete(testDir.toFile());
        assertFalse(testDir.toFile().exists(), "Test Dir does still exist.");
    }

    @Test
    public void publishFile_4() throws Exception {

        // arrange
        Path testDir = Files.createTempDirectory("alces-test-dir-");
        ReflectionTestUtils.setField(fileService, "rootDir", testDir);

        File file = new File(FileServiceImplTest.class.getResource(RES_ROOT + "jdf.xsd").toURI());

        // act
        String filename = fileService.publishFile(file);

        // assert
        System.out.println("Filename: " + filename);
        assertTrue(filename.endsWith(".xsd"), "Extension is wrong.");
        assertTrue(testDir.resolve("cache/public").resolve(filename).toFile().exists(), "File is missing.");

        FileUtils.forceDelete(testDir.toFile());
        assertFalse(testDir.toFile().exists(), "Test Dir does still exist.");
    }
}
