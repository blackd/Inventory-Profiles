#!/bin/bash

convert() {
    
    local OUT=$3
    local IN=$1
    local TMP_OUT=$2
    
    echo converting "$IN" to "$OUT"
    
    unzip -d "$TMP_OUT" "$IN" > /dev/null

    pushd . > /dev/null

    # shellcheck disable=SC2164
    cd "$2"

    find . -type f | sort | zip -q -X -9 -@ "$OUT" > /dev/null

    advzip -3 -z -i 1 "$OUT" > /dev/null

    # shellcheck disable=SC2164
    popd > /dev/null
}

if [[ "x$1" == "x" ]]; then
    echo "source jar not specified"
    exit 1
fi

if [[ "x$2" == "x" ]]; then
    echo "root dir not specified"
    exit 1
fi

mkdir "$2/jaropt/" > /dev/null

UNPACK_DIR=$(mktemp -d "$2/jaropt/XXXXXXXXXX")
TMP_OUTPUT_DIR=$(mktemp -d "$2/jaropt/XXXXXXXXXX")

JAR_NAME=$(basename "$1")

convert "$1" "$UNPACK_DIR" "$TMP_OUTPUT_DIR/$JAR_NAME"

ORGSIZE=$(stat --printf "%s" "$1")
NEWSIZE=$(stat --printf "%s" "$TMP_OUTPUT_DIR/$JAR_NAME")
PERCENT=$(awk -vo="$ORGSIZE" -vn="$NEWSIZE" 'BEGIN { printf("%.3f", n/o*100)}')

echo "$ORGSIZE -> $NEWSIZE  or $PERCENT%"

mv "$TMP_OUTPUT_DIR/$JAR_NAME" "$1"

rm -rf "$UNPACK_DIR" "$TMP_OUTPUT_DIR"
