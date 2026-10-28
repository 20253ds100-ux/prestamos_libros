package com.example.libros.exceptions;

public class CustomBadRequestException  extends RuntimeException {
    public CustomBadRequestException(String menssage){
        super(menssage);
    }
}