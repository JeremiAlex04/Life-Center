package com.example.demo.exception;

/**
 * Excepción lanzada cuando se intenta asignar un médico a un consultorio
 * cuyo tipo no es compatible con la especialidad del médico.
 */
public class IncompatibilidadEspecialidadException extends RuntimeException {

    public IncompatibilidadEspecialidadException(String message) {
        super(message);
    }

    public IncompatibilidadEspecialidadException(String message, Throwable cause) {
        super(message, cause);
    }
}
