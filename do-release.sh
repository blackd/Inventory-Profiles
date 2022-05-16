#!/bin/bash
PROJ_TO_BUILD="fabric-1.15 fabric-1.16  fabric-1.17  fabric-1.18 fabric-1.18.2 fabric-1.19 forge-1.15 forge-1.16  forge-1.17  forge-1.18.2"

PROJ_TO_RELEASE="fabric-1.15 fabric-1.16  fabric-1.17  fabric-1.18  fabric-1.18.2 forge-1.15 forge-1.16  forge-1.17  forge-1.18.2"

./gradlew clean
echo
echo
echo
echo
echo
echo

for i in $PROJ_TO_RELEASE; do
  cd platforms/$i
  ../../gradlew clean build modrinth curseforge #publishToSonatype
  cd ../../
echo
echo
echo
echo
echo
echo


done

exit 0
