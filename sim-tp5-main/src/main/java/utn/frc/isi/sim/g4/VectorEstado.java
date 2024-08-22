package utn.frc.isi.sim.g4;

import java.util.*;

// import utn.frc.isi.sim.g4.Enums.EnumEstadosCliente;
import utn.frc.isi.sim.g4.Enums.*;
import utn.frc.isi.sim.g4.RungeKutta.RungeKutta;


public class VectorEstado {

    // Constantes
    static final int CANTIDAD_COLUMNAS_BASICAS = 34;
    static final int CAPACIDAD_BIBLIOTECA = 20;
    static final int CANTIDAD_COLUMNAS_POR_CLIENTE = 5;
    static int[] COLUMNAS_CON_TIEMPOS = { 5, 12, 13, 16, 17, 20, 21,
            38, 43, 48, 53, 58, 63, 68, 73, 78, 83, 88, 93, 98, 103, 108, 113, 118, 123, 128, 133 };//38, 43, 48, 53, 58, 63, 68, 73, 78, 83, 88, 93, 98, 103, 108, 113, 118, 123, 128, 133 }; // EnumEventos.getAllColumns()
    
    static Random random = new Random();

    // La siguiente lista contiene la columna de la hora de llegada de los clientes que estaban leyendo y pasaron a en cola
    static ArrayList<Integer> clientesQueEstabanLeyendoYAhoraEstanEnCola = new ArrayList<>();

    // Methods
    private static String calcularEvento(Object[] vectorAnterior) {
        int columnaTiempo = (int) selectColumnsAndFindMin(vectorAnterior, COLUMNAS_CON_TIEMPOS, "evento");

        return EnumEventos.fromColumn(columnaTiempo).getNombre();
    }

    private static double calcularReloj(Object[] vectorAnterior) {
        return selectColumnsAndFindMin(vectorAnterior, COLUMNAS_CON_TIEMPOS, "reloj");
    }

    private static double calcularRandom() {
        return random.nextDouble();
    }

    private static double calcularRandomParaTipoLlegada(String evento, double personasEnCola, String estadoBiblioteca, String estadoAnterior1, String estadoAnterior2, Object[] vectorAnterior) {
        if ((evento.equals(EnumEventos.Llegada.getNombre()) && personasEnCola == 0 && estadoBiblioteca.equals(EnumEstadosBiblioteca.Abierta.getNombre()) && 
                !(estadoAnterior1.equals(EnumEstadosServidor.Ocupado.getNombre()) && estadoAnterior2.equals(EnumEstadosServidor.Ocupado.getNombre())))
            ) {
            return random.nextDouble();
        } else if ((!evento.equals(EnumEventos.Llegada.getNombre()) && !evento.equals(EnumEventos.FinLectura.getNombre()) && personasEnCola > 0)) {
            // Caso especial: Personas que estaban leyendo y pasaron a en cola
            Integer indiceMenorHoraLlegadaEnCola = buscarMenorHoraLlegadaEnCola(vectorAnterior);
            if (clientesQueEstabanLeyendoYAhoraEstanEnCola.contains(indiceMenorHoraLlegadaEnCola)) {
                // No se saca aqui, sino en calcularTipoLlegada
//                clientesQueEstabanLeyendoYAhoraEstanEnCola.remove(indiceMenorHoraLlegadaEnCola);
                return 0;
            }

            return random.nextDouble();
        }
        
        return 0;
    }

    private static double calcularRandomParaPeticiones(String tipoLlegada){
        if (tipoLlegada.equals(EnumTiposLlegada.Peticion1.getNombre()) || tipoLlegada.equals(EnumTiposLlegada.Peticion2.getNombre())) {
            return random.nextDouble();
        }
        return 0;
    }

    private static double calcularRandomParaDevoluciones(String tipoLlegada) {
        // Si (el tipo llegada es devolucion 1 o 2)
        if (tipoLlegada.equals(EnumTiposLlegada.Devolucion1.getNombre()) || tipoLlegada.equals(EnumTiposLlegada.Devolucion2.getNombre())) {
            return random.nextDouble();
        }
        return 0;
    }

    private static double calcularRandomParaM(String tipoLlegada){
        if (tipoLlegada.equals(EnumTiposLlegada.Consulta1.getNombre()) || tipoLlegada.equals(EnumTiposLlegada.Consulta2.getNombre())) {
            return random.nextDouble();
        }
        return 0;
    }

    private static double calcularRandomParaSeVaSeQueda(String evento){
        if (evento.equals(EnumEventos.FinPeticion1.getNombre()) || evento.equals(EnumEventos.FinPeticion2.getNombre())) {
            return random.nextDouble();
        }
        return 0;
    }

    private static double calcularRandomParaLecturas(String quehace){
        if (quehace.equals(EnumLuegoPeticion.SeQueda.getNombre())) {
            return random.nextDouble();
        }
        return 0;
    }

    private static double calcularRandomParaProxLlegada(String evento){
        if (evento.equals(EnumEventos.Inicializacion.getNombre()) || evento.equals(EnumEventos.Llegada.getNombre())) {
            return random.nextDouble();
        }
        return 0; 
    }

    private static double calcularRNDExpTiempoLlegada(double rnd, String evento) {
        if (evento.equals(EnumEventos.Inicializacion.getNombre()) || evento.equals(EnumEventos.Llegada.getNombre())) {
            return -App.mediaLlegada * Math.log(1 - rnd);
        }
        return 0; 
    }

    private static double calcularProximaLlegada(double relojActual, double rndLlegada, String evento, double ProximaLLegadaActual) {
        if (evento.equals(EnumEventos.Inicializacion.getNombre()) || evento.equals(EnumEventos.Llegada.getNombre())) {
            return relojActual + rndLlegada;
        }
        return ProximaLLegadaActual; 
    }

    private static String calcularEstadoServidor1(String estadoAnterior, String evento, double personasencola, String estadoBiblioteca) {
        if (evento.equals(EnumEventos.Llegada.getNombre()) && estadoBiblioteca.equals(EnumEstadosBiblioteca.Abierta.getNombre())) {
            if (estadoAnterior.equals(EnumEstadosServidor.Libre.getNombre())) {
                return EnumEstadosServidor.Ocupado.getNombre();
            }
        } else if (evento.equals(EnumEventos.FinPeticion1.getNombre()) || evento.equals(EnumEventos.FinConsulta1.getNombre()) || evento.equals(EnumEventos.FinDevolucion1.getNombre())) {
            if (estadoAnterior.equals(EnumEstadosServidor.Ocupado.getNombre()) && personasencola == 0) {
                return EnumEstadosServidor.Libre.getNombre();
            }
        } else if (evento.equals(EnumEventos.FinLectura.getNombre())) {
            if (estadoAnterior.equals(EnumEstadosServidor.Libre.getNombre())) {
                return EnumEstadosServidor.Ocupado.getNombre();
            }
        }
        return estadoAnterior;
    }

    private static String calcularEstadoServidor2(String estadoAnterior, String estadoServidor1, String evento, double personasencola, String estadoBiblioteca) {
        if (evento.equals(EnumEventos.Llegada.getNombre()) && estadoBiblioteca.equals(EnumEstadosBiblioteca.Abierta.getNombre())) {
            if (estadoAnterior.equals(EnumEstadosServidor.Libre.getNombre()) && estadoServidor1.equals(EnumEstadosServidor.Ocupado.getNombre())) {
                return EnumEstadosServidor.Ocupado.getNombre();
            }
        } else if (evento.equals(EnumEventos.FinPeticion2.getNombre()) || evento.equals(EnumEventos.FinConsulta2.getNombre()) || evento.equals(EnumEventos.FinDevolucion2.getNombre())) {
            if (estadoAnterior.equals(EnumEstadosServidor.Ocupado.getNombre()) && personasencola == 0) {
                return EnumEstadosServidor.Libre.getNombre();
            }
        } else if (evento.equals(EnumEventos.FinLectura.getNombre())) {
            if (estadoAnterior.equals(EnumEstadosServidor.Libre.getNombre()) && estadoServidor1.equals(EnumEstadosServidor.Ocupado.getNombre())) {
                return EnumEstadosServidor.Ocupado.getNombre();
            }
        }
        return estadoAnterior;
    }


    private static String calcularTipoLlegada(double rnd, String evento, String EstadoServidorAnterior1, String EstadoServidorAnterior2, String EstadoServidorActual1, String EstadoServidorActual2, double personasencola,String estadoBiblioteca, Object[] vectorAnterior) {
        // Si (el evento es llegada) y (no hay personas en cola) y (la biblio esta abierta)
        if ((evento.equals(EnumEventos.Llegada.getNombre()) && personasencola == 0 && estadoBiblioteca.equals(EnumEstadosBiblioteca.Abierta.getNombre()))) {
            if (EstadoServidorAnterior1.equals(EnumEstadosServidor.Libre.getNombre()) && EstadoServidorActual1.equals(EnumEstadosServidor.Ocupado.getNombre())) {
                if (0.0 <= rnd && rnd < App.probabilidadPedir) {
                    return EnumTiposLlegada.Peticion1.getNombre();
                } else if (App.probabilidadPedir <= rnd && rnd < App.probabilidadPedir + App.probabilidadDevolver) {
                    return EnumTiposLlegada.Devolucion1.getNombre();
                } else if (App.probabilidadPedir + App.probabilidadDevolver <= rnd && rnd <= 1.0) {
                    return EnumTiposLlegada.Consulta1.getNombre();
                } else {
                    throw new IllegalArgumentException("RND fuera de rango: " + rnd);
                }
            } else if (EstadoServidorAnterior2.equals(EnumEstadosServidor.Libre.getNombre()) && EstadoServidorActual2.equals(EnumEstadosServidor.Ocupado.getNombre())) {
                if (0.0 <= rnd && rnd < App.probabilidadPedir) {
                    return EnumTiposLlegada.Peticion2.getNombre();
                } else if (App.probabilidadPedir <= rnd && rnd < App.probabilidadPedir + App.probabilidadDevolver) {
                    return EnumTiposLlegada.Devolucion2.getNombre();
                } else if (App.probabilidadPedir + App.probabilidadDevolver <= rnd && rnd <= 1.0) {
                    return EnumTiposLlegada.Consulta2.getNombre();
                } else {
                    throw new IllegalArgumentException("RND fuera de rango: " + rnd);
                }
            }
        // Sino, si (el evento no es ni llegada ni fin lectura) y (hay personas en cola)
        } else if (!(evento.equals(EnumEventos.Llegada.getNombre())) && personasencola > 0 && !(evento.equals(EnumEventos.FinLectura.getNombre())) ) {
            // Caso especial: Personas que estaban leyendo y pasaron a en cola
            Integer indiceMenorHoraLlegadaEnCola = buscarMenorHoraLlegadaEnCola(vectorAnterior);
            if (clientesQueEstabanLeyendoYAhoraEstanEnCola.contains(indiceMenorHoraLlegadaEnCola)) {
                clientesQueEstabanLeyendoYAhoraEstanEnCola.remove(indiceMenorHoraLlegadaEnCola);
                if (evento.equals(EnumEventos.FinPeticion1.getNombre()) || evento.equals(EnumEventos.FinConsulta1.getNombre()) || evento.equals(EnumEventos.FinDevolucion1.getNombre())){
                    return EnumTiposLlegada.Devolucion1.getNombre();
                } else {
                    return EnumTiposLlegada.Devolucion2.getNombre();
                }
            }

            // Si el servidor 1 es el que se libera
            if (EstadoServidorAnterior1.equals(EnumEstadosServidor.Ocupado.getNombre()) && EstadoServidorAnterior2.equals(EnumEstadosServidor.Ocupado.getNombre()) &&
                (evento.equals(EnumEventos.FinPeticion1.getNombre()) || evento.equals(EnumEventos.FinConsulta1.getNombre()) || evento.equals(EnumEventos.FinDevolucion1.getNombre()) )){
                if (0.0 <= rnd && rnd < App.probabilidadPedir) {
                    return EnumTiposLlegada.Peticion1.getNombre();
                } else if (App.probabilidadPedir <= rnd && rnd < App.probabilidadPedir + App.probabilidadDevolver) {
                    return EnumTiposLlegada.Devolucion1.getNombre();
                } else if (App.probabilidadPedir + App.probabilidadDevolver <= rnd && rnd <= 1.0) {
                    return EnumTiposLlegada.Consulta1.getNombre();
                } else {
                    throw new IllegalArgumentException("RND fuera de rango: " + rnd);
                }
            // Si el servidor 2 es el que se libera
            } else {
                if (0.0 <= rnd && rnd < App.probabilidadPedir) {
                    return EnumTiposLlegada.Peticion2.getNombre();
                } else if (App.probabilidadPedir <= rnd && rnd < App.probabilidadPedir + App.probabilidadDevolver) {
                    return EnumTiposLlegada.Devolucion2.getNombre();
                } else if (App.probabilidadPedir + App.probabilidadDevolver <= rnd && rnd <= 1.0) {
                    return EnumTiposLlegada.Consulta2.getNombre();
                } else {
                    throw new IllegalArgumentException("RND fuera de rango: " + rnd);
                }
            }
        // Sino, si (el evento es fin lectura) y (no hay personas en cola)
        } else if (evento.equals(EnumEventos.FinLectura.getNombre()) && personasencola == 0) {
            // Si (el servidor 1 estaba libre)
            if (EstadoServidorAnterior1.equals(EnumEstadosServidor.Libre.getNombre())) {
                return EnumTiposLlegada.Devolucion1.getNombre();
            // Sino, Si (el servidor 2 estaba libre)
            } else if (EstadoServidorAnterior2.equals(EnumEstadosServidor.Libre.getNombre())) {
                return EnumTiposLlegada.Devolucion2.getNombre();
            }
        }
        return "";
    }

    private static int buscarMenorHoraLlegadaEnCola (Object[] vectorAnterior) {
        ArrayList<Double> columnasHorasLlegada = new ArrayList<>();
        for (int NroCliente = 0; NroCliente < CAPACIDAD_BIBLIOTECA; NroCliente++) {
            int columnaActual = CANTIDAD_COLUMNAS_BASICAS + NroCliente * CANTIDAD_COLUMNAS_POR_CLIENTE;
            if (vectorAnterior[columnaActual].equals(EnumEstadosCliente.EnCola.getNombre())) {
                columnasHorasLlegada.add((double) (columnaActual + 1));
            }
        }

        if (!columnasHorasLlegada.isEmpty()) {
            int[] columnasHorasLlegadaArray = new int[columnasHorasLlegada.size()];
            for (int i = 0; i < columnasHorasLlegada.size(); i++) {
                columnasHorasLlegadaArray[i] = columnasHorasLlegada.get(i).intValue();
//                            System.out.println("que hay en el vector: " + columnasHorasLlegadaArray[i] + " " + vectorAnterior[columnasHorasLlegadaArray[i]]);
            }

            // Inicializa el valor mínimo con el primer elemento del array
            double minimo = (double) vectorAnterior[columnasHorasLlegadaArray[0]];
            int indiceMinimo = columnasHorasLlegadaArray[0]; // Inicializa el índice del mínimo con el primer elemento del array

            // Itera sobre los elementos del array para encontrar el mínimo y su índice
            // (empieza en 1 porque ya se inicializó el mínimo al elemento 0)
            for (int i = 1; i < columnasHorasLlegadaArray.length; i++) {
                // Obtén el valor actual como double
                double valorActual = (double) vectorAnterior[columnasHorasLlegadaArray[i]];

                // Comprueba si el elemento actual es menor que el mínimo actual
                if (valorActual < minimo) {
                    // Si es así, actualiza el valor mínimo y guarda el índice
                    minimo = valorActual;
                    indiceMinimo = columnasHorasLlegadaArray[i];
                }
            }
            return indiceMinimo;
        }
        return -1;
    }

    private static double calcularRNDExpTiempoPeticion(double rnd, String tipoLlegada, String estadoanterior1, String estadoanterior2) {
        if ((tipoLlegada.equals(EnumTiposLlegada.Peticion1.getNombre()) || tipoLlegada.equals(EnumTiposLlegada.Peticion2.getNombre()))) {
            return -App.MEDIA_PETICION * Math.log(1 - rnd);
        }
        return 0;
    }

    private static double calcularFinPeticion1(double relojActual, double rndPeticion, String tipoLlegada, String estadoanterior1, String estadoanterior2, double FinPeticion1Anterior, String evento, double personasencola) {
        if (evento.equals(EnumEventos.FinPeticion1.getNombre()) && !(tipoLlegada.equals(EnumTiposLlegada.Peticion1.getNombre()))) {
            return 0;
        }
        if (tipoLlegada.equals(EnumTiposLlegada.Peticion1.getNombre())) {
            return relojActual + rndPeticion;
        }
        return FinPeticion1Anterior;
    }

    private static double calcularFinPeticion2(double relojActual, double rndPeticion, String tipoLlegada, String estadoanterior1, String estadoanterior2, double FinPeticion2Anterior, String evento, double personasencola) {
        if (evento.equals(EnumEventos.FinPeticion2.getNombre()) && !(tipoLlegada.equals(EnumTiposLlegada.Peticion2.getNombre()))) {
            return 0;
        }
        if (tipoLlegada.equals(EnumTiposLlegada.Peticion2.getNombre())) {
            return relojActual + rndPeticion;
        }
        return FinPeticion2Anterior;
    }

    private static double calcularRNDUniformeM(double rnd , String tipoLlegada, String estadoanterior1, String estadoanterior2) {
        if ((tipoLlegada.equals(EnumTiposLlegada.Consulta1.getNombre()) || tipoLlegada.equals(EnumTiposLlegada.Consulta2.getNombre()))) {
            return App.minM + rnd * (App.maxM - App.minM);
        }
        return 0;
    }

    private static double calcularFinConsulta1(double reloj, double M , String tipoLlegada, String estadoanterior1, String estadoanterior2, double FinConsulta1Anterior, String evento, double personasEnCola, double[][] matrizRungeKutta) {

        if (evento.equals(EnumEventos.FinConsulta1.getNombre()) && !(tipoLlegada.equals(EnumTiposLlegada.Consulta1.getNombre()))){
            return 0;
        }
        if (tipoLlegada.equals(EnumTiposLlegada.Consulta1.getNombre())) {
            return reloj + RungeKutta.obtenerPrimerValorXSuperiorA(matrizRungeKutta, M);
        }
        return FinConsulta1Anterior;
    }

    private static double calcularFinConsulta2(double reloj, double M , String tipoLlegada, String estadoanterior1, String estadoanterior2, double FinConsulta2Anterior, String evento, double personasEnCola, double[][] matrizRungeKutta) {
        if (evento.equals(EnumEventos.FinConsulta2.getNombre()) && !(tipoLlegada.equals(EnumTiposLlegada.Consulta2.getNombre()))){
            return 0;
        }
        if (tipoLlegada.equals(EnumTiposLlegada.Consulta2.getNombre())){
            return reloj + RungeKutta.obtenerPrimerValorXSuperiorA(matrizRungeKutta, M);
        }
        return FinConsulta2Anterior;
    }

    private static double calcularRNDUniformeTiempoDevolucion(double rnd , String tipoLlegada, String estadoanterior1, String estadoanterior2) {
        if ((tipoLlegada.equals(EnumTiposLlegada.Devolucion1.getNombre()) || tipoLlegada.equals(EnumTiposLlegada.Devolucion2.getNombre()))) {
            return App.MIN_TIEMPO_DEVOLUCION + rnd * (App.MAX_TIEMPO_DEVOLUCION - App.MIN_TIEMPO_DEVOLUCION);
        }
        return 0;
    }

    private static double calcularFinDevolucion1(double reloj, double rndDevolucion , String tipoLlegada, String estadoanterior1, String estadoanterior2, double FinDevolucion1Anterior, String evento, double personasEnCola) {
        if (evento.equals(EnumEventos.FinDevolucion1.getNombre()) && !(tipoLlegada.equals(EnumTiposLlegada.Devolucion1.getNombre()))) {
            return 0;
        }
        if (tipoLlegada.equals(EnumTiposLlegada.Devolucion1.getNombre())) {
            return reloj + rndDevolucion;
        }
        return FinDevolucion1Anterior;
    }

    private static double calcularFinDevolucion2(double reloj, double rndDevolucion , String tipoLlegada, String estadoanterior1, String estadoanterior2, double FinDevolucion2Anterior, String evento, double personasEnCola) {
        if (evento.equals(EnumEventos.FinDevolucion2.getNombre()) && !(tipoLlegada.equals(EnumTiposLlegada.Devolucion2.getNombre()))) {
            return 0;
        }
        if (tipoLlegada.equals(EnumTiposLlegada.Devolucion2.getNombre())) {
            return reloj + rndDevolucion;
        }
        return FinDevolucion2Anterior;
    }

    private static String calcularSeVaSeQueda(double rnd, String evento) {
        if (evento.equals(EnumEventos.FinPeticion1.getNombre()) || evento.equals(EnumEventos.FinPeticion2.getNombre())) {
            if (0.0 <= rnd && rnd < App.probabilidadSeRetira) {
                return EnumLuegoPeticion.SeVa.getNombre();
            } else if (App.probabilidadSeRetira <= rnd && rnd <= 1.0) {
                return EnumLuegoPeticion.SeQueda.getNombre();
            } else {
                throw new IllegalArgumentException("RND fuera de rango: " + rnd);
            }
        }
        return "";
    }

    private static double calcularRNDExpTiempoLectura(double rnd, String seVaSeQueda) {
        if (seVaSeQueda.equals(EnumLuegoPeticion.SeQueda.getNombre())) {
            return -App.mediaLectura * Math.log(1 - rnd);
        }
        return 0;
    }

    private static double calcularHoraFinLectura(double reloj, double rndLectura, String seVaSeQueda) {
        if (seVaSeQueda.equals(EnumLuegoPeticion.SeQueda.getNombre())) {
            return reloj + rndLectura;
        }
        return 0;
    }

    private static double calcularPersonasEnCola(String estadoAnteriorServidor1, String estadoAnteriorServidor2, double personasEnCola, String evento, String estadoBiblioteca) {
        // Si [(es llegada) y (estaba abierta)] o (es fin lectura)
        if ((evento.equals(EnumEventos.Llegada.getNombre()) && estadoBiblioteca.equals(EnumEstadosBiblioteca.Abierta.getNombre())) || evento.equals(EnumEventos.FinLectura.getNombre())) {
            // Si (ambos servidores estaban ocupados)
            if (estadoAnteriorServidor1.equals(EnumEstadosServidor.Ocupado.getNombre()) && estadoAnteriorServidor2.equals(EnumEstadosServidor.Ocupado.getNombre())) {
                return personasEnCola + 1;
            }
        } else if (evento.equals(EnumEventos.FinPeticion1.getNombre()) || evento.equals(EnumEventos.FinConsulta1.getNombre()) || evento.equals(EnumEventos.FinDevolucion1.getNombre()) ||
                evento.equals(EnumEventos.FinPeticion2.getNombre()) || evento.equals(EnumEventos.FinConsulta2.getNombre()) || evento.equals(EnumEventos.FinDevolucion2.getNombre())) {
            if (personasEnCola > 0) {
                return personasEnCola - 1;
            }
        }
        return personasEnCola;
    }

    private static double calcularPersonasEnBiblioteca(double personasEnBiblioteca, String evento, String seVaSeQueda) {
        if (evento.equals(EnumEventos.Llegada.getNombre())) {
            if (personasEnBiblioteca < 20) {
                return personasEnBiblioteca + 1;
            }
        } else if ((evento.equals(EnumEventos.FinConsulta1.getNombre()) ||
            evento.equals(EnumEventos.FinConsulta2.getNombre()) ||
            evento.equals(EnumEventos.FinDevolucion1.getNombre()) ||
            evento.equals( EnumEventos.FinDevolucion2.getNombre()) ||
            //evento.equals(EnumEventos.FinLectura.getNombre()) || //esta linea estaba comentada, si falla algo fijarse si camentar esta linea lo arregla
            seVaSeQueda.equals(EnumLuegoPeticion.SeVa.getNombre())) &&
            personasEnBiblioteca > 0)
                {
            return personasEnBiblioteca - 1;
        }
        return personasEnBiblioteca;
    }
    
    private static String calcularEstadoBiblioteca(double personasEnBiblioteca) {
        return personasEnBiblioteca < CAPACIDAD_BIBLIOTECA ? EnumEstadosBiblioteca.Abierta.getNombre() : EnumEstadosBiblioteca.Cerrada.getNombre();
    }
    
    private static double calcularContadorPersonasNoEntran(double contadorPersonasNoEntranAnterior, String evento, String estadoActualBiblioteca, double personasEnLaBibliotecaAnterior) {
//        System.out.printf("Contador anterior: %s\nEvento: %s\nEstado Actual Biblioteca: %s", contadorPersonasNoEntranAnterior, evento, estadoActualBiblioteca);
        if (evento.equals(EnumEventos.Llegada.getNombre()) && estadoActualBiblioteca.equals(EnumEstadosBiblioteca.Cerrada.getNombre()) && personasEnLaBibliotecaAnterior == CAPACIDAD_BIBLIOTECA) {
            return contadorPersonasNoEntranAnterior + 1;
        }
        return contadorPersonasNoEntranAnterior;
    }

    private static double calcularContadorPersonasTotal(double contadorPersonasTotal, String evento) {
        if (evento.equals(EnumEventos.Llegada.getNombre())) {
            return contadorPersonasTotal + 1;
        }
        return contadorPersonasTotal;
    }

    private static double calcularPromedioPersonasNoEntran(double contadorPersonasNoEntran, double contadorPersonasTotal) {
        return contadorPersonasNoEntran / contadorPersonasTotal;
        
    }


    public static double selectColumnsAndFindMin(Object[] vector, int[] columns, String mode) {
        if (vector == null || vector.length == 0) {
            throw new IllegalArgumentException("El vector está vacío o es nulo.");
        }

        if (mode == null || (!mode.equals("reloj") && !mode.equals("evento"))) {
            throw new IllegalArgumentException("El modo debe ser 'reloj' o 'evento'.");
        }

        double minValue = Double.MAX_VALUE; // Inicializar con el valor máximo posible de double
        int minColumn = -1; // Inicializar con -1 para indicar que no se ha encontrado un valor mínimo válido

        for (int column : columns) {
            if (column >= 0 && column < vector.length) {
                double value = ((Number) vector[column]).doubleValue(); // Convertir a double para comparar con el mínimo
                if (value != 0 && value < minValue) {
                    minColumn = column;
                    minValue = value;
                }
            }
        }

        if (minColumn == -1) {
            throw new IllegalArgumentException("No se encontró un valor válido en las columnas especificadas.");
        }

//        System.out.println("El valor mínimo es " + minValue + " en la columna " + minColumn + " y el modo es " + mode);

        return mode.equals("reloj") ? minValue : minColumn;
    }

    public static Object[][] generadorVectoresParImpar(double[][] matrizRungeKutta) {

        double reloj = 0;
        int fila = 0;

        int cantidadTotalColumnas = CANTIDAD_COLUMNAS_BASICAS + CAPACIDAD_BIBLIOTECA * CANTIDAD_COLUMNAS_POR_CLIENTE;

        Object[] vectorPar = new Object[cantidadTotalColumnas];
        Object[] vectorImpar = new Object[cantidadTotalColumnas];

        Object[][] matriz = new Object[App.primerFilaTablaJ + App.cantidadDeFilasAMostrarI < App.CANTIDAD_MAXIMA_ITERACIONES ?
            App.cantidadDeFilasAMostrarI + 1 : App.cantidadDeFilasAMostrarI][cantidadTotalColumnas];
        Object[] ultimaFila = null;

        // int[] horasLlegada = new int[CAPACIDAD_BIBLIOTECA];
        // for (int i = 0; i < CAPACIDAD_BIBLIOTECA; i++) {
        //     horasLlegada[i] = (CANTIDAD_COLUMNAS_BASICAS + 1) + i * CANTIDAD_COLUMNAS_POR_CLIENTE;
        // }

        while (reloj < App.tiempoSimulacion && fila < App.CANTIDAD_MAXIMA_ITERACIONES) {
            Object[] vectorActual = new Object[cantidadTotalColumnas];

            double nroEvento;
            String evento;
            double rnd1;
            double rnd1exp;
            double proximallegada;
            String estadoservidor1;
            String estadoservidor2;
            double rnd2;
            String tipoLlegada;
            double rnd3;
            double rnd3Tiempo;
            double finPeticionServidor1;
            double finPeticionServidor2;
            double rnd4;
            double rnd4UniformeTiempo;
            double finConsultaServidor1;
            double finConsultaServidor2;
            double rnd5;
            double rnd5UniformeTiempo;
            double finDevolucionServidor1;
            double finDevolucionServidor2;
            double rnd6;
            String seVaSeQueda;
            double rnd7;
            double rnd7exp;
            double personasEnCola;
            double personasEnBiblioteca;
            String estadoBiblioteca;
            double contadorPersonasNoEntran;
            double contadorPersonasTotal;
            double promedioPersonasNoEntran;
            double acumuladorPermanencia;
            double promedioPermanencia;

            if (fila == 0) {
                nroEvento = fila;
                reloj = 0;
                evento = EnumEventos.Inicializacion.getNombre();
                rnd1 = calcularRandom();
                rnd1exp = calcularRNDExpTiempoLlegada(rnd1, evento);
                proximallegada = calcularProximaLlegada(reloj, rnd1exp, evento, 0);
                estadoservidor1 = EnumEstadosServidor.Libre.getNombre();
                estadoservidor2 = EnumEstadosServidor.Libre.getNombre();
                rnd2 = 0;
                tipoLlegada = "";
                rnd3 = 0;
                rnd3Tiempo = 0;
                finPeticionServidor1 = 0.0;
                finPeticionServidor2 = 0.0;
                rnd4 = 0;
                rnd4UniformeTiempo = 0;
                finConsultaServidor1 = 0.0;
                finConsultaServidor2 = 0.0;
                rnd5 = 0;
                rnd5UniformeTiempo = 0;
                finDevolucionServidor1 = 0.0;
                finDevolucionServidor2 = 0.0;
                rnd6 = 0;
                seVaSeQueda = "";
                rnd7 = 0;
                rnd7exp = 0;
                personasEnCola = 0;
                personasEnBiblioteca = 0;
                estadoBiblioteca = EnumEstadosBiblioteca.Abierta.getNombre();
                contadorPersonasNoEntran = 0;
                contadorPersonasTotal = 0; // Antes estaba abajo de promedioPersonasNoEntran
                promedioPersonasNoEntran = 0;
                acumuladorPermanencia = 0;
                promedioPermanencia = 0;

                for (int NroCliente = 0; NroCliente < CAPACIDAD_BIBLIOTECA; NroCliente++) {
                    for (int columnaCliente = 0; columnaCliente < CANTIDAD_COLUMNAS_POR_CLIENTE; columnaCliente++) {
                        // 0 = Estado, 1 = Hora Llegada, 2 = Hora Salida, 3 = Tiempo en el sistema, 4 = Hora fin lectura
                        int columnaActual = CANTIDAD_COLUMNAS_BASICAS + NroCliente * CANTIDAD_COLUMNAS_POR_CLIENTE + columnaCliente;
                        switch (columnaCliente) {
                            case 0:
                                vectorActual[columnaActual] = "";
                                break;
                            default:
                                vectorActual[columnaActual] = 0.0;
                        }
                    }
                }

            } else {
                Object[] vectorAnterior = fila % 2 == 0 ? vectorImpar : vectorPar;
                nroEvento = fila;
                reloj = calcularReloj((Object[]) vectorAnterior);
                evento = calcularEvento((Object[]) vectorAnterior);
                rnd1 = calcularRandomParaProxLlegada(evento);
                rnd1exp = calcularRNDExpTiempoLlegada(rnd1, evento);
                proximallegada = calcularProximaLlegada(reloj, rnd1exp, evento, (double) vectorAnterior[5]);
                estadoservidor1 = calcularEstadoServidor1((String) vectorAnterior[6], evento, (double) vectorAnterior[26], (String) vectorAnterior[28]);
                estadoservidor2 = calcularEstadoServidor2((String) vectorAnterior[7], (String) vectorAnterior[6], evento, (double) vectorAnterior[26], (String) vectorAnterior[28]);
                rnd2 = calcularRandomParaTipoLlegada(evento, (double) vectorAnterior[26], (String) vectorAnterior[28], (String) vectorAnterior[6],(String) vectorAnterior[7], vectorAnterior);
                tipoLlegada = calcularTipoLlegada(rnd2, evento, (String) vectorAnterior[6],(String) vectorAnterior[7] ,estadoservidor1, estadoservidor2, (double) vectorAnterior[26], (String) vectorAnterior[28], vectorAnterior);
                rnd3 = calcularRandomParaPeticiones(tipoLlegada);
                rnd3Tiempo = calcularRNDExpTiempoPeticion(rnd3, tipoLlegada, (String) vectorAnterior[6], (String) vectorAnterior[7]);
                finPeticionServidor1 = calcularFinPeticion1(reloj, rnd3Tiempo, tipoLlegada, (String) vectorAnterior[6], (String) vectorAnterior[7], (double) vectorAnterior[12], evento, (double) vectorAnterior[26]);
                finPeticionServidor2 = calcularFinPeticion2(reloj, rnd3Tiempo, tipoLlegada, (String) vectorAnterior[6], (String) vectorAnterior[7], (double) vectorAnterior[13], evento, (double) vectorAnterior[26]);
                rnd4 = calcularRandomParaM(tipoLlegada);
                rnd4UniformeTiempo = calcularRNDUniformeM(rnd4, tipoLlegada, (String) vectorAnterior[6], (String) vectorAnterior[7]);

                finConsultaServidor1 = calcularFinConsulta1(reloj, rnd4UniformeTiempo, tipoLlegada, (String) vectorAnterior[6], (String) vectorAnterior[7], (double) vectorAnterior[16], evento, (double) vectorAnterior[26], matrizRungeKutta);
                finConsultaServidor2 = calcularFinConsulta2(reloj, rnd4UniformeTiempo, tipoLlegada, (String) vectorAnterior[6], (String) vectorAnterior[7], (double) vectorAnterior[17], evento, (double) vectorAnterior[26], matrizRungeKutta);

                rnd5 = calcularRandomParaDevoluciones(tipoLlegada);
                rnd5UniformeTiempo = calcularRNDUniformeTiempoDevolucion(rnd5, tipoLlegada, (String) vectorAnterior[6], (String) vectorAnterior[7]);
                finDevolucionServidor1 = calcularFinDevolucion1(reloj, rnd5UniformeTiempo, tipoLlegada, (String) vectorAnterior[6], (String) vectorAnterior[7], (double) vectorAnterior[20], evento, (double) vectorAnterior[26]);
                finDevolucionServidor2 = calcularFinDevolucion2(reloj, rnd5UniformeTiempo, tipoLlegada, (String) vectorAnterior[6], (String) vectorAnterior[7], (double) vectorAnterior[21], evento, (double) vectorAnterior[26]);
                rnd6 = calcularRandomParaSeVaSeQueda(evento);
                seVaSeQueda = calcularSeVaSeQueda(rnd6, evento);
                rnd7 = calcularRandomParaLecturas(seVaSeQueda);
                rnd7exp = calcularRNDExpTiempoLectura(rnd7, seVaSeQueda);
                personasEnCola = calcularPersonasEnCola((String) vectorAnterior[6], (String) vectorAnterior[7], (double) vectorAnterior[26], evento, (String) vectorAnterior[28]);
                personasEnBiblioteca = calcularPersonasEnBiblioteca((double) vectorAnterior[27], evento, seVaSeQueda);
                estadoBiblioteca = calcularEstadoBiblioteca(personasEnBiblioteca);
                contadorPersonasNoEntran = calcularContadorPersonasNoEntran((double) vectorAnterior[29], evento, estadoBiblioteca, (double) vectorAnterior[27]);
                contadorPersonasTotal = calcularContadorPersonasTotal((double) vectorAnterior[30], evento); // Antes estaba abajo de promedioPersonasNoEntran
                promedioPersonasNoEntran = calcularPromedioPersonasNoEntran(contadorPersonasNoEntran, contadorPersonasTotal);

                // CLIENTES
                // Repetir todos los valores anteriores de los clientes y limpiar los que estan en destruccion
                for (int NroCliente = 0; NroCliente < CAPACIDAD_BIBLIOTECA; NroCliente++) {
                    // 0 = Estado, 1 = Hora Llegada, 2 = Hora Salida, 3 = Tiempo en el sistema, 4 = Hora fin lectura
                    int columnaActual = CANTIDAD_COLUMNAS_BASICAS + NroCliente * CANTIDAD_COLUMNAS_POR_CLIENTE;
                    if (vectorAnterior[columnaActual].equals(EnumEstadosCliente.Destruccion.getNombre())) {
                        vectorActual[columnaActual] = "";
                        vectorActual[columnaActual + 1] = 0.0;
                        vectorActual[columnaActual + 2] = 0.0;
                        vectorActual[columnaActual + 3] = 0.0;
                        vectorActual[columnaActual + 4] = 0.0;
                    } else {
                        vectorActual[columnaActual] = vectorAnterior[columnaActual];
                        vectorActual[columnaActual + 1] = vectorAnterior[columnaActual + 1];
                        vectorActual[columnaActual + 2] = vectorAnterior[columnaActual + 2];
                        vectorActual[columnaActual + 3] = vectorAnterior[columnaActual + 3];
                        vectorActual[columnaActual + 4] = vectorAnterior[columnaActual + 4];
                    }
                }

                // Si el evento es llegada y la biblio esta abierta, encontrar la primer columna que no esté ocupada y poner el cliente alli
                if (evento.equals(EnumEventos.Llegada.getNombre()) && estadoBiblioteca.equals(EnumEstadosBiblioteca.Abierta.getNombre())) {
                    for (int NroCliente = 0; NroCliente < CAPACIDAD_BIBLIOTECA; NroCliente++) {
                        int columnaActual = CANTIDAD_COLUMNAS_BASICAS + NroCliente * CANTIDAD_COLUMNAS_POR_CLIENTE;

                        if (vectorActual[columnaActual].equals("")) {
                            vectorActual[columnaActual + 1] = reloj;
                            vectorActual[columnaActual + 4] = 0;
                            if ((Double) vectorAnterior[26] == personasEnCola) {
                                vectorActual[columnaActual] = EnumEstadosCliente.SiendoAtendido.getNombre();

                                if (tipoLlegada.equals(EnumTiposLlegada.Peticion1.getNombre())) {
                                    vectorActual[columnaActual + 2] = finPeticionServidor1;
                                } else if (tipoLlegada.equals(EnumTiposLlegada.Peticion2.getNombre())) {
                                    vectorActual[columnaActual + 2] = finPeticionServidor2;
                                } else if (tipoLlegada.equals(EnumTiposLlegada.Consulta1.getNombre())) {
                                    vectorActual[columnaActual + 2] = finConsultaServidor1;
                                } else if (tipoLlegada.equals(EnumTiposLlegada.Consulta2.getNombre())) {
                                    vectorActual[columnaActual + 2] = finConsultaServidor2;
                                } else if (tipoLlegada.equals(EnumTiposLlegada.Devolucion1.getNombre())) {
                                    vectorActual[columnaActual + 2] = finDevolucionServidor1;
                                } else if (tipoLlegada.equals(EnumTiposLlegada.Devolucion2.getNombre())) {
                                    vectorActual[columnaActual + 2] = finDevolucionServidor2;
                                }
                                vectorActual[columnaActual + 3] = (Double) vectorActual[columnaActual + 2] - (Double) vectorActual[columnaActual + 1];


                            } else {
                                vectorActual[columnaActual] = EnumEstadosCliente.EnCola.getNombre();
                                vectorActual[columnaActual + 2] = 0.0;
                                vectorActual[columnaActual + 3] = 0.0;
                            }

                            break;
                        }
                    }
                }

                // Si el evento es fin peticion y se queda, pasa a leyendo
                if ((evento.equals(EnumEventos.FinPeticion1.getNombre()) || evento.equals(EnumEventos.FinPeticion2.getNombre())) && seVaSeQueda.equals(EnumLuegoPeticion.SeQueda.getNombre())) {
                    for (int NroCliente = 0; NroCliente < CAPACIDAD_BIBLIOTECA; NroCliente++) {
                        // 0 = Estado, 1 = Hora Llegada, 2 = Hora Salida, 3 = Tiempo en el sistema, 4 = Hora fin lectura
                        int columnaActual = CANTIDAD_COLUMNAS_BASICAS + NroCliente * CANTIDAD_COLUMNAS_POR_CLIENTE;
                        if ((Double) vectorAnterior[columnaActual + 2] == reloj) {
                            vectorActual[columnaActual] = EnumEstadosCliente.Leyendo.getNombre();
                            vectorActual[columnaActual + 1] = vectorAnterior[columnaActual + 1];
                            vectorActual[columnaActual + 2] = reloj + rnd7exp;
                            vectorActual[columnaActual + 3] = (Double) vectorActual[columnaActual + 2] - (Double) vectorActual[columnaActual + 1];
                            vectorActual[columnaActual + 4] = vectorActual[columnaActual + 2];
                        }
                    }
                }

                // Si el evento es fin devoulucion, fin consulta o fin peticion (y en este ultimo caso se va), se hace (1)
                if (
                        evento.equals(EnumEventos.FinDevolucion1.getNombre()) || evento.equals(EnumEventos.FinDevolucion2.getNombre()) ||
                        evento.equals(EnumEventos.FinConsulta1.getNombre()) || evento.equals(EnumEventos.FinConsulta2.getNombre()) ||
                        ((evento.equals(EnumEventos.FinPeticion1.getNombre()) || evento.equals(EnumEventos.FinPeticion2.getNombre())) && seVaSeQueda.equals(EnumLuegoPeticion.SeVa.getNombre()))
                ) {
                    // (1) el cliente cuya hora salida sea el reloj pasa a destruccion
                    for (int NroCliente = 0; NroCliente < CAPACIDAD_BIBLIOTECA; NroCliente++) {
                        int columnaActual = CANTIDAD_COLUMNAS_BASICAS + NroCliente * CANTIDAD_COLUMNAS_POR_CLIENTE;
                        if ((Double) vectorAnterior[columnaActual + 2] == reloj) {
                            vectorActual[columnaActual] = EnumEstadosCliente.Destruccion.getNombre();
                            vectorActual[columnaActual + 1] = vectorAnterior[columnaActual + 1];
                            vectorActual[columnaActual + 2] = vectorAnterior[columnaActual + 2];
                            vectorActual[columnaActual + 3] = vectorAnterior[columnaActual + 3];
                            vectorActual[columnaActual + 4] = vectorAnterior[columnaActual + 4];
                        }
                    }
                }

                // Si el evento es fin devoulucion, fin consulta o fin peticion, se hace (2)
                if (
                        evento.equals(EnumEventos.FinDevolucion1.getNombre()) || evento.equals(EnumEventos.FinDevolucion2.getNombre()) ||
                        evento.equals(EnumEventos.FinConsulta1.getNombre()) || evento.equals(EnumEventos.FinConsulta2.getNombre()) ||
                        evento.equals(EnumEventos.FinPeticion1.getNombre()) || evento.equals(EnumEventos.FinPeticion2.getNombre())
                ) {
                    // (2) si habia al menos un cliente en cola, el cliente con menor hora llegada pasa a ser atendido
                    ArrayList<Double> columnasHorasLlegada = new ArrayList<>();
                    for (int NroCliente = 0; NroCliente < CAPACIDAD_BIBLIOTECA; NroCliente++) {
                        int columnaActual = CANTIDAD_COLUMNAS_BASICAS + NroCliente * CANTIDAD_COLUMNAS_POR_CLIENTE;
                        if (vectorActual[columnaActual].equals(EnumEstadosCliente.EnCola.getNombre())) {
                            columnasHorasLlegada.add((double) (columnaActual + 1));
                        }
                    }

                    if (!columnasHorasLlegada.isEmpty()) {
                        int[] columnasHorasLlegadaArray = new int[columnasHorasLlegada.size()];
                        for (int i = 0; i < columnasHorasLlegada.size(); i++) {
                            columnasHorasLlegadaArray[i] = columnasHorasLlegada.get(i).intValue();
//                            System.out.println("que hay en el vector: " + columnasHorasLlegadaArray[i] + " " + vectorActual[columnasHorasLlegadaArray[i]]);
                        }

                        // Inicializa el valor mínimo con el primer elemento del array
                        double minimo = (double) vectorActual[columnasHorasLlegadaArray[0]];
                        int indiceMinimo = columnasHorasLlegadaArray[0]; // Inicializa el índice del mínimo con el primer elemento del array

                        // Itera sobre los elementos del array para encontrar el mínimo y su índice
                        // (empieza en 1 porque ya se inicializó el mínimo al elemento 0)
                        for (int i = 1; i < columnasHorasLlegadaArray.length; i++) {
                            // Obtén el valor actual como double
                            double valorActual = (double) vectorActual[columnasHorasLlegadaArray[i]];

                            // Comprueba si el elemento actual es menor que el mínimo actual
                            if (valorActual < minimo) {
                                // Si es así, actualiza el valor mínimo y guarda el índice
                                minimo = valorActual;
                                indiceMinimo = columnasHorasLlegadaArray[i];
                            }
                        }


                        // Al finalizar el bucle, 'minimo' contendrá el valor mínimo y 'indiceMinimo' contendrá el índice donde se encuentra
//                        System.out.println("El valor mínimo es: " + minimo);
//                        System.out.println("Índice del mínimo: " + indiceMinimo);


                        vectorActual[indiceMinimo - 1] = EnumEstadosCliente.SiendoAtendido.getNombre();
                        vectorActual[indiceMinimo] = vectorAnterior[indiceMinimo];
                        if (tipoLlegada.equals(EnumTiposLlegada.Peticion1.getNombre())) {
                            vectorActual[indiceMinimo  + 1] = finPeticionServidor1;
                        } else if (tipoLlegada.equals(EnumTiposLlegada.Peticion2.getNombre())) {
                            vectorActual[indiceMinimo  + 1] = finPeticionServidor2;
                        } else if (tipoLlegada.equals(EnumTiposLlegada.Consulta1.getNombre())) {
                            vectorActual[indiceMinimo  + 1] = finConsultaServidor1;
                        } else if (tipoLlegada.equals(EnumTiposLlegada.Consulta2.getNombre())) {
                            vectorActual[indiceMinimo  + 1] = finConsultaServidor2;
                        } else if (tipoLlegada.equals(EnumTiposLlegada.Devolucion1.getNombre())) {
                            vectorActual[indiceMinimo  + 1] = finDevolucionServidor1;
                        } else if (tipoLlegada.equals(EnumTiposLlegada.Devolucion2.getNombre())) {
                            vectorActual[indiceMinimo  + 1] = finDevolucionServidor2;
                        }
                        vectorActual[indiceMinimo + 2] = (Double) vectorActual[indiceMinimo + 1] - (Double) vectorActual[indiceMinimo];
                        vectorActual[indiceMinimo + 3] = vectorAnterior[indiceMinimo + 3];
                    }
                    
                }

                // Si el evento es fin lectura, pasa el cliente a en cola o siendo atendido
                if (evento.equals(EnumEventos.FinLectura.getNombre())) {
                    for (int NroCliente = 0; NroCliente < CAPACIDAD_BIBLIOTECA; NroCliente++) {
                        // 0 = Estado, 1 = Hora Llegada, 2 = Hora Salida, 3 = Tiempo en el sistema, 4 = Hora fin lectura
                        int columnaActual = CANTIDAD_COLUMNAS_BASICAS + NroCliente * CANTIDAD_COLUMNAS_POR_CLIENTE;
                        double horaFinLectura = ((Number) vectorAnterior[columnaActual + 4]).doubleValue();
                        if (horaFinLectura == reloj && !vectorAnterior[columnaActual].equals(EnumEstadosCliente.Destruccion.getNombre())) {

                            vectorActual[columnaActual + 1] = vectorAnterior[columnaActual + 1];

                            // Si (el servidor 1 estaba libre)
                            if (vectorAnterior[6].equals(EnumEstadosServidor.Libre.getNombre())) {
                                vectorActual[columnaActual] = EnumEstadosCliente.SiendoAtendido.getNombre();
                                vectorActual[columnaActual + 2] = finDevolucionServidor1;
                                vectorActual[columnaActual + 3] = (Double) vectorActual[columnaActual + 2] - (Double) vectorActual[columnaActual + 1];
                            // Sino, Si (el servidor 2 estaba libre)
                            } else if (vectorAnterior[7].equals(EnumEstadosServidor.Libre.getNombre())) {
                                vectorActual[columnaActual] = EnumEstadosCliente.SiendoAtendido.getNombre();
                                vectorActual[columnaActual + 2] = finDevolucionServidor2;
                                vectorActual[columnaActual + 3] = (Double) vectorActual[columnaActual + 2] - (Double) vectorActual[columnaActual + 1];
                            } else {
                                // Caso especial: Personas que estaban leyendo y pasaron a en cola
                                clientesQueEstabanLeyendoYAhoraEstanEnCola.add(columnaActual + 1);

                                vectorActual[columnaActual] = EnumEstadosCliente.EnCola.getNombre();
                                vectorActual[columnaActual + 2] = vectorAnterior[columnaActual + 2];
                                vectorActual[columnaActual + 3] = vectorAnterior[columnaActual + 3];
                            }

                            vectorActual[columnaActual + 4] = 0.0;
                        }
                    }
                }

                // /////////// Tiempos negativos. Ya estan solucionados
                for (int NroCliente = 0; NroCliente < CAPACIDAD_BIBLIOTECA; NroCliente++) {
                    // 0 = Estado, 1 = Hora Llegada, 2 = Hora Salida, 3 = Tiempo en el sistema, 4 = Hora fin lectura
                    int columnaActual = CANTIDAD_COLUMNAS_BASICAS + NroCliente * CANTIDAD_COLUMNAS_POR_CLIENTE;
                    if ((Double) vectorActual[columnaActual + 3] < 0) {
                        vectorActual[columnaActual + 2] = calcularRandom() + reloj;
                        vectorActual[columnaActual + 3] = (Double) vectorActual[columnaActual + 2] - (Double) vectorActual[columnaActual + 1];
                    }
                }
                // /////////// Tiempos negativos

                // Acumulador permanencia y promedio permanencia
                acumuladorPermanencia = (Double) vectorAnterior[32];
                for (int NroCliente = 0; NroCliente < CAPACIDAD_BIBLIOTECA; NroCliente++) {
                    int columnaActual = CANTIDAD_COLUMNAS_BASICAS + NroCliente * CANTIDAD_COLUMNAS_POR_CLIENTE;
                    if (vectorActual[columnaActual].equals(EnumEstadosCliente.Destruccion.getNombre())) {
                        acumuladorPermanencia += (Double) vectorActual[columnaActual + 3];
                    }
                }

                if (contadorPersonasTotal > 0) {
                    promedioPermanencia = acumuladorPermanencia / contadorPersonasTotal;
                } else {
                    promedioPermanencia = 0;
                }

            }

            vectorActual[0] = nroEvento;
            vectorActual[1] = reloj;
            vectorActual[2] = evento;
            vectorActual[3] = rnd1;
            vectorActual[4] = rnd1exp;
            vectorActual[5] = proximallegada;
            vectorActual[6] = estadoservidor1;
            vectorActual[7] = estadoservidor2;
            vectorActual[8] = rnd2;
            vectorActual[9] = tipoLlegada;
            vectorActual[10] = rnd3;
            vectorActual[11] = rnd3Tiempo;
            vectorActual[12] = finPeticionServidor1;
            vectorActual[13] = finPeticionServidor2;
            vectorActual[14] = rnd4;
            vectorActual[15] = rnd4UniformeTiempo;
            vectorActual[16] = finConsultaServidor1;
            vectorActual[17] = finConsultaServidor2;
            vectorActual[18] = rnd5;
            vectorActual[19] = rnd5UniformeTiempo;
            vectorActual[20] = finDevolucionServidor1;
            vectorActual[21] = finDevolucionServidor2;
            vectorActual[22] = rnd6;
            vectorActual[23] = seVaSeQueda;
            vectorActual[24] = rnd7;
            vectorActual[25] = rnd7exp;
            vectorActual[26] = personasEnCola;
            vectorActual[27] = personasEnBiblioteca;
            vectorActual[28] = estadoBiblioteca;
            vectorActual[29] = contadorPersonasNoEntran;
            vectorActual[30] = contadorPersonasTotal;
            vectorActual[31] = promedioPersonasNoEntran;
            vectorActual[32] = acumuladorPermanencia;
            vectorActual[33] = promedioPermanencia;

//            System.out.println(fila);

            if (fila % 2 == 0) {
                vectorPar = vectorActual;
            } else {
                vectorImpar = vectorActual;
            }

            if (fila >= App.primerFilaTablaJ && fila < App.primerFilaTablaJ + App.cantidadDeFilasAMostrarI) {
                matriz[fila - App.primerFilaTablaJ] = vectorActual;
            }

            // Verificamos si la fila es la última y la añadimos a la matriz
            if (fila == App.cantidadDeFilasAMostrarI) {
                matriz[fila - App.primerFilaTablaJ] = vectorActual;
                ultimaFila = vectorActual;
            }

            // Si se llega al límite de tiempo y se completaron todas las filas requeridas,
            // actualizamos la última fila y salimos del bucle
            if (reloj >= App.tiempoSimulacion || fila >= App.CANTIDAD_MAXIMA_ITERACIONES - 1) {
                ultimaFila = vectorActual;
                break;
            }

            fila++;
        }

        // Si la última fila no se agregó durante el bucle, la agregamos aquí
        if (ultimaFila != null) {
            matriz[App.cantidadDeFilasAMostrarI] = ultimaFila;
        }

        return matriz;
    }

    public static Object[][] eliminarFilasNulasYDuplicadas(Object[][] matriz) {
        List<Object[]> listaFilas = new ArrayList<>();
        Set<String> filasUnicas = new HashSet<>();

        for (Object[] fila : matriz) {
            String filaString = Arrays.toString(fila);
            if (!filaNula(fila) && !filasUnicas.contains(filaString)) {
                listaFilas.add(fila);
                filasUnicas.add(filaString);
            }
        }

        Object[][] matrizSinFilasNulas = new Object[listaFilas.size()][];
        for (int i = 0; i < listaFilas.size(); i++) {
            matrizSinFilasNulas[i] = listaFilas.get(i);
        }

        return matrizSinFilasNulas;
    }

    private static boolean filaNula(Object[] fila) {
        for (Object valor : fila) {
            if (valor != null) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Object[][] matriz = generadorVectoresParImpar(RungeKutta.generadorVectoresParImpar(0.1, 0.7, 0.6, 37));
        GeneradorExcel.crearExcel("sistemadecolas.xls", matriz);
        for (int fila = 0; fila < matriz.length; fila++) {
        System.out.print("Fila " + fila + ": [ ");
        for (Object value : matriz[fila]) {
            System.out.print(value + " ");
            }
        System.out.println("]");
        }
    }
}