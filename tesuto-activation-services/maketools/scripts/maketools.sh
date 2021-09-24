#!/bin/bash

VERSION_FILE_DEV=VERSION.dev

PROPERTIES_FILE=maketools/PROPERTIES
PROPERTIES_FILE_LATEST=maketools/scripts/PROPERTIES.latest

. ${PROPERTIES_FILE}

COMPOSE_FILE='docker-compose.yml'
if [[ "$5" ]]; then
    COMPOSE_FILE="$5"
fi
export IMAGE_NAME="$2"
export CONTAINER_NAME="$3"
case "$1" in
    'check-ver')
        git archive --format=tar --remote=origin-maketools ${CURRENT_BRANCH} PROPERTIES | tar xf -
        mv PROPERTIES ${PROPERTIES_FILE_LATEST}

        current_version=${MAKETOOLS_VERSION}
        diff -q ${PROPERTIES_FILE} ${PROPERTIES_FILE_LATEST} 1>/dev/null

        if [[ $? == "0" ]]
        then
            echo -e "Maketools is up to date. \n version: ${current_version} \n branch: ${CURRENT_BRANCH}"
        else
            latest_version=$(cat ${PROPERTIES_FILE_LATEST} | grep 'VER' | awk -F'[=]' '{print $2}')
            echo -e "\033[0;31m** Maketools is out of date **\033[0m \nUse 'make update-maketools' to update. \n current_version: ${current_version}\n latest version: ${latest_version} "
        fi

        rm ${PROPERTIES_FILE_LATEST}
        ;;
    'setname')
        part=(`echo "$2" | tr ':' '\n'`)
        PTAG=${part[1]}

        if [[ -z "$PTAG" ]]; then
          PTAG=latest
        fi

        part2=(`echo $part | tr '/' '\n'`)

        PREGISTRY=${part2[0]}
        PNAME=${part2[1]}

        if [[ -z "$PNAME" ]]; then
          PNAME=$PREGISTRY
          PREGISTRY=''
        fi

        echo "REGISTRY="$PREGISTRY > ${VERSION_FILE_DEV}
        echo "IMAGE_NAME="$PNAME >> ${VERSION_FILE_DEV}
        echo "VERSION="$PTAG >> ${VERSION_FILE_DEV}
        ;;
    'compose')
        if [[ -f "$COMPOSE_FILE" ]]; then
            echo "$COMPOSE_FILE does not exist"
        fi
        docker-compose --file "$COMPOSE_FILE" "$4"
        ;;
    'compose-up')
        if [[ -f "$COMPOSE_FILE" ]]; then
            echo "$COMPOSE_FILE does not exist"
        fi
        docker-compose --file "$COMPOSE_FILE" up
        ;;
    'compose-down')
        if [[ -f "$COMPOSE_FILE" ]]; then
            echo "$COMPOSE_FILE does not exist"
        fi
        docker-compose down
        ;;
esac
