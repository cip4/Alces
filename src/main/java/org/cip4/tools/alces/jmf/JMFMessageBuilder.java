/*
 * Created on Feb 28, 2007
 */
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
import org.cip4.tools.alces.message.OutMessage;
import org.cip4.tools.alces.message.OutMessageImpl;
import org.cip4.tools.alces.util.ConfigurationHandler;

public class JMFMessageBuilder {

	private static final String JMF_MIMETYPE = "application/vnd.cip4-jmf+xml";

	private static final String JDF_MIMETYPE = "application/vnd.cip4-jdf+xml";

	// public static OutMessage buildSubmitQueueEntryMime(String jdfUrl) {
	// OutMessage outMessage = buildSubmitQueueEntry(jdfUrl);
	//        
	// return null;
	// }

	/**
	 * Builds a JMF message containing a SubmitQueueEntry command that refers to
	 * the specified JDF URL.
	 * 
	 * @param jdfUrl
	 *            JMF/Command[@Type='SubmitQueueEntry']/QueueSubmissionParams/@URL
	 * @return a JMF message containing a SubmitQueueEntry command
	 */
	public static OutMessage buildSubmitQueueEntry(String jdfUrl) {
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
	public static OutMessage buildResubmitQueueEntry(String jdfUrl, String queueEntryId) {
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
	public static OutMessage buildAbortQueueEntry(String queueEntryId) {
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
	public static OutMessage buildStopPersistentChannel(String jmfUrl, String queueEntryId, String jobId) {
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
	
	public static OutMessage buildStatus(String queueEntryId, String jobId) {
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

	public static OutMessage buildQueryResource(String jobId, String queueEntryId) {
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

	public static OutMessage buildHoldQueueEntry(String queueEntryId) {
		JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("CommandHoldQueueEntry");
		jmf.getCommand(0).getQueueEntryDef(0).setQueueEntryID(queueEntryId);
		return createMessage(jmf);
	}

	public static OutMessage buildResumeQueueEntry(String queueEntryId) {
		JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("CommandResumeQueueEntry");
		jmf.getCommand(0).getQueueEntryDef(0).setQueueEntryID(queueEntryId);
		return createMessage(jmf);
	}

	public static OutMessage buildSuspendQueueEntry(String queueEntryId) {
		JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("CommandSuspendQueueEntry");
		jmf.getCommand(0).getQueueEntryDef(0).setQueueEntryID(queueEntryId);
		return createMessage(jmf);
	}

	public static OutMessage buildSetQueueEntryPriority(String queueEntryId, int priority) {
		JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("CommandSetQueueEntryPriority");
		jmf.getCommand(0).getQueueEntryPriParams(0).setQueueEntryID(queueEntryId);
		jmf.getCommand(0).getQueueEntryPriParams(0).setPriority(priority);
		return createMessage(jmf);
	}

	public static OutMessage buildSetQueueEntryPostion(String queueEntryId, int position,
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
	public static OutMessage buildRemoveQueueEntry(String queueEntryId) {
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
		jmf.setSenderID(ConfigurationHandler.getInstance().getProp(
				ConfigurationHandler.SENDER_ID));
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
		jmf.setSenderID(ConfigurationHandler.getInstance().getProp(
				ConfigurationHandler.SENDER_ID));
		return jmf;
	}

	/**
	 * Returns an instance of a <code>OutMessage</code> generated from the
	 * specified message JMF message.
	 * 
	 * @param messageTemplate
	 *            the name of the message's template
	 * @return a message generated from the specified template
	 */
	public static OutMessage createMessage(JDFJMF jmf) {
		String header = "Content-Type: " + JMF_MIMETYPE;
		String body = jmf.getOwnerDocument_KElement().write2String(2);
		return new OutMessageImpl(header, body, true);
	}

	// /**
	// * Builds a MIME package.
	// * @param jdfResource the JMF or JDF to be stored as the first part
	// * of the package
	// * @param fileResources the other parts
	// * @return a string containing the resulting MIME package
	// * @throws Exception
	// */
	// private static String buildMimePackage(String jmfSrc, String jdfSrc,
	// boolean includeContent)
	// throws Exception {
	// // Find referenced files in JDF
	// JDFNode jdf = new JDFParser().parseString(jdfSrc).getJDFRoot();
	// List fileSpecs = jdf.getChildElementVector(ElementName.FILESPEC, null,
	// null, false, 0, false);
	// for (Iterator it = fileSpecs.iterator(); it.hasNext(); ) {
	// JDFFileSpec fileSpec = (JDFFileSpec) it.next();
	// if (fileSpec.getContainer(0) != null || fileSpec.getParentNode()
	// instanceof JDFContainer) {
	// continue; // We do not yet support FileSpec containers
	// }
	// String mimeType = fileSpec.getMimeType();
	// String fileUrl = fileSpec.getURL();
	// URI fileUri = new URI(fileUrl);
	// if (!fileUri.isAbsolute()) {
	//                
	// }
	// //TODO Copy file into MIME
	//        
	// }
	// // Create a MIME package
	// Properties dummyProps = new Properties(); // Usually contains server,
	// etc.
	// Session mailSession = Session.getDefaultInstance(dummyProps);
	// Message message = new MimeMessage(mailSession);
	// Multipart multipart = new MimeMultipart("related"); // JDF:
	// multipart/related
	// // Part 1 is JMF
	// BodyPart messageBodyPart = new MimeBodyPart();
	// messageBodyPart.setContent(jmfSrc, "text/xml");
	// messageBodyPart.setHeader("Content-Type", JMF_MIMETYPE);
	// multipart.addBodyPart(messageBodyPart);
	// // Part 2 is JDF
	// messageBodyPart = new MimeBodyPart();
	// messageBodyPart.setContent(jdfSrc, "text/xml");
	// messageBodyPart.setHeader("Content-Type", JDF_MIMETYPE);
	// messageBodyPart.setHeader("Content-ID", "<JDF001>"); // JDF: ID within <
	// >; case insensitive; escape with %hh
	// multipart.addBodyPart(messageBodyPart);
	//        
	//        
	//        
	// // TODO Get content referenced by JDF
	// // for(int i=0, imax=fileResources.size(); i<imax; i++) {
	// // String fileResource = (String) fileResources.get(i);
	// // String fileName = new File(fileResource).getName();
	// // // Part 2 is JDF
	// // messageBodyPart = new MimeBodyPart();
	// // DataSource source = new URLDataSource(getResourceAsURL(fileResource));
	// // messageBodyPart.setDataHandler(new DataHandler(source));
	// // messageBodyPart.setFileName(fileName);
	// // //messageBodyPart.setHeader("Content-Type",
	// JMFServlet.JDF_CONTENT_TYPE); // JDF: application/vnd.cip4-jdf+xml
	// // messageBodyPart.setHeader("Content-ID", "<" + fileName + ">"); // JDF:
	// ID within < >; case insensitive; escape with %hh
	// // multipart.addBodyPart(messageBodyPart);
	// }
	// // Put parts in message
	// message.setContent(multipart);
	// // Writes message to string
	// OutputStream outStream = new ByteArrayOutputStream();
	// message.writeTo(outStream);
	// String msgString = outStream.toString();
	// return msgString;
	// }

}
