/*
 * Created on May 4, 2005
 */
package org.cip4.tools.alces.swingui.tree.test;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.test.TestResult;
import org.cip4.tools.alces.test.TestResultImpl;
import org.cip4.tools.alces.test.tests.Test;

/**
 * A <code>DefaultMutableTreeNode</code> implementation of a
 * <code>TestResult</code>.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class TestResultNode extends DefaultMutableTreeNode implements
        TestResult {

    private final TestResult wrappedTestResult;
    private final DefaultTreeModel treeModel;

    /** 
     * Creates an TestResult that is a TreeNode and wraps a non-Swing TestResult
     * implementation. The wrapped TestResult contains the logic, this class is simply
     * a wrapper allowing the TestResult to be displayed in a JTree. 
     * @param treeModel the tree model this node belongs to
     */
    public TestResultNode(Test test, Message message, Result result,
            String resultString, DefaultTreeModel treeModel) {
        this(new TestResultImpl(test, message, result, resultString), treeModel);

    }
    
    /** 
     * Creates an TestResult that is a TreeNode and wraps a non-Swing TestResult
     * implementation. The wrapped TestResult contains the logic, this class is simply
     * a wrapper allowing the TestResult to be displayed in a JTree. 
     * @param testResult   The TestResult to wrap. This object may not be an instance of TreeNode.
     * @param treeModel the tree model this node belongs to
     */
    public TestResultNode(TestResult testResult, DefaultTreeModel treeModel) {
        super("TestResult");
        if (testResult instanceof TreeNode) {
            throw new IllegalArgumentException(
                    "The testResult may not be TreeNode.");
        }
        this.treeModel = treeModel;
        this.wrappedTestResult = testResult;
        setUserObject(testResult.getTest().getDescription()); //XXX invokeLater
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.cip4.tools.alces.test.TestResult#getLogMessage()
     */
    public String getResultString() {
        return wrappedTestResult.getResultString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.cip4.tools.alces.test.TestResult#getMessage()
     */
    public Message getMessage() {
        return wrappedTestResult.getMessage();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.cip4.tools.alces.test.TestResult#isPassed()
     */
    public boolean isPassed() {
        return wrappedTestResult.isPassed();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.cip4.tools.alces.test.TestResult#getTest()
     */
    public Test getTest() {
        return wrappedTestResult.getTest();
    }
    
    public TestResult getWrappedTestResult() {
    	return wrappedTestResult;    	
    }

	public Result getResult() {
		return wrappedTestResult.getResult();
	}

	public boolean isIgnored() {
		return wrappedTestResult.isIgnored();
	}
}
