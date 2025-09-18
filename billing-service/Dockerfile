# Multi-stage build para otimizar o tamanho da imagem
# Stage 1: Build
FROM maven:3.9.4-openjdk-21-slim AS build

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivos de configuração do Maven
COPY pom.xml .

# Baixar dependências (cache layer)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Compilar aplicação
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM openjdk:21-jre-slim

# Instalar curl para health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Criar usuário não-root para segurança
RUN groupadd -r billing && useradd -r -g billing billing

# Definir diretório de trabalho
WORKDIR /app

# Copiar JAR da aplicação do stage de build
COPY --from=build /app/target/*.jar app.jar

# Criar diretório para logs
RUN mkdir -p /app/logs && chown -R billing:billing /app

# Mudar para usuário não-root
USER billing

# Expor porta da aplicação
EXPOSE 8083

# Configurar JVM para containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8083/actuator/health || exit 1

# Comando para iniciar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Labels para metadados
LABEL maintainer="TechBra Team"
LABEL service="billing-service"
LABEL version="1.0.0"
LABEL description="Microserviço de cobrança e pagamentos - TechBra E-commerce"