package modelo.entidades;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Medico {
    private final String nombre;
    private final Especialidad especialidad;
    private final int consultorio;
    // Composición: los horarios no existen sin el médico.
    private final List<HorarioConsulta> horarios = new ArrayList<>();

    public Medico(String nombre, Especialidad especialidad, int consultorio) {
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.consultorio = consultorio;
    }

    public void agregarHorario(DiaSemana dia, LocalTime inicio, LocalTime fin) {
        horarios.add(new HorarioConsulta(dia, inicio, fin));
    }

    /**
     * El médico es el experto en su propio horario: true si la consulta completa
     * (inicio + duración) cabe dentro de alguno de sus horarios de consulta.
     */
    public boolean atiendeEn(LocalDateTime inicio, int duracionMinutos) {
        LocalDateTime fin = inicio.plusMinutes(duracionMinutos);
        if (!fin.toLocalDate().equals(inicio.toLocalDate())) {
            return false;
        }
        DiaSemana dia = DiaSemana.desde(inicio.getDayOfWeek());
        for (HorarioConsulta h : horarios) {
            if (h.getDia() == dia
                    && !inicio.toLocalTime().isBefore(h.getHoraInicio())
                    && !fin.toLocalTime().isAfter(h.getHoraFin())) {
                return true;
            }
        }
        return false;
    }

    public String getNombre() { return nombre; }
    public Especialidad getEspecialidad() { return especialidad; }
    public int getConsultorio() { return consultorio; }
    public List<HorarioConsulta> getHorarios() { return Collections.unmodifiableList(horarios); }

    /** Lo usa la JList de la vista para mostrar al médico. */
    @Override
    public String toString() { return nombre; }
}
