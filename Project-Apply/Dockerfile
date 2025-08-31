FROM openjdk:26-trixie

WORKDIR /app

COPY target/Project-Apply-0.0.1-SNAPSHOT.jar /app/stockmeatsolutions.jar

COPY src/main/resources/keystore.p12 /app/keystore.p12

ENTRYPOINT ["java", "-jar", "/app/stockmeatsolutions.jar"]
