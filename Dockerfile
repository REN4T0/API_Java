## Estágio 1: Build (Compilação)
# ALTERADO: Temurin 25
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /code

# Copia apenas o pom.xml primeiro para cachear as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte e compila
COPY src ./src
RUN mvn clean package -DskipTests

## Estágio 2: Runtime (Execução)
# ALTERADO: Temurin 25
FROM eclipse-temurin:25-jre
WORKDIR /work/

# Copia a pasta da aplicação gerada pelo Quarkus
COPY --from=build /code/target/quarkus-app/ /work/

# Expõe a porta
EXPOSE 8080

# Comando de execução
ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]