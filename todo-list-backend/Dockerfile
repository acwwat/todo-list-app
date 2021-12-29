### Build
FROM openjdk:11-slim-buster as build

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src src

RUN ./mvnw -B package

### Runtime
FROM openjdk:11-jre-slim-buster

# Set up user and group
RUN addgroup --system --gid 1001 spring && adduser --system -gid 1001 spring
USER spring:spring

# Set up app directory
COPY --from=build --chown=spring:spring target/todo-list-backend-*.jar /app/todo-list-backend.jar
RUN mkdir /app/h2

EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/todo-list-backend.jar"]