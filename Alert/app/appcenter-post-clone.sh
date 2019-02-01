#!/usr/bin/env bash

version=`git describe`
echo "Version $version"
sed -i '.bak' "s/versionName \".*\"/versionName \"$version\"/g" build.gradle