package com.example.student.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    @Test
    @DisplayName("EntityNotFoundException превращается в ответ 404")
    void notFound() {
        ResponseEntity<AppError> response = new GlobalExceptionHandler()
                .catchEntityNotFoundException(new EntityNotFoundException("Нет такого"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatusCode());
        assertEquals("Нет такого", response.getBody().getMessage());
    }
}
