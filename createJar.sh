if [ $# != 1 ]; then
   echo "Usage: $0 <true|false>"
   echo "If you want to recompile Xmod into .java files, use true"
   echo "If you want to use the existing files in bin/, use false"
   exit 1
fi

recompile=$1;

# Check script is run in the correct place
if [ ! -e "bin" ] || [ ! -e "lib/jSerialComm-2.11.0.jar" ]; then
    echo "Note this script assumes the following: "
    echo "\t1) it is run in the parent directory to bin and lib "
    echo "\t2) lib contains jSerialComm-2.11.0.jar"
    echo "These assumptions have not been met."
    exit
fi;

unpackDependency () {
    cp lib/jSerialComm-2.11.0.jar bin
    cd bin
    tar xf jSerialComm-2.11.0.jar
    rm jSerialComm-2.11.0.jar
    cd ..
}

#To compile xmod again first
if [ $recompile ]; then
    #Empty current bin contents
    rm -rf bin/*
    #Compile Xmod
    javac ./src/java/xmod/Xmod.java src/java/xmod/*/*.java -cp "lib/*" -d bin/
    #Unzip jSerialComm
    unpackDependency
fi

if [ !$recompile ] && [ ! -d "bin/com" ]; then
    unpackDependency
fi

mkdir -p dist

xmodJar="../dist/Xmod-2.0.jar"
manifest="../dist/XmodManifest.mf"

cd bin
{
manifestText="Manifest-Version: 1.0\nBuilt-By: Emily Lindsay-Smith\n"
manifestText="${manifestText}Class-Path: .\nMain-Class: xmod/Xmod\n"
echo $manifestText > $manifest
jar cmf $manifest $xmodJar *

} || (echo "Failed to create ${xmodJar}"; exit)

echo "${xmodJar} succesfully created"
chmod 775 $xmodJar
java -jar $xmodJar
