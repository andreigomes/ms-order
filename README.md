# Microserviço de Pedidos - Seguradora

Este microserviço foi desenvolvido seguindo os princípios de **Clean Architecture** e **SOLID**, implementando um sistema de gestão de pedidos para uma seguradora.

## 🏗️ Arquitetura

O projeto foi estruturado seguindo Clean Architecture com as seguintes camadas:

- **Core/Domain**: Entidades de negócio, value objects e regras de domínio
- **Application**: Casos de uso e DTOs
- **Infrastructure**: Adaptadores para banco de dados, mensageria e API REST

### Princípios SOLID Aplicados

- **S** - Single Responsibility: Cada classe tem uma responsabilidade específica
- **O** - Open/Closed: Extensível através de interfaces, fechado para modificação
- **L** - Liskov Substitution: Implementações podem ser substituídas por suas interfaces
- **I** - Interface Segregation: Interfaces específicas para cada responsabilidade
- **D** - Dependency Inversion: Dependências invertidas através de portas e adaptadores

## 🛠️ Tecnologias Utilizadas

### Banco de Dados
- **PostgreSQL**: Escolhido por sua robustez, ACID compliance e suporte a JSON
- **H2**: Para testes automatizados

### Mensageria
- **Apache Kafka**: Para comunicação assíncrona e eventos de domínio
- **Spring Kafka**: Integração nativa com Spring Boot

### Stack Principal
- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- MapStruct para mapeamento
- Testcontainers para testes de integração

## 🚀 Como Executar

### Pré-requisitos
- Docker e Docker Compose
- Java 17+ (apenas para desenvolvimento)
- Maven 3.8+ (apenas para desenvolvimento)

### Executar com Docker Compose

```bash
# Clonar o repositório
git clone <repository-url>
cd ms-order

# Construir e executar toda a infraestrutura
docker-compose up -d

# Verificar se os serviços estão rodando
docker-compose ps
```

### Executar para Desenvolvimento

```bash
# Iniciar apenas a infraestrutura (PostgreSQL e Kafka)
docker-compose up -d postgres kafka zookeeper

# Executar a aplicação
mvn spring-boot:run
```

## 📋 Funcionalidades

### Gerenciamento de Pedidos
- ✅ Criar pedidos de seguro
- ✅ Consultar pedidos por ID
- ✅ Listar pedidos por cliente
- ✅ Listar pedidos por status
- ✅ Atualizar status dos pedidos (aprovar, rejeitar, cancelar, processar, concluir)

### Tipos de Seguro Suportados
- AUTO (Seguro Automóvel)
- HOME (Seguro Residencial)
- LIFE (Seguro de Vida)
- HEALTH (Seguro Saúde)
- TRAVEL (Seguro Viagem)
- BUSINESS (Seguro Empresarial)

### Status dos Pedidos
- PENDING (Pendente)
- PROCESSING (Processando)
- APPROVED (Aprovado)
- REJECTED (Rejeitado)
- CANCELLED (Cancelado)
- COMPLETED (Concluído)

## 🔌 APIs Disponíveis

### Criar Pedido
```http
POST /api/v1/orders
Content-Type: application/json

{
  "customerId": "CUST001",
  "insuranceType": "AUTO",
  "amount": 1500.00,
  "description": "Seguro auto para veículo modelo 2023"
}
```

### Consultar Pedido
```http
GET /api/v1/orders/{orderId}
```

### Listar Todos os Pedidos
```http
GET /api/v1/orders
```

### Listar Pedidos por Cliente
```http
GET /api/v1/orders/customer/{customerId}
```

### Listar Pedidos por Status
```http
GET /api/v1/orders/status/{status}
```

### Atualizar Status
```http
PUT /api/v1/orders/{orderId}/approve
PUT /api/v1/orders/{orderId}/reject
PUT /api/v1/orders/{orderId}/cancel
PUT /api/v1/orders/{orderId}/process
PUT /api/v1/orders/{orderId}/complete
```

## 📊 Monitoramento

A aplicação inclui endpoints de monitoramento via Spring Actuator:

- Health Check: `http://localhost:8080/actuator/health`
- Métricas: `http://localhost:8080/actuator/metrics`
- Info: `http://localhost:8080/actuator/info`

## 📨 Eventos Kafka

O sistema publica eventos nos seguintes tópicos:

- **order-events**: Eventos de criação, aprovação, rejeição, cancelamento e conclusão de pedidos

### Kafka UI
Interface web disponível em: `http://localhost:8081`

## 🧪 Testes

### Executar Testes
```bash
# Todos os testes
mvn test

# Apenas testes unitários
mvn test -Dtest="*Test"

# Apenas testes de integração
mvn test -Dtest="*IntegrationTest"
```

### Cobertura de Testes
- Testes unitários para entidades de domínio
- Testes unitários para casos de uso
- Testes de integração para APIs REST
- Testes com Testcontainers para PostgreSQL e Kafka

## 🗄️ Banco de Dados

### Estrutura das Tabelas

```sql
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    customer_id VARCHAR(255) NOT NULL,
    insurance_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### Configuração do Banco
- Host: localhost:5432
- Database: seguradora_orders
- Username: seguradora_user
- Password: seguradora_pass

## 🔧 Configuração

### Variáveis de Ambiente

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/seguradora_orders
SPRING_DATASOURCE_USERNAME=seguradora_user
SPRING_DATASOURCE_PASSWORD=seguradora_pass
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

## 📦 Build e Deploy

### Build da Aplicação
```bash
mvn clean package
```

### Build da Imagem Docker
```bash
docker build -t seguradora/ms-order:latest .
```

## 🤝 Contribuição

1. Fork do projeto
2. Criar branch para feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit das mudanças (`git commit -am 'Adiciona nova funcionalidade'`)
4. Push para branch (`git push origin feature/nova-funcionalidade`)
5. Criar Pull Request

## 📄 Licença

Este projeto está sob a licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.
