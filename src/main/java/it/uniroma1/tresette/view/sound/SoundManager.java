package it.uniroma1.tresette.view.sound;

import javax.sound.sampled.*;
import javax.swing.Timer;
import java.io.IOException;

/**
 * Gestore centralizzato per tutti i suoni del gioco.
 * Gestisce il caricamento e la riproduzione di effetti sonori.
 */
public class SoundManager {
    private static Clip clipCartaGiocata;
    private static Clip clipVittoria;
    private static Clip clipSconfitta;
    private static Clip clipClick;
    private static Clip clipCarteMischiate;
    private static Clip clipContinua;
    private static Clip clipMano;

    static {
        caricaSuoni();
    }

    /**
     * Carica tutti i file audio necessari
     */
    private static void caricaSuoni() {
        clipCartaGiocata = caricaSuono("/sounds/carta_giocata.wav");
        clipVittoria = caricaSuono("/sounds/vittoria.wav");
        clipSconfitta = caricaSuono("/sounds/sconfitta.wav");
        clipClick = caricaSuono("/sounds/click.wav");
        clipCarteMischiate = caricaSuono("/sounds/carte_mischiate.wav");
        clipContinua = caricaSuono("/sounds/continua.wav");
        clipMano = caricaSuono("/sounds/mano.wav");
    }

    /**
     * Carica un singolo file audio
     * @param percorso percorso del file audio nelle risorse
     * @return clip audio caricato o null se errore
     */
    private static Clip caricaSuono(String percorso) {
        try {
            java.net.URL audioUrl = SoundManager.class.getResource(percorso);
            if (audioUrl == null) {
                System.err.println("File audio non trovato: " + percorso);
                return null;
            }
            
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioUrl);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Errore nel caricamento del suono " + percorso + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Riproduce il suono quando una carta viene giocata
     */
    public static void riproduciSuonoCarta() {
        riproduciSuono(clipCartaGiocata);
    }

    /**
     * Riproduce il suono di vittoria
     */
    public static void riproduciSuonoVittoria() {
        riproduciSuono(clipVittoria);
    }

    /**
     * Riproduce il suono di sconfitta
     */
    public static void riproduciSuonoSconfitta() {
        riproduciSuono(clipSconfitta);
    }

    /**
     * Riproduce il suono di click
     */
    public static void riproduciSuonoClick() {
        riproduciSuono(clipClick);
    }

    /**
     * Riproduce il suono delle carte mischiate con un delay di 2 secondi
     */
    public static void riproduciSuonoCarteMischiate() {
        // Usa un Timer per aggiungere un delay di 2 secondi
        Timer timer = new Timer(2000, e -> riproduciSuono(clipCarteMischiate));
        timer.setRepeats(false); // Esegue solo una volta
        timer.start();
    }

    /**
     * Riproduce il suono per continuare la partita
     */
    public static void riproduciSuonoContinuaPartita() {
        riproduciSuono(clipContinua);
    }

    /**
     * Riproduce il suono di fine mano con un delay di 2 secondi
     */
    public static void riproduciSuonoFineMano() {
        // Usa un Timer per aggiungere un delay di 2 secondi
        Timer timer = new Timer(2000, e -> riproduciSuono(clipMano));
        timer.setRepeats(false); // Esegue solo una volta
        timer.start();
    }

    /**
     * Metodo generico per riprodurre un clip audio
     * @param clip il clip da riprodurre
     */
    private static void riproduciSuono(Clip clip) {
        if (clip != null) {
            // Ferma il clip se è già in riproduzione
            if (clip.isRunning()) {
                clip.stop();
            }
            // Riavvolge all'inizio
            clip.setFramePosition(0);
            // Riproduce
            clip.start();
        }
    }

    
}
