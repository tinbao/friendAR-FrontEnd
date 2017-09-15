#!/bin/bash
#set -eox

export SEMVER_LAST_TAG=$(git describe --abbrev=0 --tags 2>/dev/null)
export SEMVER_RELEASE_LEVEL=$(git log --oneline -1 --pretty=%B | cat | tr -d '\n' | cut -d "[" -f2 | cut -d "]" -f1)

if [ -z $SEMVER_LAST_TAG ]; then
    echo "No tags defined"
fi

if [ -n $SEMVER_RELEASE_LEVEL ]; then
    git clone https://github.com/fsaintjacques/semver-tool /tmp/semver
    $(cd /tmp/semver; git checkout tags/1.2.1)
    export PATH=$PATH:/tmp/semver/src
    semver init $SEMVER_LAST_TAG &>/dev/null
    semver bump $SEMVER_RELEASE_LEVEL &>/dev/null
    git tag $(semver)
    git push origin --tags
else
    echo "No release level defined"
fi

exit 0
