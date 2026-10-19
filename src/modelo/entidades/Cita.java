package modelo.entidades;

import java.time.LocalDateTime;

public class Cita {
    /** duracion de cita de media hora */
    public static final int DURACION_MINUTOS = 30;

    private final Paciente paciente;
    private final LocalDateTime fechaYHora;
    private final Medico medico;

    public Cita(Paciente paciente, Medico medico, LocalDateTime fechaYHora) {
        this.paciente = paciente;
        this.medico = medico;
        this.fechaYHora = fechaYHora;
    }

    public Paciente getPaciente() { return paciente; }
    public LocalDateTime getFechaYHora() { return fechaYHora; }
    public Medico getMedico() { return medico; }
    public LocalDateTime getFechaFin() { return fechaYHora.plusMinutes(DURACION_MINUTOS); }
}
