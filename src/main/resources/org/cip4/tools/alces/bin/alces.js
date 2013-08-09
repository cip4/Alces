/**
 * Available objects in context:
 *
 *  testRunner  - Alces TestRunner (org.cip4.elk.alces.test.TestRunner)
 *  jmfUrl      - the JMF URL to send messages to (passed from command-line)
 *  testdata    - the path to the test file or directory containing the test files to send (passed from command-line)
 *  config      - test configuration
 *  log         - logger (org.apache.commons.logging.Log)
 */
log.info("Testdata: " + testdata.getCanonicalPath());
log.info("JMF URL: " + jmfUrl);
testRunner.runTests(jmfUrl, testdata);