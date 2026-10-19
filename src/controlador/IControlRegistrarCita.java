package controlador;

import java.time.LocalDateTime;
import modelo.entidades.Medico;

public interface IControlRegistrarCita {
    void ingresarNSS(String nss);
    void seleccionarMedico(Medico medico);
    void apartarCita(LocalDateTime fechaHora);
}
