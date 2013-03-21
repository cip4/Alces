/*
 * Created on Sep 2, 2004
 */
package org.cip4.elk.alces.jmf;

/**
 * Thrown when a JMFMessageFactory cannot be loaded.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class JMFMessageFactoryLoaderException extends RuntimeException
{
    public JMFMessageFactoryLoaderException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
