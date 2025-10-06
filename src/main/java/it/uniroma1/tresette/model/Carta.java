package it.uniroma1.tresette.model;

/**
 * Rappresenta una carta del gioco del Tresette.
 */
public class Carta {
    // Nuove dimensioni per le carte (usate dalla view/loader)
    /** Larghezza standard della carta in pixel */
    public static final int LARGHEZZA_CARTA = 105;
    /** Altezza standard della carta in pixel */
    public static final int ALTEZZA_CARTA = 142;

    private final int valore; // 1-10
    private final Seme seme;
    private final String nome;
    private final int punti;
    // nome della risorsa immagine (es. "spade_1.png")
    private final String risorsaNome;

    /**
     * Costruttore per creare una carta con seme e valore specificati
     * 
     * @param valore valore della carta (1-10)
     * @param seme seme della carta
     */
    public Carta(int valore, Seme seme) {
        this.valore = valore;
        this.seme = seme;

        switch (valore) {
            case 1:
                nome = "A";
                break;
            case 2:
                nome = "2";
                break;
            case 3:
                nome = "3";
                break;
            case 4:
                nome = "4";
                break;
            case 5:
                nome = "5";
                break;
            case 6:
                nome = "6";
                break;
            case 7:
                nome = "7";
                break;
            case 8:
                nome = "F";
                break;
            case 9:
                nome = "C";
                break;
            case 10:
                nome = "R";
                break;
            default:
                nome = String.valueOf(valore);
        }

        if (valore == 1) {
            punti = 100;
        } else if (valore == 2 || valore == 3 || valore == 8 || valore == 9 || valore == 10) {
            punti = 33;
        } else {
            punti = 0;
        }

        this.risorsaNome = String.format("%s_%d.png", seme.name().toLowerCase(), valore);
    }

    /** @return valore numerico della carta (1-10) */
    public int getValore() {
        return valore;
    }

    /** @return seme della carta */
    public Seme getSeme() {
        return seme;
    }

    /** @return nome simbolico della carta (A, 2-7, F, C, R) */
    public String getNome() {
        return nome;
    }

    /** @return punti assegnati alla carta per il calcolo del punteggio */
    public int getPunti() {
        return punti;
    }

    /**
     * Restituisce il nome della risorsa immagine per questa carta
     * @return nome file della risorsa (es. "spade_1.png")
     */
    public String getRisorsaNome() {
        return risorsaNome;
    }

    /**
     * Restituisce la forza della carta per determinare chi prende la mano
     * @return valore numerico per il confronto tra carte
     */
    public int getForzaPerPresa() {
        switch (valore) {
            case 3:
                return 10;
            case 2:
                return 9;
            case 1:
                return 8;
            case 10:
                return 7;
            case 9:
                return 6;
            case 8:
                return 5;
            case 7:
                return 4;
            case 6:
                return 3;
            case 5:
                return 2;
            case 4:
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public String toString() {
        return nome + seme.getSimbolo();
    }
}
