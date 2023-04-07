#!/usr/local/bin/bash

mvn clean package
docker compose up --build $1
