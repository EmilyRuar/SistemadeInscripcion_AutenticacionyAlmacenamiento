# ─────────────────────────────────────────────────────────────
#  Etapa 1: compilar con Maven
# ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Instalar Maven y pre-descargar dependencias (cache de capas Docker)
COPY pom.xml .
RUN apk add --no-cache maven && mvn dependency:go-offline -q

# Compilar omitiendo tests (los tests corren en CI antes de esta etapa)
COPY src ./src
RUN mvn clean package -DskipTests -q

# ─────────────────────────────────────────────────────────────
#  Etapa 2: imagen final liviana (solo JRE)
# ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar el JAR compilado
COPY --from=build /app/target/sistema-inscripcion-1.0.0.jar app.jar

# Directorio para el Wallet de Oracle Cloud (se monta como volumen en EC2)
RUN mkdir -p /app/wallet

EXPOSE 8080

# Activar perfil de producción al iniciar
ENTRYPOINT ["java", \
            "-Dspring.profiles.active=prod", \
            "-Doracle.net.wallet_location=(SOURCE=(METHOD=FILE)(METHOD_DATA=(DIRECTORY=/app/wallet)))", \
            "-jar", "app.jar"]
