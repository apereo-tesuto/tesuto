#!/bin/sh
TOKEN=$(curl 'mitre.k8s.portal.ccctcportal.org/f/token?client_id=client&client_secret=secret&grant_type=client_credentials&response_type=token+id_token&scope=superuser' | jq '.access_token' -r);
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/springSecurity/oauth-secured