FROM adoptopenjdk/maven-openjdk11
COPY target/ws-client-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]