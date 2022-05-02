#!/bin/bash

./gradlew clean build &

wait $(jobs -p)

exit 0
