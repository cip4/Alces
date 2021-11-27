package org.cip4.tools.alces.jmf;

import java.util.Iterator;

import org.cip4.jdflib.auto.JDFAutoStatusQuParams.EnumDeviceDetails;
import org.cip4.jdflib.auto.JDFAutoStatusQuParams.EnumJobDetails;
import org.cip4.jdflib.core.JDFComment;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.jmf.JDFMessage;
import org.cip4.jdflib.jmf.JDFResourceQuParams;
import org.cip4.jdflib.jmf.JDFResponse;
import org.cip4.jdflib.jmf.JDFStatusQuParams;
import org.cip4.jdflib.jmf.JDFStopPersChParams;
import org.cip4.jdflib.resource.JDFNotification;
import org.cip4.tools.alces.service.testrunner.model.OutgoingJmfMessage;

public class JMFMessageBuilder {

	private static final String JMF_MIMETYPE = "application/vnd.cip4-jmf+xml";

	private static final String JDF_MIMETYPE = "application/vnd.cip4-jdf+xml";

	/**
	 * Builds a JMF message containing a SubmitQueueEntry command that refers to
	 * the specified JDF URL.
	 * 
	 * @param jdfUrl
	 *            JMF/Command[@Type='SubmitQueueEntry']/QueueSubmissionParams/@URL
	 * @return a JMF message containing a SubmitQueueEntry command
	 */
	public static OutgoingJmfMessage buildSubmitQueueEntry(String jdfUrl) {
		JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("CommandSubmitQueueEntry");
		jmf.getCommand(0).getQueueSubmissionParams(0).setURL(jdfUrl);
		return createMessage(jmf);
	}

	/**
	 * Builds a JMF message containing a ResubmitQueueEntry command that refers
	 * to the specified JDF URL.
	 * 
	 * @param jdfUrl
	 *            JMF/Command[@Type='ResubmitQueueEntry']/ResubmissionParams/@URL
	 * @param queueEntryId
	 *            JMF/Command[@Type='ResubmitQueueEntry']/ResubmissionParams/@QueueEntryID
	 * @return a JMF message containing a ResubmitQueueEntry command
	 */
	public static OutgoingJmfMessage buildResubmitQueueEntry(String jdfUrl, String queueEntryId) {
		JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("CommandResubmitQueueEntry");
		jmf.getCommand(0).getResubmissionParams(0).setURL(jdfUrl);
		jmf.getCommand(0).getResubmissionParams(0).setQueueEntryID(queueEntryId);
		return createMessage(jmf);
	}

	/**
	 * Builds a JMF message containing an AbortQueueEntry command.
	 * 
	 * @param queueEntryId
	 *            JMF/Command[@Type='AbortQueueEntry']/QueueEntryDef/@QueueEntryID
	 * @return a JMF message containing an AbortQueueEntry command
	 */
	public static OutgoingJmfMessage buildAbortQueueEntry(String queueEntryId) {
		JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("CommandAbortQueueEntry");
		jmf.getCommand(0).getQueueEntryDef(0).setQueueEntryID(queueEntryId);
		return createMessage(jmf);
	}

	/**
	 * Builds a JMF message containing a StopPersistentChannel command. If the parameters <code>queueEntryId</code> 
	 * or <code>jobId</code> are specified a StopPersistentChannel command is created for stopping subscriptions 
	 * for the specified job.  
	 * 
	 * @param jmfUrl 
	 * 			the JMF URL owning the subscription (/JMF/Command/StopPersChParams/@URL)
	 * @param queueEntryId
	 * 			the QueueEntryID of the job to stop the subscription for; <code>null</code> to stop all subscriptiosn to the JMF URL 
	 * @param jobId	
	 * 			the JobID of the job to stop the subscription for; null to stop all subscriptions to the JMF URL
	 * @return
	 */
	public static OutgoingJmfMessage buildStopPersistentChannel(String jmfUrl, String queueEntryId, String jobId) {
		JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("CommandStopPersistentChannel");
		JDFStopPersChParams params = jmf.getCommand(0).getStopPersChParams(0);
		if (queueEntryId != null) {
			params.setQueueEntryID(queueEntryId);
		}
		if (jobId != null) {
			params.setJobID(jobId);
		}
		params.setURL(jmfUrl);
		return createMessage(jmf);
	}
	
	public static OutgoingJmfMessage buildStatus(String queueEntryId, String jobId) {
		JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("QueryStatus");
		JDFStatusQuParams params = jmf.getQuery(0).getCreateStatusQuParams(0);
		if (queueEntryId != null) {
			params.setQueueEntryID(queueEntryId);
		}
		if (jobId != null) {
			params.setJobID(jobId);
		}
		params.setJobDetails(EnumJobDetails.Full);
		params.setQueueInfo(false);
		params.setDeviceDetails(EnumDeviceDetails.None);
		return createMessage(jmf);
	}

	public static OutgoingJmfMessage buildQueryResource(String jobId, String queueEntryId) {
		JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("QueryResource");
		JDFResourceQuParams params = jmf.getQuery(0).getResourceQuParams(0);
		if (jobId != null) {
			params.setJobID(jobId);
		}
		if (queueEntryId != null) {
			params.setQueueEntryID(queueEntryId);
		}
		return createMessage(jmf);
	}

	public static OutgoingJmfMessage buildHoldQueueEntry(String queueEntryId) {
		JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("CommandHoldQueueEntry");
		jmf.getCommand(0).getQueueEntryDef(0).setQueueEntryID(queueEntryId);
		return createMessage(jmf);
	}

	public static OutgoingJmfMessage buildResumeQueueEntry(String queueEntryId) {
		JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("CommandResumeQueueEntry");
		jmf.getCommand(0).getQueueEntryDef(0).setQueueEntryID(queueEntryId);
		return createMessage(jmf);
	}

	public static OutgoingJmfMessage buildSuspendQueueEntry(String queueEntryId) {
		JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("CommandSuspendQueueEntry");
		jmf.getCommand(0).getQueueEntryDef(0).setQueueEntryID(queueEntryId);
		return createMessage(jmf);
	}

	public static OutgoingJmfMessage buildSetQueueEntryPriority(String queueEntryId, int priority) {
		JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("CommandSetQueueEntryPriority");
		jmf.getCommand(0).getQueueEntryPriParams(0).setQueueEntryID(queueEntryId);
		jmf.getCommand(0).getQueueEntryPriParams(0).setPriority(priority);
		return createMessage(jmf);
	}

	public static OutgoingJmfMessage buildSetQueueEntryPostion(String queueEntryId, int position,
			String previousQueueEntryId, String nextQueueEntryId) {
		JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("CommandSetQueueEntryPosition");
		jmf.getCommand(0).getQueueEntryPosParams(0).setQueueEntryID(queueEntryId);
		if (position != -1) {
			jmf.getCommand(0).getQueueEntryPosParams(0).setPosition(position);
		} else if (nextQueueEntryId != null) {
			jmf.getCommand(0).getQueueEntryPosParams(0).setNextQueueEntryID(nextQueueEntryId);
		} else if (previousQueueEntryId != null) {
			jmf.getCommand(0).getQueueEntryPosParams(0).setPrevQueueEntryID(
					previousQueueEntryId);
		}
		return createMessage(jmf);
	}

	/**
	 * Builds a JMF message containing an RemoveQueueEntry command.
	 * 
	 * @param queueEntryId
	 *            JMF/Command[@Type='RemoveQueueEntry']/QueueEntryDef/@QueueEntryID
	 * @return a JMF message containing an RemoveQueueEntry command
	 */
	public static OutgoingJmfMessage buildRemoveQueueEntry(String queueEntryId) {
		JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("CommandRemoveQueueEntry");
		jmf.getCommand(0).getQueueEntryDef(0).setQueueEntryID(queueEntryId);
		return createMessage(jmf);
	}
	
	/**
	 * Creates a JMF response skeleton for the specified incoming message. The
	 * JMF contains one Response element for each message contained in the
	 * incoming JMF. Each response element contains a Notification with a
	 * Comment.
	 * 
	 * @param jmfIn
	 * @return a JMF response
	 */
	public static JDFJMF buildResponse(JDFJMF jmfIn) {
		JDFJMF jmf = JMFMessageFactory.getInstance().createJMF();
		for (Iterator i = jmfIn.getMessageVector(null, null).iterator(); i.hasNext();) {
			JDFMessage msg = (JDFMessage) i.next();
			JDFResponse resp = jmf.appendResponse();
			resp.setType(msg.getType());
			resp.setrefID(msg.getID());
			JDFNotification not = resp.appendNotification();
			not.setClass(JDFNotification.EnumClass.Information);
			JDFComment comment = not.appendComment();
			comment.setText("Alces has received and processed your message.");
		}
		jmf.setSenderID("ALCES");
		return jmf;
	}

	/**
	 * Creates a JMF response with return code 5, Query/command not implemented,
	 * for the specified incoming message. The JMF contains one Response
	 * element for each message contained in the incoming JMF. Each response
	 * element contains a Notification with a Comment.
	 * 
	 * @param jmfIn
	 * @return a JMF response
	 */
	public static JDFJMF buildNotImplementedResponse(JDFJMF jmfIn) {
		JDFJMF jmf = JMFMessageFactory.getInstance().createJMF();
		for (Iterator i = jmfIn.getMessageVector(null, null).iterator(); i.hasNext();) {
			JDFMessage msg = (JDFMessage) i.next();
			JDFResponse resp = jmf.appendResponse();
			resp.setType(msg.getType());
			resp.setrefID(msg.getID());
			resp.setReturnCode(JDFJMF.EnumJMFReturnCode.MESSAGE_NOT_IMPLEMENTED.getValue());
			JDFNotification not = resp.appendNotification();
			not.setClass(JDFNotification.EnumClass.Information);
			JDFComment comment = not.appendComment();
			comment.setText("Alces has received and logged your messages but does not know how to process the message.");
		}
		jmf.setSenderID("ALCES");
		return jmf;
	}

	/**
	 * Returns an instance of a <code>OutMessage</code> generated from the
	 * specified message JMF message.
	 * 
	 *            the name of the message's template
	 * @return a message generated from the specified template
	 */
	public static OutgoingJmfMessage createMessage(JDFJMF jmf) {
		String jmfBody = jmf.getOwnerDocument_KElement().write2String(2);
		return new OutgoingJmfMessage(jmfBody);
	}
}
