#!/bin/bash

#
# Inventory Profiles Next
#
#   Copyright (c) 2024 Plamen K. Kosseff <p.kosseff@gmail.com>
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
#     along with this program.  If not, see <https://www.gnu.org/licenses/>.
#

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

    if [[ "x$IPNEXT_RELEASE" == "x" ]]; then
        advzip -3 -z -i 100 "$OUT" > /dev/null
    else
        advzip -4 -z -i 100 "$OUT" > /dev/null
    fi

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

mkdirhier $(dirname "$3")

mv "$TMP_OUTPUT_DIR/$JAR_NAME" "$3"

rm -rf "$UNPACK_DIR" "$TMP_OUTPUT_DIR"
