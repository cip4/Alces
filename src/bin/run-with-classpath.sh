#!/bin/sh
#Make sure prerequisite environment variables are set
if [ "JAVA_HOME" = "" ]
then
    echo "The JAVA_HOME environment variable is not defined"
    echo "This environment variable is needed to run this program"
fi

#Setup Environment variables for Alces
# Set any jars you want to appear at the begining of the classpath here
# remember to terminate with a :

PRE_ALCES_CLASSPATH=

#Set any jars you want to appear at the end of the classpath here
#remember to terminate with a :

POST_ALCES_CLASSPATH=


LIB_DIR=../lib
CLASSPATH=.:../classes:../conf:$PRE_ALCES_CLASSPATH:

CLASSPATH=$CLASSPATH$LIB_DIR/activation-1.0.2.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/bounce.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/commons-codec-1.3.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/commons-httpclient-3.0.1.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/commons-io-1.2.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/commons-lang-2.3.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/commons-logging-1.0.4.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/commons-vfs-1.0-RC8-SNAPSHOT.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/concurrent-1.3.4.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/core-renderer.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/javax.servlet.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/jaxen-1.0-FCS-full.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/JDFLibJ.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/jdom-1.0.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/js.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/log4j-1.2.8.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/mail-1.3.3.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/org.mortbay.jetty.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/org.mortbay.jmx.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/saxpath-1.0-FCS.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/serializer.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/swing-worker-1.2.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/xalan.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/xercesImpl.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/xml-apis.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/alces.jar:
CLASSPATH=$CLASSPATH$LIB_DIR/crimson.jar:

CLASSPATH=$CLASSPATH$POST_ALCES_CLASSPATH

#if using cygwin under windows we need to make the paths windows friendly

case "`uname`" in
CYGWIN*)
  if [ -n "CLASSPATH" ]
  then
   CLASSPATH=`cygpath -pw $CLASSPATH`
  fi
esac

$JAVA_HOME/bin/java -classpath $CLASSPATH -Djava.library.path=$LIB_DIR -Dxr.util-logging.loggingEnabled=false $@