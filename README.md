# MS-ORDER - Microsserviço de Pedidos de Seguro

Sistema para gerenciamento de solicitações de apólices de seguro, desenvolvido seguindo os princípios de Clean Architecture, Domain-Driven Design (DDD) e Arquitetura Hexagonal (Ports & Adapters).

---

## 🏗️ Arquitetura e Padrões Utilizados

- **Arquitetura Hexagonal (Ports & Adapters)**: Separação clara entre domínio, casos de uso e infraestrutura, facilitando testes, manutenção e evolução.
- **Domain-Driven Design (DDD)**: Entidades ricas, Value Objects imutáveis, regras de negócio centralizadas no domínio.
- **Clean Architecture**: Camadas bem definidas, dependências sempre apontando para o domínio.
- **Event-Driven**: Comunicação assíncrona via Kafka para integração com outros serviços.
- **Cache com Evict**: Uso de cache para performance, com limpeza automática a cada alteração de status.
- **Controle de Concorrência**: Versionamento otimista (campo version) para evitar conflitos em atualizações concorrentes.
- **Testcontainers**: Testes de integração reais com PostgreSQL e Kafka em containers.
- **Flyway**: Versionamento de schema do banco de dados.

---

## 🚀 Funcionalidades Implementadas

### 1. API REST para Solicitações de Apólice
- **POST** `/api/v1/orders` - Criar nova solicitação
- **GET** `/api/v1/orders/{id}` - Buscar por ID
- **GET** `/api/v1/orders/customer/{customerId}` - Buscar por cliente
- **PUT** `/api/v1/orders/{id}/cancel` - Cancelar solicitação

### 2. Integração com API de Fraudes (Mock Wiremock)
- **Consulta de risco**: Chamada HTTP para mock configurado via Wiremock
- **IDs disponíveis para consulta**: 1001, 1002, 1003, 1004 (veja exemplos abaixo)

### 3. Regras de Validação por Risco
- **Validação de valor e risco**: RECEIVED → VALIDATED → PENDING → APPROVED
- **Rejeição direta**: Para valores acima do limite permitido

### 4. Persistência e Versionamento
- **Banco**: PostgreSQL (produção e testes)
- **Flyway**: Versionamento automático do schema
- **JPA/Hibernate**: ORM
- **Testcontainers**: Testes de integração reais

### 5. Sistema de Eventos Kafka
- **Tópicos produzidos**: `order-events`
- **Tópicos consumidos**: `payment-events`, `subscription-events`
- **Eventos**: ORDER_CREATED, ORDER_VALIDATED, ORDER_PENDING, ORDER_APPROVED, ORDER_REJECTED, ORDER_CANCELLED

### 6. Endpoints Manuais para Teste de Eventos
- **POST** `/api/v1/manual-events/payment` - Publica evento de pagamento manual
- **POST** `/api/v1/manual-events/subscription` - Publica evento de subscrição manual

---

## 🧪 Testes Automatizados
- **Cobertura**: +400 testes automatizados (unitários e integração)
- **Testes de Integração**: Usam Testcontainers para PostgreSQL e Kafka reais
- **Destaque**: `shouldCompleteFullOrderFlowWithApproval` (CompleteIntegrationTest)
  - Cria pedido, simula aprovação de pagamento e subscrição, verifica publicação de eventos e status final APPROVED.

### Como rodar os testes de integração

```sh
mvn clean test
```

- É necessário Docker rodando para os testes de integração.
- O teste `shouldCompleteFullOrderFlowWithApproval` cobre todo o ciclo de vida do pedido.

---

## 🔎 Exemplos de Consulta

### Criar Pedido
```sh
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "1001",
    "productId": "PROD001",
    "category": "AUTO",
    "salesChannel": "WEB_SITE",
    "paymentMethod": "CREDIT_CARD",
    "totalMonthlyPremiumAmount": 1000.00,
    "insuredAmount": 50000.00,
    "coverages": {"Roubo": 20000.00, "Perda Total": 30000.00},
    "assistances": ["Guincho até 250km"]
  }'
```

### Consultar Pedido por ID
```sh
curl http://localhost:8080/api/v1/orders/{id}
```

### Consultar Pedidos por Cliente
```sh
curl http://localhost:8080/api/v1/orders/customer/1001
```

### Aprovar Pagamento Manualmente
```sh
curl -X POST http://localhost:8080/api/v1/manual-events/payment \
  -H "Content-Type: application/json" \
  -d '{"orderId": "<orderId>", "status": "APPROVED", "reason": "Manual test"}'
```

### Aprovar Subscrição Manualmente
```sh
curl -X POST http://localhost:8080/api/v1/manual-events/subscription \
  -H "Content-Type: application/json" \
  -d '{"orderId": "<orderId>", "status": "APPROVED", "reason": "Manual test"}'
```


## 🔄 Outros Endpoints de Troca de Status

- **PUT** `/api/v1/orders/{orderId}/approve` - Aprovar solicitação
- **PUT** `/api/v1/orders/{orderId}/reject` - Rejeitar solicitação
- **PUT** `/api/v1/orders/{orderId}/cancel` - Cancelar solicitação
- **PUT** `/api/v1/orders/{orderId}/pending` - solicitação Pendente

---


## 🧑‍💻 Princípios SOLID Aplicados

- **S - Single Responsibility Principle**: Cada classe tem uma única responsabilidade (ex: OrderService só lida com regras de pedido).
- **O - Open/Closed Principle**: Casos de uso e entidades são abertos para extensão, fechados para modificação (ex: novas regras de negócio via polimorfismo).
- **L - Liskov Substitution Principle**: Interfaces e abstrações permitem substituição sem quebrar o sistema.
- **I - Interface Segregation Principle**: Ports (interfaces) são específicas para cada caso de uso.
- **D - Dependency Inversion Principle**: Domínio depende de abstrações, nunca de implementações concretas.

### Exemplo de Classe Aplicando SOLID
```java
// Exemplo simplificado
@Service
public class OrderService {
    private final OrderRepositoryPort orderRepository;
    private final OrderEventPublisherPort eventPublisher;
    // SRP: só lida com regras de pedido
    // DIP: depende de interfaces, não de implementações
    public OrderService(OrderRepositoryPort orderRepository, OrderEventPublisherPort eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }
    public void approveOrder(String orderId) {
        var order = orderRepository.findById(OrderId.of(orderId)).orElseThrow();
        order.approve();
        orderRepository.save(order);
        eventPublisher.publishOrderApproved(order);
    }
}
```

## 🔒 Controle de Concorrência Otimista

O campo `version` implementa o controle de concorrência otimista (optimistic locking) no banco de dados, utilizando JPA/Hibernate:

- O campo `version` (do tipo Long) é incrementado automaticamente a cada atualização da entidade Order.
- Ao tentar salvar uma entidade, o Hibernate inclui o valor atual do `version` na cláusula WHERE do UPDATE.
- Se outro processo/usuário já tiver alterado a mesma entidade (e incrementado o `version`), o UPDATE não encontra nenhum registro para atualizar (pois o version não bate).
- O Hibernate então lança uma `OptimisticLockingFailureException`.
- O método é anotado com `@Retryable` para tentar novamente em caso de concorrência, evitando perda de dados ou sobrescrita indevida.

**Resumo:** O `version` garante que duas transações concorrentes não sobrescrevam dados uma da outra sem perceber. Se houver conflito, uma delas falha e pode tentar novamente, garantindo integridade dos dados.

---

## 🏛️ Clean Architecture

O projeto segue os princípios da Clean Architecture, separando responsabilidades em camadas bem definidas:

- **Domain (Domínio):**
  - Contém entidades, value objects, regras de negócio e interfaces (ports) do domínio.
  - Exemplo: `Order`, `OrderStatus`, `OrderRepositoryPort`, `OrderEventPublisherPort`.

- **Application (Aplicação):**
  - Casos de uso (use cases) que orquestram as regras de negócio e coordenam as operações entre domínio e infraestrutura.
  - Exemplo: `CreateOrderService`, `UpdateOrderStatusUseCase`, `GetOrderService`.

- **Infrastructure (Infraestrutura):**
  - Implementações técnicas de persistência, mensageria, integrações externas, web, cache, etc.
  - Exemplo: `OrderPersistenceAdapter`, `OrderEventPublisherAdapter`, `FraudAnalysisAdapter`, controllers REST, configurações de cache, métricas, tracing, etc.

- **Adapters (Adaptadores):**
  - Pontes entre a aplicação e o mundo externo (REST, Kafka, banco de dados, serviços externos).
  - Exemplo: Controllers REST, Consumers/Producers Kafka, Adapters de persistência e integrações.

### Benefícios
- **Baixo acoplamento:** Domínio não depende de frameworks ou detalhes de infraestrutura.
- **Alta testabilidade:** Casos de uso e domínio podem ser testados isoladamente.
- **Facilidade de manutenção e evolução:** Mudanças em tecnologia ou integrações não afetam o núcleo do domínio.

### Exemplo de Fluxo
1. **Controller REST** recebe requisição e converte para DTO.
2. **Mapper** converte DTO para entidade de domínio.
3. **Use Case** executa lógica de negócio, consulta/adapta dados via ports.
4. **Adapters** implementam ports para persistência, mensageria, etc.
5. **Resposta** é convertida de entidade para DTO e retornada ao cliente.

---

## 🛠️ Problemas Resolvidos
- Concorrência em atualização de status (versionamento otimista)
- Cache sincronizado com o banco (evict automático)
- Testes de integração confiáveis com Testcontainers
- Mock de API de fraudes via Wiremock
- Fluxo de eventos robusto e auditável

---

## ☁️ Configuração dos Tópicos Kafka

- Os tópicos Kafka são criados automaticamente pela aplicação Spring Boot se a configuração `KAFKA_AUTO_CREATE_TOPICS_ENABLE` estiver como `true` (default no docker-compose).
- Para ambientes de produção, recomenda-se criar os tópicos manualmente com configurações específicas de partições e replicação, usando scripts ou comandos do Kafka CLI.

---

## 🌐 Ambientes e Perfis de Configuração

- Perfis disponíveis: `local`, `prod`.
- Para rodar localmente, utilize o `application-local.yml`.
- O Spring Boot seleciona o perfil via variável de ambiente `SPRING_PROFILES_ACTIVE`.

---

## 📊 Métricas e Observabilidade

- **Prometheus**: Métricas expostas automaticamente no endpoint:
  - `GET /actuator/prometheus`

Exemplo de uso:

Acesse http://localhost:8080/actuator/prometheus para visualizar as métricas da aplicação e integrar com Prometheus/Grafana.

---

## ⚠️ Limitações Conhecidas

- Em cenários de concorrência extrema, pode haver tentativas de gravação concorrente que resultarão em exceção otimista (tratada com retry).
- O mock da API de fraudes (Wiremock) deve estar ativo para simulação dos fluxos de risco.
- O cache pode causar atraso na visualização do status atualizado se não for invalidado corretamente.

---

## 🤝 Contato

- Contato: andrei
