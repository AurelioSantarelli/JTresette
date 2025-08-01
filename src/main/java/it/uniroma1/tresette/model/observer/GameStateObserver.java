package it.uniroma1.tresette.model.observer;

import it.uniroma1.tresette.model.Carta;

/**
 * Interfaccia Observer per il pattern Observer/Observable del gioco Tresette.
 * Definisce i metodi che devono essere implementati dagli observer che vogliono
 * essere notificati degli eventi del gioco.
 */
public interface GameStateObserver {
    
    /**
     * Notifica il cambio di stato del gioco
     * @param nuovoStato il nuovo stato del gioco
     */
    void onGameStateChanged(GameState nuovoStato);
    
    /**
     * Notifica che una carta è stata giocata
     * @param carta la carta giocata
     * @param nomeGiocatore il nome del giocatore che ha giocato la carta
     */
    void onCartaGiocata(Carta carta, String nomeGiocatore);
    
    /**
     * Notifica l'aggiornamento dei punteggi
     * @param punteggioCoppia1 punteggio della coppia 1
     * @param punteggioCoppia2 punteggio della coppia 2
     */
    void onPunteggiAggiornati(double punteggioCoppia1, double punteggioCoppia2);
    
    /**
     * Notifica il cambio di turno
     * @param nomeGiocatore nome del giocatore di cui è il turno
     * @param indiceGiocatore indice del giocatore (0-3)
     */
    void onTurnoCambiato(String nomeGiocatore, int indiceGiocatore);
    
    /**
     * Notifica la fine di una mano
     * @param vincitore nome del giocatore che ha vinto la mano
     * @param puntiMano punti totalizzati nella mano
     */
    void onFineMano(String vincitore, double puntiMano);
    
    /**
     * Notifica il toggle della pausa
     * @param inPausa true se il gioco è stato messo in pausa, false se è stato ripreso
     */
    void onPausaToggled(boolean inPausa);
    
    /**
     * Notifica la fine di una mano completa (10 carte)
     * @param numeroMano numero della mano appena completata
     * @param punteggioCoppia1 punteggio totale coppia 1
     * @param punteggioCoppia2 punteggio totale coppia 2
     */
    void onFineManoCompleta(int numeroMano, double punteggioCoppia1, double punteggioCoppia2);
}
