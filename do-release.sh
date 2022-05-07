#!/bin/bash
PROJ="fabric-1.15 fabric-1.16  fabric-1.17  fabric-1.18  fabric-1.18.2 forge-1.15 forge-1.16  forge-1.17  forge-1.18.2"


./gradlew clean

for i in $PROJ; do
  cd platforms/$i
  ../../gradlew --no-daemon --no-parallel --max-workers 1 -S --build-cache build
  cd ../../
done

for i in $PROJ; do
  cd platforms/$i
  ./gradlew --no-daemon --no-parallel --max-workers 1 -S --build-cache build modrinth curseforge #publishToSonatype
  cd ../../
done




exit 0
