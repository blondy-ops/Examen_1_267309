package modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import modelo.entidades.Cita;
import modelo.entidades.Medico;
import modelo.entidades.Paciente;

/** vista de solo lectura para que las vistas no vean directo al modelo*/
public interface IModel {
    Cita getCita();
    EstadoRegistro getEstado();
    Paciente getPaciente();
    Medico getMedicoSeleccionado();
    List<Medico> getMedicos();

    /** Horas de inicio libres (citas de 30 min) del médico seleccionado en esa fecha. */
    List<LocalTime> getHorariosDisponibles(LocalDate fecha);
}
