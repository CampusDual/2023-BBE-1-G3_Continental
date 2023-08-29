FROM maven:3.9.1-eclipse-temurin-11-alpine
WORKDIR /app
COPY ./Continental-api ./Continental-api
COPY ./Continental-boot ./Continental-boot
COPY ./Continental-model ./Continental-model
COPY ./Continental-ws ./Continental-ws
COPY pom.xml .
EXPOSE 33333
RUN mvn install -DskipTests=true
WORKDIR /app/Continental-boot
CMD mvn spring-boot:run