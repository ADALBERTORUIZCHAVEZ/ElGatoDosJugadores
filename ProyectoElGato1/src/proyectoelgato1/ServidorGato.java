
package proyectoelgato1;

import java.io.*;
import java.net.*;

public class ServidorGato {
    private static final int PUERTO = 5000;

    private ClientHandler jugador1;
    private ClientHandler jugador2;

    private Gato juego = new Gato();
    private int turno = 1; // 1 = X, 2 = O

    public static void main(String[] args) {
        new ServidorGato().iniciar();
    }

    public void iniciar() {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en puerto " + PUERTO);

            System.out.println("Esperando jugador 1...");
            Socket socket1 = servidor.accept();
            jugador1 = new ClientHandler(socket1, this, 1);
            jugador1.enviar("ASIGNAR:1");
            jugador1.iniciar();

            System.out.println("Esperando jugador 2...");
            Socket socket2 = servidor.accept();
            jugador2 = new ClientHandler(socket2, this, 2);
            jugador2.enviar("ASIGNAR:2");
            jugador2.iniciar();

            broadcast("INICIO:El juego ha comenzado");
            enviarTablero();
            enviarTurno();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void realizarJugada(int jugador, int pos) {

        char ficha = (jugador == 1) ? 'X' : 'O';

        if ((jugador == 1 && turno != 1) || (jugador == 2 && turno != 2)) {
            getJugador(jugador).enviar("MSG:No es tu turno");
            return;
        }

        if (!juego.jugar(pos, ficha)) {
            getJugador(jugador).enviar("MSG:Jugada inválida");
            return;
        }

        enviarTablero();

        if (juego.hayGanador()) {
            broadcast("FIN:Ganador:" + juego.getGanador());
            return;
        }

        if (juego.estaLleno()) {
            broadcast("FIN:Empate");
            return;
        }

        turno = (turno == 1) ? 2 : 1;
        enviarTurno();
    }

    public void enviarTablero() {
        String estado = obtenerSoloCasillas();
        broadcast("TABLERO:" + estado);
    }

    private String obtenerSoloCasillas() {
        // Devuelve algo como: "X O  X   O"
        StringBuilder sb = new StringBuilder();
        for (char c : juego.tablero().replace("|", "")
                .replace("-", "")
                .replace("\n", "")
                .toCharArray()) {

            if (c == 'X' || c == 'O' || c == ' ')
                sb.append(c);
        }
        return sb.substring(0, 9);
    }

    public void enviarTurno() {
        broadcast("TURNO:" + turno);
    }

    public void broadcast(String msg) {
        jugador1.enviar(msg);
        jugador2.enviar(msg);
    }

    public ClientHandler getJugador(int id) {
        return id == 1 ? jugador1 : jugador2;
    }
    
    public synchronized void reiniciarJuego() {
    juego.reiniciar();
    turno = 1; // Vuelve a empezar la X
    
    broadcast("MSG:El juego ha sido reiniciado por un jugador.");
    enviarTablero(); // Enviará el tablero vacío
    enviarTurno();   // Enviará TURNO:1
}
 
}
