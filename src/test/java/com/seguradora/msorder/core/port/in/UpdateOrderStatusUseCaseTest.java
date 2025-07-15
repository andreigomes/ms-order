package com.seguradora.msorder.core.port.in;

import com.seguradora.msorder.core.domain.valueobject.OrderId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes para os records da interface UpdateOrderStatusUseCase
 */
class UpdateOrderStatusUseCaseTest {

    @Test
    void shouldCreateApproveOrderCommandWithValidOrderId() {
        // Given
        OrderId orderId = OrderId.generate();

        // When
        UpdateOrderStatusUseCase.ApproveOrderCommand command =
            new UpdateOrderStatusUseCase.ApproveOrderCommand(orderId);

        // Then
        assertThat(command.orderId()).isEqualTo(orderId);
        assertThat(command.orderId()).isNotNull();
    }

    @Test
    void shouldCreateApproveOrderCommandWithNullOrderId() {
        // Given
        OrderId orderId = null;

        // When
        UpdateOrderStatusUseCase.ApproveOrderCommand command =
            new UpdateOrderStatusUseCase.ApproveOrderCommand(orderId);

        // Then
        assertThat(command.orderId()).isNull();
    }

    @Test
    void shouldTestApproveOrderCommandEquality() {
        // Given
        OrderId orderId = OrderId.generate();
        UpdateOrderStatusUseCase.ApproveOrderCommand command1 =
            new UpdateOrderStatusUseCase.ApproveOrderCommand(orderId);
        UpdateOrderStatusUseCase.ApproveOrderCommand command2 =
            new UpdateOrderStatusUseCase.ApproveOrderCommand(orderId);

        // When & Then
        assertThat(command1).isEqualTo(command2);
        assertThat(command1.hashCode()).isEqualTo(command2.hashCode());
        assertThat(command1.toString()).isNotNull();
        assertThat(command1.toString()).contains(orderId.toString());
    }

    @Test
    void shouldTestApproveOrderCommandInequality() {
        // Given
        OrderId orderId1 = OrderId.generate();
        OrderId orderId2 = OrderId.generate();
        UpdateOrderStatusUseCase.ApproveOrderCommand command1 =
            new UpdateOrderStatusUseCase.ApproveOrderCommand(orderId1);
        UpdateOrderStatusUseCase.ApproveOrderCommand command2 =
            new UpdateOrderStatusUseCase.ApproveOrderCommand(orderId2);

        // When & Then
        assertThat(command1).isNotEqualTo(command2);
        assertThat(command1.hashCode()).isNotEqualTo(command2.hashCode());
    }

    @Test
    void shouldCreateRejectOrderCommandWithValidOrderId() {
        // Given
        OrderId orderId = OrderId.generate();

        // When
        UpdateOrderStatusUseCase.RejectOrderCommand command =
            new UpdateOrderStatusUseCase.RejectOrderCommand(orderId);

        // Then
        assertThat(command.orderId()).isEqualTo(orderId);
        assertThat(command.orderId()).isNotNull();
    }

    @Test
    void shouldCreateRejectOrderCommandWithNullOrderId() {
        // Given
        OrderId orderId = null;

        // When
        UpdateOrderStatusUseCase.RejectOrderCommand command =
            new UpdateOrderStatusUseCase.RejectOrderCommand(orderId);

        // Then
        assertThat(command.orderId()).isNull();
    }

    @Test
    void shouldTestRejectOrderCommandEquality() {
        // Given
        OrderId orderId = OrderId.generate();
        UpdateOrderStatusUseCase.RejectOrderCommand command1 =
            new UpdateOrderStatusUseCase.RejectOrderCommand(orderId);
        UpdateOrderStatusUseCase.RejectOrderCommand command2 =
            new UpdateOrderStatusUseCase.RejectOrderCommand(orderId);

        // When & Then
        assertThat(command1).isEqualTo(command2);
        assertThat(command1.hashCode()).isEqualTo(command2.hashCode());
        assertThat(command1.toString()).isNotNull();
        assertThat(command1.toString()).contains(orderId.toString());
    }

    @Test
    void shouldTestRejectOrderCommandInequality() {
        // Given
        OrderId orderId1 = OrderId.generate();
        OrderId orderId2 = OrderId.generate();
        UpdateOrderStatusUseCase.RejectOrderCommand command1 =
            new UpdateOrderStatusUseCase.RejectOrderCommand(orderId1);
        UpdateOrderStatusUseCase.RejectOrderCommand command2 =
            new UpdateOrderStatusUseCase.RejectOrderCommand(orderId2);

        // When & Then
        assertThat(command1).isNotEqualTo(command2);
        assertThat(command1.hashCode()).isNotEqualTo(command2.hashCode());
    }

    @Test
    void shouldCreateCancelOrderCommandWithValidOrderId() {
        // Given
        OrderId orderId = OrderId.generate();

        // When
        UpdateOrderStatusUseCase.CancelOrderCommand command =
            new UpdateOrderStatusUseCase.CancelOrderCommand(orderId);

        // Then
        assertThat(command.orderId()).isEqualTo(orderId);
        assertThat(command.orderId()).isNotNull();
    }

    @Test
    void shouldCreateCancelOrderCommandWithNullOrderId() {
        // Given
        OrderId orderId = null;

        // When
        UpdateOrderStatusUseCase.CancelOrderCommand command =
            new UpdateOrderStatusUseCase.CancelOrderCommand(orderId);

        // Then
        assertThat(command.orderId()).isNull();
    }

    @Test
    void shouldTestCancelOrderCommandEquality() {
        // Given
        OrderId orderId = OrderId.generate();
        UpdateOrderStatusUseCase.CancelOrderCommand command1 =
            new UpdateOrderStatusUseCase.CancelOrderCommand(orderId);
        UpdateOrderStatusUseCase.CancelOrderCommand command2 =
            new UpdateOrderStatusUseCase.CancelOrderCommand(orderId);

        // When & Then
        assertThat(command1).isEqualTo(command2);
        assertThat(command1.hashCode()).isEqualTo(command2.hashCode());
        assertThat(command1.toString()).isNotNull();
        assertThat(command1.toString()).contains(orderId.toString());
    }

    @Test
    void shouldTestCancelOrderCommandInequality() {
        // Given
        OrderId orderId1 = OrderId.generate();
        OrderId orderId2 = OrderId.generate();
        UpdateOrderStatusUseCase.CancelOrderCommand command1 =
            new UpdateOrderStatusUseCase.CancelOrderCommand(orderId1);
        UpdateOrderStatusUseCase.CancelOrderCommand command2 =
            new UpdateOrderStatusUseCase.CancelOrderCommand(orderId2);

        // When & Then
        assertThat(command1).isNotEqualTo(command2);
        assertThat(command1.hashCode()).isNotEqualTo(command2.hashCode());
    }

    @Test
    void shouldCreatePendingOrderCommandWithValidOrderId() {
        // Given
        OrderId orderId = OrderId.generate();

        // When
        UpdateOrderStatusUseCase.PendingOrderCommand command =
            new UpdateOrderStatusUseCase.PendingOrderCommand(orderId);

        // Then
        assertThat(command.orderId()).isEqualTo(orderId);
        assertThat(command.orderId()).isNotNull();
    }

    @Test
    void shouldCreatePendingOrderCommandWithNullOrderId() {
        // Given
        OrderId orderId = null;

        // When
        UpdateOrderStatusUseCase.PendingOrderCommand command =
            new UpdateOrderStatusUseCase.PendingOrderCommand(orderId);

        // Then
        assertThat(command.orderId()).isNull();
    }

    @Test
    void shouldTestPendingOrderCommandEquality() {
        // Given
        OrderId orderId = OrderId.generate();
        UpdateOrderStatusUseCase.PendingOrderCommand command1 =
            new UpdateOrderStatusUseCase.PendingOrderCommand(orderId);
        UpdateOrderStatusUseCase.PendingOrderCommand command2 =
            new UpdateOrderStatusUseCase.PendingOrderCommand(orderId);

        // When & Then
        assertThat(command1).isEqualTo(command2);
        assertThat(command1.hashCode()).isEqualTo(command2.hashCode());
        assertThat(command1.toString()).isNotNull();
        assertThat(command1.toString()).contains(orderId.toString());
    }

    @Test
    void shouldTestPendingOrderCommandInequality() {
        // Given
        OrderId orderId1 = OrderId.generate();
        OrderId orderId2 = OrderId.generate();
        UpdateOrderStatusUseCase.PendingOrderCommand command1 =
            new UpdateOrderStatusUseCase.PendingOrderCommand(orderId1);
        UpdateOrderStatusUseCase.PendingOrderCommand command2 =
            new UpdateOrderStatusUseCase.PendingOrderCommand(orderId2);

        // When & Then
        assertThat(command1).isNotEqualTo(command2);
        assertThat(command1.hashCode()).isNotEqualTo(command2.hashCode());
    }

    @Test
    void shouldTestAllCommandsWithSameOrderId() {
        // Given
        OrderId orderId = OrderId.generate();

        // When
        UpdateOrderStatusUseCase.ApproveOrderCommand approveCommand =
            new UpdateOrderStatusUseCase.ApproveOrderCommand(orderId);
        UpdateOrderStatusUseCase.RejectOrderCommand rejectCommand =
            new UpdateOrderStatusUseCase.RejectOrderCommand(orderId);
        UpdateOrderStatusUseCase.CancelOrderCommand cancelCommand =
            new UpdateOrderStatusUseCase.CancelOrderCommand(orderId);
        UpdateOrderStatusUseCase.PendingOrderCommand processCommand =
            new UpdateOrderStatusUseCase.PendingOrderCommand(orderId);

        // Then
        assertThat(approveCommand.orderId()).isEqualTo(orderId);
        assertThat(rejectCommand.orderId()).isEqualTo(orderId);
        assertThat(cancelCommand.orderId()).isEqualTo(orderId);
        assertThat(processCommand.orderId()).isEqualTo(orderId);

        // Verifica que são tipos diferentes mesmo com o mesmo OrderId
        assertThat(approveCommand.getClass()).isNotEqualTo(rejectCommand.getClass());
        assertThat(rejectCommand.getClass()).isNotEqualTo(cancelCommand.getClass());
        assertThat(cancelCommand.getClass()).isNotEqualTo(processCommand.getClass());
        assertThat(processCommand.getClass()).isNotEqualTo(approveCommand.getClass());
    }

    @Test
    void shouldTestAllCommandsWithSpecificOrderId() {
        // Given
        OrderId orderId = OrderId.of("550e8400-e29b-41d4-a716-446655440000");

        // When
        UpdateOrderStatusUseCase.ApproveOrderCommand approveCommand =
            new UpdateOrderStatusUseCase.ApproveOrderCommand(orderId);
        UpdateOrderStatusUseCase.RejectOrderCommand rejectCommand =
            new UpdateOrderStatusUseCase.RejectOrderCommand(orderId);
        UpdateOrderStatusUseCase.CancelOrderCommand cancelCommand =
            new UpdateOrderStatusUseCase.CancelOrderCommand(orderId);
        UpdateOrderStatusUseCase.PendingOrderCommand processCommand =
            new UpdateOrderStatusUseCase.PendingOrderCommand(orderId);

        // Then
        assertThat(approveCommand.orderId().getValue().toString()).isEqualTo("550e8400-e29b-41d4-a716-446655440000");
        assertThat(rejectCommand.orderId().getValue().toString()).isEqualTo("550e8400-e29b-41d4-a716-446655440000");
        assertThat(cancelCommand.orderId().getValue().toString()).isEqualTo("550e8400-e29b-41d4-a716-446655440000");
        assertThat(processCommand.orderId().getValue().toString()).isEqualTo("550e8400-e29b-41d4-a716-446655440000");
    }

    @Test
    void shouldTestCommandsToStringMethods() {
        // Given
        OrderId orderId = OrderId.generate();

        // When
        UpdateOrderStatusUseCase.ApproveOrderCommand approveCommand =
            new UpdateOrderStatusUseCase.ApproveOrderCommand(orderId);
        UpdateOrderStatusUseCase.RejectOrderCommand rejectCommand =
            new UpdateOrderStatusUseCase.RejectOrderCommand(orderId);
        UpdateOrderStatusUseCase.CancelOrderCommand cancelCommand =
            new UpdateOrderStatusUseCase.CancelOrderCommand(orderId);
        UpdateOrderStatusUseCase.PendingOrderCommand processCommand =
            new UpdateOrderStatusUseCase.PendingOrderCommand(orderId);

        // Then
        assertThat(approveCommand.toString()).isNotNull();
        assertThat(rejectCommand.toString()).isNotNull();
        assertThat(cancelCommand.toString()).isNotNull();
        assertThat(processCommand.toString()).isNotNull();

        assertThat(approveCommand.toString()).contains("ApproveOrderCommand");
        assertThat(rejectCommand.toString()).contains("RejectOrderCommand");
        assertThat(cancelCommand.toString()).contains("CancelOrderCommand");
        assertThat(processCommand.toString()).contains("PendingOrderCommand");
    }
}
