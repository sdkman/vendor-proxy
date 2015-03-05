#!/bin/bash

if [ "$GIT_BRANCH" == 'RELEASE' ]; then
  ENV="prod"
else
  ENV="dev"
fi

RELEASE=$(grep 'version' gradle.properties | sed 's_version=__g')

if [[ "$RELEASE" == "1.0.0-SNAPSHOT" ]]; then
	RELEASE="1.0.0-build-$DRONE_BUILD_NUMBER"
	sed -i "s/1.0.0-SNAPSHOT/$RELEASE/g" gradle.properties
fi

echo "Environment: $ENV, Release: $RELEASE"
./gradlew --stacktrace -Penv="$ENV" clean check cfPush
