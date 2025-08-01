package it.uniroma1.tresette.model.observer;

import it.uniroma1.tresette.model.Carta;

/**
 * Observer per il debug degli eventi del gioco
 * Fornisce informazioni dettagliate per il debugging durante lo sviluppo
 */
public class DebugObserver implements GameStateObserver {
    
    private boolean debugEnabled = true;
    private int cartePlayed = 0;
    private int stateChanges = 0;
    
    public DebugObserver() {
        // Costruttore di default
    }
    
    public DebugObserver(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }
    
    public void setDebugEnabled(boolean enabled) {
        this.debugEnabled = enabled;
    }
    
    public void setDebugAbilitato(boolean abilitato) {
        this.debugEnabled = abilitato;
    }
    
    public boolean isDebugAbilitato() {
        return debugEnabled;
    }
    
    public void resetStats() {
        cartePlayed = 0;
        stateChanges = 0;
    }
    
    @Override
    public void onGameStateChanged(GameState nuovoStato) {
        if (debugEnabled) {
            stateChanges++;
            System.out.println("[DEBUG] === CAMBIO STATO === " + nuovoStato + " (cambio #" + stateChanges + ")");
        }
    }
    
    @Override
    public void onCartaGiocata(Carta carta, String nomeGiocatore) {
        if (debugEnabled) {
            cartePlayed++;
            System.out.println("[DEBUG] Carta giocata (#" + cartePlayed + "): " + nomeGiocatore + " -> " + carta.toString());
        }
    }
    
    @Override
    public void onPunteggiAggiornati(double punteggioCoppia1, double punteggioCoppia2) {
        if (debugEnabled) {
            System.out.println("[DEBUG] Punteggi aggiornati - Coppia 1: " + punteggioCoppia1 + ", Coppia 2: " + punteggioCoppia2);
        }
    }
    
    @Override
    public void onTurnoCambiato(String nomeGiocatore, int indiceGiocatore) {
        if (debugEnabled) {
            System.out.println("[DEBUG] === TURNO DI " + nomeGiocatore + " (indice: " + indiceGiocatore + ") ===");
        }
    }
    
    @Override
    public void onFineMano(String vincitore, double puntiMano) {
        if (debugEnabled) {
            System.out.println("[DEBUG] === FINE MANO ===");
            System.out.println("[DEBUG] Vincitore: " + vincitore + ", Punti: " + puntiMano);
        }
    }
    
    @Override
    public void onPausaToggled(boolean inPausa) {
        if (debugEnabled) {
            System.out.println("[DEBUG] === PAUSA " + (inPausa ? "ON" : "OFF") + " ===");
        }
    }
    
    @Override
    public void onFineManoCompleta(int numeroMano, double punteggioCoppia1, double punteggioCoppia2) {
        if (debugEnabled) {
            System.out.println("[DEBUG] === FINE MANO COMPLETA " + numeroMano + " ===");
            System.out.println("[DEBUG] Tutte le 10 carte giocate - Coppia 1: " + punteggioCoppia1 + ", Coppia 2: " + punteggioCoppia2);
            System.out.println("[DEBUG] Statistiche debug - Carte giocate: " + cartePlayed + ", Cambi di stato: " + stateChanges);
        }
    }
}
