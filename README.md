# MS-ORDER - Microsserviço de Pedidos de Seguro

Sistema para gerenciamento de solicitações de apólices de seguro, desenvolvido seguindo os princípios de Clean Architecture e Domain-Driven Design.

## 🏗️ Arquitetura

O projeto utiliza **Arquitetura Hexagonal (Ports & Adapters)** com as seguintes camadas:

### Core (Domínio)
- **Entities**: `Order` - Entidade principal do domínio
- **Value Objects**: `OrderStatus`, `InsuranceType`, `RiskLevel` - Objetos de valor imutáveis
- **Use Cases**: Casos de uso da aplicação (CreateOrder, GetOrder, ProcessPayment, etc.)
- **Ports**: Interfaces que definem contratos (In/Out ports)

### Infrastructure (Infraestrutura)
- **Adapters In**: Controllers REST, Consumers Kafka
- **Adapters Out**: Repositórios JPA, clientes HTTP, publishers Kafka
- **Configuration**: Configurações do Spring Boot, Kafka, banco de dados

## 🚀 Funcionalidades Implementadas

### 1. ✅ API REST para Solicitações de Apólice
- **POST** `/api/v1/orders` - Criar nova solicitação
- **GET** `/api/v1/orders/{id}` - Buscar por ID
- **GET** `/api/v1/orders/customer/{customerId}` - Buscar por cliente
- **PUT** `/api/v1/orders/{id}/cancel` - Cancelar solicitação

### 2. ✅ Integração com API de Fraudes (Mock Interno)
- **Análise de Risco**: Mock interno que simula consulta a API externa
- **Classificação por Valor e Tipo**: Baseado no valor da apólice e tipo de seguro
- **Níveis de Risco**:
  - **HIGH_RISK**: Valor > R$ 500.000 - Requer análise manual
  - **REGULAR**: Valor entre R$ 100.000 - R$ 500.000 - Processamento padrão
  - **PREFERENTIAL**: Valor entre R$ 50.000 - R$ 100.000 - Cliente premium
  - **NO_INFO**: Valor < R$ 50.000 - Informações insuficientes

### 3. ✅ Regras de Validação por Risco
- **Validação de Valor**: Limites baseados no nível de risco do cliente
- **Fluxo Otimizado**: RECEIVED → VALIDATED → PENDING (se aprovado)
- **Rejeição Direta**: Para valores acima do limite permitido

### 4. ✅ Persistência em Banco de Dados
- **PostgreSQL** para produção (via Docker)
- **Flyway** para versionamento de schema
- **JPA/Hibernate** para mapeamento objeto-relacional
- **Testcontainers** para testes de integração

### 5. ✅ Sistema de Eventos Kafka

#### 📤 Tópicos de Produção (Outbound)
**Tópico**: `order-events`

Eventos publicados pelo ms-order:
- `ORDER_CREATED` - Pedido criado (estado RECEIVED)
- `ORDER_VALIDATED` - Passou na análise de fraudes
- `ORDER_PENDING` - Aguardando aprovação de pagamento e subscrição
- `ORDER_APPROVED` - Totalmente aprovado para emissão
- `ORDER_REJECTED` - Rejeitado por qualquer motivo
- `ORDER_CANCELLED` - Cancelado pelo cliente

**Exemplo de mensagem produzida**:
```json
{
  "eventType": "ORDER_CREATED",
  "orderId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "customerId": "12345",
  "status": "RECEIVED",
  "timestamp": "2025-07-14T10:30:00Z",
  "orderDetails": {
    "category": "AUTO",
    "insuredAmount": 150000.00,
    "riskLevel": "REGULAR"
  }
}
```

#### 📥 Tópicos de Consumo (Inbound)

**Tópico**: `payment-events`  
**Consumer Group**: `order-service-payment-group`

Eventos consumidos de serviços de pagamento:
```json
{
  "eventType": "PAYMENT_PROCESSED",
  "orderId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "status": "APPROVED",
  "reason": "Payment processed successfully",
  "timestamp": "2025-07-14T10:32:15Z",
  "paymentDetails": {
    "paymentId": "pay_1234567890",
    "paymentMethod": "CREDIT_CARD",
    "amount": 1200.50,
    "transactionId": "txn_abcd1234",
    "processedAt": "2025-07-14T10:32:10Z"
  }
}
```

**Tópico**: `subscription-events`  
**Consumer Group**: `order-service-subscription-group`

Eventos consumidos de serviços de subscrição:
```json
{
  "eventType": "SUBSCRIPTION_ANALYZED",
  "orderId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "status": "APPROVED",
  "reason": "Risk analysis completed - approved",
  "timestamp": "2025-07-14T10:33:20Z",
  "subscriptionDetails": {
    "subscriptionId": "sub_9876543210",
    "analyst": "John Doe",
    "analysisDate": "2025-07-14T10:33:15Z",
    "comments": "Low risk customer with good payment history"
  }
}
```

### 6. ✅ Simulador de Serviços Externos
**Simulação de Pagamento**:
- Delay configurável (padrão: 2s)
- Status: APPROVED (90%) ou REJECTED (10%)
- Geração de transaction ID

**Simulação de Subscrição**:
- Delay configurável (padrão: 3s)
- Status: APPROVED (85%) ou REJECTED (15%)
- Análise de risco simulada

### 7. ✅ Coordenação de Eventos
- **Processamento Assíncrono**: Validação e serviços externos não bloqueiam criação
- **Resilência**: Fallback para risco REGULAR em caso de falha na API de fraudes
- **Rastreabilidade**: Logs detalhados de todas as operações

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 17** - Linguagem principal
- **Spring Boot 3.2** - Framework principal
- **Spring WebFlux** - Para clientes HTTP assíncronos (consumo da API de fraudes)
- **Spring Data JPA** - Persistência
- **Spring Kafka** - Mensageria assíncrona para coordenação de eventos
- **Flyway** - Migrations de banco

### Banco de Dados
- **PostgreSQL** - Banco principal (produção e testes)

### Mensageria
- **Apache Kafka** - Sistema de eventos para coordenação entre microserviços
- **Kafka Connect** - Integração de dados

### Mock de Serviços Externos
- **WireMock** - Simulação da API de fraudes externa

### Infraestrutura
- **Docker & Docker Compose** - Containerização
- **Maven** - Gerenciamento de dependências

### Qualidade
- **JUnit 5** - Testes unitários
- **Testcontainers** - Testes de integração com PostgreSQL real
- **EmbeddedKafka** - Testes com Kafka real

## 🔄 Fluxo de Negócio

1. **Recepção**: Cliente envia solicitação → Status `RECEIVED`
2. **Análise de Fraudes**: Consulta API de fraudes para classificar risco
3. **Validação**: Aplica regras de valor baseadas no risco → Status `VALIDATED`
4. **Processamento**: Dispara serviços de pagamento e subscrição → Status `PENDING`
5. **Aprovação**: Aguarda ambos aprovarem → Status `APPROVED`
6. **Rejeição**: Qualquer falha → Status `REJECTED`
7. **Cancelamento**: Cliente pode cancelar → Status `CANCELLED`

## 🚦 Estados do Pedido

```
RECEIVED → VALIDATED → PENDING → APPROVED
    ↓          ↓          ↓
REJECTED   REJECTED   REJECTED
    ↓          ↓          ↓
CANCELLED  CANCELLED     ❌
```

## 📊 Métricas e Observabilidade

- **Logs Estruturados**: SLF4J com padrões consistentes
- **Health Checks**: Spring Actuator
- **Métricas**: Tempo de processamento, taxa de aprovação
- **Tracing**: Rastreamento de requests e eventos
