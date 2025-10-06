package it.uniroma1.tresette.model.observer;

import it.uniroma1.tresette.model.Carta;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe che implementa il pattern Observer per notificare i cambiamenti di stato del gioco
 */
public class GameStateObservable {
    
    private List<GameStateObserver> observers = new ArrayList<>();
    private GameState currentState = GameState.NON_INIZIATO;
    
    /** 
     * Aggiunge un observer alla lista di notifica
     * @param observer observer da aggiungere
     */
    public void addObserver(GameStateObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    /** 
     * Rimuove un observer dalla lista di notifica
     * @param observer observer da rimuovere
     */
    public void removeObserver(GameStateObserver observer) {
        observers.remove(observer);
    }
    
    /** @return stato corrente del gioco */
    public GameState getCurrentState() {
        return currentState;
    }
    
    /** 
     * Notifica il cambio di stato del gioco a tutti gli observer
     * @param nuovoStato nuovo stato del gioco
     */
    public void notifyGameStateChanged(GameState nuovoStato) {
        this.currentState = nuovoStato;
        for (GameStateObserver observer : observers) {
            observer.onGameStateChanged(nuovoStato);
        }
    }
    
    /** Notifica che una carta è stata giocata */
    public void notifyCartaGiocata(Carta carta, String nomeGiocatore) {
        for (GameStateObserver observer : observers) {
            observer.onCartaGiocata(carta, nomeGiocatore);
        }
    }
    
    /** Notifica aggiornamento dei punteggi */
    public void notifyPunteggiAggiornati(double punteggioCoppia1, double punteggioCoppia2) {
        for (GameStateObserver observer : observers) {
            observer.onPunteggiAggiornati(punteggioCoppia1, punteggioCoppia2);
        }
    }
    
    /** Notifica cambio di turno */
    public void notifyTurnoCambiato(String nomeGiocatore, int indiceGiocatore) {
        for (GameStateObserver observer : observers) {
            observer.onTurnoCambiato(nomeGiocatore, indiceGiocatore);
        }
    }
    
    /** Notifica fine mano con vincitore e punti */
    public void notifyFineMano(String vincitore, double puntiMano) {
        for (GameStateObserver observer : observers) {
            observer.onFineMano(vincitore, puntiMano);
        }
    }
    
    /** Notifica cambio stato pausa */
    public void notifyPausaToggled(boolean inPausa) {
        for (GameStateObserver observer : observers) {
            observer.onPausaToggled(inPausa);
        }
    }
    
    /** Notifica fine mano completa */
    public void notifyFineManoCompleta(int numeroMano, double punteggioCoppia1, double punteggioCoppia2) {
        for (GameStateObserver observer : observers) {
            observer.onFineManoCompleta(numeroMano, punteggioCoppia1, punteggioCoppia2);
        }
    }
}
