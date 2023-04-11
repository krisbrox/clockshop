README
---

This repository contains a spring boot web server application implementing the api for a watchmaker's shop.
Uses maven to manage dependencies and building, and docker for integration test dependencies as well as 
for running the app and dependencies in a local test environment.

The dockerfile specifies an arm-compliant base image, which might have to be swapped out
to run on another architecture (this has not been tested).

Run the tests using ````mvn clean verify````, or import to your IDE of choice (this author used JetBrains Intellij IDEA)

To run the application along with required dependencies (the postgres db), run ````docker compose up````. Requires port 8080 and 5432 (modify docker-compose.yml if these ports are not available)

One endpoint is implemented, ```/checkout```, which accepts a json-formatted list of product ids and returns the total price (applying any applicable discounts).
Example request: 
````
curl --location --request POST 'http://localhost:8080/checkout' \
--header 'Content-Type: application/json' \
--data-raw '[
1,1,1,2,3,4
]'
````
Response:
````
{"price":360}
````

## Thoughts

The approach taken was to set up a spring boot web application with the configuration necessary
to connect to a postgres database, adding the structure necessary to easily test and run the
application as development proceeded. 

The resulting structure is far from perfect, but this author believes the road from the current
state of the repository to a cloud-deployed production ready service is not overly long. Some
quirks/details of Spring/Spring Boot's dependency management structure are unfortunately still
not properly understood by the developer, and as a result there are certainly improvements to 
be made, especially in the integration test configuration.

The app is of very simple, but is intended to be a reasonable starting point for a 
hypothetical real application. Most features of a real web server api should be present, and
extending the service with more functionality should not present much difficulty.
