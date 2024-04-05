FROM openjdk:17
LABEL authors="thiago frazao"
WORKDIR /app
EXPOSE 8084
COPY target/products-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]