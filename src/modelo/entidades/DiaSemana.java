package modelo.entidades;

import java.time.DayOfWeek;

public enum DiaSemana {
    LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO;

    /** Convierte el DayOfWeek de java.time (MONDAY = 0) al enum del dominio. */
    public static DiaSemana desde(DayOfWeek dia) {
        return values()[dia.ordinal()];
    }
}
