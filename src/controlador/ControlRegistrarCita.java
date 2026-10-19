package controlador;

import java.time.LocalDateTime;
import modelo.ModeloCitas;
import modelo.entidades.Medico;

/** la vista solo ve a este y no al modelo para seguir el mvc */
public class ControlRegistrarCita implements IControlRegistrarCita {
    private final ModeloCitas modelo;

    public ControlRegistrarCita(ModeloCitas modelo) {
        this.modelo = modelo;
    }

    @Override
    public void ingresarNSS(String nss) {
        modelo.buscarPaciente(nss);
    }

    @Override
    public void seleccionarMedico(Medico medico) {
        modelo.seleccionarMedico(medico);
    }

    @Override
    public void apartarCita(LocalDateTime fechaHora) {
        modelo.registrarCita(fechaHora);
    }
}
