#!/bin/bash

rm -rf tmp
mkdir tmp

CLASSPATH="lib/kettle-core.jar:lib/kettle-engine.jar:lib/kettle-db.jar:lib/kettle-vfs-20100924.jar"

javac -cp $CLASSPATH \
  src/*.java \
  src/test/*.java \
  src/net/sf/saxon/*.java \
  src/org/pentaho/di/job/entries/xslt/*.java \
  -d tmp

# remove pseudo-saxon class.
rm -rf tmp/net

echo "# Should be o.k."
java -cp tmp Test1

echo "# Should fail."
java -cp tmp Test1 abc

echo "# Should always fail."
java -cp tmp Test2

echo "# Should not fail?"
java -cp tmp:$CLASSPATH Test3
