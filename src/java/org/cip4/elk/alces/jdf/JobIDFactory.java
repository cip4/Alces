/*
 * Created on Mar 20, 2007
 */
package org.cip4.elk.alces.jdf;

/**
 * A factory for generating JDF job IDs (JDF/@JobID).
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public interface JobIDFactory {

    /**
     * Generates a new JDF job ID.
     * @return
     */
    public String newJobID();
    
    /**
     * Generates a new JDF job ID based on an existing job ID. This is useful
     * if you are reusing the same job and want to be able to easily identify
     * the job but at the same time have a unique job ID.
     * @param originalJobId
     * @return
     */
    public String newJobID(String originalJobId);
}
