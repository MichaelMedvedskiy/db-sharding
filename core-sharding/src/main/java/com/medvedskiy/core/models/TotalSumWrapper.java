package com.medvedskiy.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Builder
@AllArgsConstructor
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode
@JsonDeserialize(builder = TotalSumWrapper.TotalSumWrapperBuilder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TotalSumWrapper {

    @NonNull
    @JsonProperty("totalSum")
    private final Long totalSum;
}
