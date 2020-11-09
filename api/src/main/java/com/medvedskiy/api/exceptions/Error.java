package com.medvedskiy.api.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Error {

    @JsonProperty("code")
    private final int code;
    @JsonProperty("error")
    private final String errorMessage;
    @JsonProperty("description")
    private final String description;

    public Error(
            int code,
            String message,
            String description
    ) {
        this.code = code;
        this.errorMessage = message;
        this.description = description;
    }

}
