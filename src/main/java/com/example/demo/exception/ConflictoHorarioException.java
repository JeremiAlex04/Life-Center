package com.example.demo.exception;

/**
 * Excepción lanzada cuando se intenta asignar un médico a un consultorio
 * que ya tiene otro médico asignado en el mismo turno.
 */
public class ConflictoHorarioException extends RuntimeException {

    public ConflictoHorarioException(String message) {
        super(message);
    }

    public ConflictoHorarioException(String message, Throwable cause) {
        super(message, cause);
    }
}
