package org.cip4.tools.alces.service.file;

import java.io.File;
import java.nio.file.Path;

/**
 * Interface encapsulating all file and caching management.
 */
public interface FileService {

    /**
     * Returns the path to the JDF Schema.
     * @return The path to JDF Schema as path object.
     */
    Path getJdfSchema();

    /**
     * Makes a file available via http for job submission.
     * @param file The file to be published.
     * @return The new filename of the published file.
     */
    String publishFile(File file);

    /**
     * Returns a published file as byte array.
     * @param filename The requested file's name.
     * @return The requested file as file.
     */
    File getPublishedFile(String filename);
}
