package it.uniroma1.tresette.controller;

import it.uniroma1.tresette.model.Seme;

/**
 * Gestisce lo stato corrente del gioco di Tresette.
 * Centralizza tutte le informazioni sullo stato della partita.
 */
public class GameStateManager {
    private boolean giocoInCorso;
    private boolean giocoInPausa;
    private boolean valutazioneInCorso;
    private boolean aiInEsecuzione;
    
    private int giocatoreCorrente;
    private int primoGiocatoreMano;
    private int carteGiocateInMano;
    private int mano;
    private int giocata;
    
    private double punteggioCoppia1Totale;
    private double punteggioCoppia2Totale;
    private double puntiBonus1;
    private double puntiBonus2;
    
    private Seme semeRichiesto;
    
    private final boolean modalitaDueGiocatori;
    private final int numeroGiocatori;
    private final double punteggioVittoria;
    
    /**
     * Costruttore del gestore dello stato del gioco
     * 
     * @param modalitaDueGiocatori true per modalità 2 giocatori, false per 4 giocatori
     * @param punteggioVittoria punteggio necessario per vincere la partita
     */
    public GameStateManager(boolean modalitaDueGiocatori, double punteggioVittoria) {
        this.modalitaDueGiocatori = modalitaDueGiocatori;
        this.numeroGiocatori = modalitaDueGiocatori ? 2 : 4;
        this.punteggioVittoria = punteggioVittoria;
        reset();
    }
    
    /**
     * Resetta lo stato del gioco per una nuova partita
     */
    public void reset() {
        giocoInCorso = false;
        giocoInPausa = false;
        valutazioneInCorso = false;
        aiInEsecuzione = false;
        giocatoreCorrente = 0;
        primoGiocatoreMano = 0;
        carteGiocateInMano = 0;
        mano = 0;
        giocata = 0;
        punteggioCoppia1Totale = 0;
        punteggioCoppia2Totale = 0;
        puntiBonus1 = 0;
        puntiBonus2 = 0;
        semeRichiesto = null;
    }
    
    /**
     * Avanza al giocatore successivo
     */
    public void avanzaGiocatore() {
        giocatoreCorrente = (giocatoreCorrente + 1) % numeroGiocatori;
    }
    
    /**
     * Incrementa il numero di giocata
     */
    public void incrementaGiocata() {
        giocata++;
    }
    
    /**
     * Incrementa il numero di carte giocate nella mano corrente
     */
    public void incrementaCarteGiocateInMano() {
        carteGiocateInMano++;
    }
    
    /**
     * Resetta le carte giocate nella mano corrente
     */
    public void resetCarteGiocateInMano() {
        carteGiocateInMano = 0;
    }
    
    /**
     * Controlla se è stata raggiunta la condizione di vittoria
     * 
     * @return true se una coppia ha raggiunto il punteggio di vittoria
     */
    public boolean isVittoriaRaggiunta() {
        if (modalitaDueGiocatori) {
            return punteggioCoppia1Totale >= punteggioVittoria || 
                   punteggioCoppia2Totale >= punteggioVittoria;
        } else {
            return punteggioCoppia1Totale >= punteggioVittoria || 
                   punteggioCoppia2Totale >= punteggioVittoria;
        }
    }
    
    /**
     * Determina il vincitore della partita
     * 
     * @return stringa identificativa del vincitore
     */
    public String getVincitorePartita() {
        if (modalitaDueGiocatori) {
            return punteggioCoppia1Totale > punteggioCoppia2Totale ? 
                   "Giocatore 1" : "Giocatore 2";
        } else {
            return punteggioCoppia1Totale > punteggioCoppia2Totale ? 
                   "Coppia 1" : "Coppia 2";
        }
    }
    
    // Getters e Setters
    
    /** @return true se il gioco è in corso */
    public boolean isGiocoInCorso() { return giocoInCorso; }
    /** @param giocoInCorso stato del gioco */
    public void setGiocoInCorso(boolean giocoInCorso) { this.giocoInCorso = giocoInCorso; }
    
    /** @return true se il gioco è in pausa */
    public boolean isGiocoInPausa() { return giocoInPausa; }
    /** @param giocoInPausa stato di pausa del gioco */
    public void setGiocoInPausa(boolean giocoInPausa) { this.giocoInPausa = giocoInPausa; }
    
    /** @return true se è in corso la valutazione della mano */
    public boolean isValutazioneInCorso() { return valutazioneInCorso; }
    /** @param valutazioneInCorso stato della valutazione */
    public void setValutazioneInCorso(boolean valutazioneInCorso) { this.valutazioneInCorso = valutazioneInCorso; }
    
    /** @return true se l'AI è in esecuzione */
    public boolean isAiInEsecuzione() { return aiInEsecuzione; }
    /** @param aiInEsecuzione stato dell'AI */
    public void setAiInEsecuzione(boolean aiInEsecuzione) { this.aiInEsecuzione = aiInEsecuzione; }
    
    /** @return indice del giocatore corrente */
    public int getGiocatoreCorrente() { return giocatoreCorrente; }
    /** @param giocatoreCorrente nuovo giocatore corrente */
    public void setGiocatoreCorrente(int giocatoreCorrente) { this.giocatoreCorrente = giocatoreCorrente; }
    
    /** @return primo giocatore della mano */
    public int getPrimoGiocatoreMano() { return primoGiocatoreMano; }
    /** @param primoGiocatoreMano nuovo primo giocatore */
    public void setPrimoGiocatoreMano(int primoGiocatoreMano) { this.primoGiocatoreMano = primoGiocatoreMano; }
    
    /** @return numero di carte giocate nella mano corrente */
    public int getCarteGiocateInMano() { return carteGiocateInMano; }
    /** @return numero della mano corrente */
    public int getMano() { return mano; }
    /** @param mano numero della mano */
    public void setMano(int mano) { this.mano = mano; }
    
    /** @return numero della giocata corrente */
    public int getGiocata() { return giocata; }
    
    public double getPunteggioCoppia1Totale() { return punteggioCoppia1Totale; }
    public void setPunteggioCoppia1Totale(double punteggioCoppia1Totale) { 
        this.punteggioCoppia1Totale = punteggioCoppia1Totale; 
    }
    
    public double getPunteggioCoppia2Totale() { return punteggioCoppia2Totale; }
    public void setPunteggioCoppia2Totale(double punteggioCoppia2Totale) { 
        this.punteggioCoppia2Totale = punteggioCoppia2Totale; 
    }
    
    public double getPuntiBonus1() { return puntiBonus1; }
    public void setPuntiBonus1(double puntiBonus1) { this.puntiBonus1 = puntiBonus1; }
    
    public double getPuntiBonus2() { return puntiBonus2; }
    public void setPuntiBonus2(double puntiBonus2) { this.puntiBonus2 = puntiBonus2; }
    
    public Seme getSemeRichiesto() { return semeRichiesto; }
    public void setSemeRichiesto(Seme semeRichiesto) { this.semeRichiesto = semeRichiesto; }
    
    public boolean isModalitaDueGiocatori() { return modalitaDueGiocatori; }
    public int getNumeroGiocatori() { return numeroGiocatori; }
    public double getPunteggioVittoria() { return punteggioVittoria; }
}
