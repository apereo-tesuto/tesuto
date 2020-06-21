#!/bin/bash
#
# the script can be run while settins the following arguments:
#   -Daws.accessKeyId= AWS key to access dynamodb data, need proper permissions to read dynamoDB
#   -Daws.secretKey= AWS key to access dynamodb data, need proper permissions to write dynamoDB
#   -Daws.tablePrefix= a table prefix
#   -Dseed.data.aws.endpoint= emdpoint of the dynamoDB, default comes from application.properties
#   -Dignore.dynamodb.tables= a commas separated list of table names that should be excluded from export,
#           default value: ci-credential-store, credential-store
#
# example executions:
#  export local data to json files
#  >./scripts/dynamo-db-archive.sh dynamodb_export -Dseed.data.store=true -Dseed.data.aws.endpoint=http://localhost:8000
#
#  import drools-editor-rule* table
#  >./scripts/dynamo-db-archive.sh dynamodb_import -Dseed.data.store=true  -Dseed.data.aws.endpoint=
#      -Daws.accessKeyId=<access key> -Daws.secretKey=<secret key> -Daws.tablePrefix=test
#      -Dignore.dynamodb.tables=credential-store,variable-set,student-multiple-measures,drools-editor-application,drools-editor-college


dynamodb_export () {
  cd $TESUTO_HOME/tesuto-rules-services/tesuto-rules-core &&
  echo "Exporting all dynamo db tables with args $1 to current folder" &&
  mvn test -U -Dtest=DynamoDbRawSeedDataTest#exportAllTables  -Dseed.data.store=true $1 &&
  if [ $? -ne 0 ]; then echo "ERROR"; cd $TESUTO_HOME; exit 1; fi
  cd $TESUTO_HOME
}

dynamodb_import() {
  cd $TESUTO_HOME/tesuto-rules-services/tesuto-rules-core &&
  echo "Importing all file in the current folder into dynamo db tables with args $1" &&
  mvn test -Dtest=DynamoDbRawSeedDataTest#populateAllTables  -Dseed.data.store=true $1 &&
  if [ $? -ne 0 ]; then echo "ERROR"; cd $TESUTO_HOME; exit 1; fi
  cd $TESUTO_HOME
}

case $1 in
  dynamodb_export) dynamodb_export "${*:2}"
	;;
  dynamodb_import) dynamodb_import "${*:2}"
	;;
  help) help
	;;
	*)
	;;
esac
