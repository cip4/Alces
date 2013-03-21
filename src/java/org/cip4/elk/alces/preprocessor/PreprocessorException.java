/*
 * Created on Apr 25, 2005
 */
package org.cip4.elk.alces.preprocessor;

/**
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class PreprocessorException extends Exception {

    private static final long serialVersionUID = -7428128346324530047L;

    public PreprocessorException(String description, Throwable throwable) {
        super(description, throwable);
    }
    
}
