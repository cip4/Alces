/*
 * Created on Apr 10, 2007
 */
package org.cip4.tools.alces.jdf;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AlcesJobIdFactoryTest {

    @Test
    public void testNewJobID() {
        AlcesJobIDFactory factory = new AlcesJobIDFactory();
        String jobId = factory.newJobID();
        Assertions.assertNotNull(jobId);
    }

    @Test
    public void testNewJobIDString() {
        String oldJobId = "This is a long string that is longer than 63 characters. Yes, it is longer than 63 characters.";
        Assertions.assertTrue(oldJobId.length() > 63);
        AlcesJobIDFactory factory = new AlcesJobIDFactory();
        String jobId = factory.newJobID(oldJobId);
        Assertions.assertNotNull(jobId);
        Assertions.assertEquals(63, jobId.length());
    }
}
