/*
 * Created on May 21, 2006
 */
package org.cip4.elk.alces.test.tests;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.cip4.jdflib.jmf.JDFCommand;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.jmf.JDFMessage.EnumFamily;
import org.cip4.jdflib.jmf.JDFMessage.EnumType;

/**
 * If the message tested contains a JMF ReturnQueueEntry message, the JDF file
 * referenced by the SubmitQueueEntry message
 * (JMF/Message[@Type='ReturnQueueEntry']/ReturnQueueEntryParams/@URL) will be
 * validated.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class ReturnQueueEntryTest extends SubmitQueueEntryTest {

    /**
     * Creates a new test and initializes the file system resources required for
     * dereferencing JDF files.
     * 
     * @throws IOException
     *             if the file system resources could not be initialized
     */
    public ReturnQueueEntryTest() throws IOException {
        this("ReturnQueueEntryTest - Tests that the JDF files referenced by JMF ReturnQueueEntry Commands are valid using JDFLib-J's CheckJDF.");
    }

    /**
     * Creates a new test and initializes the file system resources required for
     * dereferencing JDF files.
     * 
     * @param description
     *            a description of the test
     * @throws IOException
     *             if the file system resources could not be initialized
     */
    public ReturnQueueEntryTest(String description) throws IOException {
        super(description);
    }

    @Override
	protected List<JDFCommand> getJMFCommands(JDFJMF jmf) {
    	List<JDFCommand> msgs = (Vector)jmf.getMessageVector(EnumFamily.Command, EnumType.ReturnQueueEntry);
    	return msgs;
    }
    
    @Override
    protected String getJDFURL(JDFCommand jmfCommand) {
    	return jmfCommand.getReturnQueueEntryParams(0).getURL();
    }
}
