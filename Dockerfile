# Dockerfile para o microserviço de pedidos
FROM openjdk:17-jdk-slim

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivo JAR
COPY target/ms-order-*.jar app.jar

# Expor porta da aplicação
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
