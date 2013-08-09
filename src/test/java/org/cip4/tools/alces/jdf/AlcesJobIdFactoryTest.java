/*
 * Created on Apr 10, 2007
 */
package org.cip4.tools.alces.jdf;

import org.cip4.tools.alces.jdf.AlcesJobIDFactory;

import junit.framework.TestCase;

public class AlcesJobIdFactoryTest extends TestCase {

	public void testNewJobID() {
		AlcesJobIDFactory factory = new AlcesJobIDFactory();
		String jobId = factory.newJobID();
		assertNotNull(jobId);
	}

	public void testNewJobIDString() {
		String oldJobId = "This is a long string that is longer than 63 characters. Yes, it is longer than 63 characters.";
		assertTrue(oldJobId.length() > 63);
		AlcesJobIDFactory factory = new AlcesJobIDFactory();
		String jobId = factory.newJobID(oldJobId);
		assertNotNull(jobId);
		assertEquals(63, jobId.length());
	}
}
