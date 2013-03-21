<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text"/>
<xsl:template match="/">
ALCES TEST REPORT
Passed all tests: <xsl:value-of select="count(//message/tests/test[@passed='false'])=0"/>
Test sessions: <xsl:value-of select="count(//session)"/>
Total tests:   <xsl:value-of select="count(//message/tests/test)"/>
Passed tests:  <xsl:value-of select="count(//message/tests/test[@passed='true'])"/> (<xsl:value-of select="count(//message[@type='in']/tests/test[@passed='true'])"/> incoming, <xsl:value-of select="count(//message[@type='out']/tests/test[@passed='true'])"/> outgoing)
Failed tests:  <xsl:value-of select="count(//message/tests/test[@passed='false'])"/> (<xsl:value-of select="count(//message[@type='in']/tests/test[@passed='false'])"/> incoming, <xsl:value-of select="count(//message[@type='out']/tests/test[@passed='false'])"/> outgoing)
Total outgoing messages: <xsl:value-of select="count(//message[@type='out'])"/> 
Total incoming messages: <xsl:value-of select="count(//message[@type='in'])"/>
</xsl:template>
</xsl:stylesheet>