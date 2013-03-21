/*
 * Created on Apr 23, 2005
 */
package org.cip4.elk.alces.test;

import org.cip4.elk.alces.message.Message;
import org.cip4.elk.alces.test.tests.Test;


/**
 * @author Claes Buckwalter
 */
public class TestResultImpl implements TestResult {

    private final String resultString;
    private final Result result;
    private final Message message;
    private final Test test;
    
    public TestResultImpl(Test test, Message message, Result result, String resultString) {
        this.test = test;
        this.message = message;
        this.result= result;
        this.resultString = resultString;
    }
 
    public String getResultString() {
        return resultString;
    }

    public Message getMessage() {
        return message;
    }

    public boolean isPassed() {
        return (result == Result.PASSED);
    }

    public Test getTest() {
        return test;
    }
    
    @Override
    public String toString() {
        return "TestResult[ result=" + result + "; test=" + test + "; log=" + 
            resultString + "; tested message=" + message + " ]";
    }

	public boolean isIgnored() {
		return (result == Result.IGNORED);
	}

	public Result getResult() {
		return result;
	}
}
