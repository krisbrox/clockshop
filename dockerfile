FROM amazoncorretto:17.0.6-al2023
COPY "target/clockshop-0.0.1-SNAPSHOT.jar" app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
