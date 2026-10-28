package com.example.libros.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
//TODAS LAS EXCEPCIONES SE DECLARAN AHI ADENRO
public class ErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
//en parentesis tipo de excepcion que se maneja0
    public ResponseEntity<Map<String, String>> validar(
            MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>()
                ;
        for (FieldError err : ex.getBindingResult().getFieldErrors()) {
            errores.put(err.getField(), err.getDefaultMessage())
            ;
        }
        return ResponseEntity.badRequest().body(errores)
                ;
    }


}