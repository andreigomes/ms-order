package com.seguradora.msorder.core.domain.valueobject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.HashMap;

/**
 * Value Object para representar o histórico de alterações de estado de um pedido
 */
public class OrderHistory {
    private final List<HistoryEntry> entries;

    private OrderHistory(List<HistoryEntry> entries) {
        this.entries = Collections.unmodifiableList(Objects.requireNonNull(entries, "History entries cannot be null"));
    }

    public static OrderHistory empty() {
        return new OrderHistory(new ArrayList<>());
    }

    public static OrderHistory of(List<HistoryEntry> entries) {
        return new OrderHistory(new ArrayList<>(entries));
    }

    public OrderHistory addEntry(OrderStatus fromStatus, OrderStatus toStatus, String reason) {
        List<HistoryEntry> newEntries = new ArrayList<>(this.entries);
        newEntries.add(new HistoryEntry(fromStatus, toStatus, reason, LocalDateTime.now()));
        return new OrderHistory(newEntries);
    }

    public List<HistoryEntry> getEntries() {
        return entries;
    }

    public List<Map<String, Object>> toJson() {
        return entries.stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("fromStatus", entry.getFromStatus() != null ? entry.getFromStatus().name() : null);
                    map.put("toStatus", entry.getToStatus().name());
                    map.put("reason", entry.getReason());
                    map.put("timestamp", entry.getTimestamp().toString());
                    return map;
                })
                .toList();
    }

    public static OrderHistory fromJson(List<Map<String, Object>> jsonList) {
        if (jsonList == null || jsonList.isEmpty()) {
            return empty();
        }

        List<HistoryEntry> entries = jsonList.stream()
                .map(map -> {
                    OrderStatus fromStatus = map.get("fromStatus") != null ?
                            OrderStatus.valueOf((String) map.get("fromStatus")) : null;
                    OrderStatus toStatus = OrderStatus.valueOf((String) map.get("toStatus"));
                    String reason = (String) map.get("reason");
                    LocalDateTime timestamp = LocalDateTime.parse((String) map.get("timestamp"));
                    return new HistoryEntry(fromStatus, toStatus, reason, timestamp);
                })
                .toList();

        return new OrderHistory(entries);
    }

    public static class HistoryEntry {
        private final OrderStatus fromStatus;
        private final OrderStatus toStatus;
        private final String reason;
        private final LocalDateTime timestamp;

        public HistoryEntry(OrderStatus fromStatus, OrderStatus toStatus, String reason, LocalDateTime timestamp) {
            this.fromStatus = fromStatus;
            this.toStatus = Objects.requireNonNull(toStatus, "To status cannot be null");
            this.reason = reason;
            this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        }

        // Getters
        public OrderStatus getFromStatus() { return fromStatus; }
        public OrderStatus getToStatus() { return toStatus; }
        public String getReason() { return reason; }
        public LocalDateTime getTimestamp() { return timestamp; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HistoryEntry that = (HistoryEntry) o;
            return Objects.equals(fromStatus, that.fromStatus) &&
                   Objects.equals(toStatus, that.toStatus) &&
                   Objects.equals(reason, that.reason) &&
                   Objects.equals(timestamp, that.timestamp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fromStatus, toStatus, reason, timestamp);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderHistory that = (OrderHistory) o;
        return Objects.equals(entries, that.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entries);
    }
}
