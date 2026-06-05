# Étape 1 : Construction (Build)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# On copie tout le contenu du dossier wemouv dans /app
COPY . .
# On utilise le wrapper (mvnw) au lieu de mvn
RUN ./mvnw clean package -DskipTests

# Étape 2 : Exécution (Run)
FROM eclipse-temurin:21-jdk
WORKDIR /app
# On copie uniquement le .jar final généré à l'étape "build"
COPY --from=build /app/target/wemouv-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]