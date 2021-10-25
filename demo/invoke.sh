#!/usr/bin/env bash

## get the secure greetings
curl  -v -u jlong:pw \
  http://localhost:8080/graphql \
  -H 'Content-Type: application/json' \
  --data-raw '{"query":"query { secureHello }" }'

