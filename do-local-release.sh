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

. ~/.config/secrets/modrinth.sh
. ~/.config/secrets/curseforge.sh
#. ~/.config/secrets/env-setup.sh

pushd . 

cd $(mktemp -d /tmp/ipn-release.XXXX)

git clone --recurse-submodules git@github.com:blackd/Inventory-Profiles.git IPN

cd IPN/description

python build_html.py
python build_release_notes.py

cd ..

#./gradlew --max-workers 32 createMcpToSrg
./gradlew --max-workers 32 compileKotlin compileJava

./gradlew --max-workers 4 modrinth curseforge publishAllPublicationsToIpnOfficialRepoRepository


ls -la build/lib/

pwd

popd
