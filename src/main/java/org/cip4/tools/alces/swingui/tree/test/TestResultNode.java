/*
 * Created on May 4, 2005
 */
package org.cip4.tools.alces.swingui.tree.test;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.cip4.tools.alces.model.AbstractJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.TestResult;
import org.cip4.tools.alces.service.testrunner.tests.Test;

/**
 * A <code>DefaultMutableTreeNode</code> implementation of a
 * <code>TestResult</code>.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class TestResultNode extends DefaultMutableTreeNode {

    private final TestResult testResult;
    private final DefaultTreeModel treeModel;

    /** 
     * Creates an TestResult that is a TreeNode and wraps a non-Swing TestResult
     * implementation. The wrapped TestResult contains the logic, this class is simply
     * a wrapper allowing the TestResult to be displayed in a JTree. 
     * @param treeModel the tree model this node belongs to
     */
    public TestResultNode(Test test, AbstractJmfMessage message, TestResult.Result result,
                          String resultString, DefaultTreeModel treeModel) {
        this(new TestResult(test, message, result, resultString), treeModel);

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
        this.testResult = testResult;
        setUserObject(testResult.getTest().getDescription()); //XXX invokeLater
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.cip4.tools.alces.service.testrunner.model.TestResult#getLogMessage()
     */
    public String getResultString() {
        return testResult.getResultString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.cip4.tools.alces.service.testrunner.model.TestResult#getMessage()
     */
    public AbstractJmfMessage getMessage() {
        return testResult.getMessage();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.cip4.tools.alces.service.testrunner.model.TestResult#isPassed()
     */
    public boolean isPassed() {
        return testResult.isPassed();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.cip4.tools.alces.service.testrunner.model.TestResult#getTest()
     */
    public Test getTest() {
        return testResult.getTest();
    }
    
    public TestResult getWrappedTestResult() {
    	return testResult;
    }

	public TestResult.Result getResult() {
		return testResult.getResult();
	}

	public boolean isIgnored() {
		return testResult.isIgnored();
	}
}
