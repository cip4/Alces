@REM Setup Environment variables for Alces
@REM Set any jars you want to appear at the begining of the classpath here
@REM remember to terminate with a ;

@SET PRE_ALCES_CLASSPATH=

@REM Set any jars you want to appear at the end of the classpath here
@REM remember to terminate with a ;

@SET POST_ALCES_CLASSPATH=

@SET LIB_DIR=../lib
@SET CLASSPATH=.;../classes;../conf;%PRE_ALCES_CLASSPATH%;

@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/activation-1.0.2.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/bounce.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/commons-codec-1.3.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/commons-httpclient-3.0.1.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/commons-io-1.2.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/commons-lang-2.3.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/commons-logging-1.0.4.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/commons-vfs-1.0-RC8-SNAPSHOT.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/concurrent-1.3.4.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/core-renderer.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/javax.servlet.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/jaxen-1.0-FCS-full.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/JDFLibJ.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/jdom-1.0.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/js.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/log4j-1.2.8.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/mail-1.3.3.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/org.mortbay.jetty.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/org.mortbay.jmx.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/saxpath-1.0-FCS.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/serializer.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/swing-worker-1.2.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/xalan.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/xercesImpl.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/xml-apis.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/alces.jar;
@SET CLASSPATH=%CLASSPATH%%LIB_DIR%/crimson.jar;

@SET CLASSPATH=%CLASSPATH%%POST_ALCES_CLASSPATH%

@java -Dswing.defaultlaf=com.sun.java.swing.plaf.windows.WindowsLookAndFeel -Dxr.util-logging.loggingEnabled=false -classpath "%CLASSPATH%" %1 %2 %3 %4 %5 %6 %7 %8 %9

:end