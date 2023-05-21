#!/bin/bash

. ~/.config/secrets/modrinth.sh
. ~/.config/secrets/curseforge.sh

pushd . 

cd $(mktemp -d /tmp/ipn-release.XXXX)

git clone --recurse-submodules git@github.com:blackd/Inventory-Profiles.git IPN

cd IPN/description

python build_html.py
python build_release_notes.py

cd ..

./gradlew --max-workers 32 createMcpToSrg
./gradlew --max-workers 32 compileKotlin compileJava

./gradlew --max-workers 4 build modrinth curseforge


ls -la build/lib/

pwd

popd
