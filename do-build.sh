#!/bin/bash

./gradlew --no-daemon --no-parallel --max-workers 2 -S clean build &

wait $(jobs -p)

exit 0
