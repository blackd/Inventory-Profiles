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

PROJECT_NAME="IPN"

BUILD_PATH=""

if [[ n$1 != "n" ]]; then
  BUILD_PATH="$1:"
fi

echo "BUILD_PATH=${BUILD_PATH}"

pushd .

mkdir /tmp/IPN
cd /tmp/IPN

if [[ -e /tmp/IPN/${PROJECT_NAME} ]]; then
  rm -rf /tmp/IPN/${PROJECT_NAME}
fi

git clone git@gitea.lan:Inventory-Profiles-Next/${PROJECT_NAME}.git ${PROJECT_NAME}

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

cd ${PROJECT_NAME}/description

python build_html.py
python build_release_notes.py

cd ..

export _JAVA_OPTIONS=-Xmx8G

GRADLE_ARG="--exclude-task compileTestJava --exclude-task test ${BUILD_PATH}build"


if [[ n${IPNEXT_M} != "n" ]]; then
  GRADLE_ARG="${GRADLE_ARG} ${BUILD_PATH}modrinth"
fi

if [[ n${IPNEXT_C} != "n" ]]; then
  GRADLE_ARG="${GRADLE_ARG} ${BUILD_PATH}curseforge"
fi

if [[ n${IPNEXT_P} != "n" ]]; then
  GRADLE_ARG="${GRADLE_ARG} ${BUILD_PATH}publishAllPublicationsToIpnOfficialRepoRepository"
fi


GRADLE_ARG="--max-workers 32 ${GRADLE_ARG}"

echo will run "./gradlew ${GRADLE_ARG}"
echo

./gradlew ${GRADLE_ARG}


pwd

popd
