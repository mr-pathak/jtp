#! /usr/bin/env bash
#
# The jtp command script. 
#
# /*
#  * Copyright 2013-14 Mitesh Pathak <miteshpathak05@gmail.com>
#  *
#  * This file is part of JTP (Java Trusted Peer).
#  *
#  * JTP is free software: you can redistribute it and/or modify it under the terms 
#  * of the GNU General Public License as published by the Free Software Foundation, 
#  * either version 3 of the License, or (at your option) any later version.
#  *
#  * JTP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
#  * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
#  * PARTICULAR PURPOSE. See the GNU General Public License for more details.
#  *
#  * You should have received a copy of the GNU General Public License along 
#  * with JTP; if not, see <http://www.gnu.org/licenses/>.
#  */
#


# Environment Variables:
#
#   JAVA_HOME	The java implementation to use. Overrides JAVA_HOME.
#
#   MAVEN_HOME  Maven implementation to use. Overrides MAVEN_HOME.
#
#   JTP_HOME	Root directory of project JTP.
#

ODIR=$PWD
bin=`dirname "$0"`
cd "$bin/../"

# SET JTP_HOME
JTP_HOME=$PWD

# SHOW USAGE if NO ARGS PASSED
if [ ! $# = 2 ]; then
  echo "Usage:   jtp <user-name> <path/to/share>"
  echo "Example: jtp user1 /tmp/share/"
  exit 1
fi

# JAVA_HOME
if [ "$JAVA_HOME" = "" ]; then
  echo "Error: JAVA_HOME is not set."
  exit 1
fi
JAVA=$JAVA_HOME/bin/java

# CHECK if BUILD REQ
FILE=$JTP_HOME/target/jtp*jar-with-dependencies.jar
if [ ! -f $FILE ]; then
   
  if [ "$MAVEN_HOME" = "" ]; then
    echo "Error: MAVEN_HOME is not set."
    exit 1
  fi
  MAVEN=$MAVEN_HOME/bin/mvn
  echo "[BUILDING PROJECT]"
  "$MAVEN" "clean"
  echo "---"
  "$MAVEN" "install"
  echo "[BUILD SUCCESFULL]"
fi

# CHECK if SUCC BUILD
if [ ! -f $FILE ]; then
  echo "Error: Cannot locate runnable jtp jar 'jtp*-jar-with-dependencies.jar'"
  exit 1
fi
FILE=`find $FILE`
# GET ARGS
USERNAME=$1
SHARE=$2
shift

# RUN
cd $JTP_HOME/target
exec "$JAVA" "-jar" $FILE $USERNAME $SHARE
cd $ODIR
