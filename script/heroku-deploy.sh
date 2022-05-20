#!/bin/bash

./gradlew clean build

heroku login

heroku container:login

APP_NAME=$(heroku info | grep  "=== .*" |sed "s/=== //")

IMAGE_NAME="quarkus/$APP_NAME"

docker build -f src/main/docker/Dockerfile.jvm -t "$IMAGE_NAME" .

REGISTRY_NAME="registry.heroku.com/$APP_NAME/web"

docker tag "$IMAGE_NAME" "$REGISTRY_NAME"
docker push "$REGISTRY_NAME"

heroku container:release web -a "$APP_NAME"

docker image prune -f
