# Etapa de build: usa a imagem do JDK apenas para executar o Maven Wrapper.
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

COPY src ./src
RUN ./mvnw -B clean package -DskipTests

# Etapa de execução: imagem JRE enxuta, com usuário não-root.
FROM eclipse-temurin:21-jre

RUN groupadd --system app && useradd --system --gid app app

WORKDIR /app

COPY --from=build --chown=app:app /app/target/*.jar app.jar

USER app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
