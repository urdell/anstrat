#!/bin/bash
# WARNING: This script is outdated, the locations of some files within the libgdx-nightly-latest.zip have changed

wget http://libgdx.badlogicgames.com/nightlies/libgdx-nightly-latest.zip
unzip -od libgdx-latest libgdx-nightly-latest.zip

# anstrat-android
cp -r libgdx-latest/armeabi anstrat-android/libs
cp -r libgdx-latest/armeabi-v7a anstrat-android/libs
cp libgdx-latest/gdx-backend-android.jar anstrat-android/libs
cp libgdx-latest/sources/gdx-backend-android-sources.jar anstrat-android/libs
cp libgdx-latest/extensions/armeabi/libgdx-freetype.so anstrat-android/libs/armeabi
cp libgdx-latest/extensions/armeabi-v7a/libgdx-freetype.so anstrat-android/libs/armeabi-v7a

# anstrat-core
cp libgdx-latest/gdx.jar anstrat-core/libs
cp libgdx-latest/sources/gdx-sources.jar anstrat-core/libs
cp libgdx-latest/extensions/gdx-freetype.jar anstrat-core/libs
cp libgdx-latest/extensions/sources/gdx-freetype-sources.jar anstrat-core/libs

# anstrat-desktop
cp libgdx-latest/gdx-natives.jar anstrat-desktop/libs
cp libgdx-latest/extensions/gdx-tools.jar anstrat-desktop/libs
cp libgdx-latest/extensions/sources/gdx-tools-sources.jar anstrat-desktop/libs
cp libgdx-latest/gdx-backend-lwjgl.jar anstrat-desktop/libs
cp libgdx-latest/sources/gdx-backend-lwjgl-sources.jar anstrat-desktop/libs
cp libgdx-latest/gdx-backend-lwjgl-natives.jar anstrat-desktop/libs
cp libgdx-latest/extensions/gdx-freetype-natives.jar anstrat-desktop/libs

# anstrat-test
cp libgdx-latest/gdx-natives.jar anstrat-test/libs
cp libgdx-latest/gdx-backend-jogl.jar anstrat-test/libs
cp libgdx-latest/sources/gdx-backend-jogl-sources.jar anstrat-test/libs
cp libgdx-latest/gdx-backend-jogl-natives.jar anstrat-test/libs
cp libgdx-latest/extensions/gdx-freetype-natives.jar anstrat-test/libs

# Remove temp files and directories
rm libgdx-nightly-latest.zip
rm -r libgdx-latest
