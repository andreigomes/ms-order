FROM wiremock/wiremock:2.35.0

# Copia os mocks para o container
COPY mappings /home/wiremock/mappings
COPY __files /home/wiremock/__files

# Expõe a porta 8080 do WireMock
EXPOSE 8080

# Comando para iniciar o WireMock
CMD ["--port", "8080"]
