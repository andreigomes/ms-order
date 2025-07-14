package com.seguradora.msorder.infrastructure.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Garante que as tabelas sejam criadas antes dos testes usando SQL direto
 */
@Component
@Profile("test")
@Order(1)
public class TestDatabaseInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public TestDatabaseInitializer(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("🔧 Executando inicialização do banco via ApplicationRunner...");

        try {
            // Limpar tabela se existir (para testes limpos)
            jdbcTemplate.execute("DROP TABLE IF EXISTS orders CASCADE");

            // Criar tabela orders com schema atualizado
            jdbcTemplate.execute("""
                CREATE TABLE orders (
                    id VARCHAR(36) PRIMARY KEY,
                    customer_id VARCHAR(255) NOT NULL,
                    product_id VARCHAR(255) NOT NULL,
                    category VARCHAR(50) NOT NULL,
                    sales_channel VARCHAR(50) NOT NULL,
                    payment_method VARCHAR(50) NOT NULL,
                    total_monthly_premium_amount DECIMAL(19,2) NOT NULL,
                    insured_amount DECIMAL(19,2) NOT NULL,
                    coverages JSON,
                    assistances JSON,
                    description TEXT,
                    status VARCHAR(50) NOT NULL,
                    created_at TIMESTAMP NOT NULL,
                    updated_at TIMESTAMP NOT NULL,
                    finished_at TIMESTAMP,
                    history JSON
                )
            """);

            // Criar índices
            jdbcTemplate.execute("CREATE INDEX idx_orders_customer_id ON orders(customer_id)");
            jdbcTemplate.execute("CREATE INDEX idx_orders_status ON orders(status)");
            jdbcTemplate.execute("CREATE INDEX idx_orders_category ON orders(category)");
            jdbcTemplate.execute("CREATE INDEX idx_orders_created_at ON orders(created_at)");

            System.out.println("✅ Tabela ORDERS criada com sucesso via ApplicationRunner");

        } catch (Exception e) {
            System.err.println("❌ Erro ao inicializar banco: " + e.getMessage());
            throw new RuntimeException("Falha na inicialização do banco", e);
        }
    }
}
