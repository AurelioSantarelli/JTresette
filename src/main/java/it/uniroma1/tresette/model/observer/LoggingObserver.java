package it.uniroma1.tresette.model.observer;

import it.uniroma1.tresette.model.Carta;
import java.util.function.Consumer;

/**
 * Observer per il logging degli eventi del gioco
 * Si occupa di registrare tutti gli eventi significativi per debug e analisi
 */
public class LoggingObserver implements GameStateObserver {
    
    private boolean loggingEnabled = true;
    private boolean loggingDettagliato = false;
    private Consumer<String> logFunction;
    
    public LoggingObserver() {
        // Costruttore di default
        this.logFunction = System.out::println;
    }
    
    public LoggingObserver(Consumer<String> logFunction) {
        this.logFunction = logFunction;
    }
    
    public LoggingObserver(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
        this.logFunction = System.out::println;
    }
    
    public void setLoggingEnabled(boolean enabled) {
        this.loggingEnabled = enabled;
    }
    
    public void setLoggingDettagliato(boolean dettagliato) {
        this.loggingDettagliato = dettagliato;
    }
    
    private void log(String messaggio) {
        if (loggingEnabled && logFunction != null) {
            logFunction.accept(messaggio);
        }
    }
    
    @Override
    public void onGameStateChanged(GameState nuovoStato) {
        // Non logghiamo i cambi di stato per ridurre il rumore
        // Solo eventi importanti come carte giocate e vincitori
    }
    
    @Override
    public void onCartaGiocata(Carta carta, String nomeGiocatore) {
        if (loggingEnabled) {
            log(nomeGiocatore + " gioca: " + carta);
        }
    }
    
    @Override
    public void onPunteggiAggiornati(double punteggioCoppia1, double punteggioCoppia2) {
        // Non logghiamo ogni aggiornamento punteggio per ridurre il rumore
        if (loggingEnabled && loggingDettagliato) {
            log("[DEBUG] Punteggi aggiornati - Coppia 1: " + punteggioCoppia1 + ", Coppia 2: " + punteggioCoppia2);
        }
    }
    
    @Override
    public void onTurnoCambiato(String nomeGiocatore, int indiceGiocatore) {
        // Non logghiamo ogni cambio turno per ridurre il rumore
        if (loggingEnabled && loggingDettagliato) {
            log("[DEBUG] Turno di: " + nomeGiocatore + " (indice: " + indiceGiocatore + ")");
        }
    }
    
    @Override
    public void onFineMano(String vincitore, double puntiMano) {
        if (loggingEnabled) {
            log(">> " + vincitore + " vince la presa (" + puntiMano + " punti)");
        }
    }
    
    @Override
    public void onPausaToggled(boolean inPausa) {
        if (loggingEnabled) {
            log("=== GIOCO " + (inPausa ? "IN PAUSA" : "RIPRESO") + " ===");
        }
    }
    
    @Override
    public void onFineManoCompleta(int numeroMano, double punteggioCoppia1, double punteggioCoppia2) {
        if (loggingEnabled) {
            log("=== FINE MANO " + numeroMano + " ===");
            log("Punteggi totali - Coppia 1: " + String.format("%.1f", punteggioCoppia1) + 
                ", Coppia 2: " + String.format("%.1f", punteggioCoppia2));
        }
    }
}
