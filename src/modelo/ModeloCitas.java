package modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import modelo.entidades.Cita;
import modelo.entidades.DiaSemana;
import modelo.entidades.HorarioConsulta;
import modelo.entidades.Medico;
import modelo.entidades.Paciente;

/** modelo del caso de uso  de registro de citas medicas en este caso esta clase seria el subject del patron observer*/
public class ModeloCitas implements IModel, IObservable {
    private Paciente paciente;
    private Medico medicoSeleccionado;
    private Cita cita;
    private final List<IObservador> observadores = new ArrayList<>();
    private EstadoRegistro estado;
    private final List<Paciente> pacientes;
    private final List<Medico> medicos;
    private final List<Cita> citas = new ArrayList<>();

    public ModeloCitas(List<Paciente> pacientes, List<Medico> medicos) {
        this.pacientes = new ArrayList<>(pacientes);
        this.medicos = new ArrayList<>(medicos);
    }

    // ---------- Lógica de negocio ----------

    /** Flujo básico paso 1 / alternativo A1. */
    public void buscarPaciente(String nss) {
        String buscado = (nss == null) ? "" : nss.trim();
        Paciente encontrado = null;
        for (Paciente p : pacientes) {
            if (p.getNumeroSS().equals(buscado)) {
                encontrado = p;
                break;
            }
        }
        paciente = encontrado;
        estado = (encontrado == null) ? EstadoRegistro.NSS_INVALIDO : EstadoRegistro.PACIENTE_ENCONTRADO;
        notifyObservers();
    }

    /** Flujo básico paso 2. */
    public void seleccionarMedico(Medico medico) {
        if (paciente == null || medico == null) {
            return;
        }
        medicoSeleccionado = medico;
        estado = EstadoRegistro.MEDICO_SELECCIONADO;
        notifyObservers();
    }

    /** Flujo básico pasos 3-4 / alternativo A2. */
    public void registrarCita(LocalDateTime fechaHora) {
        if (paciente == null || medicoSeleccionado == null || fechaHora == null) {
            return;
        }
        LocalDateTime fecha = fechaHora.truncatedTo(ChronoUnit.MINUTES);
        if (esHorarioValido(medicoSeleccionado, fecha)) {
            cita = new Cita(paciente, medicoSeleccionado, fecha);
            citas.add(cita);
            estado = EstadoRegistro.CITA_REGISTRADA;
        } else {
            estado = EstadoRegistro.HORARIO_INVALIDO;
        }
        notifyObservers();
    }

    /** Regla de negocio: no pasada, dentro del horario del médico y sin empalmar con otra cita. */
    private boolean esHorarioValido(Medico medico, LocalDateTime inicio) {
        return inicio.isAfter(LocalDateTime.now())
                && medico.atiendeEn(inicio, Cita.DURACION_MINUTOS)
                && !horarioOcupado(medico, inicio);
    }

    /** Dos citas de 30 min se empalman si sus inicios distan menos de 30 min. */
    private boolean horarioOcupado(Medico medico, LocalDateTime inicio) {
        for (Cita c : citas) {
            if (c.getMedico() == medico
                    && inicio.isBefore(c.getFechaFin())
                    && c.getFechaYHora().isBefore(inicio.plusMinutes(Cita.DURACION_MINUTOS))) {
                return true;
            }
        }
        return false;
    }

    // ---------- IModel (solo lectura) ----------

    @Override public Cita getCita() { return cita; }
    @Override public EstadoRegistro getEstado() { return estado; }
    @Override public Paciente getPaciente() { return paciente; }
    @Override public Medico getMedicoSeleccionado() { return medicoSeleccionado; }
    @Override public List<Medico> getMedicos() { return Collections.unmodifiableList(medicos); }

    @Override
    public List<LocalTime> getHorariosDisponibles(LocalDate fecha) {
        List<LocalTime> libres = new ArrayList<>();
        if (medicoSeleccionado == null || fecha == null) {
            return libres;
        }
        for (HorarioConsulta h : medicoSeleccionado.getHorarios()) {
            if (h.getDia() != DiaSemana.desde(fecha.getDayOfWeek())) {
                continue;
            }
            LocalDateTime inicio = fecha.atTime(h.getHoraInicio());
            LocalDateTime limite = fecha.atTime(h.getHoraFin());
            while (!inicio.plusMinutes(Cita.DURACION_MINUTOS).isAfter(limite)) {
                if (esHorarioValido(medicoSeleccionado, inicio)) {
                    libres.add(inicio.toLocalTime());
                }
                inicio = inicio.plusMinutes(Cita.DURACION_MINUTOS);
            }
        }
        Collections.sort(libres);
        return libres;
    }

    // ---------- IObservable ----------

    @Override
    public void addObserver(IObservador o) {
        if (o != null && !observadores.contains(o)) {
            observadores.add(o);
        }
    }

    @Override
    public void removeObserver(IObservador o) {
        observadores.remove(o);
    }

    @Override
    public void notifyObservers() {
        // Copia defensiva: un observador podría (des)suscribirse durante la notificación.
        for (IObservador o : new ArrayList<>(observadores)) {
            o.update(this);
        }
    }
}
