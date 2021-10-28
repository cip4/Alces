/**
 * The CIP4 Software License, Version 1.0
 *
 * Copyright (c) 2001-2009 The International Cooperation for the Integration of 
 * Processes in  Prepress, Press and Postpress (CIP4).  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        The International Cooperation for the Integration of 
 *        Processes in  Prepress, Press and Postpress (www.cip4.org)"
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "CIP4" and "The International Cooperation for the Integration of 
 *    Processes in  Prepress, Press and Postpress" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact info@cip4.org.
 *
 * 5. Products derived from this software may not be called "CIP4",
 *    nor may "CIP4" appear in their name, without prior written
 *    permission of the CIP4 organization
 *
 * Usage of this software in commercial products is subject to restrictions. For
 * details please consult info@cip4.org.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE INTERNATIONAL COOPERATION FOR
 * THE INTEGRATION OF PROCESSES IN PREPRESS, PRESS AND POSTPRESS OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the The International Cooperation for the Integration 
 * of Processes in Prepress, Press and Postpress and was
 * originally based on software 
 * copyright (c) 1999-2001, Heidelberger Druckmaschinen AG 
 * copyright (c) 1999-2001, Agfa-Gevaert N.V. 
 *  
 * For more information on The International Cooperation for the 
 * Integration of Processes in  Prepress, Press and Postpress , please see
 * <http://www.cip4.org/>.
 *  
 * 
 */
package org.cip4.tools.alces.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.cip4.tools.alces.test.TestSuiteSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages all Alces paths.
 * @author smeissner
 * @date 07.04.2013
 */
public class AlcesPathUtil {

	private static Logger log = LoggerFactory.getLogger(AlcesPathUtil.class);

	public final static String ALCES_ROOT_DIR = initAlcesPath();

	public final static String ALCES_CONFIG_DIR = initAlcesConfigPath();

	public final static String ALCES_LOG_DIR = initAlcesLogPath();

	public final static String ALCES_OUTPUT_DIR = initAlcesOutputPath();

	public final static String ALCES_BIN_DIR = initAlcesBinPath();

	public final static String ALCES_TEST_DATA_DIR = initAlcesTestDataPath();

	public final static String ALCES_USER_FILES_DIR = initAlcesUserFilesPath();

	private final static String RES_TESTDATA_ROOT = "/org/cip4/tools/alces/testdata/";

	private final static String RES_BIN_ROOT = "/org/cip4/tools/alces/bin/";

	/**
	 * Creates the Alces User Root Path.
	 * @return Alces User Root directory as String.
	 */
	private static String initAlcesPath() {

		// set alces user directory
		String pathDir = FilenameUtils.concat(FileUtils.getUserDirectoryPath(), "CIP4Tools");
		pathDir = FilenameUtils.concat(pathDir, "Alces");
		new File(pathDir).mkdirs();

		// return path
		return pathDir;
	}

	/**
	 * Creates the config directory.
	 * @return Alces Config directory as String.
	 */
	private static String initAlcesConfigPath() {

		// set alces user directory
		String pathDir = FilenameUtils.concat(ALCES_ROOT_DIR, "Config");
		new File(pathDir).mkdirs();

		// return path
		return pathDir;
	}

	/**
	 * Creates the user files directory.
	 * @return Alces Config directory as String.
	 */
	private static String initAlcesUserFilesPath() {

		// set alces user directory
		String pathDir = FilenameUtils.concat(ALCES_ROOT_DIR, "UserFiles");
		new File(pathDir).mkdirs();

		// return path
		return pathDir;
	}

	/**
	 * Creates the user files directory.
	 * @return Alces Config directory as String.
	 */
	private static String initAlcesBinPath() {

		// set alces user directory
		String pathDir = FilenameUtils.concat(ALCES_ROOT_DIR, "Bin");
		boolean isCreated = new File(pathDir).mkdirs();

		if (isCreated) {
			// create list of all report resources
			List<String> res = new ArrayList<String>(25);
			res.add("alces.js");

			try {
				// copy to output directory
				for (String path : res) {
					String target = FilenameUtils.concat(pathDir, path);

					new File(FilenameUtils.getFullPath(target)).mkdirs();
					FileOutputStream fos = new FileOutputStream(target);
					InputStream is = TestSuiteSerializer.class.getResourceAsStream(RES_BIN_ROOT + path);
					IOUtils.copy(is, fos);
					fos.close();
					is.close();
				}
			} catch (Exception ex) {
				log.error("An error has occured during copying bin data.", ex);
			}

		}

		// return path
		return pathDir;
	}

	/**
	 * Creates the log directory.
	 * @return Alces log directory as String.
	 */
	private static String initAlcesTestDataPath() {

		// set alces user directory
		String pathDir = FilenameUtils.concat(ALCES_ROOT_DIR, "TestData");
		boolean isCreated = new File(pathDir).mkdirs();

		if (isCreated) {
			// create list of all report resources
			List<String> res = new ArrayList<String>(25);
			res.add("01.KnownDevices.jmf");
			res.add("02.KnownMessages.jmf");
			res.add("03.KnownDevices-SubscriptionTime.jmf");
			res.add("04.Status-Subscription.jmf");
			res.add("05.QueueStatus-Subscription.jmf");
			res.add("06.CloseQueue.jmf");
			res.add("07.OpenQueue.jmf");
			res.add("08.Status.jmf");
			res.add("09.SubmitQueueEntry-Approval.jmf");
			res.add("09.SubmitQueueEntry-ConventionalPrinting.jmf");
			res.add("10.QueueStatus.jmf");
			res.add("11.SubmitQueueEntry-AcknowledgeURL-Approval.jmf");
			res.add("11.SubmitQueueEntry-AcknowledgeURL-ConventionalPrinting.jmf");
			res.add("12.StopPersistentChannel.jmf");

			res.add("schema/JDF.xsd");
			res.add("schema/JDFCapability.xsd");
			res.add("schema/JDFCore.xsd");
			res.add("schema/JDFCoreStrict.xsd");
			res.add("schema/JDFMessage.xsd");
			res.add("schema/JDFProcess.xsd");
			res.add("schema/JDFResource.xsd");
			res.add("schema/JDFResourceStrict.xsd");
			res.add("schema/JDFSchema13.spp");
			res.add("schema/JDFStrict.xsd");
			res.add("schema/JDFTypes.xsd");
			res.add("schema/ReleaseNotes.txt");

			res.add("mime/elk-approval.mjm");

			res.add("jdf/Elk_Approval.jdf");
			res.add("jdf/Elk_ConventionalPrinting.jdf");

			res.add("elk-testdata/001KnownMessagesAll.jmf");
			res.add("elk-testdata/002KnownMessagesNoCommands.jmf");
			res.add("elk-testdata/003KnownMessagesNoQueries.jmf");
			res.add("elk-testdata/004KnownMessagesOnlySignals.jmf");
			res.add("elk-testdata/01.KnownDevices.jmf");
			res.add("elk-testdata/011KnownDevicesBrief.jmf");
			res.add("elk-testdata/012KnownDevicesDetails.jmf");
			res.add("elk-testdata/02.KnownMessages.jmf");
			res.add("elk-testdata/021SubmitQueueEntry.jmf");
			res.add("elk-testdata/022SubmitQueueEntryFilterDetailsNone.jmf");
			res.add("elk-testdata/023SubmitQueueEntryFilterDetailsJobPhase.jmf");
			res.add("elk-testdata/03.KnownDevices-SubscriptionTime.jmf");
			res.add("elk-testdata/03.QueueStatus-SubscriptionTime.jmf");
			res.add("elk-testdata/04.Status-Subscription.jmf");
			res.add("elk-testdata/04.Status-SubscriptionTime.jmf");
			res.add("elk-testdata/05.CloseQueue.jmf");
			res.add("elk-testdata/05.QueueStatus-Subscription.jmf");
			res.add("elk-testdata/06.CloseQueue.jmf");
			res.add("elk-testdata/06.SubmitQueueEntry.jmf");
			res.add("elk-testdata/07.OpenQueue.jmf");
			res.add("elk-testdata/08.Status.jmf");
			res.add("elk-testdata/09.QueueStatus-Subscription.jmf");
			res.add("elk-testdata/09.SubmitQueueEntry.jmf");
			res.add("elk-testdata/09.SubmitQueueEntry-AcknowledgeURL.jmf");
			res.add("elk-testdata/10.QueueStatus.jmf");
			res.add("elk-testdata/10.SubmitQueueEntry-AcknowledgeURL.jmf");
			res.add("elk-testdata/11.QueueStatus.jmf");
			res.add("elk-testdata/11.SubmitQueueEntry-AcknowledgeURL.jmf");
			res.add("elk-testdata/12.StopPersistentChannel.jmf");
			res.add("elk-testdata/KnownDevicesDetailsSubscriptionTime.jmf");
			res.add("elk-testdata/KnownDevicesSubscriptionTime.jmf");
			res.add("elk-testdata/QueueStatusSubscription.jmf");
			res.add("elk-testdata/sendKDBrief.jmf");
			res.add("elk-testdata/sendKDCaps.jmf");
			res.add("elk-testdata/sendKDDef.jmf");
			res.add("elk-testdata/sendKDNone.jmf");
			res.add("elk-testdata/sendKMOnlyPersistent.jmf");
			res.add("elk-testdata/Status.jmf");
			res.add("elk-testdata/SubmitQueueEntryAck.jmf");
			res.add("elk-testdata/u10.StopPersistentChannel.jmf");
			res.add("elk-testdata/unsubscribeURL.jmf");
			res.add("elk-testdata/uQSevent.jmf");

			res.add("elk-testdata/jdf/Approval.jdf");

			try {
				// copy to output directory
				for (String path : res) {
					String target = FilenameUtils.concat(pathDir, path);

					new File(FilenameUtils.getFullPath(target)).mkdirs();
					FileOutputStream fos = new FileOutputStream(target);
					InputStream is = TestSuiteSerializer.class.getResourceAsStream(RES_TESTDATA_ROOT + path);
					IOUtils.copy(is, fos);
					fos.close();
					is.close();
				}
			} catch (Exception ex) {
				log.error("An error has occured during copying testdata.", ex);
			}
		}

		// return path
		return pathDir;
	}

	/**
	 * Creates the log directory.
	 * @return Alces log directory as String.
	 */
	private static String initAlcesLogPath() {

		// set alces user directory
		String pathDir = FilenameUtils.concat(ALCES_ROOT_DIR, "Logs");
		pathDir = FilenameUtils.separatorsToUnix(pathDir);
		new File(pathDir).mkdirs();

		// return path
		return pathDir;
	}

	/**
	 * Creates the output directory.
	 * @return Alces output directory as String.
	 */
	private static String initAlcesOutputPath() {

		// set alces user directory
		String pathDir = FilenameUtils.concat(ALCES_ROOT_DIR, "Output");
		new File(pathDir).mkdirs();

		// return path
		return pathDir;
	}
}
