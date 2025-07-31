package it.uniroma1.tresette.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un giocatore nel gioco di Tresette.
 * Mantiene informazioni su nome, carte in mano, carte prese e tipo (umano/AI).
 */
public class Giocatore {
    private final String nome;
    private final List<Carta> mano;
    private final List<Carta> cartePrese;
    private final boolean isUmano;

    /**
     * Costruttore per creare un giocatore
     * @param nome il nome del giocatore
     * @param isUmano true se è un giocatore umano, false se è AI
     */
    public Giocatore(String nome, boolean isUmano) {
        this.nome = nome;
        this.isUmano = isUmano;
        this.mano = new ArrayList<>();
        this.cartePrese = new ArrayList<>();
    }

    /**
     * Aggiunge una carta alla mano del giocatore
     * @param carta la carta da aggiungere
     */
    public void aggiungiCarta(Carta carta) {
        mano.add(carta);
    }

    /**
     * Rimuove una carta dalla mano del giocatore
     * @param carta la carta da rimuovere
     */
    public void rimuoviCarta(Carta carta) {
        mano.remove(carta);
    }

    /**
     * Azzera tutte le carte del giocatore (mano e carte prese)
     */
    public void azzeraCarte() {
        mano.clear();
        cartePrese.clear();
    }

    /**
     * Restituisce la mano del giocatore
     * @return lista delle carte in mano
     */
    public List<Carta> getMano() {
        return mano;
    }

    /**
     * Restituisce le carte prese dal giocatore
     * @return lista delle carte prese
     */
    public List<Carta> getCartePrese() {
        return cartePrese;
    }

    /**
     * Restituisce il nome del giocatore
     * @return il nome del giocatore
     */
    public String getNome() {
        return nome;
    }

    /**
     * Indica se il giocatore è umano o AI
     * @return true se è umano, false se è AI
     */
    public boolean isUmano() {
        return isUmano;
    }

    /**
     * Calcola il punteggio totale delle carte prese dal giocatore
     * @return il punteggio in punti di gioco
     */
    public double calcolaPunteggio() {
        double punteggio = 0;
        for (Carta carta : cartePrese) {
            punteggio += carta.getPunti();
        }
        return punteggio / 100.0; // Converte in punti di gioco
    }

    @Override
    public String toString() {
        return nome + (isUmano ? " (Umano)" : " (AI)");
    }
}
