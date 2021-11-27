package org.cip4.tools.alces.service.file;

import java.nio.file.Path;

/**
 * Interface encapsulating all file and caching management.
 */
public interface FileService {

    /**
     * Returns the directory of the JDF Schema.
     * @return The directory of the JDF Schema as path.
     */
    Path getJdfSchemaDir();
}
