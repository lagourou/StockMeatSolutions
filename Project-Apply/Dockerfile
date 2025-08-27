FROM openjdk:26-trixie

WORKDIR /app

COPY target/*.jar /app/stockmeatsolutions.jar

ENTRYPOINT ["java", "-jar", "/app/stockmeatsolutions.jar"]