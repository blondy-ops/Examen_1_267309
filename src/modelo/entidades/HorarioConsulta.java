package modelo.entidades;

import java.time.LocalTime;

public class HorarioConsulta {
    private final DiaSemana dia;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;

    public HorarioConsulta(DiaSemana dia, LocalTime horaInicio, LocalTime horaFin) {
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public DiaSemana getDia() { return dia; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }

    @Override
    public String toString() {
        return dia + " " + horaInicio + " - " + horaFin;
    }
}
