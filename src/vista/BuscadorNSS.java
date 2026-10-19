package vista;

import controlador.IControlRegistrarCita;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import modelo.EstadoRegistro;
import modelo.IModel;
import modelo.IObservador;

/** Pantalla "Login": pide el número de seguridad social. */
public class BuscadorNSS extends JFrame implements IObservador {
    private final IControlRegistrarCita control;
    private final JTextField txtNSS = new JTextField(22);
    private final JLabel lblError = new JLabel(" ");
    private final JButton btnIngresar = new JButton("Ingresar");

    public BuscadorNSS(IControlRegistrarCita control) {
        super("Login");
        this.control = control;
        construirInterfaz();
    }

    private void construirInterfaz() {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(new Color(220, 220, 220));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titulo = new JLabel("Introduzca su numero de seguridad social");
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        txtNSS.setMaximumSize(new Dimension(300, 28));
        lblError.setForeground(Color.RED);
        lblError.setAlignmentX(CENTER_ALIGNMENT);
        btnIngresar.setAlignmentX(CENTER_ALIGNMENT);

        tarjeta.add(titulo);
        tarjeta.add(javax.swing.Box.createVerticalStrut(10));
        tarjeta.add(lblError);
        tarjeta.add(javax.swing.Box.createVerticalStrut(4));
        tarjeta.add(txtNSS);
        tarjeta.add(javax.swing.Box.createVerticalStrut(14));
        tarjeta.add(btnIngresar);

        JPanel fondo = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        fondo.setBackground(new Color(245, 245, 245));
        fondo.add(tarjeta);

        // La vista solo delega al control; no decide nada.
        btnIngresar.addActionListener(e -> control.ingresarNSS(txtNSS.getText()));
        txtNSS.addActionListener(e -> control.ingresarNSS(txtNSS.getText()));

        setContentPane(fondo);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void update(IModel model) {
        EstadoRegistro estado = model.getEstado();
        if (estado == EstadoRegistro.NSS_INVALIDO) {          // Flujo alternativo A1
            lblError.setText("Numero de seguridad social no valido");
            txtNSS.selectAll();
            txtNSS.requestFocusInWindow();
        } else if (estado == EstadoRegistro.PACIENTE_ENCONTRADO) {
            lblError.setText(" ");
            setVisible(false);
        }
    }
}
