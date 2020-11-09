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
    private Long sender;


    @JsonProperty("receiver")
    private Long receiver;


    @JsonProperty("price")
    private Long price;
}
