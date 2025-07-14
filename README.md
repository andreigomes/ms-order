# MS-ORDER - Microsserviço de Pedidos de Seguro

Sistema para gerenciamento de solicitações de apólices de seguro, desenvolvido seguindo os princípios de Clean Architecture e Domain-Driven Design.

## 🏗️ Arquitetura

O projeto utiliza **Arquitetura Hexagonal (Ports & Adapters)** com as seguintes camadas:

### Core (Domínio)
- **Entities**: `Order` - Entidade principal do domínio
- **Value Objects**: `OrderStatus`, `InsuranceType` - Objetos de valor imutáveis
- **Use Cases**: Casos de uso da aplicação (CreateOrder, GetOrder, etc.)
- **Ports**: Interfaces que definem contratos (In/Out ports)

### Infrastructure (Infraestrutura)
- **Adapters In**: Controllers REST (`OrderController`)
- **Adapters Out**: Repositórios JPA, clientes HTTP, publishers Kafka
- **Configuration**: Configurações do Spring Boot, Kafka, banco de dados

## 🚀 Funcionalidades Implementadas

### 1. ✅ API REST para Solicitações de Apólice
- **POST** `/api/v1/orders` - Criar nova solicitação
- **GET** `/api/v1/orders/{id}` - Buscar por ID
- **GET** `/api/v1/orders/customer/{customerId}` - Buscar por cliente
- **PUT** `/api/v1/orders/{id}/payment` - Processar pagamento
- **PUT** `/api/v1/orders/{id}/cancel` - Cancelar solicitação

### 2. ✅ Integração com API de Fraudes (Mock)
- **Análise de Risco**: Consulta API externa para classificar risco do cliente
- **Detecção de Bloqueio**: Verifica se cliente está na lista de bloqueados
- **Regras de Negócio**:
  - **REGULAR**: Perfil baixo risco, limites padrão de seguro
  - **ALTO_RISCO**: Perfil alto risco, limites reduzidos
  - **PREFERENCIAL**: Cliente premium, limites elevados
  - **SEM_INFORMACAO**: Pouco histórico, limites conservadores
  - **BLOCKED**: Cliente bloqueado → Status `REJECTED`

### 3. ✅ Persistência em Banco de Dados
- **H2** para desenvolvimento/testes
- **PostgreSQL** para produção (via Docker)
- **Flyway** para versionamento de schema
- **JPA/Hibernate** para mapeamento objeto-relacional

### 4. ✅ Sistema de Eventos (Kafka)
Publica eventos para outros serviços da cadeia:
- `ORDER_CREATED` - Pedido criado (estado RECEIVED)
- `ORDER_VALIDATED` - Passou na análise de fraudes
- `ORDER_APPROVED` - Pagamento e subscrição aprovados
- `ORDER_REJECTED` - Rejeitado por fraude, pagamento ou subscrição
- `ORDER_CANCELLED` - Pedido cancelado pelo cliente

### 5. ✅ Estados da Solicitação
Ciclo de vida baseado nas regras de negócio da seguradora:
- **RECEIVED** → Estado inicial quando solicitação é criada
- **VALIDATED** → Passou na análise de fraudes
- **PENDING** → Aguarda pagamento e subscrição
- **APPROVED** → Pronto para emissão da apólice
- **REJECTED** → Rejeitado por fraude, pagamento ou subscrição
- **CANCELLED** → Cancelado (exceto se já aprovado)

### 6. ✅ Processamento de Eventos Externos
Consome eventos dos serviços de:
- **Pagamento**: Status `APPROVED`/`REJECTED`
- **Subscrição**: Status `APPROVED`/`REJECTED`

### 7. ✅ Simulador de Serviços Externos
Para desenvolvimento e testes, simula:
- **Processamento de Pagamento**: Simula aprovação/rejeição baseada em valor
- **Análise de Subscrição**: Simula underwriting baseado em tipo e valor

## 🛠️ Tecnologias e Justificativas

### Spring Boot 3.2
**Por quê?** Framework maduro e robusto para desenvolvimento de microsserviços, com excelente suporte para:
- Injeção de dependências
- Auto-configuração
- Integração com múltiplas tecnologias
- Observabilidade e métricas

### Spring WebFlux
**Por quê?** Programação reativa para melhor performance em I/O intensivo:
- **Non-blocking I/O**: Melhor utilização de threads para chamadas externas (API de fraudes)
- **Backpressure**: Controle de fluxo quando há muitas solicitações
- **Escalabilidade**: Suporta mais conexões concorrentes com menos recursos

### Apache Kafka
**Por quê?** Sistema de messaging distribuído ideal para microsserviços:
- **Event Sourcing**: Registro completo de todas as mudanças de estado
- **Desacoplamento**: Serviços se comunicam via eventos sem conhecer uns aos outros
- **Resiliência**: Garantia de entrega e durabilidade dos eventos
- **Escalabilidade**: Suporta milhões de eventos por segundo

### PostgreSQL + H2
**Por quê?** 
- **PostgreSQL**: Banco robusto para produção com suporte a JSON, transações ACID
- **H2**: Banco em memória para testes rápidos e desenvolvimento local

### Flyway
**Por quê?** Versionamento de banco de dados:
- **Controle de Versão**: Schema evolui junto com o código
- **Rollback Seguro**: Possibilidade de reverter mudanças
- **Deploy Automatizado**: Migrações aplicadas automaticamente

### Docker + Docker Compose
**Por quê?** Containerização para desenvolvimento e produção:
- **Ambiente Consistente**: Mesmo ambiente em dev, test e prod
- **Orquestração**: Kafka, PostgreSQL e aplicação sobem juntos
- **Isolamento**: Dependências não conflitam entre projetos

### WireMock (Fraud API)
**Por quê?** Mock da API de fraudes externa:
- **Desenvolvimento Independente**: Não depende da API real para desenvolvimento
- **Testes Determinísticos**: Respostas controladas para cenários específicos
- **Simulação de Falhas**: Testa comportamento em caso de indisponibilidade

### Maven
**Por quê?** Gerenciamento de dependências e build:
- **Padrão da Indústria**: Amplamente adotado no ecossistema Java
- **Gestão de Dependências**: Resolve conflitos de versões automaticamente
- **Plugins**: Integração com Flyway, Docker, testes

### JUnit 5 + AssertJ + Mockito
**Por quê?** Stack de testes robusta:
- **JUnit 5**: Framework de testes moderno com melhor organização
- **AssertJ**: Assertions fluentes e legíveis
- **Mockito**: Mocking para testes unitários isolados

### Spring Boot Test + Testcontainers
**Por quê?** Testes de integração realistas:
- **Spring Boot Test**: Sobe contexto completo da aplicação
- **Embedded Kafka**: Testa integração real com Kafka
- **WebTestClient**: Testes de API reativa

## 🐳 Execução com Docker

```bash
# Subir todos os serviços
docker-compose up -d

# Verificar logs
docker-compose logs -f ms-order

# Parar serviços
docker-compose down
```

## 🧪 Testes

```bash
# Todos os testes
mvn test

# Apenas testes unitários
mvn test -Dtest="*Test"

# Apenas testes de integração
mvn test -Dtest="*IntegrationTest"

# Teste específico com Kafka
mvn test -Dtest=OrderIntegrationWithRealKafkaTest
```

## 📊 Monitoramento

### Actuator Endpoints
- `/actuator/health` - Status da aplicação
- `/actuator/metrics` - Métricas da aplicação
- `/actuator/info` - Informações da aplicação

### Logs Estruturados
- **SLF4J + Logback**: Logs estruturados em JSON para melhor observabilidade
- **Correlation ID**: Rastreamento de requisições através dos serviços
- **Levels**: DEBUG para desenvolvimento, INFO para produção

## 🔧 Configuração

### Profiles
- **default**: Desenvolvimento local com H2
- **docker**: Produção com PostgreSQL
- **test**: Testes com H2 e mocks

### Variáveis de Ambiente
```properties
# Banco de dados
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/orders
SPRING_DATASOURCE_USERNAME=orders_user
SPRING_DATASOURCE_PASSWORD=orders_pass

# Kafka
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# API de Fraudes
FRAUD_API_BASE_URL=http://fraud-api:8080
```
