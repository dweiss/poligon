#!/bin/bash

rm -rf tmp
mkdir tmp

javac src/*.java src/test/*.java src/net/sf/saxon/*.java -d tmp

# remove pseudo-saxon class.
rm -rf tmp/net

echo "# Should be o.k."
java -cp tmp Test1

echo "# Should fail."
java -cp tmp Test1 abc

echo "# Should always fail."
java -cp tmp Test2
