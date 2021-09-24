#!/bin/bash

validateYaml() {
  ruby -e "require 'yaml';puts YAML.load_file('/tmp/param.values')"
  if [ $? -ne 0 ]; then
    echo "Failed to create valid yaml file in /tmp/param.values"
    sleep 300 # Prevent fast cycling of the container
    exit 1
  fi
}

# Adjust properties according to the environment
if [ -n "$AWS_ENV" ]; then
  env | sed -e 's/=\(.*\)/: '"'"'\1'"'"'/' > /tmp/param.values
  if [ -f "/tmp/stack.data" ]; then
    cat /tmp/stack.data >> /tmp/param.values
  fi

  for param in $(aws --region=us-west-2 ssm get-parameters-by-path \
                     --path /${AWS_ENV} --query 'Parameters[*].{Param: Name}' \
                     --output text); do
    value=$(aws --region=us-west-2 ssm get-parameter --name $param \
                --with-decryption --query  'Parameter.Value' --output text)
    echo "$param: '$value'" | sed -e "s@^/${AWS_ENV}/@@" >> /tmp/param.values
  done

  validateYaml
  mustache-replace /tmp/param.values /opt/ccc/config
  credstash-replace -e ${AWS_ENV} -s /opt/ccc/config -d /opt/ccc/config -n /tmp/notify
fi

COM_STRING="-jar /opt/ccc/tesuto-placement.jar --spring.config.location=file:/opt/ccc/config/config.properties"
COM_STRING="$JAVA_OPTS $COM_STRING"

echo "Using $COM_STRING"

# Launch the service
java $COM_STRING
