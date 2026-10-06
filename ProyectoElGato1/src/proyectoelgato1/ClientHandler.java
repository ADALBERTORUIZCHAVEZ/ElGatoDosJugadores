package proyectoelgato1;

import java.io.*;
import java.net.*;

public class ClientHandler {

    private Socket socket;
    private ServidorGato servidor;
    private int idJugador;

    private BufferedReader entrada;
    private PrintWriter salida;

    public ClientHandler(Socket socket, ServidorGato servidor, int id) {
        this.socket = socket;
        this.servidor = servidor;
        this.idJugador = id;

        try {
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void iniciar() {
        Thread hilo = new Thread(() -> {
            try {
                String mensaje;
                while ((mensaje = entrada.readLine()) != null) {

                    // Protocolo: "JUGAR:X"
                    if (mensaje.startsWith("JUGAR:")) {
                        int pos = Integer.parseInt(mensaje.substring(6));
                        servidor.realizarJugada(idJugador, pos);
                    } else if (mensaje.equals("REINICIAR")) {
                        servidor.reiniciarJuego();
                    }
                }

            } catch (IOException e) {
                System.out.println("Jugador " + idJugador + " desconectado.");
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }

                servidor.broadcast("MSG:Jugador " + idJugador + " se ha desconectado");
            }
        });

        hilo.start();
    }

    public void enviar(String msg) {
        salida.println(msg);
    }

    public int getIdJugador() {
        return idJugador;
    }
}
