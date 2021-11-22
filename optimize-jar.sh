#!/bin/bash

convert() {
    
    local OUT=$3
    local IN=$1
    local TMP_OUT=$2
    
    echo converting $IN to $OUT
    
    unzip -d $TMP_OUT $IN

    pushd .

    cd $2

    find . -type f | sort | zip -X -9 -@ $OUT 1> /dev/null

#    advzip -i 50 -4 -z $OUT

    popd
}

if [[ "x$1" == "" ]]; then
    echo "source jar not specified"
    exit 1
fi

if [[ "x$2" == "" ]]; then 
    echo "rootdir not specified"
    exit 1
fi

mkdir $2/jaropt/

UNPACK_DIR=$(mktemp -d $2/jaropt/XXXXXXXXXX)
TMP_OUTPUT_DIR=$(mktemp -d $2/jaropt/XXXXXXXXXX)

echo $UNPACK_DIR

JAR_NAME=$(basename $1)

convert $1 $UNPACK_DIR $TMP_OUTPUT_DIR/$JAR_NAME

mv $TMP_OUTPUT_DIR/$JAR_NAME $1

rm -rf $UNPACK_DIR $TMP_OUTPUT_DIR
