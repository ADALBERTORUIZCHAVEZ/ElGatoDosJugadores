
package proyectoelgato1;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.border.LineBorder;

public class ClienteGatoUI extends javax.swing.JFrame {

    private final Color AZUL_UNISON = Color.decode("#00529e");
    private final Color AZUL_OSCURO = Color.decode("#015294");
    private final Color DORADO_UNISON = Color.decode("#f8bb00");
    private final Color DORADO_OSCURO = Color.decode("#d99e30");
    private final Font FUENTE_GENERAL = new Font("Segoe UI", Font.BOLD, 14);
    
    private JTextField txtIp = new JTextField("127.0.0.1");
    private JTextField txtPuerto = new JTextField("5000");
    private JTextField txtNombre = new JTextField("Jugador");

    private JButton btnConectar = new JButton("Conectar");
    private JButton btnReiniciar = new JButton("Reiniciar");

    private JButton[] botones = new JButton[9];

    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;

    private int miId = 0;
    private int turnoActual = 0;

    public ClienteGatoUI() {
        super("Cliente Gato");
        setSize(400, 550);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Color de fondo de la ventana
        getContentPane().setBackground(Color.WHITE);

        JLabel l1 = new JLabel("IP:");
        l1.setBounds(30, 20, 50, 20);
        add(l1);
        txtIp.setBounds(80, 20, 150, 20);
        add(txtIp);

        JLabel l2 = new JLabel("Puerto:");
        l2.setBounds(30, 50, 50, 20);
        add(l2);
        txtPuerto.setBounds(80, 50, 150, 20);
        add(txtPuerto);

        JLabel l3 = new JLabel("Nombre:");
        l3.setBounds(30, 80, 50, 20);
        add(l3);
        txtNombre.setBounds(80, 80, 150, 20);
        add(txtNombre);

        btnConectar.setBounds(240, 20, 90, 80);
        add(btnConectar);
        estilizarBoton(btnConectar, DORADO_UNISON, AZUL_OSCURO);
        
        btnReiniciar.setBounds(130, 450, 100, 35);
        estilizarBoton(btnReiniciar, DORADO_UNISON, AZUL_OSCURO);
        add(btnReiniciar);

        btnConectar.addActionListener(e -> conectar());
        btnReiniciar.addActionListener(e-> salida.println("REINICIAR"));
        

        int x = 30, y = 130;
        for (int i = 0; i < 9; i++) {
            botones[i] = new JButton("");
            botones[i].setBounds(x, y, 90, 90);
            botones[i].setFont(new Font("Arial", Font.BOLD, 40));
            botones[i].setBackground(Color.WHITE);
            botones[i].setBorder(new LineBorder(AZUL_UNISON, 2));
            botones[i].setEnabled(false);
            
            final int pos = i; 
            botones[i].addActionListener(e -> {
                salida.println("JUGAR:" + (pos + 1)); 
            });

            add(botones[i]);

            x += 100;
            if ((i + 1) % 3 == 0) {
                x = 30;
                y += 100;
            }
        }

        setVisible(true);
    }

    private void conectar() {
        if (socket != null && socket.isConnected()) {
            JOptionPane.showMessageDialog(this, "Ya estás conectado");
            return;
        }

        try {
            socket = new Socket(txtIp.getText(), Integer.parseInt(txtPuerto.getText()));
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            btnConectar.setEnabled(false);
            new Thread(this::escucharServidor).start();
            JOptionPane.showMessageDialog(this, "Conectado al servidor. Esperando oponente...");

        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar: " + e.getMessage());
        }
    }

    private void escucharServidor() {
        try {
            String msg;
            while ((msg = entrada.readLine()) != null) {

                if (msg.startsWith("ASIGNAR:")) {
                    miId = Integer.parseInt(msg.substring(8));
                    SwingUtilities.invokeLater(() -> this.setTitle("Cliente Gato - Jugador " + miId + " (" + (miId == 1 ? "X" : "O") + ")"));
                }

                else if (msg.startsWith("INICIO:")) {
                    JOptionPane.showMessageDialog(this, msg.substring(7));
                }

                else if (msg.startsWith("TABLERO:")) {
                    actualizarTablero(msg.substring(8));
                }

                else if (msg.startsWith("TURNO:")) {
                    turnoActual = Integer.parseInt(msg.substring(6));
                    SwingUtilities.invokeLater(() -> habilitarBotones(turnoActual == miId));
                }

                else if (msg.startsWith("FIN:")) {
                    JOptionPane.showMessageDialog(this, msg.substring(4));
                    SwingUtilities.invokeLater(() -> habilitarBotones(false));
                }

                else if (msg.startsWith("MSG:")) {
                    JOptionPane.showMessageDialog(this, msg.substring(4));
                }
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Desconectado del servidor");
        }
    }

    private void actualizarTablero(String t) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < 9; i++) {
                botones[i].setText(t.charAt(i) == ' ' ? "" : String.valueOf(t.charAt(i)));

                // Bloquear casillas ya usadas
                botones[i].setEnabled(t.charAt(i) == ' ');
            }
        });
    }

    private void habilitarBotones(boolean habilitar) {
        for (int i = 0; i < 9; i++) {
            if (botones[i].getText().equals("")) {
                botones[i].setEnabled(habilitar);
            }
        }
    }
    private void reiniciarJuego() {
        for (JButton b : botones) {
            b.setText("");
            b.setEnabled(false);
        }
    }
    
    private void estilizarInput(JTextField txt, int x, int y) {
        txt.setBounds(x, y, 140, 25);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(new LineBorder(AZUL_UNISON, 1));
        add(txt);
    }

    private void estilizarBoton(JButton btn, Color fondo, Color texto) {
        btn.setBackground(fondo);
        btn.setForeground(texto);
        btn.setFont(FUENTE_GENERAL);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(AZUL_OSCURO, 2));
        btn.setOpaque(true); 
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClienteGatoUI::new);

    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents



    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
