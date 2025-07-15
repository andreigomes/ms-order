package com.seguradora.msorder.core.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssistancesTest {

    @Test
    void shouldCreateAssistancesWithValidList() {
        // Given
        List<String> assistanceList = List.of("24h roadside assistance", "Emergency towing");

        // When
        Assistances assistances = Assistances.of(assistanceList);

        // Then
        assertThat(assistances.getAssistanceList()).isEqualTo(assistanceList);
        assertThat(assistances.getCount()).isEqualTo(2);
    }

    @Test
    void shouldThrowExceptionWhenListIsNull() {
        // When & Then
        assertThatThrownBy(() -> Assistances.of(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Assistance list cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenListIsEmpty() {
        // Given
        List<String> emptyList = List.of();

        // When & Then
        assertThatThrownBy(() -> Assistances.of(emptyList))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Assistance list cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenContainsNullValue() {
        // Given
        List<String> listWithNull = new ArrayList<>();
        listWithNull.add("24h assistance");
        listWithNull.add(null);

        // When & Then
        assertThatThrownBy(() -> Assistances.of(listWithNull))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Assistance cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenContainsEmptyValue() {
        // Given
        List<String> listWithEmpty = List.of("24h assistance", "");

        // When & Then
        assertThatThrownBy(() -> Assistances.of(listWithEmpty))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Assistance cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenContainsBlankValue() {
        // Given
        List<String> listWithBlank = List.of("24h assistance", "   ");

        // When & Then
        assertThatThrownBy(() -> Assistances.of(listWithBlank))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Assistance cannot be null or empty");
    }

    @Test
    void shouldReturnCorrectCount() {
        // Given
        List<String> assistanceList = List.of("Assistance 1", "Assistance 2", "Assistance 3");

        // When
        Assistances assistances = Assistances.of(assistanceList);

        // Then
        assertThat(assistances.getCount()).isEqualTo(3);
    }

    @Test
    void shouldReturnImmutableList() {
        // Given
        List<String> originalList = new ArrayList<>();
        originalList.add("24h assistance");
        Assistances assistances = Assistances.of(originalList);

        // When & Then
        assertThatThrownBy(() -> assistances.getAssistanceList().add("new assistance"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldBeEqualWhenSameList() {
        // Given
        List<String> assistanceList = List.of("24h assistance", "Emergency towing");
        Assistances assistances1 = Assistances.of(assistanceList);
        Assistances assistances2 = Assistances.of(assistanceList);

        // Then
        assertThat(assistances1).isEqualTo(assistances2);
        assertThat(assistances1.hashCode()).isEqualTo(assistances2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentList() {
        // Given
        List<String> assistanceList1 = List.of("24h assistance");
        List<String> assistanceList2 = List.of("Emergency towing");
        Assistances assistances1 = Assistances.of(assistanceList1);
        Assistances assistances2 = Assistances.of(assistanceList2);

        // Then
        assertThat(assistances1).isNotEqualTo(assistances2);
    }

    @Test
    void shouldNotBeEqualToNull() {
        // Given
        Assistances assistances = Assistances.of(List.of("24h assistance"));

        // Then
        assertThat(assistances).isNotEqualTo(null);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        // Given
        Assistances assistances = Assistances.of(List.of("24h assistance"));
        String otherObject = "test";

        // Then
        assertThat(assistances).isNotEqualTo(otherObject);
    }

    @Test
    void shouldBeEqualToItself() {
        // Given
        Assistances assistances = Assistances.of(List.of("24h assistance"));

        // Then
        assertThat(assistances).isEqualTo(assistances);
    }

    @Test
    void shouldHaveConsistentHashCode() {
        // Given
        Assistances assistances = Assistances.of(List.of("24h assistance"));

        // When
        int hashCode1 = assistances.hashCode();
        int hashCode2 = assistances.hashCode();

        // Then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    void shouldHandleSingleAssistance() {
        // Given
        List<String> singleAssistance = List.of("Emergency assistance");

        // When
        Assistances assistances = Assistances.of(singleAssistance);

        // Then
        assertThat(assistances.getCount()).isEqualTo(1);
        assertThat(assistances.getAssistanceList()).containsExactly("Emergency assistance");
    }

    @Test
    void shouldPreserveOrderOfAssistances() {
        // Given
        List<String> orderedList = List.of("First", "Second", "Third");

        // When
        Assistances assistances = Assistances.of(orderedList);

        // Then
        assertThat(assistances.getAssistanceList()).containsExactly("First", "Second", "Third");
    }
}
