package com.fulfilment.application.monolith;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global Exception Handler for the Application using Spring Web compatibility.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Inject
    ObjectMapper objectMapper;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ObjectNode> handleAllExceptions(Exception exception) {
        LOGGER.error("Failed to handle request globally via Spring @RestControllerAdvice", exception);

        int code = 500;
        if (exception instanceof WebApplicationException webAppEx) {
            code = webAppEx.getResponse().getStatus();
        } else if (exception instanceof IllegalArgumentException) {
            code = 400; // Map standard bad arguments to bad request
        }

        ObjectNode exceptionJson = objectMapper.createObjectNode();
        exceptionJson.put("exceptionType", exception.getClass().getName());
        exceptionJson.put("code", code);

        if (exception.getMessage() != null && !exception.getMessage().isBlank()) {
            exceptionJson.put("error", exception.getMessage());
        }

        return ResponseEntity.status(HttpStatus.valueOf(code)).body(exceptionJson);
    }
}
