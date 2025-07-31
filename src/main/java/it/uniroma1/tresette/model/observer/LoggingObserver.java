package it.uniroma1.tresette.model.observer;

import it.uniroma1.tresette.model.Carta;

/**
 * Observer per il logging degli eventi del gioco
 * Si occupa di registrare tutti gli eventi importanti nel log
 */
public class LoggingObserver implements GameStateObserver {
    
    private final java.util.function.Consumer<String> logFunction;
    private boolean loggingDettagliato = true;
    
    /**
     * Costruttore che accetta una funzione per scrivere nel log
     * @param logFunction funzione che accetta una stringa e la scrive nel log
     */
    public LoggingObserver(java.util.function.Consumer<String> logFunction) {
        this.logFunction = logFunction;
    }
    
    /**
     * Abilita o disabilita il logging dettagliato
     * @param dettagliato true per logging dettagliato, false per logging essenziale
     */
    public void setLoggingDettagliato(boolean dettagliato) {
        this.loggingDettagliato = dettagliato;
    }
    
    @Override
    public void onGameStateChanged(GameState newState) {
        // Se il logging dettagliato è disabilitato, non stampare MAI i messaggi di stato
        if (!loggingDettagliato) {
            return;
        }
        
        String messaggio = formatMessage("STATO", "Cambio stato: " + newState.toString());
        logFunction.accept(messaggio);
    }
    
    @Override
    public void onCartaGiocata(Carta carta, String giocatore) {
        String messaggio = giocatore + " gioca " + carta.toString();
        logFunction.accept(messaggio);
    }
    
    @Override
    public void onPunteggiAggiornati(double punteggioCoppia1, double punteggioCoppia2) {
        if (loggingDettagliato) {
            String messaggio = formatMessage("PUNTEGGI", 
                String.format("Aggiornamento punteggi - Coppia 1: %.2f, Coppia 2: %.2f", 
                    punteggioCoppia1, punteggioCoppia2));
            logFunction.accept(messaggio);
        }
    }
    
    @Override
    public void onTurnoCambiato(String nomeGiocatore, int indiceTurno) {
        if (loggingDettagliato) {
            String messaggio = formatMessage("TURNO", "È il turno di " + nomeGiocatore);
            logFunction.accept(messaggio);
        }
    }
    
    @Override
    public void onFineMano(String vincitore, double puntiMano) {
        String messaggio = String.format("%s vince la mano (%.2f punti)", vincitore, puntiMano);
        logFunction.accept(messaggio);
    }
    
    @Override
    public void onPausaToggled(boolean inPausa) {
        String azione = inPausa ? "MESSO IN PAUSA" : "RIPRESO";
        String messaggio = formatMessage("PAUSA", "Gioco " + azione);
        logFunction.accept(messaggio);
    }
    
    /**
     * Formatta un messaggio con un prefisso
     * @param tipo il tipo di evento
     * @param messaggio il messaggio
     * @return il messaggio formattato
     */
    private String formatMessage(String tipo, String messaggio) {
        return String.format("[%s] %s", tipo, messaggio);
    }
}
