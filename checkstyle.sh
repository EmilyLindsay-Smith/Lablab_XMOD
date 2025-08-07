#!/bin/bash

if [[ $# -ne 1 ]]; then
    echo "Usage: checkstyle.sh directory"
    echo "e.g. ./checkstyle.sh test"
    echo "e.g. ./checkstyle.sh serial"
fi


if [[ $1 = "test" ]]; then
    dirToCheck=$1
else
    dirToCheck="src/java/xmod/$1"
fi

echo $dirToCheck


java -jar lib/checkstyle-10.25.0-all.jar -c lib/sun_checks.xml $dirToCheck
