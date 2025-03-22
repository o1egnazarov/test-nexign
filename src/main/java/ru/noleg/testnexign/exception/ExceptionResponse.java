package ru.noleg.testnexign.exception;

public record ExceptionResponse(int status, String message, String cause) {
}