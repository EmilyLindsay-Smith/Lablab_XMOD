#!/bin/bash

#use this script from xmod/ to compile and run the program

javac ./src/java/xmod/Xmod.java src/java/xmod/*/*.java -cp "lib/*" -d bin/ \
	 && cd bin \
     && java -classpath .:../lib/jSerialComm-2.11.0.jar xmod.Xmod 