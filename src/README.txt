ALCES @major.version@

Alces is a device/worker test tool, inspired by Heidelberg Kiel's JMFTransmitter.

= DOCUMENTATION =

- FAQ: http://www.cip4.org/publicwiki/AlcesFAQ
- Homepage: http://www.cip4.org/open_source/alces/
- Issue tracking: http://www.cip4.org/jira/browse/ALCES 


= QUICK START GUIDE =
First, make sure you have J2SE 5 SDK (http://java.sun.com) or later installed and that the environment variable JAVA_HOME is set correctly.

== INTERACTIVE TESTING (GUI) ==
1. Start Alces
     Windows:  Double-click alces.bat
     Mac OS X: Double-click Alces, or run alces.sh from the Terminal
2. Enter a device URL and click the Connect button
3. Buttons representing all messages the device support are displayed. Click a button to send a message.
4. Click 'Send File...' to send a file containing a JMF message, for example one of the files in the 'testdata' directory.

== AUTOMATED TESTING ==
1. Open a command-line prompt and go to the 'bin' directory
2. Start Alces
     Windows:  Run auto-alces.bat to display the command-line options.
     Mac OS X: Run auto-alces.sh to display the command-line options.
3. When Alces has finished running open the test report file in an XSLT-capable web browser such as Firefox, Safari, or Explorer.


= CHANGELOG =

IMPORTANT CHANGES 0.9.9.5 (2008-12-04)
- Interactive Alces
  - Added JDF preprocessor that replaces e-mail addresses in ComChannels. Disabled by default. [ALCES-188]
  - Context menu of "Send File" button now lists files in last used directory [ALCES-185][ALCES-66]
  - Context menu of "SubmitQueueEntry" button now lists JDF files in last used directory [ALCES-185][ALCES-66]
  - Improved Preferences dialog
- Added TestSessionListener interface so that API users can listen for incoming JMF messages. Useful when embedding Alces in unit tests. [ALCES-124]
- Added VariablesPreprocessor that replaces variable names with values in JDF files and JMF messages [ALCES-186]
- Upgraded to JDFLibJ 2.1.3.50

CHANGES CONTRIBUTED BY ALEX KHILOV:
- Interactive Alces
  - Added buttons to clear all or selected messages in message tree [ALCES-52]
  - An error is shown if a HTTP 404 is returned when connecting to the device/controller [ALCES-196]
  - Right-click to select option to collapse a node in the message tree [ALCES-194] 
  - Right-click a message in the message tree to save it to a pre-configured directory [ALCES-165]
  - Automatically expand message tree when new message is received [ALCES-195]
  - @WatchURL in JMF is now preprocessed [ALCES-197] 
  - Option in Preferences dialog to show the JMF messages sent/received during Connect [ALCES-198]
  - Option in Preferences dialog to treat incoming message without Content-Type header as JMF [ALCES-200]
  - Option in Preferences dialog to configured hostname/IP address used in JMF [ALCES-199]
  - Hitting enter in address bar connects [ALCES-191]
  - Right-click in message pane to select option to wrap lines [ALCES-192]
  - Added batch mode for sending JMF
- The timeout for outgoing HTTP connections is now set to infinity [ALCES-193]


IMPORTANT CHANGES 0.9.9.2 (2007-11-26)
- Added support for NTLM proxy authentication. To specify that NTLM authentication should be used the proxy.user property must be on the form: domain\\username
- Added rendering of the results from Agfa Graphics's ICS validation service
- Fixed problem that caused validation of MIME packages to incorrectly fail


IMPORTANT CHANGES 0.9.9.1 (2007-10-08)
- Added missing crimson.jar to classpath


IMPORTANT CHANGES 0.9.9 (2007-10-06)
- Interactive Alces:
  - JDF files can be submitted in MIME packages by holding down ALT while clicking the SubmitQueueEntry/ResubmitQueueEntry button [ALCES-13]
  - Preprocessing of JDF file can be disabled by holding down SHIFT while clicking the SubmitQueueEntry/ResubmitQueueEntry button [ALCES-147] 
  - CheckJDF test result is now rendered as XHTML [ALCES-130]
  - Added "Test ignored" icon [ALCES-159]
  - Checkbox for enabling/disabling proxy settings [ALCES-162]
- Automated Alces:
  - Single file can now be submitted from command-line, not just directory of files [ALCES-164]
- Started porting Alces to Java 5.0 [ALCES-160]
- Temp files are now deleted when Alces is shutdown [ALCES-166]
- MIME package contents are validated [ALCES-93]
- Added test PreviewTest that verifies that Preview files referenced by a JMF exist and downloads them [ALCES-168]
- Upgraded to JDFLibJ 2.1.3.47
- Upgraded to latest JDF Schema from SVN trunk
- Changed folder name from "elk-alces" to "alces"


IMPORTANT CHANGES 0.9.8 (2007-07-10)
- Interactive Alces:
  - Test results are now displayed at top level in the test session tree
  - If the ALT key is pressed when clicking the SubmitQueueEntry button the JDF file will not be preprocessed
  - Selecting a queue entry in the queue view and then clicking the StopPersistentChannel command attempts to stop the queue entry's subscriptions
- CheckJDF command-line arguments for CheckJDFTest, SubmitQueueEntryTest, and ReturnQueueEntryTest are now configurable
- CheckJDF can now validate against custom XML schemas using the command-line configuration


IMPORTANT CHANGES 0.9.7.5 (2007-05-29)
- Interactive Alces:
  - Only displays buttons for messages that the device can receive, not send
  - SubmitQueueEntry button is displayed for devices that do not respond to KnownMessages handshake
- Automated Alces:
  - Improved API for programmtic testing
- JMF message templates are now located in conf/jmf_templates so that they can easily be edited
- Base URL for replacing file URLs can now be configured in conf/alces.properties
- Fixed bug in handling of non-JMF messages
- Path to XSLT files can now be configured in conf/alces.properties
- Upgraded to JDFLib-J 2.1.3.43

IMPORTANT CHANGES 0.9.7.4 (2007-05-08)
- Interactive Alces:
  - Queue-handling buttons generate valid JMF messages
  - Queue view and status are updated when connecting to a device 
  - Added Refresh-button for updating the queue view
  - Selecting queue entry and clicking Status button queries queue entry's status
  - Clicking Status button without selecting queue entry queries device's status
  - Selecting queue entry and clicking Resource query button query's queue entry's status
  - The KnownDevices pane now automatically displays device info for the connected device
- Automated Alces:
  - Scriptable with JavaScript (using Rhino)
  - Improved API for programmatic testing
  - Javadoc added
- Preprocessing framework for JDF files
  - NodeInfoPreprocessor - updates subscriptions in a JDF's NodeInfo are updated with Alces JMF URL
  - JobIDPreprocessor - replaces /JDF/@JobID
  - UrlResolvingPreprocessor - replaces relative URLs in JDF with absolute file or http URLs 
- JMF and JDF files saved with test report have meaningful names

IMPORTANT CHANGES 0.9.5 (2007-03-29)
- Interactive Alces:
  - If the ALT key is pressed when clicking the SubmitQueueEntry button, Alces will updatedthe JDF files /JDF/@JobID with new ID.
  - Queue entries can now be resubmitted by selecting the queue entry in the queue view and then clicking the ResubmitQueueEntry button.
  - Fixed bug that prevented connection history from being remembered 
- New test that fails if a received JMF message's return code is not zero (0)
- Messages can now be sent through a HTTP proxy. Configurable in 'conf/alces.properties'
- Configuration option to resolve relative URLs (URLs without a scheme) in JDF files to absolute file URLs or http URLs. Manual configuration in 'conf/alces.properties'. Is turned off by default. 
- Added extensible JMF preprocessing framework. Replacing of JobID, TimeStamps, SenderID, and URLs can now be configured in 'conf/alces.properties'.
- Upgraded to JDFLib-J 2.1.3.42
A complete list of all bug fixes and new features can be found here: http://www.cip4.org/jira/browse/ALCES?report=com.atlassian.jira.plugin.system.project:changelog-panel

IMPORTANT CHANGES 0.9 (2006-08-02)
- Interactive Alces:
  - Refactored GUI layout.
  - Windows look-and-feel is now default on Windows. (This can be changed in 'bin/run-with-classpath.bat')
  - Refactored the queue view. The queue buttons have been removed; instead the queue buttons displayed in the Known Messages pane should be used.
  - Test session tree always scrolls to the last added test session. [ALCES-81]
  - Connecting to a device can now be aborted. [ALCES-79]
  - Replaced the Device Info pane with a Known Devices pane. The device IDs of all devices returned in the ResponseKnownDevices received during the connect phase are dispalyed in a menu. Selecting a device from the menu displays its device information and status. All messages sent by Alces will have JMF/@DeviceID set to that of the selected device.
  - Pre-built MIME packages can be sent using the Send File button. Actually, any type of file can be sent.
- Automated Alces:
  - Exit code when Alces quits is 0 if all tests pass; 1 if any test fails; <0 if an error occurred. [ALCES-10]
  - Any file located in the specified test data directory will be sent, not just JMF. This is useful for sending JDF or pre-built MIME packages.
- Upgraded to JDFLib-J 2.1.3 build 35
- Renamed JDFValidation test CheckJDFTest to emphasize that JDFLib-J's CheckJDF is used.
- CheckJDFTest ingores non-JDF namespaces when validating JMF/JDF. [ALCES-69]
- Added a test that validates JDF files referenced by SubmitQueueEntry messages. [ALCES-3]
- Added a test that validates JDF files referenced by ReturnQueueEntry messages. [ALCES-4]
- Added DTD for XML test report. [ALCES-24]
- Added test jobs to 'testdata/jdf' for testing the current release (elk-20060601) of Elk ConventionalPrinting and Approval devices. [ALCES-61]
- The XML test report is now self contained. A test report only references files in its own directory. [ALCES-49]
- Upgraded to Commons HTTP Client 3.0.1 (and added dependency Commons Codec 1.3)

IMPORTANT CHANGES 2006-05-09
- Moved start scripts for Interactive Alces to top directory. Automated Alces scripts are still in 'bin' directory. [ALCES-78]
- Upgraded to JDFLib-J 2.1.3 build 32. [ALCES-74]
- Upgraded 'test/schema' and 'testdata/schema' to JDF Schema 1.3 20060329. [ALCES-74]
- Alces can be run from paths containing spaces. [ALCES-45]
- Preference dialog dynamically loads incoming and outgoing tests. [ALCES-77]
- It is possible to send messages to device's that do not respond to 'KnownMessages' queries. [ALCES-76]
- Alces now responds with a JMF Response to incoming JMF Command/Query/Signal/Acknowledge messages. [ALCES-21]
- Refactored CheckJDFWrapper to configure CheckJDF programmatically, not through command line.
- CheckJDF no longer prints to the command line.
- Removed dependency on Commons VFS.

IMPORTANT CHANGES 2005-12-21
- Interactive Alces:
  - The responsiveness of the user interface is greatly improved. [ALCES-32]
  - The test tree no longer needs to be manually refreshed. [ALCES-20]
  - The SubmitQueueEntry... file dialog now remembers the last used directory. [ALCES-66]
  - Fixed a queue view redraw bug. [ALCES-54]
- Test reports are now longer overwritten. A new directory named "output "+timestamp is now created. The output directories are written to ./ instead of the previous ./bin/. [ALCES-63]
- Alces can now send and receive all types of files, not just JMF and JDF. [ALCES-62][ALCES-64][ALCES-65]
- Alces now properly processes and logs received messages that have the incorrect content-type, for example messages that are JMF but have the content-type "text/xml". [ALCES-64][ALCES-65]
- All message processing is now done in the background. [ALCES-32]

IMPORTANT CHANGES 2005-11-26
- Interactive Alces 
  - Added queue view with buttons for manipulating the queue
  - Added context menus to Send JMF File... and SubmitQueueEntry... buttons
  - Added preferences dialog box for configuring tests and context menus
  - Displays messages pretty-printed
  - Shows test message type in test session tree
  - Saves a test report and all messages before quitting
- Messages logged during a test session are now saved with file extensions that match their content-type
- Both Interactive Alces and Automated Alces now use the same test engine
- Upgraded to be 2.1.3 build 26
- Fixed bug that caused StopPersChannel button to generate invalid messages
- Fixed bug that caused KnownDevices button to generate invalid messages
- Fixed bug that caused Status button to generate invalid messages
- Fixed bug that prevented messages with unknown content-types from being received 

IMPORTANT CHANGES 2005-08-05
- Fixed bug in content-type tests that prevented outgoing messages from passing
- SenderID is now set for all outgoing message. SenderID can be configured in 'conf/alces.properties'.
- JDF/JMF validation is now done by CheckJDF. The following command line options are used: -q -c -x <reportfile> -v -L ../testdata/schema/JDF.xsd
- Imporved formatting of test log in HTML test report
- HTML test report prints shows XML report from CheckJDF. In a future release this will be replaced by a link to the XML report file.
- Renamed folder "test-cases" to "testdata"
- Removed charset from Content-type HTTP header.
- Reorganized the sequence of the test-cases.
- Added test that fetches and validates the JDF instances referenced by a ReturnQueueEntry JMF message. However, validation is not completely implemented yet.
- Updated build script so that source code is now included with distribution.

IMPORTANT CHANGES 2005-06-02
- Added some new JMF test messages to folder 'test-cases'
- Renamed "Send File..." to "Send JMF File...".
- If the worker supports the SubmitQueueEntry command the SubmitQueueEntry button allows the user to select a JDF job ticket to submit.
- Added keyboard shortcut to SubmitQueueEntry button.

IMPORTANT CHANGES 2005-05-25
- Renamed automated Alces start scripts from 'alces-text.*' to 'auto-alces.*'
- Renamed interactive/GUI Acles start scripts from 'alces-swing.*' to 'alces.*'
- The SubmitQueueEntry test-cases now submit a JDF located at http://elk.itn.liu.se/jdf/Approval.jdf.
- Fixed incorrect property names in 'conf/alces.properties' that prevented some properties from being set.
- Tests are now configurable using the properties alces.incoming.tests and alces.outgoing.test in 'conf/alces.properties'.
- The IP address of the host is now the default used to build URLs used in outgoing messages, for example ReturnJMF.
- Interactive Alces now uses 'conf/alces.properties'.
- Interactive Alces now quits properly.
- Added keyboard shortcuts to interactive Alces.

IMPORTANT CHANGES 2005-05-20
- Fixed the path to suite-html.xsl in suite.xml so that it should work correctly on Windows in all web browsers.

IMPORTANT CHANGES 2005-05-18
- Renumbered test messages in 'test-cases'
- Added a last test message that unregisters all subscriptions.
- Added icons to test report 'suite.xml' for clarity
- Added source code to distribution
- Added software licences
