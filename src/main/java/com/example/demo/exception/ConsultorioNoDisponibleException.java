package com.example.demo.exception;

/**
 * Excepción lanzada cuando se intenta asignar un médico a un consultorio
 * que no está disponible (en mantenimiento, limpieza, fuera de servicio, etc.).
 */
public class ConsultorioNoDisponibleException extends RuntimeException {

    public ConsultorioNoDisponibleException(String message) {
        super(message);
    }

    public ConsultorioNoDisponibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
