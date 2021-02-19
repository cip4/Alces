/*
 * Created on Apr 10, 2007
 */
package org.cip4.tools.alces.jdf;

import org.junit.Assert;
import org.junit.Test;

public class AlcesJobIdFactoryTest {

    @Test
    public void testNewJobID() {
        AlcesJobIDFactory factory = new AlcesJobIDFactory();
        String jobId = factory.newJobID();
        Assert.assertNotNull(jobId);
    }

    @Test
    public void testNewJobIDString() {
        String oldJobId = "This is a long string that is longer than 63 characters. Yes, it is longer than 63 characters.";
        Assert.assertTrue(oldJobId.length() > 63);
        AlcesJobIDFactory factory = new AlcesJobIDFactory();
        String jobId = factory.newJobID(oldJobId);
        Assert.assertNotNull(jobId);
        Assert.assertEquals(63, jobId.length());
    }
}
