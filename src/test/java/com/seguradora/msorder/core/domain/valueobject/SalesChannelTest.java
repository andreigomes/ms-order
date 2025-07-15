package com.seguradora.msorder.core.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SalesChannelTest {

    @Test
    void shouldHaveAllRequiredChannels() {
        // When & Then
        assertThat(SalesChannel.WEB_SITE).isNotNull();
        assertThat(SalesChannel.MOBILE).isNotNull();
        assertThat(SalesChannel.BRANCH).isNotNull();
        assertThat(SalesChannel.PHONE).isNotNull();
    }

    @Test
    void shouldHaveCorrectStringRepresentation() {
        // When & Then
        assertThat(SalesChannel.WEB_SITE.name()).isEqualTo("WEB_SITE");
        assertThat(SalesChannel.MOBILE.name()).isEqualTo("MOBILE");
        assertThat(SalesChannel.BRANCH.name()).isEqualTo("BRANCH");
        assertThat(SalesChannel.PHONE.name()).isEqualTo("PHONE");
    }

    @Test
    void shouldBeComparable() {
        // When & Then
        assertThat(SalesChannel.WEB_SITE).isEqualTo(SalesChannel.WEB_SITE);
        assertThat(SalesChannel.MOBILE).isNotEqualTo(SalesChannel.WEB_SITE);
    }

    @Test
    void shouldSupportValueOfOperation() {
        // When & Then
        assertThat(SalesChannel.valueOf("WEB_SITE")).isEqualTo(SalesChannel.WEB_SITE);
        assertThat(SalesChannel.valueOf("MOBILE")).isEqualTo(SalesChannel.MOBILE);
        assertThat(SalesChannel.valueOf("BRANCH")).isEqualTo(SalesChannel.BRANCH);
        assertThat(SalesChannel.valueOf("PHONE")).isEqualTo(SalesChannel.PHONE);
    }

    @Test
    void shouldHaveAllValuesMethod() {
        // When
        SalesChannel[] values = SalesChannel.values();

        // Then
        assertThat(values).hasSize(6); // MOBILE, WHATSAPP, WEB_SITE, PHONE, BRANCH, PARTNER
        assertThat(values).containsExactlyInAnyOrder(
            SalesChannel.MOBILE,
            SalesChannel.WHATSAPP,
            SalesChannel.WEB_SITE,
            SalesChannel.PHONE,
            SalesChannel.BRANCH,
            SalesChannel.PARTNER
        );
    }
}
