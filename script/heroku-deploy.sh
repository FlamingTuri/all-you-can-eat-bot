#!/bin/bash

./gradlew clean build

heroku login

heroku container:login

APP_NAME=$(heroku info | grep  "=== .*" |sed "s/=== //")

IMAGE_NAME="quarkus/$APP_NAME"

docker build -f src/main/docker/Dockerfile.jvm -t "$IMAGE_NAME" .

docker run --rm -e PORT=8080 -p 8080:8080 -d "$APP_NAME"

REGISTRY_NAME="registry.heroku.com/$APP_NAME/web"

docker tag "$IMAGE_NAME" "$REGISTRY_NAME"
docker push "$REGISTRY_NAME"

heroku container:release web -a "$APP_NAME"
