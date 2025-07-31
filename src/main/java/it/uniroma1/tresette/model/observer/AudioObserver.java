package it.uniroma1.tresette.model.observer;

import it.uniroma1.tresette.model.Carta;

/**
 * Observer per il sistema audio del gioco
 * Si occupa di riprodurre i suoni appropriati basandosi sugli eventi del gioco
 */
public class AudioObserver implements GameStateObserver {
    
    private boolean audioAbilitato = true;
    
    public AudioObserver() {
        // Costruttore di default
    }
    
    public AudioObserver(boolean audioAbilitato) {
        this.audioAbilitato = audioAbilitato;
    }
    
    public void setAudioAbilitato(boolean abilitato) {
        this.audioAbilitato = abilitato;
    }
    
    public boolean isAudioAbilitato() {
        return audioAbilitato;
    }
    
    @Override
    public void onGameStateChanged(GameState newState) {
        if (!audioAbilitato) return;
        
        switch (newState) {
            case DISTRIBUZIONE_CARTE:
                // TODO: Spostare SoundManager nel controller
                // SoundManager.riproduciSuonoCarteMischiate();
                break;
            case TERMINATO:
                // Il suono di vittoria/sconfitta viene gestito altrove
                break;
            case IN_PAUSA:
                // Potresti aggiungere un suono specifico per la pausa
                break;
            case IN_CORSO:
                // Potresti aggiungere un suono per la ripresa del gioco
                break;
            default:
                // Nessun suono specifico per altri stati
                break;
        }
    }
    
    @Override
    public void onCartaGiocata(Carta carta, String giocatore) {
        if (!audioAbilitato) return;
        
        // TODO: Spostare SoundManager nel controller
        // SoundManager.riproduciSuonoCarta();
    }
    
    @Override
    public void onPunteggiAggiornati(double punteggioCoppia1, double punteggioCoppia2) {
        // Nessun suono specifico per l'aggiornamento punteggi
        // Ma potresti aggiungerne uno se desiderato
    }
    
    @Override
    public void onTurnoCambiato(String nomeGiocatore, int indiceTurno) {
        // Nessun suono specifico per il cambio turno
        // Ma potresti aggiungere un suono soft per il turno dell'utente
    }
    
    @Override
    public void onFineMano(String vincitore, double puntiMano) {
        if (!audioAbilitato) return;
        
        // TODO: Spostare SoundManager nel controller
        // SoundManager.riproduciSuonoFineMano();
    }
    
    @Override
    public void onPausaToggled(boolean inPausa) {
        if (!audioAbilitato) return;
        
        // TODO: Spostare SoundManager nel controller
        // SoundManager.riproduciSuonoClick();
    }
}
