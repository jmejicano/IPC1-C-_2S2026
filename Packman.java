/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package packman;

/**
 *
 * @author cvgjo
 */
import java.util.Scanner;
import java.util.Random;

public class Packman {
    
    // Constantes del juego
    private static final char FANTASMA = '@';
    private static final char PREMIO_SIMPLE = '0';
    private static final char PREMIO_ESPECIAL = '$';
    private static final char PARED = 'X';
    private static final char PACMAN = '<';
    private static final char VACIO = ' ';
    
    // Variables de juego
    private static String nombreUsuario;
    private static int puntaje = 0;
    private static int vidas = 3;
    private static char[][] tablero;
    private static int filas, columnas;
    private static int pacmanFila, pacmanColumna;
    private static int premiosRestantes = 0;
    private static boolean juegoActivo = false;
    private static boolean juegoPausado = false;
    
    // Historial de partidas (máximo 100 partidas)
    private static String[] historialNombres = new String[100];
    private static int[] historialPuntos = new int[100];
    private static int historialCount = 0;
    
    private static Scanner scanner = new Scanner(System.in);
    private static Random random = new Random();
    
    public static void main(String[] args) {
        mostrarMenuPrincipal();
    }
    
    private static void mostrarMenuPrincipal() {
        int opcion;
        //dibujo menu
        do {
            System.out.println("\n=== PAC-MAN CONSOLA ===");
            System.out.println("1. Iniciar Juego");
            System.out.println("2. Historial de Partidas");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opcion: ");
            
            opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer
            //menu
            switch(opcion) {
                case 1:
                    iniciarJuego();
                    break;
                case 2:
                    mostrarHistorial();
                    break;
                case 3:
                    System.out.println("Gracias por jugar!");
                    break;
                default:
                    System.out.println("Opcion no valida");
            }
        } while(opcion != 3);
    }
    
    private static void iniciarJuego() {
        // Solicitar nombre de usuario
        System.out.print("Ingrese su nombre de usuario: ");
        nombreUsuario = scanner.nextLine();
        
        // Configurar tablero
        configurarTablero();
        
        // Inicializar juego
        puntaje = 0;
        vidas = 3;
        juegoActivo = true;
        juegoPausado = false;
        
        // Posicionar Pac-Man
        posicionarPacMan();
        
        // Iniciar loop del juego
        loopJuego();
    }
    
    private static void configurarTablero() {
        char tipoTablero;
        //opciones menu para iniciar
        do {
            System.out.print("Seleccione tipo de tablero (P) Pequeio 5x6, (G) Grande 10x10: ");
            tipoTablero = scanner.nextLine().toUpperCase().charAt(0);
        } while(tipoTablero != 'P' && tipoTablero != 'G');
        
        if(tipoTablero == 'P') {
            filas = 5;
            columnas = 6;
        } else {
            filas = 10;
            columnas = 10;
        }
        
        tablero = new char[filas][columnas];
        inicializarTableroVacio();
        
        int totalEspacios = filas * columnas;
        int maxPremios = (int)(totalEspacios * 0.4);
        int maxParedes = (int)(totalEspacios * 0.2);
        int maxTrampas = (int)(totalEspacios * 0.2);
        
        // Solicitar cantidades
        int cantidadPremios = solicitarCantidad("premios", 1, maxPremios);
        int cantidadParedes = solicitarCantidad("paredes", 1, maxParedes);
        int cantidadTrampas = solicitarCantidad("trampas", 1, maxTrampas);
        
        // Colocar elementos aleatoriamente
        colocarElementos(PREMIO_SIMPLE, cantidadPremios, true);
        colocarElementos(PARED, cantidadParedes, false);
        colocarElementos(FANTASMA, cantidadTrampas, false);
        
        premiosRestantes = cantidadPremios;
    }
    
    private static void inicializarTableroVacio() {
        for(int i = 0; i < filas; i++) {
            for(int j = 0; j < columnas; j++) {
                tablero[i][j] = VACIO;
            }
        }
    }
    
    private static int solicitarCantidad(String tipo, int min, int max) {
        int cantidad;
        do {
            System.out.print("Ingrese cantidad de " + tipo + " (" + min + "-" + max + "): ");
            cantidad = scanner.nextInt();
            scanner.nextLine();
        } while(cantidad < min || cantidad > max);
        return cantidad;
    }
    
    private static void colocarElementos(char elemento, int cantidad, boolean esPremio) {
        int colocados = 0;
        
        while(colocados < cantidad) {
            int fila = random.nextInt(filas);
            int columna = random.nextInt(columnas);
            
            if(tablero[fila][columna] == VACIO) {
                if(esPremio && random.nextBoolean()) {
                    tablero[fila][columna] = PREMIO_ESPECIAL;
                } else {
                    tablero[fila][columna] = elemento;
                }
                colocados++;
            }
        }
    }
    
    private static void posicionarPacMan() {
        int fila, columna;
        
        do {
            mostrarTablero();
            System.out.print("Ingrese fila para Pac-Man (0-" + (filas-1) + "): ");
            fila = scanner.nextInt();
            System.out.print("Ingrese columna para Pac-Man (0-" + (columnas-1) + "): ");
            columna = scanner.nextInt();
            scanner.nextLine();
        } while(fila < 0 || fila >= filas || columna < 0 || columna >= columnas || 
                tablero[fila][columna] == PARED);
        
        pacmanFila = fila;
        pacmanColumna = columna;
        tablero[pacmanFila][pacmanColumna] = PACMAN;
    }
    
    private static void loopJuego() {
        while(juegoActivo && vidas > 0 && premiosRestantes > 0) {
            mostrarPanelControl();
            mostrarTablero();
            
            if(!juegoPausado) {
                System.out.print("Movimiento (8:arriba, 5:abajo, 6:derecha, 4:izquierda, F:Pausa): ");
                String input = scanner.nextLine();
                
                if(input.equalsIgnoreCase("F")) {
                    pausarJuego();
                } else {
                    procesarMovimiento(input);
                }
            }
        }
        
        // Terminar partida
        if(vidas == 0) {
            System.out.println("Game Over! Te quedaste sin vidas.");
        } else if(premiosRestantes == 0) {
            System.out.println("Felicidades!!! Has ganado la partida.");
        }
        
        registrarPartida();
    }
    
    private static void mostrarPanelControl() {
        System.out.println("\n=== PANEL DE CONTROL ===");
        System.out.println("Usuario: " + nombreUsuario);
        System.out.println("Puntaje: " + puntaje);
        System.out.println("Vidas: " + vidas);
        System.out.println("Premios restantes: " + premiosRestantes);
        System.out.println("=========================");
    }
    
    private static void mostrarTablero() {
        System.out.println("\n   " + "=".repeat(columnas * 2 + 1));
        for(int i = 0; i < filas; i++) {
            System.out.print("  |");
            for(int j = 0; j < columnas; j++) {
                System.out.print(tablero[i][j] + " ");
            }
            System.out.println("|");
        }
        System.out.println("   " + "=".repeat(columnas * 2 + 1));
    }
    
    private static void procesarMovimiento(String tecla) {
        int nuevaFila = pacmanFila;
        int nuevaColumna = pacmanColumna;
        
        switch(tecla) {
            case "8": nuevaFila = (pacmanFila - 1 + filas) % filas; break;
            case "5": nuevaFila = (pacmanFila + 1) % filas; break;
            case "6": nuevaColumna = (pacmanColumna + 1) % columnas; break;
            case "4": nuevaColumna = (pacmanColumna - 1 + columnas) % columnas; break;
            default: System.out.println("Tecla no valida"); return;
        }
        
        // Verificar si hay pared
        if(tablero[nuevaFila][nuevaColumna] == PARED) {
            System.out.println("Hay una pared No puedes moverte alli.");
            return;
        }
        
        // Mover Pac-Man
        tablero[pacmanFila][pacmanColumna] = VACIO;
        
        // Verificar elementos en la nueva posición
        char elemento = tablero[nuevaFila][nuevaColumna];
        
        if(elemento == FANTASMA) {
            vidas--;
            System.out.println("¡Te encontraste con un fantasma! Vidas restantes: " + vidas);
        } else if(elemento == PREMIO_SIMPLE) {
            puntaje += 10;
            premiosRestantes--;
            System.out.println("¡Premio simple! +10 puntos");
        } else if(elemento == PREMIO_ESPECIAL) {
            puntaje += 15;
            premiosRestantes--;
            System.out.println("¡Premio especial! +15 puntos");
        }
        
        pacmanFila = nuevaFila;
        pacmanColumna = nuevaColumna;
        tablero[pacmanFila][pacmanColumna] = PACMAN;
    }
    
    private static void pausarJuego() {
        juegoPausado = true;
        int opcion;
        
        do {
            System.out.println("\n=== JUEGO PAUSADO ===");
            System.out.println("3. Regresar al juego");
            System.out.println("4. Terminar partida");
            System.out.print("Seleccione opcion: ");
            
            opcion = scanner.nextInt();
            scanner.nextLine();
            
            switch(opcion) {
                case 3:
                    juegoPausado = false;
                    break;
                case 4:
                    juegoActivo = false;
                    juegoPausado = false;
                    System.out.println("Partida terminada.");
                    break;
                default:
                    System.out.println("Opcion no valida");
            }
        } while(opcion != 3 && opcion != 4 && juegoPausado);
    }
    
    private static void registrarPartida() {
        if(historialCount < 100) {
            // Desplazar registros para mantener orden inverso (más reciente primero)
            for(int i = historialCount; i > 0; i--) {
                historialNombres[i] = historialNombres[i-1];
                historialPuntos[i] = historialPuntos[i-1];
            }
            
            historialNombres[0] = nombreUsuario;
            historialPuntos[0] = puntaje;
            historialCount++;
        }
    }
    
    private static void mostrarHistorial() {
        if(historialCount == 0) {
            System.out.println("\nNo hay partidas registradas.");
            return;
        }
        
        System.out.println("\n=== HISTORIAL DE PARTIDAS ===");
        System.out.println("Usuario\t\tPuntos");
        System.out.println("---------------------");
        
        for(int i = 0; i < historialCount; i++) {
            System.out.println(historialNombres[i] + "\t\t" + historialPuntos[i]);
        }
        System.out.println("==============================");
        
        System.out.print("Presione Enter para continuar...");
        scanner.nextLine();
    }
}