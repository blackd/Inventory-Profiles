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

if [[ n$IPNEXT_RELEASE != "n" ]]; then
  . ~/.config/secrets/modrinth.sh
  . ~/.config/secrets/curseforge.sh
fi

pushd .

mkdir /tmp/IPN
cd $(mktemp -d /tmp/IPN/IPN-release.XXXX)

git clone --recurse-submodules git@gitea.lan:Inventory-Profiles-Next/IPN.git IPN

if [[ ! -e ../venv ]]; then
  python -m venv ../venv
  . ../venv/bin/activate
  pip install pandoc
  pip install pypandoc
  pip install premailer
  pip install pandoc_include
else
  . ../venv/bin/activate
fi

cd IPN/description

python build_html.py
python build_release_notes.py

cd ..

if [[ n$IPNEXT_PATREON != "n" ]]; then
  IPNEXT_RELEASE=1 --max-workers 32 clean compileKotlin compileJava
else
  ./gradlew --max-workers 32 clean compileKotlin compileJava
fi

GRADLE_ARG="build"

if [[ n$IPNEXT_RELEASE != "n" ]]; then
  GRADLE_ARG="--max-workers 4 $GRADLE_ARG publishAllPublicationsToIpnOfficialRepoRepository modrinth curseforge"
  ./gradlew $GRADLE_ARG
elif [[ n$IPNEXT_PATREON != "n" ]]; then
  GRADLE_ARG="--max-workers 32 $GRADLE_ARG"
  IPNEXT_RELEASE=1 ./gradlew $GRADLE_ARG
else
  GRADLE_ARG="--max-workers 32 $GRADLE_ARG"
  ./gradlew $GRADLE_ARG
fi


ls -la build/lib/

pwd

popd
