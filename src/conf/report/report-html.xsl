<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:template match="/">
        <html>
            <head>
                <title>Alces Test Results</title>
                <link rel="stylesheet" type="text/css" media="all" href="resources/report.css" />
            </head>            
            <body>
                <h1>Alces Test Report</h1>
                <div class="summary">
                    <h2>Summary</h2>
                    Passed all tests: 
                    <xsl:if test="count(//message/tests/test[@passed='false'])>0">
                        <img alt="Failed" src="resources/images/test_fail.gif" /> 
                    </xsl:if> 
                    <xsl:if test="count(//message/tests/test[@passed='false'])=0">
                        <img alt="Passed" src="resources/images/test_pass.gif" /> 
                    </xsl:if> 
                    <strong><xsl:value-of select="count(//message/tests/test[@passed='false'])=0"/></strong><br />
                    Date: <strong><xsl:value-of select="suite/@timestamp"/></strong><br />
                    Test sessions: <strong><xsl:value-of select="count(//session)"/></strong><br />
                    Total tests: <strong><xsl:value-of select="count(//message/tests/test)"/></strong><br />
                    Passed tests: <strong><xsl:value-of select="count(//message/tests/test[@passed='true'])"/></strong><br />
                    Failed tests: <strong><xsl:value-of select="count(//message/tests/test[@passed='false'])"/></strong><br />
                    Total outgoing messages: <strong><xsl:value-of select="count(//message[@type='out'])"/></strong><br />
                    Total incoming messages: <strong><xsl:value-of select="count(//message[@type='in'])"/></strong><br />
                </div>
                <xsl:apply-templates select="suite/session"/>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="suite/session">
        <div class="session">
            <h2>
                <img alt="Session" src="resources/images/session.gif" />
                Session</h2>
            Target URL: <strong><xsl:value-of select="@url"/></strong><br />
            Passed all tests: <strong><xsl:value-of select="(count(message/tests/test[@passed='false'])+count(message/messages/message/tests/test[@passed='false']))=0"/></strong><br />
            Total tests: <strong><xsl:value-of select="count(message/tests/test)+count(message/messages/message/tests/test)"/></strong><br />
            Passed tests: <strong><xsl:value-of select="count(message/tests/test[@passed='true'])+count(message/messages/message/tests/test[@passed='true'])"/></strong><br />
            Failed tests: <strong><xsl:value-of select="count(message/tests/test[@passed='false'])+count(message/messages/message/tests/test[@passed='false'])"/></strong><br />    
            Outgoing messages: <strong><xsl:value-of select="count(message[@type='out'])+count(message/messages/message[@type='out'])"/></strong><br />
            Incoming messages: <strong><xsl:value-of select="count(message[@type='in'])+count(message/messages/message[@type='in'])"/></strong><br />
            <h3>
                <xsl:if test="message/@type='out'">
                    <img alt="Outgoing message" src="resources/images/message_out.gif" />
                </xsl:if> 
                <xsl:if test="message/@type='in'">
                    <img alt="Incoming message" src="resources/images/message_in.gif" />
                </xsl:if> 
                Initiating Message</h3>
            Type: <strong><xsl:value-of select="message/@type"/></strong><br />
            Header: <strong><xsl:value-of select="message/header"/></strong><br />
            Body:
            <a>
                <xsl:attribute name="href">
                    <xsl:value-of select="message/body/@url"/>
                </xsl:attribute>
                <xsl:value-of select="message/body/@url"/>
            </a><br />
            <div class="message">
                <code>
                    <xsl:value-of select="message/body"/>
                </code>
            </div>
            <br />
            <h4>Tests</h4>
            <ul>
                <xsl:apply-templates select="message/tests/test"/>
            </ul>
            <xsl:apply-templates select="message/messages"/>        
        </div>
    </xsl:template>
    
    <xsl:template match="messages">
        <h4>Messages</h4>
        <ul>                
            <xsl:apply-templates select="message"/>
        </ul>
    </xsl:template>
    
    <xsl:template match="message">
        <li>
            <h4>
                <xsl:if test="@type='out'">
                    <img alt="Outgoing message" src="resources/images/message_out.gif" /> 
                </xsl:if>
                <xsl:if test="@type='in'">
                    <img alt="Incoming message" src="resources/images/message_in.gif" /> 
                </xsl:if>
                Message</h4>
            Type: <strong><xsl:value-of select="@type"/></strong><br />
            Header: <strong><xsl:value-of select="header"/></strong><br />
            Body:
            <a>
                <xsl:attribute name="href">
                    <xsl:value-of select="body/@url"/>
                </xsl:attribute>
                <xsl:value-of select="body/@url"/>
            </a><br />
            <div class="message">
                <code>
                    <xsl:value-of select="body"/>
                </code>
            </div>
            <br />
            <h4>Tests</h4>
            <ul>
                <xsl:apply-templates select="tests/test"/>
            </ul>
            <xsl:apply-templates select="messages"/>
        </li>
    </xsl:template>
    
    <xsl:template match="tests/test">
        <li>
            <xsl:if test="@passed='false'">
                <img alt="Failed" src="resources/images/test_fail.gif" />
            </xsl:if>
            <xsl:if test="@passed='true'">
                <img alt="Failed" src="resources/images/test_pass.gif" />
            </xsl:if>
            Passed: <strong><xsl:value-of select="@passed"/></strong><br />
            Type: <strong><xsl:value-of select="@type"/></strong><br />
            Description: <strong><xsl:value-of select="@description"/></strong><br />
            Test log:
            <div class="log">
                <pre>
                    <xsl:value-of select="log"/>
                </pre>
            </div>
        </li>
    </xsl:template>
</xsl:stylesheet>
