package it.uniroma1.tresette.controller;

import it.uniroma1.tresette.controller.ScoreCalculator;
import it.uniroma1.tresette.model.Carta;
import it.uniroma1.tresette.model.Seme;
import java.util.List;

/**
 * Facade per la gestione dei punteggi e della valutazione della mano.
 * Incapsula {@link ScoreCalculator} offrendo un'API più chiara al controller.
 */
public class ScoreManager {
    private final ScoreCalculator calculator;
    private double punteggioCoppia1Totale;
    private double punteggioCoppia2Totale;

    public ScoreManager(ScoreCalculator calculator) {
        this.calculator = calculator;
    }

    public void resetPunteggi() {
        punteggioCoppia1Totale = 0.0;
        punteggioCoppia2Totale = 0.0;
    }

    public void accumulaPunteggioCoppia1(double punti) {
        punteggioCoppia1Totale += punti;
    }

    public void accumulaPunteggioCoppia2(double punti) {
        punteggioCoppia2Totale += punti;
    }

    public double getPunteggioCoppia1Totale() { return punteggioCoppia1Totale; }
    public double getPunteggioCoppia2Totale() { return punteggioCoppia2Totale; }

    /**
     * Determina il vincitore della mano e ritorna l'indice del giocatore vincente.
     * Richiede l'indice del primo giocatore della mano e il seme richiesto.
     */
    public int determinaVincitoreMano(List<Carta> carteGiocate, int primoGiocatoreMano, Seme semeRichiesto) {
        return calculator.determinaVincitoreMano(carteGiocate, primoGiocatoreMano, semeRichiesto);
    }

    /** Calcola i punti totali della mano (somma dei punti carta divisa per 100). */
    public double calcolaPuntiMano(List<Carta> carteGiocate) {
        return calculator.calcolaPuntiMano(carteGiocate);
    }
}
