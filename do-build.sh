#!/bin/bash

./gradlew --no-daemon --no-parallel --max-workers 1 -S build &

wait $(jobs -p)

./gradlew --no-daemon --no-parallel --max-workers 1 -S  build &

wait $(jobs -p)

exit 0
