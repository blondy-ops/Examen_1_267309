package vista;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import controlador.IControlRegistrarCita;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import modelo.EstadoRegistro;
import modelo.IModel;
import modelo.IObservador;
import modelo.entidades.Cita;
import modelo.entidades.HorarioConsulta;
import modelo.entidades.Medico;
import modelo.entidades.Paciente;

/**
 * Pantalla principal: lista de médicos a la izquierda y, a la derecha, una misma zona
 * que muestra primero los datos del médico y después los datos de la cita.
 */
public class ApartarCita extends JFrame implements IObservador {
    private static final String CARD_VACIO = "vacio";
    private static final String CARD_MEDICO = "medico";
    private static final String CARD_CITA = "cita";
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Color GRIS = new Color(220, 220, 220);

    private final IControlRegistrarCita control;

    private final JLabel lblPaciente = new JLabel(" ");
    private final DefaultListModel<Medico> listaModelo = new DefaultListModel<>();
    private final JList<Medico> listaMedicos = new JList<>(listaModelo);
    private final CardLayout cards = new CardLayout();
    private final JPanel zonaDerecha = new JPanel(cards);
    private final JButton btnApartar = new JButton("Apartar cita");
    private boolean actualizando = false;

    // Tarjeta "datos del médico"
    private final JLabel lblNombre = new JLabel();
    private final JLabel lblEspecialidad = new JLabel();
    private final JLabel lblConsultorio = new JLabel();
    private final JTextArea txtHorarios = new JTextArea(4, 18);
    private final DatePicker selectorFecha;
    private final DefaultComboBoxModel<LocalTime> horasModelo = new DefaultComboBoxModel<>();
    private final JComboBox<LocalTime> comboHoras = new JComboBox<>(horasModelo);
    private boolean medicoActivo = false;
    /** Última referencia de solo lectura recibida del modelo (la usa el selector de fechas). */
    private IModel modelo;

    // Tarjeta "datos de la cita"
    private final JLabel lblCitaPaciente = new JLabel();
    private final JLabel lblCitaMedico = new JLabel();
    private final JLabel lblCitaConsultorio = new JLabel();
    private final JLabel lblCitaFecha = new JLabel();

    public ApartarCita(IControlRegistrarCita control) {
        super("Seleccion de medico y horario");
        this.control = control;
        DatePickerSettings ajustes = new DatePickerSettings(Locale.forLanguageTag("es-MX"));
        selectorFecha = new DatePicker(ajustes);
        // LGoodDatePicker exige asignar la política de veto DESPUÉS de construir el DatePicker.
        // Solo se pueden elegir fechas futuras en las que el médico tenga horas libres.
        ajustes.setVetoPolicy(fecha -> modelo != null && !modelo.getHorariosDisponibles(fecha).isEmpty());
        selectorFecha.addDateChangeListener(e -> cargarHorarios());
        comboHoras.addActionListener(e -> actualizarBotonApartar());
        construirInterfaz();
    }

    private void construirInterfaz() {
        // --- Información del paciente (arriba)
        lblPaciente.setOpaque(true);
        lblPaciente.setBackground(GRIS);
        lblPaciente.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        // --- Lista de médicos (izquierda)
        listaMedicos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollLista = new JScrollPane(listaMedicos);
        scrollLista.setBorder(BorderFactory.createTitledBorder("Lista de medicos"));
        scrollLista.setPreferredSize(new Dimension(190, 260));
        listaMedicos.addListSelectionListener(e -> {
            Medico elegido = listaMedicos.getSelectedValue();
            if (!e.getValueIsAdjusting() && !actualizando && elegido != null) {
                control.seleccionarMedico(elegido);
            }
        });

        // --- Zona derecha (tres tarjetas en el mismo espacio)
        zonaDerecha.setBackground(GRIS);
        zonaDerecha.add(construirTarjetaVacia(), CARD_VACIO);
        zonaDerecha.add(construirTarjetaMedico(), CARD_MEDICO);
        zonaDerecha.add(construirTarjetaCita(), CARD_CITA);
        zonaDerecha.setPreferredSize(new Dimension(360, 300));

        JPanel centro = new JPanel(new BorderLayout(10, 0));
        centro.add(scrollLista, BorderLayout.WEST);
        centro.add(zonaDerecha, BorderLayout.CENTER);

        // --- Botón (abajo)
        btnApartar.setEnabled(false);
        btnApartar.addActionListener(e -> control.apartarCita(fechaSeleccionada()));
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sur.add(btnApartar);

        JPanel raiz = new JPanel(new BorderLayout(0, 10));
        raiz.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        raiz.add(lblPaciente, BorderLayout.NORTH);
        raiz.add(centro, BorderLayout.CENTER);
        raiz.add(sur, BorderLayout.SOUTH);

        setContentPane(raiz);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel construirTarjetaVacia() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(GRIS);
        JLabel l = new JLabel("Selecciona un medico para ver su informacion", JLabel.CENTER);
        p.add(l, BorderLayout.CENTER);
        return p;
    }

    private JPanel construirTarjetaMedico() {
        JPanel datos = new JPanel(new GridLayout(3, 1, 0, 4));
        datos.setOpaque(false);
        datos.add(lblNombre);
        datos.add(lblEspecialidad);
        datos.add(lblConsultorio);

        txtHorarios.setEditable(false);
        txtHorarios.setBackground(Color.WHITE);
        JPanel horarios = new JPanel(new BorderLayout(0, 4));
        horarios.setOpaque(false);
        horarios.add(new JLabel("Horarios disponibles:"), BorderLayout.NORTH);
        horarios.add(new JScrollPane(txtHorarios), BorderLayout.CENTER);

        JPanel fecha = new JPanel(new GridLayout(2, 2, 6, 6));
        fecha.setOpaque(false);
        fecha.add(new JLabel("Fecha:"));
        fecha.add(selectorFecha);
        fecha.add(new JLabel("Hora (30 min):"));
        fecha.add(comboHoras);

        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(GRIS);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p.add(datos, BorderLayout.NORTH);
        p.add(horarios, BorderLayout.CENTER);
        p.add(fecha, BorderLayout.SOUTH);
        return p;
    }

    private JPanel construirTarjetaCita() {
        JLabel titulo = new JLabel("Cita apartada");
        titulo.setFont(titulo.getFont().deriveFont(java.awt.Font.BOLD));

        JPanel datos = new JPanel(new GridLayout(5, 1, 0, 6));
        datos.setOpaque(false);
        datos.add(titulo);
        datos.add(lblCitaPaciente);
        datos.add(lblCitaMedico);
        datos.add(lblCitaConsultorio);
        datos.add(lblCitaFecha);

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(GRIS);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p.add(datos, BorderLayout.NORTH);
        return p;
    }

    // ---------- Observer: la vista reacciona a lo que el modelo reporta ----------

    @Override
    public void update(IModel model) {
        this.modelo = model;
        EstadoRegistro estado = model.getEstado();
        if (estado == null) {
            return;
        }
        switch (estado) {
            case PACIENTE_ENCONTRADO:
                mostrarPaciente(model.getPaciente(), model.getMedicos());
                break;
            case MEDICO_SELECCIONADO:
                mostrarMedico(model.getMedicoSeleccionado());
                break;
            case CITA_REGISTRADA:
                mostrarCita(model.getCita());
                break;
            case HORARIO_INVALIDO:                                  // Flujo alternativo A2
                mostrarAvisoFechaNoDisponible();
                break;
            default:
                break;
        }
    }

    private void mostrarPaciente(Paciente p, java.util.List<Medico> medicos) {
        actualizando = true;
        listaModelo.clear();
        for (Medico m : medicos) {
            listaModelo.addElement(m);
        }
        listaMedicos.clearSelection();
        actualizando = false;

        lblPaciente.setText("Paciente: " + p.getNombre() + "   |   NSS: " + p.getNumeroSS()
                + "   |   Edad: " + p.getEdad() + "   |   Genero: " + p.getGenero());
        cards.show(zonaDerecha, CARD_VACIO);
        medicoActivo = false;
        btnApartar.setEnabled(false);
        setVisible(true);
    }

    private void mostrarMedico(Medico m) {
        lblNombre.setText("Nombre: " + m.getNombre());
        lblEspecialidad.setText("Especialidad: " + m.getEspecialidad());
        lblConsultorio.setText("Consultorio: " + m.getConsultorio());
        StringBuilder sb = new StringBuilder();
        for (HorarioConsulta h : m.getHorarios()) {
            sb.append(h).append('\n');
        }
        txtHorarios.setText(sb.toString());
        txtHorarios.setCaretPosition(0);
        medicoActivo = true;
        selectorFecha.setDate(null);
        cargarHorarios();
        cards.show(zonaDerecha, CARD_MEDICO);
    }

    /** Llena la lista de horas libres del día elegido (la calcula el modelo). */
    private void cargarHorarios() {
        horasModelo.removeAllElements();
        LocalDate fecha = selectorFecha.getDate();
        if (modelo != null && fecha != null) {
            for (LocalTime hora : modelo.getHorariosDisponibles(fecha)) {
                horasModelo.addElement(hora);
            }
        }
        if (horasModelo.getSize() > 0) {
            comboHoras.setSelectedIndex(0);
        }
        actualizarBotonApartar();
    }

    private void actualizarBotonApartar() {
        btnApartar.setEnabled(medicoActivo && selectorFecha.getDate() != null
                && comboHoras.getSelectedItem() != null);
    }

    private void mostrarCita(Cita c) {
        lblCitaPaciente.setText("Informacion del paciente: " + c.getPaciente().getNombre());
        lblCitaMedico.setText("Nombre del medico: " + c.getMedico().getNombre());
        lblCitaConsultorio.setText("Consultorio: " + c.getMedico().getConsultorio());
        lblCitaFecha.setText("Fecha y hora: " + c.getFechaYHora().format(FORMATO)
                + " - " + c.getFechaFin().toLocalTime());
        cards.show(zonaDerecha, CARD_CITA);
        medicoActivo = false;
        actualizarBotonApartar();
        actualizando = true;              // permite volver a elegir el mismo médico
        listaMedicos.clearSelection();
        actualizando = false;
        JOptionPane.showMessageDialog(this, "Cita apartada exitosamente.", "Aviso",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarAvisoFechaNoDisponible() {
        cargarHorarios();
        JOptionPane.showMessageDialog(this, "El horario seleccionado no esta disponible",
                "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private LocalDateTime fechaSeleccionada() {
        return LocalDateTime.of(selectorFecha.getDate(), (LocalTime) comboHoras.getSelectedItem());
    }
}
