package it.uniroma1.tresette.model;

/**
 * Rappresenta una carta del gioco del Tresette.
 * Modello puro: non contiene codice di rendering o caricamento immagini.
 */
public class Carta {
    // Nuove dimensioni per le carte (usate dalla view/loader)
    public static final int LARGHEZZA_CARTA = 105;
    public static final int ALTEZZA_CARTA = 142;

    private final int valore; // 1-10
    private final Seme seme;
    private final String nome;
    private final int punti;
    // nome della risorsa immagine (es. "spade_1.png")
    private final String risorsaNome;
    private static final String CARTA_RETRO_RESOURCE = "retro.png";

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

    public int getValore() {
        return valore;
    }

    public Seme getSeme() {
        return seme;
    }

    public String getNome() {
        return nome;
    }

    public int getPunti() {
        return punti;
    }

    /**
     * Nome della risorsa immagine associata a questa carta (es. "spade_1.png").
     * La view si occupa di caricare l'immagine tramite CardImageLoader.
     */
    public String getRisorsaNome() {
        return risorsaNome;
    }

    /**
     * Resource name for the back of the card.
     */
    public static String getRetroResourceName() {
        return CARTA_RETRO_RESOURCE;
    }

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
