#!/bin/bash

. ~/.config/secrets/modrinth.sh
. ~/.config/secrets/curseforge.sh

pushd . 

cd $(mktemp -d /tmp/ipn-release.XXXX)

git clone git@github.com:blackd/Inventory-Profiles.git IPN

cd IPN/description

python build_html.py
python build_release_notes.py

cd ..

./gradlew clean build modrinth curseforge

ls -la build/lib/

pwd

popd
