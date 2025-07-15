# Scripts para Build e Deploy

## Pré-requisitos
- Java 17+
- Maven 3.8+
- Docker e Docker Compose

## Comandos de Build

### Compilar o projeto
```bash
mvn clean compile
```

### Executar testes
```bash
mvn test
```

### Gerar JAR
```bash
mvn clean package
```

### Pular testes durante o build
```bash
mvn clean package -DskipTests
```

## Comandos Docker

### Build da imagem
```bash
docker build -t seguradora/ms-order:latest .
```

### Executar com Docker Compose
```bash
# Subir toda a infraestrutura
docker-compose up -d

# Ver logs da aplicação
docker-compose logs -f ms-order

# Parar todos os serviços
docker-compose down

# Parar e remover volumes
docker-compose down -v
```

## Comandos de Desenvolvimento

### Executar apenas infraestrutura
```bash
docker-compose up -d postgres kafka zookeeper kafka-ui
```

### Executar aplicação local
```bash
mvn spring-boot:run
```

## Verificação de Saúde

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Métricas
```bash
curl http://localhost:8080/actuator/metrics
```

## Testes da API

### Criar pedido
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST001",
    "insuranceType": "AUTO",
    "amount": 1500.00,
    "description": "Seguro auto para veículo modelo 2023"
  }'
```

### Consultar pedido
```bash
curl http://localhost:8080/api/v1/orders/{orderId}
```

### Aprovar pedido
```bash
curl -X PUT http://localhost:8080/api/v1/orders/{orderId}/approve
```
