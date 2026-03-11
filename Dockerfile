FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Paso 1: Instalar Maven — solo se ejecuta 1 vez, queda en caché
RUN apk add --no-cache maven

# Paso 2: Copiar SOLO el pom.xml y descargar las dependencias
# Esta capa se re-ejecuta ÚNICAMENTE si modificas el pom.xml
# (cosa que casi nunca haces)
COPY pom.xml .
RUN mvn dependency:resolve

# Paso 3: Ahora sí copiar el código y compilar
# Esta es la ÚNICA capa que se re-ejecuta cuando cambias un .java
COPY src ./src
RUN mvn package -DskipTests

# --- Imagen final liviana (solo JRE, sin Maven ni código fuente) ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]