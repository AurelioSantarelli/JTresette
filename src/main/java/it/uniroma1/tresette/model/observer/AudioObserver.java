package it.uniroma1.tresette.model.observer;

import it.uniroma1.tresette.view.sound.SoundManager;
import it.uniroma1.tresette.model.Carta;

/**
 * Observer per il sistema audio del gioco
 * Si occupa di riprodurre i suoni appropriati basandosi sugli eventi del gioco
 */
public class AudioObserver implements GameStateObserver {
    
    private boolean audioAbilitato = true;
    
    /** Costruttore di default con audio abilitato */
    public AudioObserver() {
        // Costruttore di default
    }
    
    /** 
     * Costruttore con configurazione audio personalizzata
     * @param audioAbilitato true per abilitare l'audio, false per disabilitarlo
     */
    public AudioObserver(boolean audioAbilitato) {
        this.audioAbilitato = audioAbilitato;
    }
    
    /** 
     * Imposta lo stato dell'audio
     * @param abilitato true per abilitare, false per disabilitare
     */
    public void setAudioAbilitato(boolean abilitato) {
        this.audioAbilitato = abilitato;
    }
    
    /** @return true se l'audio è abilitato */
    public boolean isAudioAbilitato() {
        return audioAbilitato;
    }
    
    @Override
    public void onGameStateChanged(GameState newState) {
        if (!audioAbilitato) return;
        
        switch (newState) {
            case DISTRIBUZIONE_CARTE:
                // Riproduci suono delle carte mischiate
                SoundManager.riproduciSuonoCarteMischiate();
                break;
            case TERMINATO:
                // Il suono di vittoria/sconfitta viene gestito altrove
                break;
            case IN_PAUSA:
                // Aggiungere un suono per la pausa
                break;
            case IN_CORSO:
                // Aggiungere un suono per la ripresa del gioco
                break;
            default:
                // Nessun suono specifico per altri stati
                break;
        }
    }
    
    @Override
    public void onCartaGiocata(Carta carta, String giocatore) {
        if (!audioAbilitato) return;
        // Riproduci suono della carta giocata
        SoundManager.riproduciSuonoCarta();
    }
    
    @Override
    public void onPunteggiAggiornati(double punteggioCoppia1, double punteggioCoppia2) {
        // Nessun suono specifico per l'aggiornamento punteggi
    }
    
    @Override
    public void onTurnoCambiato(String nomeGiocatore, int indiceTurno) {
        // Nessun suono specifico per il cambio turno
    }
    
    @Override
    public void onFineMano(String vincitore, double puntiMano) {
        if (!audioAbilitato) return;
        // Riproduci suono di fine mano
        SoundManager.riproduciSuonoFineMano();
    }
    
    @Override
    public void onPausaToggled(boolean inPausa) {
        if (!audioAbilitato) return;
        // Riproduci suono del click
        SoundManager.riproduciSuonoClick();
    }
    
    @Override
    public void onFineManoCompleta(int numeroMano, double punteggioCoppia1, double punteggioCoppia2) {
        if (!audioAbilitato) return;
        // Riproduci suono di fine mano completa
        SoundManager.riproduciSuonoContinuaPartita();
    }
}
