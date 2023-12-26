#!/bin/bash
INSTALLER_TYPE=app-image

rm -rf target
mkdir -p target/installer
JAVA_VERSION=20
detected_modules=`$JAVA_HOME/bin/jdeps -q --multi-release ${JAVA_VERSION} --ignore-missing-deps --print-module-deps \
--class-path "build/libs/chatclient-all.jar" build/classes/java/main/chatclient/Application.class`
echo "detected modules: $detected_modules"
manual_modules=,jdk.crypto.ec,jdk.localedata

$JAVA_HOME/bin/jlink --strip-native-commands --no-header-files --no-man-pages --compress=2 --strip-debug \
--add-modules "${detected_modules}${manual_modules}" --include-locales=en,de --output target/java-runtime --module-path "$JAVA_HOME/jmods;build" || exit 1

$JAVA_HOME/bin/jpackage --type $INSTALLER_TYPE --dest target/installer --input build/libs --name webkicks_chatclient \
 --main-class chatclient.Application --main-jar chatclient-all.jar --runtime-image target/java-runtime \
 --app-version 0.0.1 --vendor "slackito" --copyright "slackito" || exit 1


