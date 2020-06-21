#!/bin/sh

# This code ignores https self signed certificates, which is good because that's what we use for local development :-)
# It queries for an Open ID token from the provider and uses that temporary token to make an upload to validate an
# assessment package.  Start up Assess and issue something like this:
# ./token-test.sh ../../../../tesuto-web/src/test/resources/selenium/assessmentItems/branch-and.zip
# It will indicate success with an HTTP 200 and assessmentSessionUrls among other values.

TOKEN=$(curl 'mitre.k8s.portal.ccctcportal.org/f/token?client_id=client&client_secret=secret&grant_type=client_credentials&response_type=token+id_token&scope=superuser' | jq '.access_token' -r);
curl "https://localhost:8443/service/v1/preview/upload" -H "Authorization: Bearer $TOKEN" -XPOST -F name=$1 -F file=@$1 -v -k