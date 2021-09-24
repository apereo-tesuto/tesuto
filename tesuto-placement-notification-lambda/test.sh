#!/bin/bash
export ROUTER_HOST=service-router.qa.ccctechcenter.org
#export CLIENT_ID=course-exchange-college-adaptor-client
#export CLIENT_SECRET=bde935dd-40da-4991-b002-d2c0a6901749
export CLIENT_ID=gateway-client-tester
export CLIENT_SECRET=2b6c3a6c-364f-4504-a9ee-e461ce27d79e
export ACCESS_TOKEN_URI=https://service-router.qa.ccctechcenter.org:443/token/v1/token
export QUEUE_URL=https://sqs.us-east-1.amazonaws.com/181246307823/test
export REGION=us-east-1
java -cp `find . -name mmi-placement-notification-lambda.jar` org.cccnext.tesuto.placement.lambda.InvocationHandler

https://service-router.qa.ccctechcenter.org/token/v1/token
client id = gateway-client-tester

secret = 2b6c3a6c-364f-4504-a9ee-e461ce27d79e
