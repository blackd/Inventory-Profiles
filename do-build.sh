#!/bin/bash

./gradlew --no-daemon --no-parallel --max-workers 1 -S  --foreground build &

wait $(jobs -p)

./gradlew --no-daemon --no-parallel --max-workers 1 -S  --foreground build &

exit 0
