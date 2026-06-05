# Étape 1 : Construction (Build)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .

# git On donne les droits d'exécution au script
RUN chmod +x mvnw

# On utilise le wrapper (mvnw)
RUN ./mvnw clean package -Dmaven.test.skip=true

# Étape 2 : Exécution (Run)
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/wemouv-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]