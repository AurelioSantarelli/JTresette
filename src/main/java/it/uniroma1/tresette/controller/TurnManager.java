package it.uniroma1.tresette.controller;

import it.uniroma1.tresette.model.Giocatore;
import it.uniroma1.tresette.model.observer.GameState;
import it.uniroma1.tresette.model.observer.GameStateObservable;

/**
 * Gestisce la logica dei turni nel gioco di Tresette.
 * Si occupa del cambio turno e della notifica degli eventi di turno.
 */
public class TurnManager {
    
    private final GameStateObservable gameObservable;
    private final GameView view;
    
    public TurnManager(GameStateObservable gameObservable, GameView view) {
        this.gameObservable = gameObservable;
        this.view = view;
    }
    
    /**
     * Inizia il turno di gioco
     * @param giocatori array dei giocatori
     * @param gameState gestore dello stato del gioco
     */
    public void iniziaTurno(Giocatore[] giocatori, GameStateManager gameState) {
        // Imposta il giocatore corrente come primo della mano
        gameState.setGiocatoreCorrente(gameState.getPrimoGiocatoreMano());
        
        // Notifica il cambio turno
        gameObservable.notifyTurnoCambiato(
            giocatori[gameState.getGiocatoreCorrente()].getNome(), 
            gameState.getGiocatoreCorrente()
        );
        
        view.aggiornaTurno(
            giocatori[gameState.getGiocatoreCorrente()].getNome(), 
            gameState.getGiocatoreCorrente()
        );
        
        // Determina il tipo di turno
        if (giocatori[gameState.getGiocatoreCorrente()].isUmano()) {
            gameObservable.notifyGameStateChanged(GameState.TURNO_UMANO);
            view.abilitaBottoniCarte(true);
        } else {
            gameObservable.notifyGameStateChanged(GameState.TURNO_AI);
            view.abilitaBottoniCarte(false);
        }
    }
    
    /**
     * Gestisce il cambio turno dopo che una carta è stata giocata
     * @param giocatori array dei giocatori
     * @param gameState gestore dello stato del gioco
     */
    public void gestisciCambioTurno(Giocatore[] giocatori, GameStateManager gameState) {
        // Passa al giocatore successivo
        gameState.avanzaGiocatore();
        
        // Notifica il cambio turno
        gameObservable.notifyTurnoCambiato(
            giocatori[gameState.getGiocatoreCorrente()].getNome(), 
            gameState.getGiocatoreCorrente()
        );
        
        view.aggiornaTurno(
            giocatori[gameState.getGiocatoreCorrente()].getNome(), 
            gameState.getGiocatoreCorrente()
        );
        
        // Determina il tipo di turno
        if (giocatori[gameState.getGiocatoreCorrente()].isUmano()) {
            gameObservable.notifyGameStateChanged(GameState.TURNO_UMANO);
            view.abilitaBottoniCarte(true);
        } else {
            gameObservable.notifyGameStateChanged(GameState.TURNO_AI);
            view.abilitaBottoniCarte(false);
        }
    }
    
    /**
     * Prepara il turno per la nuova mano dopo la valutazione
     * @param vincitore l'indice del giocatore vincitore della mano precedente
     * @param gameState gestore dello stato del gioco
     */
    public void preparaNuovaMano(int vincitore, GameStateManager gameState) {
        // Il vincitore inizia la prossima mano
        gameState.setPrimoGiocatoreMano(vincitore);
        gameState.resetCarteGiocateInMano();
    }
    
    /**
     * Controlla se il giocatore corrente è umano
     * @param giocatori array dei giocatori
     * @param gameState gestore dello stato del gioco
     * @return true se il giocatore corrente è umano
     */
    public boolean isGiocatoreCorrenteUmano(Giocatore[] giocatori, GameStateManager gameState) {
        return giocatori[gameState.getGiocatoreCorrente()].isUmano();
    }
    
    /**
     * Ottiene il giocatore corrente
     * @param giocatori array dei giocatori
     * @param gameState gestore dello stato del gioco
     * @return il giocatore corrente
     */
    public Giocatore getGiocatoreCorrente(Giocatore[] giocatori, GameStateManager gameState) {
        return giocatori[gameState.getGiocatoreCorrente()];
    }
    
    /**
     * Riprende il turno dalla pausa senza cambiare il giocatore corrente
     * @param giocatori array dei giocatori
     * @param gameState gestore dello stato del gioco
     */
    public void riprendiTurnoDaPausa(Giocatore[] giocatori, GameStateManager gameState) {
        // Non cambiare il giocatore corrente, solo aggiorna l'interfaccia
        
        // Notifica il turno corrente (senza cambiarlo)
        gameObservable.notifyTurnoCambiato(
            giocatori[gameState.getGiocatoreCorrente()].getNome(), 
            gameState.getGiocatoreCorrente()
        );
        
        view.aggiornaTurno(
            giocatori[gameState.getGiocatoreCorrente()].getNome(), 
            gameState.getGiocatoreCorrente()
        );
        
        // Determina il tipo di turno
        if (giocatori[gameState.getGiocatoreCorrente()].isUmano()) {
            gameObservable.notifyGameStateChanged(GameState.TURNO_UMANO);
        } else {
            gameObservable.notifyGameStateChanged(GameState.TURNO_AI);
        }
    }
}
