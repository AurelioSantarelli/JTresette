package it.uniroma1.tresette;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Observer di debug che stampa informazioni dettagliate sulla console
 * Utile per il debugging e l'analisi del comportamento del gioco
 */
public class DebugObserver implements GameStateObserver {
    
    private boolean debugAbilitato = false;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private int carteGiocateCount = 0;
    private int cambiTurnoCount = 0;
    private int maniCompletateCount = 0;
    
    public DebugObserver() {
        // Costruttore di default - debug disabilitato
    }
    
    public DebugObserver(boolean debugAbilitato) {
        this.debugAbilitato = debugAbilitato;
    }
    
    public void setDebugAbilitato(boolean abilitato) {
        this.debugAbilitato = abilitato;
        if (abilitato) {
            printDebug("=== DEBUG ABILITATO ===");
            printStats();
        } else {
            printDebug("=== DEBUG DISABILITATO ===");
        }
    }
    
    public boolean isDebugAbilitato() {
        return debugAbilitato;
    }
    
    @Override
    public void onGameStateChanged(GameState newState) {
        if (!debugAbilitato) return;
        
        printDebug("STATO CAMBIATO: " + newState.toString());
        
        // Informazioni aggiuntive per stati specifici
        switch (newState) {
            case NON_INIZIATO:
                printDebug("  └─ Gioco pronto per l'inizializzazione");
                break;
            case IN_CORSO:
                printDebug("  └─ Gioco attivo e funzionante");
                break;
            case NUOVA_MANO:
                printDebug("  └─ Preparazione per iniziare nuova mano");
                break;
            case DISTRIBUZIONE_CARTE:
                printDebug("  └─ Preparazione nuova mano in corso...");
                break;
            case TURNO_UMANO:
                printDebug("  └─ Aspettando input dall'utente");
                break;
            case TURNO_AI:
                printDebug("  └─ AI sta pensando alla mossa...");
                break;
            case VALUTAZIONE_MANO:
                printDebug("  └─ Determinazione vincitore mano...");
                break;
            case IN_PAUSA:
                printDebug("  └─ Tutti i processi di gioco sospesi");
                break;
            case TERMINATO:
                printDebug("  └─ Gioco concluso definitivamente");
                printStats();
                break;
        }
    }
    
    @Override
    public void onCartaGiocata(TresetteGame.Carta carta, String giocatore) {
        if (!debugAbilitato) return;
        
        carteGiocateCount++;
        printDebug(String.format("CARTA GIOCATA #%d: %s da %s [Punti: %d, Forza: %d]", 
            carteGiocateCount, carta.toString(), giocatore, 
            carta.getPunti(), carta.getForzaPerPresa()));
    }
    
    @Override
    public void onPunteggiAggiornati(double punteggioCoppia1, double punteggioCoppia2) {
        if (!debugAbilitato) return;
        
        printDebug(String.format("PUNTEGGI AGGIORNATI: Coppia1=%.2f, Coppia2=%.2f [Differenza: %.2f]", 
            punteggioCoppia1, punteggioCoppia2, Math.abs(punteggioCoppia1 - punteggioCoppia2)));
    }
    
    @Override
    public void onTurnoCambiato(String nomeGiocatore, int indiceTurno) {
        if (!debugAbilitato) return;
        
        cambiTurnoCount++;
        String tipoGiocatore = (indiceTurno == 0) ? "UMANO" : "AI";
        printDebug(String.format("TURNO CAMBIATO #%d: %s [Indice: %d, Tipo: %s]", 
            cambiTurnoCount, nomeGiocatore, indiceTurno, tipoGiocatore));
    }
    
    @Override
    public void onFineMano(String vincitore, double puntiMano) {
        if (!debugAbilitato) return;
        
        maniCompletateCount++;
        printDebug(String.format("MANO COMPLETATA #%d: Vincitore=%s, Punti=%.2f", 
            maniCompletateCount, vincitore, puntiMano));
        
        // Statistiche aggiuntive ogni 5 mani
        if (maniCompletateCount % 5 == 0) {
            printDebug("  └─ Checkpoint ogni 5 mani raggiunto");
            printStats();
        }
    }
    
    @Override
    public void onPausaToggled(boolean inPausa) {
        if (!debugAbilitato) return;
        
        String azione = inPausa ? "ATTIVATA" : "DISATTIVATA";
        printDebug("PAUSA " + azione + " dall'utente");
    }
    
    /**
     * Stampa un messaggio di debug con timestamp
     */
    private void printDebug(String messaggio) {
        String timestamp = LocalTime.now().format(timeFormatter);
        System.out.println(String.format("[DEBUG %s] %s", timestamp, messaggio));
    }
    
    /**
     * Stampa statistiche riassuntive
     */
    private void printStats() {
        printDebug("=== STATISTICHE DEBUG ===");
        printDebug("  Carte giocate totali: " + carteGiocateCount);
        printDebug("  Cambi turno totali: " + cambiTurnoCount);
        printDebug("  Mani completate: " + maniCompletateCount);
        if (carteGiocateCount > 0) {
            printDebug("  Media carte per mano: " + String.format("%.1f", (double)carteGiocateCount / Math.max(1, maniCompletateCount)));
        }
        printDebug("========================");
    }
    
    /**
     * Reset delle statistiche (utile per nuove partite)
     */
    public void resetStats() {
        carteGiocateCount = 0;
        cambiTurnoCount = 0;
        maniCompletateCount = 0;
        if (debugAbilitato) {
            printDebug("Statistiche debug resettate");
        }
    }
}
