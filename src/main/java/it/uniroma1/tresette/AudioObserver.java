package it.uniroma1.tresette;

import javax.swing.Timer;
import it.uniroma1.tresette.model.observer.GameStateObserver;
import it.uniroma1.tresette.model.observer.GameState;
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
            case AVVIO_GIOCO:
                // Riproduci il suono delle carte mischiate con un piccolo delay
                // per assicurarsi che l'interfaccia sia pronta
                javax.swing.Timer timer = new javax.swing.Timer(500, e -> {
                    TresetteGame.SoundManager.riproduciSuonoCarteMischiate();
                });
                timer.setRepeats(false);
                timer.start();
                break;
            case DISTRIBUZIONE_CARTE:
                // Il suono delle carte mischiate viene già riprodotto all'avvio
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
        
        // Riproduci il suono della carta giocata
        TresetteGame.SoundManager.riproduciSuonoCarta();
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
        
        // Riproduci il suono della fine mano con un ritardo di 2 secondi
        javax.swing.Timer timer = new javax.swing.Timer(2000, e -> {
            TresetteGame.SoundManager.riproduciSuonoFineMano();
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    @Override
    public void onPausaToggled(boolean inPausa) {
        if (!audioAbilitato) return;
        
        // Riproduci il suono del click per il toggle della pausa
        TresetteGame.SoundManager.riproduciSuonoClick();
    }
    
    @Override
    public void onFineManoCompleta(int numeroMano, double punteggioCoppia1, double punteggioCoppia2) {
        // Nessun suono specifico per la fine di una mano completa
        // Il suono viene gestito da onFineMano per ogni singola presa
    }
}
