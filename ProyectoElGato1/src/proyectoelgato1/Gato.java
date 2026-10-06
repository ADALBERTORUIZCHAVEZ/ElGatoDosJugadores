
package proyectoelgato1;

public class Gato {
    public char[] tablero = {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
    private char ganador = ' ';

    public boolean jugar(int pos, char jugador) {
        if (pos < 1 || pos > 9) return false;
        if (tablero[pos - 1] != ' ') return false;
        tablero[pos - 1] = jugador;
        verificarGanador();
        return true;
    }

    public boolean hayGanador() {
        return ganador != ' ';
    }

    public char getGanador() {
        return ganador;
    }

    public boolean estaLleno() {
        for (char c : tablero) if (c == ' ') return false;
        return true;
    }

    public String tablero() {
        return
                tablero[0]+"|"+tablero[1]+"|"+tablero[2]+"\n"+
                        "-+-+-\n"+
                        tablero[3]+"|"+tablero[4]+"|"+tablero[5]+"\n"+
                        "-+-+-\n"+
                        tablero[6]+"|"+tablero[7]+"|"+tablero[8]+"\n";
    }

    private void verificarGanador() {
        int[][] lineas = {
                {0,1,2},{3,4,5},{6,7,8},
                {0,3,6},{1,4,7},{2,5,8},
                {0,4,8},{2,4,6}
        };

        for (int[] l : lineas) {
            if (tablero[l[0]] != ' ' &&
                    tablero[l[0]] == tablero[l[1]] &&
                    tablero[l[1]] == tablero[l[2]]) {
                ganador = tablero[l[0]];
            }
        }
    }
    
    public void reiniciar() {
    for (int i = 0; i < 9; i++) {
        tablero[i] = ' ';
    }
    ganador = ' ';
}
}
