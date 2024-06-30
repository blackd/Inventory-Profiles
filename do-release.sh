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
