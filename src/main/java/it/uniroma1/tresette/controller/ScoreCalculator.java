package it.uniroma1.tresette.controller;

import it.uniroma1.tresette.model.Carta;
import it.uniroma1.tresette.model.Giocatore;
import it.uniroma1.tresette.model.Seme;

import java.util.List;

/**
 * Gestisce il calcolo e l'aggiornamento dei punteggi nel gioco.
 * Si occupa di calcolare i punti delle mani e aggiornare i punteggi totali.
 */
public class ScoreCalculator {
    private final boolean modalitaDueGiocatori;
    
    public ScoreCalculator(boolean modalitaDueGiocatori) {
        this.modalitaDueGiocatori = modalitaDueGiocatori;
    }
    
    /**
     * Calcola i punti di una mano di carte giocate
     * @param carteGiocate le carte giocate nella mano
     * @return i punti totali della mano
     */
    public double calcolaPuntiMano(List<Carta> carteGiocate) {
        double puntiMano = 0;
        for (Carta carta : carteGiocate) {
            puntiMano += carta.getPunti();
        }
        // Converte i punti in punti di gioco (divide per 100)
        return puntiMano / 100.0;
    }
    
    /**
     * Determina il vincitore di una mano
     * @param carteGiocate le carte giocate nella mano
     * @param primoGiocatoreMano l'indice del primo giocatore della mano
     * @param semeRichiesto il seme richiesto per questa mano
     * @return l'indice del giocatore vincitore
     */
    public int determinaVincitoreMano(List<Carta> carteGiocate, int primoGiocatoreMano, 
                                    Seme semeRichiesto) {
        if (carteGiocate.isEmpty()) {
            return primoGiocatoreMano;
        }
        
        Carta cartaVincente = carteGiocate.get(0);
        int indiceVincitore = primoGiocatoreMano;
        
        // Trova la carta più forte del seme richiesto
        for (int i = 0; i < carteGiocate.size(); i++) {
            Carta carta = carteGiocate.get(i);
            int indiceGiocatoreCorrente = (primoGiocatoreMano + i) % carteGiocate.size();
            
            // Una carta vince se:
            // 1. È del seme richiesto e quella vincente non lo è
            // 2. Entrambe sono del seme richiesto e ha valore più alto
            // 3. Entrambe non sono del seme richiesto e ha valore più alto
            if (dovrebbeVincere(carta, cartaVincente, semeRichiesto)) {
                cartaVincente = carta;
                indiceVincitore = indiceGiocatoreCorrente;
            }
        }
        
        return indiceVincitore;
    }
    
    /**
     * Determina se una carta dovrebbe vincere rispetto a un'altra
     */
    private boolean dovrebbeVincere(Carta nuovaCarta, Carta cartaCorrente, 
                                  Seme semeRichiesto) {
        boolean nuovaDelSeme = nuovaCarta.getSeme() == semeRichiesto;
        boolean correnteDelSeme = cartaCorrente.getSeme() == semeRichiesto;
        
        // Se solo la nuova carta è del seme richiesto, vince
        if (nuovaDelSeme && !correnteDelSeme) {
            return true;
        }
        
        // Se solo la carta corrente è del seme richiesto, non vince
        if (!nuovaDelSeme && correnteDelSeme) {
            return false;
        }
        
        // Se entrambe sono o non sono del seme richiesto, vince quella con valore più alto
        return nuovaCarta.getForzaPerPresa() > cartaCorrente.getForzaPerPresa();
    }
    
    /**
     * Aggiorna i punteggi delle coppie/giocatori
     * @param giocatori array dei giocatori
     * @param gameState il gestore dello stato del gioco
     */
    public void aggiornaPunteggi(Giocatore[] giocatori, GameStateManager gameState) {
        if (modalitaDueGiocatori) {
            aggiornaPunteggiDueGiocatori(giocatori, gameState);
        } else {
            aggiornaPunteggiQuattroGiocatori(giocatori, gameState);
        }
    }
    
    /**
     * Aggiorna i punteggi per modalità 2 giocatori
     */
    private void aggiornaPunteggiDueGiocatori(Giocatore[] giocatori, GameStateManager gameState) {
        double punteggioGiocatore1 = 0;
        for (Carta carta : giocatori[0].getCartePrese()) {
            punteggioGiocatore1 += carta.getPunti();
        }
        
        double punteggioGiocatore2 = 0;
        for (Carta carta : giocatori[1].getCartePrese()) {
            punteggioGiocatore2 += carta.getPunti();
        }
        
        // Converte in punti di gioco e aggiunge i punti bonus
        gameState.setPunteggioCoppia1Totale(punteggioGiocatore1 / 100.0 + gameState.getPuntiBonus1());
        gameState.setPunteggioCoppia2Totale(punteggioGiocatore2 / 100.0 + gameState.getPuntiBonus2());
    }
    
    /**
     * Aggiorna i punteggi per modalità 4 giocatori (coppie)
     */
    private void aggiornaPunteggiQuattroGiocatori(Giocatore[] giocatori, GameStateManager gameState) {
        // Coppia 1: giocatori 0 e 2
        double punteggioCoppia1 = 0;
        for (Carta carta : giocatori[0].getCartePrese()) {
            punteggioCoppia1 += carta.getPunti();
        }
        for (Carta carta : giocatori[2].getCartePrese()) {
            punteggioCoppia1 += carta.getPunti();
        }
        
        // Coppia 2: giocatori 1 e 3
        double punteggioCoppia2 = 0;
        for (Carta carta : giocatori[1].getCartePrese()) {
            punteggioCoppia2 += carta.getPunti();
        }
        for (Carta carta : giocatori[3].getCartePrese()) {
            punteggioCoppia2 += carta.getPunti();
        }
        
        // Converte in punti di gioco e aggiunge i punti bonus
        gameState.setPunteggioCoppia1Totale(punteggioCoppia1 / 100.0 + gameState.getPuntiBonus1());
        gameState.setPunteggioCoppia2Totale(punteggioCoppia2 / 100.0 + gameState.getPuntiBonus2());
    }
    
    /**
     * Controlla se è stata raggiunta l'ultima giocata della mano
     * @param giocata numero della giocata corrente
     * @return true se è l'ultima giocata
     */
    public boolean isUltimaGiocata(int giocata) {
        int giocatePerMano = modalitaDueGiocatori ? 20 : 10;
        return giocata == giocatePerMano;
    }
}
