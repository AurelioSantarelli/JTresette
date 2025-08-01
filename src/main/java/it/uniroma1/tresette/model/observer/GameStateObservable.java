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
    
    public void addObserver(GameStateObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    public void removeObserver(GameStateObserver observer) {
        observers.remove(observer);
    }
    
    public GameState getCurrentState() {
        return currentState;
    }
    
    public void notifyGameStateChanged(GameState nuovoStato) {
        this.currentState = nuovoStato;
        for (GameStateObserver observer : observers) {
            observer.onGameStateChanged(nuovoStato);
        }
    }
    
    public void notifyCartaGiocata(Carta carta, String nomeGiocatore) {
        for (GameStateObserver observer : observers) {
            observer.onCartaGiocata(carta, nomeGiocatore);
        }
    }
    
    public void notifyPunteggiAggiornati(double punteggioCoppia1, double punteggioCoppia2) {
        for (GameStateObserver observer : observers) {
            observer.onPunteggiAggiornati(punteggioCoppia1, punteggioCoppia2);
        }
    }
    
    public void notifyTurnoCambiato(String nomeGiocatore, int indiceGiocatore) {
        for (GameStateObserver observer : observers) {
            observer.onTurnoCambiato(nomeGiocatore, indiceGiocatore);
        }
    }
    
    public void notifyFineMano(String vincitore, double puntiMano) {
        for (GameStateObserver observer : observers) {
            observer.onFineMano(vincitore, puntiMano);
        }
    }
    
    public void notifyPausaToggled(boolean inPausa) {
        for (GameStateObserver observer : observers) {
            observer.onPausaToggled(inPausa);
        }
    }
    
    public void notifyFineManoCompleta(int numeroMano, double punteggioCoppia1, double punteggioCoppia2) {
        for (GameStateObserver observer : observers) {
            observer.onFineManoCompleta(numeroMano, punteggioCoppia1, punteggioCoppia2);
        }
    }
}
