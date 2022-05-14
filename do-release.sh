#!/bin/bash
PROJ_TO_BUILD="fabric-1.15 fabric-1.16  fabric-1.17  fabric-1.18 fabric-1.18.2 fabric-1.19 forge-1.15 forge-1.16  forge-1.17  forge-1.18.2"

PROJ_TO_RELEASE="fabric-1.15 fabric-1.16  fabric-1.17  fabric-1.18  fabric-1.18.2 forge-1.15 forge-1.16  forge-1.17  forge-1.18.2"

./gradlew clean

for i in $PROJ_TO_BUILD; do
  cd platforms/$i
  ../../gradlew --max-workers 1 -S --build-cache build
  cd ../../
  killall -9 java
done

for i in $PROJ_TO_RELEASE; do
  cd platforms/$i
  ../../gradlew --max-workers 1 -S --build-cache build modrinth curseforge #publishToSonatype
  cd ../../
  killall -9 java
done

killall -9 java
./gradlew --max-workers 1 -S --build-cache build


exit 0
