#!/bin/sh

function help() {
  echo "Preview Upload Help."
  echo
  echo "Either 1 or 2 paramters must be passed as arguments to this script."
  echo
  echo "If one paramter is passed in, it must be the QTI package name. The default upload location will be localhost:8443"
  echo "Example:"
  echo "preview-upload.sh branch-and.zip"
  echo
  echo "If 2 parameters are passed in, the IP address or DNS resolved name can be used."
  echo "Example:"
  echo "preview-upload.sh branch-and.zip https://ci.tesuto.org/service/v1/preview/upload"
  echo
  echo "Note: jq must be installed to parse the JSON in this shell script!"
}

function preview_upload() {
  # Get the token from the CI Mitre instance
  #TOKEN=$(curl -XPOST 'http://login.ci.cccmypath.org/f/token?client_id=lsi_preview_upload&client_secret=MpQ4DqYx1SOe57FaJ9zE7pmkLd1HiecWcdgitMB7rVAhS8TL5TrA8r9CkCIj6-Ud_glym9b_VC1sq7FujJiZQg&grant_type=client_credentials&response_type=token+id_token&scope=superuser+UPLOAD_PREVIEW_ASSESSMENT_PACKAGE' | jq '.access_token' -r);
  TOKEN=$(curl -XPOST 'http://login.ci.cccmypath.org/f/token?client_id=lsi_preview_upload&client_secret=MpQ4DqYx1SOe57FaJ9zE7pmkLd1HiecWcdgitMB7rVAhS8TL5TrA8r9CkCIj6-Ud_glym9b_VC1sq7FujJiZQg&grant_type=client_credentials&response_type=token+id_token' | jq '.access_token' -r);

  ENDPOINT=https://localhost:8443/service/v1/preview/upload
  # Check for a second argument and default it to localhost if not supplied
  if [ -n "${2+set}" ]
  then 
    ENDPOINT=$2
  fi

  # Make the call to the service endpoint
  curl -X POST -i -k -H"Content-Type: application/zip" -H "Authorization: Bearer $TOKEN" --data-binary @$1 $ENDPOINT
  echo
  echo "********************************************************************************"
  echo "Endpoint queried: $ENDPOINT"
  echo "********************************************************************************"
}

case "$1" in
  'help')
    help
  ;;
  *)
    preview_upload $*
#  echo "Usage $0 help|qti_package.zip|qti_package.zip https://ci.tesuto.org/service/v1/preview/upload"
esac

