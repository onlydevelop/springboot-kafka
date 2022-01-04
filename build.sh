#!/bin/bash
pushd services/parser
./gradlew clean build
docker rmi parser -f
docker build . -t parser
popd
pushd services/uploader
./gradlew clean build
docker rmi uploader -f
docker build . -t uploader
