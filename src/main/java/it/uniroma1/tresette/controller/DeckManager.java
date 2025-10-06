package it.uniroma1.tresette.controller;

import it.uniroma1.tresette.model.Carta;
import it.uniroma1.tresette.model.Giocatore;
import it.uniroma1.tresette.model.Seme;

import java.util.*;

/**
 * Gestisce il mazzo di carte e la distribuzione ai giocatori.
 * Si occupa della creazione, mescolamento e distribuzione delle carte.
 */
public class DeckManager {
    private final List<Carta> mazzo;
    private final List<Carta> carteGiocate;
    private final Carta[] cartePerPosizione;
    private final boolean modalitaDueGiocatori;
    private final int numeroGiocatori;
    
    public DeckManager(boolean modalitaDueGiocatori) {
        this.modalitaDueGiocatori = modalitaDueGiocatori;
        this.numeroGiocatori = modalitaDueGiocatori ? 2 : 4;
        this.mazzo = new ArrayList<>();
        this.carteGiocate = new ArrayList<>();
        this.cartePerPosizione = new Carta[4]; // Massimo 4 giocatori
    }
    
    /**
     * Crea un mazzo completo di carte
     * Per ogni seme crea le carte da 1 a 10
     */
    public void creaMazzo() {
        mazzo.clear();
        for (Seme seme : Seme.values()) {
            for (int valore = 1; valore <= 10; valore++) {
                mazzo.add(new Carta(valore, seme));
            }
        }
    }
    
    /**
     * Mescola il mazzo di carte
     */
    public void mescolaMazzo() {
        Collections.shuffle(mazzo);
    }
    
    /**
     * Distribuisce le carte ai giocatori
     * @param giocatori array dei giocatori a cui distribuire le carte
     */
    public void distribuisciCarte(Giocatore[] giocatori) {
        // In modalità 2 giocatori: 20 carte a testa
        // In modalità 4 giocatori: 10 carte a testa
        int cartePerGiocatore = modalitaDueGiocatori ? 20 : 10;
        
        // Distribuisce le carte a ogni giocatore
        for (int i = 0; i < cartePerGiocatore; i++) {
            for (int j = 0; j < numeroGiocatori; j++) {
                if (i * numeroGiocatori + j < mazzo.size()) {
                    giocatori[j].aggiungiCarta(mazzo.get(i * numeroGiocatori + j));
                }
            }
        }
    }
    
    /**
     * Aggiunge una carta alle carte giocate
     * @param carta la carta da aggiungere
     * @param posizione la posizione del giocatore che ha giocato la carta
     */
    public void aggiungiCartaGiocata(Carta carta, int posizione) {
        carteGiocate.add(carta);
        if (posizione >= 0 && posizione < cartePerPosizione.length) {
            cartePerPosizione[posizione] = carta;
        }
    }
    
    /**
     * Pulisce le carte giocate per una nuova mano
     */
    public void pulisciCarteGiocate() {
        carteGiocate.clear();
        Arrays.fill(cartePerPosizione, null);
    }
    
    /**
     * Restituisce la carta giocata dal giocatore in una specifica posizione
     * @param posizione posizione fisica del giocatore (0-3)
     * @return la carta giocata o null se non ha ancora giocato
     */
    public Carta getCartaPerPosizione(int posizione) {
        if (posizione >= 0 && posizione < cartePerPosizione.length) {
            return cartePerPosizione[posizione];
        }
        return null;
    }
    
    /**
     * Controlla se una carta può essere giocata secondo le regole del seme
     * @param carta la carta da controllare
     * @param giocatore il giocatore che vuole giocare la carta
     * @param semeRichiesto il seme richiesto per questa mano
     * @return true se la carta è giocabile
     */
    public boolean isCartaGiocabile(Carta carta, Giocatore giocatore, Seme semeRichiesto) {
        // Se non c'è seme richiesto (prima carta della mano), qualsiasi carta è valida
        if (semeRichiesto == null) {
            return true;
        }
        
        // Se la carta è del seme richiesto, è sempre giocabile
        if (carta.getSeme() == semeRichiesto) {
            return true;
        }
        
        // Se la carta non è del seme richiesto, controlla se il giocatore ha carte di quel seme
        return giocatore.getMano().stream()
                .noneMatch(c -> c.getSeme() == semeRichiesto);
    }
    
    // Getters
    public List<Carta> getMazzo() { 
        return new ArrayList<>(mazzo); 
    }
    
    public List<Carta> getCarteGiocate() { 
        return new ArrayList<>(carteGiocate); 
    }
    
    /** @return numero di carte attualmente giocate */
    public int getNumeroCarteGiocate() {
        return carteGiocate.size();
    }
    
    /** @return true se è attiva la modalità a due giocatori */
    public boolean isModalitaDueGiocatori() {
        return modalitaDueGiocatori;
    }
    
    /** @return numero totale di giocatori */
    public int getNumeroGiocatori() {
        return numeroGiocatori;
    }
}
