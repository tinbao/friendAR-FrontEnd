#!/bin/bash
set -ex
set -o pipefail
mvn(){
	docker run -it --rm -v "$(pwd)/friendar-app":/usr/src/mymaven -w /usr/src/mymaven maven:alpine mvn -B "$@" | grep -v 'Download.* http'
}
mvn pmd:check
mvn pmd:cpd-check
mvn clean test
