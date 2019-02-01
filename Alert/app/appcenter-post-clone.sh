#!/usr/bin/env bash

version=`git describe`
sed -i '.bak' 's/versionName ".*"/versionName "$version"/g' build.gradle