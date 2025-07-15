package com.seguradora.msorder.infrastructure.adapter.in.web.exception;

import com.seguradora.msorder.core.usecase.order.GetOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void shouldHandleOrderNotFoundException() {
        // Given
        GetOrderService.OrderNotFoundException exception =
            new GetOrderService.OrderNotFoundException("Order not found with ID: 123");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
            globalExceptionHandler.handleOrderNotFound(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("ORDER_NOT_FOUND");
        assertThat(response.getBody().message()).isEqualTo("Order not found with ID: 123");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Invalid parameter value");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
            globalExceptionHandler.handleIllegalArgument(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("INVALID_ARGUMENT");
        assertThat(response.getBody().message()).isEqualTo("Invalid parameter value");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void shouldHandleIllegalStateException() {
        // Given
        IllegalStateException exception = new IllegalStateException("Invalid state transition");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
            globalExceptionHandler.handleIllegalState(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("INVALID_STATE");
        assertThat(response.getBody().message()).isEqualTo("Invalid state transition");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() {
        // Given
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "testObject");
        bindingResult.addError(new FieldError("testObject", "customerId", "Customer ID é obrigatório"));
        bindingResult.addError(new FieldError("testObject", "amount", "Amount deve ser positivo"));

        // Criar um MethodParameter mock válido
        MethodParameter methodParameter = mock(MethodParameter.class);
        Method mockMethod = null;
        try {
            mockMethod = this.getClass().getMethod("dummyMethod", String.class);
        } catch (NoSuchMethodException e) {
            // Fallback
        }
        when(methodParameter.getExecutable()).thenReturn(mockMethod);
        when(methodParameter.getParameterIndex()).thenReturn(0);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
            methodParameter, bindingResult);

        // When
        ResponseEntity<GlobalExceptionHandler.ValidationErrorResponse> response =
            globalExceptionHandler.handleValidation(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().message()).isEqualTo("Invalid request data");
        assertThat(response.getBody().timestamp()).isNotNull();
        assertThat(response.getBody().errors()).hasSize(2);
        assertThat(response.getBody().errors()).containsEntry("customerId", "Customer ID é obrigatório");
        assertThat(response.getBody().errors()).containsEntry("amount", "Amount deve ser positivo");
    }

    // Método dummy para criar um Method válido no teste
    public void dummyMethod(String param) {
        // Método usado apenas para testes
    }

    @Test
    void shouldHandleGenericException() {
        // Given
        Exception exception = new RuntimeException("Unexpected database error");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
            globalExceptionHandler.handleGeneral(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void shouldHandleNullPointerException() {
        // Given
        NullPointerException exception = new NullPointerException("Null value encountered");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
            globalExceptionHandler.handleGeneral(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void shouldCreateErrorResponseRecord() {
        // Given & When
        GlobalExceptionHandler.ErrorResponse errorResponse = new GlobalExceptionHandler.ErrorResponse(
            "TEST_CODE",
            "Test message",
            java.time.LocalDateTime.now()
        );

        // Then
        assertThat(errorResponse.code()).isEqualTo("TEST_CODE");
        assertThat(errorResponse.message()).isEqualTo("Test message");
        assertThat(errorResponse.timestamp()).isNotNull();
    }

    @Test
    void shouldCreateValidationErrorResponseRecord() {
        // Given & When
        java.util.Map<String, String> errors = java.util.Map.of("field1", "error1", "field2", "error2");
        GlobalExceptionHandler.ValidationErrorResponse validationErrorResponse =
            new GlobalExceptionHandler.ValidationErrorResponse(
                "VALIDATION_ERROR",
                "Validation failed",
                java.time.LocalDateTime.now(),
                errors
            );

        // Then
        assertThat(validationErrorResponse.code()).isEqualTo("VALIDATION_ERROR");
        assertThat(validationErrorResponse.message()).isEqualTo("Validation failed");
        assertThat(validationErrorResponse.timestamp()).isNotNull();
        assertThat(validationErrorResponse.errors()).hasSize(2);
        assertThat(validationErrorResponse.errors()).containsEntry("field1", "error1");
        assertThat(validationErrorResponse.errors()).containsEntry("field2", "error2");
    }
}
