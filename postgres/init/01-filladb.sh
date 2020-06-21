#!/bin/bash

# Immediately exits if any error occurs during the script
# execution. If not set, an error could occur and the
# script would continue its execution.
set -o errexit


# Creating an array that defines the environment variables
# that must be set. This can be consumed later via arrray
# variable expansion ${REQUIRED_ENV_VARS[@]}.
readonly REQUIRED_ENV_VARS=(
  "FILLA_DB_USER"
  "FILLA_DB_PASSWORD"
  "FILLA_DB_DATABASE"
  "POSTGRES_USER")


# Main execution:
# - verifies if all environment variables are set
# - runs the SQL code to create user and database
main() {
  check_env_vars_set
  init_user_and_db
}


# Checks if all of the required environment
# variables are set. If one of them isn't,
# echoes a text explaining which one isn't
# and the name of the ones that need to be
check_env_vars_set() {
  for required_env_var in ${REQUIRED_ENV_VARS[@]}; do
    if [[ -z "${!required_env_var}" ]]; then
      echo "Error:
    Environment variable '$required_env_var' not set.
    Make sure you have the following environment variables set:
      ${REQUIRED_ENV_VARS[@]}
Aborting."
      exit 1
    fi
  done
}


# Performs the initialization in the already-started PostgreSQL
# using the preconfigured POSTGRE_USER user.
init_user_and_db() {
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    create database tesuto ENCODING='UTF8';
    create user tesuto with password 'tesuto_admin';
    grant all privileges on database "tesuto" to tesuto;
    create database tesuto_placement ENCODING='UTF8';
    create user tesuto_placement with password 'tesuto_placement';
    grant all privileges on database "tesuto_placement" to tesuto_placement;
    create database tesuto_admin ENCODING='UTF8';
    create user tesuto_admin with password 'tesuto_admin';
    grant all privileges on database "tesuto_admin" to tesuto_admin;
    create database tesuto_content ENCODING='UTF8';
    create user tesuto_content with password 'tesuto_content';
    grant all privileges on database "tesuto_content" to tesuto_content;
    create database tesuto_delivery ENCODING='UTF8';
    create user tesuto_delivery with password 'tesuto_delivery';
    grant all privileges on database "tesuto_delivery" to tesuto_delivery;
    create database tesuto_activation ENCODING='UTF8';
    create user tesuto_activation with password 'tesuto_activation';
    grant all privileges on database "tesuto_activation" to tesuto_activation;
    create database tesuto_reports ENCODING='UTF8';
    create user tesuto_reports with password 'tesuto_reports';
    grant all privileges on database "tesuto_reports" to tesuto_reports;
EOSQL
}

# Executes the main routine with environment variables
# passed through the command line. We don't use them in
# this script but now you know 
main "$@"