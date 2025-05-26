#!/bin/bash

#Use this script from xmod/ to compile and run the tests

javac -d out ./test/*.java ./src/java/xmod/*/*.java -cp "./lib/*" \
	&& java -jar ./lib/junit-platform-console-standalone-1.8.2.jar -cp out --scan-classpath
