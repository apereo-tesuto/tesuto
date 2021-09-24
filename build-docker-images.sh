#!/bin/bash -x

ENV=$1

if [ "x$ENV" == "x" ]; then
    echo "One argument required (name of environment)"
    exit 1
fi


REGISTRY=docker.dev.ccctechcenter.org
VERSION=`git rev-parse --abbrev-ref HEAD`
VERSION=`basename $VERSION`
GIT_TAG=`git log --pretty=format:'%h' -n 1`
TIMESTAMP=`date +%s`
TAG="${VERSION}-${GIT_TAG}-${TIMESTAMP}"
START_DIR=`pwd`
TASKDEF="${START_DIR}/taskdef-block.properties"

echo "${ENV}:" > ${TASKDEF}
if [ "${ENV}" == "ci" ]; then
cat  <<EOF >> ${TASKDEF}
  credstash-image: credstash:0.0.1-SNAPSHOT
  new-relic-env: CI
  new-relic-enabled: false
EOF
else
cat  <<EOF >> ${TASKDEF}
  credstash-image: credstash:0.0.1
  new-relic-env: ${ENV^}
  new-relic-enabled: false
EOF
fi

for dir in tesuto-web tesuto-placement-services/tesuto-placement-microservice tesuto-rules-services/tesuto-rules-microservice
do
    case $dir in
        tesuto-web)
            IMAGE_NAME=tesuto-ui
            TASKDEF_LABEL=assess-image
            ;;
        *placement*)
            IMAGE_NAME=tesuto-placement
            TASKDEF_LABEL=placement-image
            ;;
        *rules*)
            IMAGE_NAME=tesuto-rules
            TASKDEF_LABEL=rules-image
            ;;
        *)
            IMAGE_NAME=$dir
            TASKDEF_LABEL=""
           ;;
    esac

    cd $dir
    component=`basename $dir`
    TAGGED_IMAGE_NAME="${REGISTRY}/${IMAGE_NAME}:${TAG}"
    
    if [ "x${TASKDEF_LABEL}" != x ]; then
        echo "  ${TASKDEF_LABEL}: ${IMAGE_NAME}:${TAG}" >> ${TASKDEF}
    fi

    #Assume just one jar in the target directory.
    #ls seems to pick up an extra file when used in backticks    
    JAR=`find target -name '*.jar'`
    
    chmod 751 $JAR
    cp -f $JAR includes/opt/ccc/
    TODAY=`date '+%Y%m%d_%H%M%S'`
    touch includes/kill_cache_${TODAY}
	docker build --tag=${TAGGED_IMAGE_NAME} .
	rm includes/kill_cache_*
    docker push ${TAGGED_IMAGE_NAME}
    cd $START_DIR
done

