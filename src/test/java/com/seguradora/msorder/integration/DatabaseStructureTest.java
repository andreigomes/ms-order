package com.seguradora.msorder.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

/**
 * Teste para verificar estrutura do banco de dados
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class DatabaseStructureTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("orders_test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(false)
            .withTmpFs(Map.of("/var/lib/postgresql/data", "rw"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
        registry.add("spring.flyway.clean-disabled", () -> "false");
        registry.add("spring.flyway.baseline-on-migrate", () -> "true");
        registry.add("spring.flyway.validate-on-migrate", () -> "false");

        // Logs para debug
        registry.add("logging.level.org.flywaydb", () -> "DEBUG");
        registry.add("logging.level.org.hibernate.SQL", () -> "DEBUG");
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldVerifyDatabaseStructure() {
        System.out.println("=== VERIFICANDO ESTRUTURA DO BANCO ===");

        // Verificar se a tabela orders existe
        String checkTableSql = "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'orders')";
        Boolean tableExists = jdbcTemplate.queryForObject(checkTableSql, Boolean.class);
        System.out.println("Tabela 'orders' existe: " + tableExists);

        if (tableExists) {
            // Listar todas as colunas da tabela orders
            String columnsSql = """
                SELECT column_name, data_type, is_nullable 
                FROM information_schema.columns 
                WHERE table_name = 'orders' 
                ORDER BY ordinal_position
                """;

            List<Map<String, Object>> columns = jdbcTemplate.queryForList(columnsSql);
            System.out.println("\nColunas da tabela 'orders':");
            for (Map<String, Object> column : columns) {
                System.out.println("  - " + column.get("column_name") +
                                 " (" + column.get("data_type") +
                                 ", nullable: " + column.get("is_nullable") + ")");
            }

            // Verificar especificamente as colunas problemáticas
            String checkPaymentApprovedSql = """
                SELECT EXISTS (
                    SELECT FROM information_schema.columns 
                    WHERE table_name = 'orders' AND column_name = 'payment_approved'
                )
                """;
            Boolean paymentApprovedExists = jdbcTemplate.queryForObject(checkPaymentApprovedSql, Boolean.class);
            System.out.println("\nColuna 'payment_approved' existe: " + paymentApprovedExists);

            String checkSubscriptionApprovedSql = """
                SELECT EXISTS (
                    SELECT FROM information_schema.columns 
                    WHERE table_name = 'orders' AND column_name = 'subscription_approved'
                )
                """;
            Boolean subscriptionApprovedExists = jdbcTemplate.queryForObject(checkSubscriptionApprovedSql, Boolean.class);
            System.out.println("Coluna 'subscription_approved' existe: " + subscriptionApprovedExists);
        }

        // Verificar histórico do Flyway
        String flywayHistorySql = """
            SELECT version, description, success, checksum, installed_on 
            FROM flyway_schema_history 
            ORDER BY installed_rank
            """;

        try {
            List<Map<String, Object>> flywayHistory = jdbcTemplate.queryForList(flywayHistorySql);
            System.out.println("\nHistórico do Flyway:");
            for (Map<String, Object> migration : flywayHistory) {
                System.out.println("  - Versão: " + migration.get("version") +
                                 ", Descrição: " + migration.get("description") +
                                 ", Sucesso: " + migration.get("success") +
                                 ", Instalado em: " + migration.get("installed_on"));
            }
        } catch (Exception e) {
            System.out.println("Erro ao consultar histórico do Flyway: " + e.getMessage());
        }

        System.out.println("\n=== FIM DA VERIFICAÇÃO ===");
    }
}
