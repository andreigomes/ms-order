package com.seguradora.msorder.core.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderHistoryTest {

    @Test
    void shouldCreateEmptyOrderHistory() {
        // When
        OrderHistory history = OrderHistory.empty();

        // Then
        assertThat(history.getEntries()).isEmpty();
        assertThat(history.getEntries()).hasSize(0);
    }

    @Test
    void shouldCreateOrderHistoryFromList() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        OrderHistory.HistoryEntry entry = new OrderHistory.HistoryEntry(
            OrderStatus.RECEIVED, OrderStatus.VALIDATED, "Validated successfully", timestamp
        );
        List<OrderHistory.HistoryEntry> entries = List.of(entry);

        // When
        OrderHistory history = OrderHistory.of(entries);

        // Then
        assertThat(history.getEntries()).hasSize(1);
        assertThat(history.getEntries().get(0)).isEqualTo(entry);
    }

    @Test
    void shouldAddEntryToHistory() {
        // Given
        OrderHistory history = OrderHistory.empty();

        // When
        OrderHistory newHistory = history.addEntry(OrderStatus.RECEIVED, OrderStatus.VALIDATED, "Validated");

        // Then
        assertThat(newHistory.getEntries()).hasSize(1);
        assertThat(newHistory.getEntries().get(0).getToStatus()).isEqualTo(OrderStatus.VALIDATED);
        assertThat(newHistory.getEntries().get(0).getFromStatus()).isEqualTo(OrderStatus.RECEIVED);
        assertThat(newHistory.getEntries().get(0).getReason()).isEqualTo("Validated");
        assertThat(newHistory.getEntries().get(0).getTimestamp()).isNotNull();
    }

    @Test
    void shouldAddMultipleEntries() {
        // Given
        OrderHistory history = OrderHistory.empty();

        // When
        OrderHistory step1 = history.addEntry(null, OrderStatus.RECEIVED, "Order created");
        OrderHistory step2 = step1.addEntry(OrderStatus.RECEIVED, OrderStatus.VALIDATED, "Validated");
        OrderHistory step3 = step2.addEntry(OrderStatus.VALIDATED, OrderStatus.PENDING, "Pending payment");

        // Then
        assertThat(step3.getEntries()).hasSize(3);
        assertThat(step3.getEntries().get(0).getToStatus()).isEqualTo(OrderStatus.RECEIVED);
        assertThat(step3.getEntries().get(1).getToStatus()).isEqualTo(OrderStatus.VALIDATED);
        assertThat(step3.getEntries().get(2).getToStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void shouldConvertToJson() {
        // Given
        OrderHistory history = OrderHistory.empty()
            .addEntry(null, OrderStatus.RECEIVED, "Order created")
            .addEntry(OrderStatus.RECEIVED, OrderStatus.VALIDATED, "Validated");

        // When
        List<Map<String, Object>> json = history.toJson();

        // Then
        assertThat(json).hasSize(2);

        Map<String, Object> firstEntry = json.get(0);
        assertThat(firstEntry.get("fromStatus")).isNull();
        assertThat(firstEntry.get("toStatus")).isEqualTo("RECEIVED");
        assertThat(firstEntry.get("reason")).isEqualTo("Order created");
        assertThat(firstEntry.get("timestamp")).isNotNull();

        Map<String, Object> secondEntry = json.get(1);
        assertThat(secondEntry.get("fromStatus")).isEqualTo("RECEIVED");
        assertThat(secondEntry.get("toStatus")).isEqualTo("VALIDATED");
        assertThat(secondEntry.get("reason")).isEqualTo("Validated");
        assertThat(secondEntry.get("timestamp")).isNotNull();
    }

    @Test
    void shouldCreateFromJson() {
        // Given
        List<Map<String, Object>> jsonList = List.of(
            Map.of(
                "fromStatus", "RECEIVED",
                "toStatus", "VALIDATED",
                "reason", "Validated successfully",
                "timestamp", "2025-01-15T10:30:00"
            )
        );

        // When
        OrderHistory history = OrderHistory.fromJson(jsonList);

        // Then
        assertThat(history.getEntries()).hasSize(1);
        OrderHistory.HistoryEntry entry = history.getEntries().get(0);
        assertThat(entry.getFromStatus()).isEqualTo(OrderStatus.RECEIVED);
        assertThat(entry.getToStatus()).isEqualTo(OrderStatus.VALIDATED);
        assertThat(entry.getReason()).isEqualTo("Validated successfully");
        assertThat(entry.getTimestamp()).isEqualTo(LocalDateTime.parse("2025-01-15T10:30:00"));
    }

    @Test
    void shouldCreateFromJsonWithNullFromStatus() {
        // Given
        List<Map<String, Object>> jsonList = List.of(
            Map.of(
                "toStatus", "RECEIVED",
                "reason", "Order created",
                "timestamp", "2025-01-15T10:30:00"
            )
        );

        // When
        OrderHistory history = OrderHistory.fromJson(jsonList);

        // Then
        assertThat(history.getEntries()).hasSize(1);
        OrderHistory.HistoryEntry entry = history.getEntries().get(0);
        assertThat(entry.getFromStatus()).isNull();
        assertThat(entry.getToStatus()).isEqualTo(OrderStatus.RECEIVED);
    }

    @Test
    void shouldReturnEmptyFromNullJson() {
        // When
        OrderHistory history = OrderHistory.fromJson(null);

        // Then
        assertThat(history.getEntries()).isEmpty();
    }

    @Test
    void shouldReturnEmptyFromEmptyJson() {
        // When
        OrderHistory history = OrderHistory.fromJson(List.of());

        // Then
        assertThat(history.getEntries()).isEmpty();
    }

    @Test
    void shouldCreateHistoryEntry() {
        // Given
        LocalDateTime timestamp = LocalDateTime.of(2025, 1, 15, 10, 30);

        // When
        OrderHistory.HistoryEntry entry = new OrderHistory.HistoryEntry(
            OrderStatus.RECEIVED, OrderStatus.VALIDATED, "Test reason", timestamp
        );

        // Then
        assertThat(entry.getFromStatus()).isEqualTo(OrderStatus.RECEIVED);
        assertThat(entry.getToStatus()).isEqualTo(OrderStatus.VALIDATED);
        assertThat(entry.getReason()).isEqualTo("Test reason");
        assertThat(entry.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    void shouldCreateHistoryEntryWithNullFromStatus() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        OrderHistory.HistoryEntry entry = new OrderHistory.HistoryEntry(
            null, OrderStatus.RECEIVED, "Initial status", timestamp
        );

        // Then
        assertThat(entry.getFromStatus()).isNull();
        assertThat(entry.getToStatus()).isEqualTo(OrderStatus.RECEIVED);
    }

    @Test
    void shouldThrowExceptionWhenToStatusIsNull() {
        // When & Then
        assertThatThrownBy(() -> new OrderHistory.HistoryEntry(
            OrderStatus.RECEIVED, null, "reason", LocalDateTime.now()
        ))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("To status cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenTimestampIsNull() {
        // When & Then
        assertThatThrownBy(() -> new OrderHistory.HistoryEntry(
            OrderStatus.RECEIVED, OrderStatus.VALIDATED, "reason", null
        ))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Timestamp cannot be null");
    }

    @Test
    void shouldBeEqualWhenSameEntries() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        OrderHistory.HistoryEntry entry1 = new OrderHistory.HistoryEntry(
            OrderStatus.RECEIVED, OrderStatus.VALIDATED, "reason", timestamp
        );
        OrderHistory.HistoryEntry entry2 = new OrderHistory.HistoryEntry(
            OrderStatus.RECEIVED, OrderStatus.VALIDATED, "reason", timestamp
        );

        // Then
        assertThat(entry1).isEqualTo(entry2);
        assertThat(entry1.hashCode()).isEqualTo(entry2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentEntries() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        OrderHistory.HistoryEntry entry1 = new OrderHistory.HistoryEntry(
            OrderStatus.RECEIVED, OrderStatus.VALIDATED, "reason1", timestamp
        );
        OrderHistory.HistoryEntry entry2 = new OrderHistory.HistoryEntry(
            OrderStatus.RECEIVED, OrderStatus.VALIDATED, "reason2", timestamp
        );

        // Then
        assertThat(entry1).isNotEqualTo(entry2);
    }

    @Test
    void shouldRoundTripJsonConversion() {
        // Given
        OrderHistory originalHistory = OrderHistory.empty()
            .addEntry(null, OrderStatus.RECEIVED, "Created")
            .addEntry(OrderStatus.RECEIVED, OrderStatus.VALIDATED, "Validated")
            .addEntry(OrderStatus.VALIDATED, OrderStatus.PENDING, "Pending");

        // When
        List<Map<String, Object>> json = originalHistory.toJson();
        OrderHistory reconstructedHistory = OrderHistory.fromJson(json);

        // Then
        assertThat(reconstructedHistory.getEntries()).hasSize(3);
        assertThat(reconstructedHistory.getEntries().get(0).getToStatus()).isEqualTo(OrderStatus.RECEIVED);
        assertThat(reconstructedHistory.getEntries().get(1).getToStatus()).isEqualTo(OrderStatus.VALIDATED);
        assertThat(reconstructedHistory.getEntries().get(2).getToStatus()).isEqualTo(OrderStatus.PENDING);
    }
}
