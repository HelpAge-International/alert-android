#!/usr/bin/env bash

version=`git describe`
sed -i '.bak' 's/versionName ".*"/versionName "$version"/g' Alert/app/build.gradle