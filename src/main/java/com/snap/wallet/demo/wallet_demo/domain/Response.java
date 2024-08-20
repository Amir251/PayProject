package com.snap.wallet.demo.wallet_demo.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Schema(description = "A standardized response object for API responses")
public record Response(String time, int code, String path, HttpStatus status, String message, String exception, Map<?,?> data) { }
