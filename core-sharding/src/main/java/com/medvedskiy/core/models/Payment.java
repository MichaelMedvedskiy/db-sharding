package com.medvedskiy.core.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Payment model
 */
@Getter
@Builder
@AllArgsConstructor
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode
@JsonDeserialize(builder = Payment.PaymentBuilder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Payment {

    @JsonProperty("sender")
    private final Long sender;


    @JsonProperty("receiver")
    private final Long receiver;


    @JsonProperty("price")
    private final Long price;
}
