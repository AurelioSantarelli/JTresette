package it.uniroma1.tresette;

/**
 * Enum che rappresenta i possibili stati del gioco Tresette.
 * Utilizzato dal pattern Observer per notificare i cambiamenti di stato.
 */
public enum GameState {
    /**
     * Gioco non ancora iniziato
     */
    NON_INIZIATO,
    
    /**
     * Avvio del gioco (suono carte mischiate)
     */
    AVVIO_GIOCO,
    
    /**
     * Gioco in corso
     */
    IN_CORSO,
    
    /**
     * Distribuzione delle carte
     */
    DISTRIBUZIONE_CARTE,
    
    /**
     * Turno del giocatore umano
     */
    TURNO_UMANO,
    
    /**
     * Turno del giocatore AI
     */
    TURNO_AI,
    
    /**
     * Inizio di una nuova mano
     */
    NUOVA_MANO,
    
    /**
     * Valutazione della mano appena conclusa
     */
    VALUTAZIONE_MANO,
    
    /**
     * Gioco in pausa
     */
    IN_PAUSA,
    
    /**
     * Gioco terminato
     */
    TERMINATO
}