FROM openjdk:17-jdk-slim

# Instalar WireMock
RUN apt-get update && apt-get install -y curl
RUN curl -o /app/wiremock-standalone.jar https://repo1.maven.org/maven2/com/github/tomakehurst/wiremock-jre8-standalone/2.35.0/wiremock-jre8-standalone-2.35.0.jar

# Criar diretório de trabalho
WORKDIR /app

# Copiar arquivos de configuração
COPY mappings/ /app/mappings/
COPY __files/ /app/__files/

# Expor porta
EXPOSE 8081

# Comando para executar o WireMock
CMD ["java", "-jar", "wiremock-standalone.jar", "--port", "8081", "--verbose"]
