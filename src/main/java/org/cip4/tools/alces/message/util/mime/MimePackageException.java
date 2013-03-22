/*
 * Created on Sep 28, 2004
 */
package org.cip4.tools.alces.message.util.mime;

/**
 * Thrown when an exceptioin occurs while processing MIME packages.
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class MimePackageException extends Exception {
    
    public MimePackageException() {
        super();
    }
    
    public MimePackageException(String message) {
        super(message);
    }
    
    public MimePackageException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public MimePackageException(Throwable cause) {
        super(cause);
    }
}
