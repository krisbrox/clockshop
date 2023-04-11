#!/usr/local/bin/bash

# Helper script to easily rebuild and restart local docker environment
mvn clean package
docker compose up --build $1
