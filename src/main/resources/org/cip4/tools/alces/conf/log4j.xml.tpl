<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/" debug="false">
    
    <appender name="A1" class="org.apache.log4j.RollingFileAppender">        
        <param name="File" value="${LOG_PATH}/alces.log"/>  
        <param name="MaxFileSize" value="500KB"/>
        <param name="MaxBackupIndex" value="10"/>
        <param name="Threshold" value="DEBUG"/>
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d %-5p [%c] %m%n"/>
        </layout>
    </appender>
    
    <appender name="A2" class="org.apache.log4j.ConsoleAppender">
        <param name="Threshold" value="INFO"/>
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d %-5p [%c] %m%n"/>
        </layout>
    </appender>
    <category name="org">
        <priority value="ERROR"/>
    </category>
    <category name="httpclient">
        <priority value="ERROR"/>
    </category>
    <category name="org.cip4.tools">
        <priority value="INFO"/>
    </category>    
    <root>
        <appender-ref ref="A1"/>
        <appender-ref ref="A2"/>
    </root>
    <!--
    <appender name="A3" class="org.apache.log4j.net.SocketAppender">
        <param name="RemoteHost" value="localhost"/>
        <param name="Port" value="4445"/>
    </appender>
    -->
</log4j:configuration>
