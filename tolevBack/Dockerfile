# ---------- build ----------
# Java 21 para bater com <java.version>21</java.version> no pom.
FROM eclipse-temurin:21-jdk AS build

WORKDIR /build

# Dependencias numa layer propria: so refaz o download quando o pom muda,
# nao a cada alteracao de codigo.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

COPY src/ src/
RUN ./mvnw -B clean package -DskipTests


# ---------- runtime ----------
# JRE, nao JDK: a imagem final nao carrega compilador nem cache do Maven.
FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

# Usuario sem privilegios. Se a aplicacao for comprometida, o processo nao e root.
RUN groupadd --system tolev && useradd --system --gid tolev --home /app tolev

# Wildcard em vez do nome fixo: sobrevive a um bump de versao no pom.
COPY --from=build --chown=tolev:tolev /build/target/*.jar app.jar

USER tolev

EXPOSE 8080

# sh -c para que JAVA_OPTS (limite de heap no App Service) seja expandido.
# exec para que o java vire PID 1 e receba o SIGTERM do shutdown graceful.
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
