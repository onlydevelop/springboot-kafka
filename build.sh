#!/bin/bash
pushd services/filestore
./gradlew clean build
docker rmi filestore -f
docker build . -t filestore
popd
pushd services/parser
./gradlew clean build
docker rmi parser -f
docker build . -t parser
popd
pushd services/uploader
./gradlew clean build
docker rmi uploader -f
docker build . -t uploader
