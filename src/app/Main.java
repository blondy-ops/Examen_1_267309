package app;

import controlador.ControlRegistrarCita;
import controlador.IControlRegistrarCita;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import modelo.ModeloCitas;
import modelo.entidades.DiaSemana;
import modelo.entidades.Especialidad;
import modelo.entidades.Genero;
import modelo.entidades.Medico;
import modelo.entidades.Paciente;
import vista.ApartarCita;
import vista.BuscadorNSS;

/** Compone la aplicación: crea modelo, control y vistas, y suscribe las vistas al modelo. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ModeloCitas modelo = new ModeloCitas(crearPacientes(), crearMedicos());
            IControlRegistrarCita control = new ControlRegistrarCita(modelo);

            BuscadorNSS buscadorNSS = new BuscadorNSS(control);
            ApartarCita apartarCita = new ApartarCita(control);

            modelo.addObserver(buscadorNSS);
            modelo.addObserver(apartarCita);

            buscadorNSS.setVisible(true);
        });
    }

    private static List<Paciente> crearPacientes() {
        List<Paciente> lista = new ArrayList<>();
        lista.add(new Paciente("12345678901", "Juan Perez", 30, Genero.MASCULINO));
        lista.add(new Paciente("98765432109", "Maria Lopez", 28, Genero.FEMENINO));
        return lista;
    }

    private static List<Medico> crearMedicos() {
        Medico m1 = new Medico("Dr. Carlos Ramirez", Especialidad.CARDIOLOGIA, 101);
        m1.agregarHorario(DiaSemana.LUNES, LocalTime.of(9, 0), LocalTime.of(13, 0));
        m1.agregarHorario(DiaSemana.MIERCOLES, LocalTime.of(9, 0), LocalTime.of(13, 0));
        m1.agregarHorario(DiaSemana.VIERNES, LocalTime.of(9, 0), LocalTime.of(13, 0));

        Medico m2 = new Medico("Dra. Ana Torres", Especialidad.PEDIATRIA, 102);
        m2.agregarHorario(DiaSemana.MARTES, LocalTime.of(10, 0), LocalTime.of(14, 0));
        m2.agregarHorario(DiaSemana.JUEVES, LocalTime.of(10, 0), LocalTime.of(14, 0));

        Medico m3 = new Medico("Dr. Luis Mendoza", Especialidad.NEUROLOGIA, 201);
        for (DiaSemana d : new DiaSemana[] {DiaSemana.LUNES, DiaSemana.MARTES, DiaSemana.MIERCOLES,
                DiaSemana.JUEVES, DiaSemana.VIERNES}) {
            m3.agregarHorario(d, LocalTime.of(15, 0), LocalTime.of(19, 0));
        }

        Medico m4 = new Medico("Dra. Sofia Herrera", Especialidad.DERMATOLOGIA, 202);
        m4.agregarHorario(DiaSemana.SABADO, LocalTime.of(8, 0), LocalTime.of(12, 0));
        m4.agregarHorario(DiaSemana.MARTES, LocalTime.of(16, 0), LocalTime.of(20, 0));

        List<Medico> lista = new ArrayList<>();
        lista.add(m1);
        lista.add(m2);
        lista.add(m3);
        lista.add(m4);
        return lista;
    }
}
