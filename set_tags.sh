#!/bin/bash
#set -eox

BRANCH="travis-cd"
echo "Current Travis $TRAVIS_BRANCH"

if [ "$TRAVIS_BRANCH"="$BRANCH" ];
then
  echo "success"
  if [ "$TRAVIS_PULL_REQUEST"=false ];
  then
  	if [ -z "$TRAVIS_TAG" ];
    then
	    echo "Starting to tag commit.\n"

	    git config --global user.email "travis@travis-ci.org"
	    git config --global user.name "Travis"

	    # Add tag and push to master.
	    git tag -a v${TRAVIS_BUILD_NUMBER} -m "Travis build $TRAVIS_BUILD_NUMBER pushed a tag."
	    git push origin --tags
	    git fetch origin

	    echo "Done magic with tags.\n"
	  fi
  fi
fi

exit 0
