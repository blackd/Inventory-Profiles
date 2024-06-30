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

PROJ="fabric-1.14 fabric-1.15 fabric-1.16  fabric-1.17  fabric-1.18  fabric-1.18.2 fabric-1.19 forge-1.14 forge-1.15 forge-1.16  forge-1.17  forge-1.18.2"
echo
echo
echo
free
echo
echo
echo
echo


for i in $PROJ; do
  echo
  echo
  echo '**********************************'
  echo "Will build $i"
  echo '**********************************'
  echo
  echo

  cd platforms/$i

  ../../gradlew --max-workers 1 -S --build-cache  build
  cd ../../
  killall -9 java
done
killall -9 java

  echo
  echo
  echo '**********************************'
  echo "End with full build"
  echo '**********************************'
  echo
  echo

./gradlew --max-workers 1 -S --build-cache build

exit 0
