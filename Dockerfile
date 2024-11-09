FROM maven:3-amazoncorretto-21 AS build

COPY src /app/src
COPY pom.xml /app

WORKDIR /app
RUN mvn clean install

FROM amazoncorretto:21

COPY --from=build /app/target/*.jar /app/app.jar

WORKDIR /app

ENV DATABASE_URL=${DATABASE_URL}
ENV DATABASE_USER=${DATABASE_USER}
ENV DATABASE_PASSWORD=${DATABASE_PASSWORD}
ENV S3_ACCESSKEY=${S3_ACCESSKEY}
ENV S3_SECRETACCESSKEY=${S3_SECRETACCESSKEY}
ENV APPLICATION_BUCKET_NAME=${APPLICATION_BUCKET_NAME}
ENV SIGNATURE_SERVICE_URL=${SIGNATURE_SERVICE_URL}

EXPOSE 8082

CMD ["java", "-jar", "app.jar"]