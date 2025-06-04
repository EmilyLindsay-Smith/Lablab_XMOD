#!/bin/bash

#Use this script from xmod/ to compile and run the tests

javac -d out ./test/*.java ./src/java/xmod/*/*.java -cp "lib/*" \
	&& java -jar ./lib/junit-platform-console-standalone-1.8.2.jar -cp ./lib/jSerialComm-2.11.0.jar  -cp out --scan-classpath


#	&& java -jar ./lib/junit-platform-console-standalone-1.8.2.jar -cp out --scan-classpath



#javac ./src/java/xmod/Xmod.java src/java/xmod/*/*.java -cp "lib/*" -d bin/ \
#	 && cd bin \
#	 && java -classpath .:../lib/jSerialComm-2.11.0.jar xmod.Xmod 