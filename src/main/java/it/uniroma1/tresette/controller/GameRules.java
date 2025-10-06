package it.uniroma1.tresette.controller;

import it.uniroma1.tresette.model.Carta;
import it.uniroma1.tresette.model.Seme;
import java.util.List;

/**
 * Regole core del gioco: validazioni e confronto carte.
 * Questa classe può essere usata da ScoreCalculator e dal Controller per
 * validare mosse e determinare risultati.
 */
public class GameRules {

    /** Verifica se una carta è giocabile rispetto al seme richiesto e alla mano del giocatore. */
    public boolean isCartaGiocabile(List<Carta> mano, Carta cartaDaGiocare, Seme semeRichiesto) {
        if (semeRichiesto == null) return true; // apertura: qualsiasi carta
        boolean haSemeRichiesto = mano.stream().anyMatch(c -> c.getSeme() == semeRichiesto);
        if (!haSemeRichiesto) return true; // non può seguire, qualsiasi carta va bene
        return cartaDaGiocare.getSeme() == semeRichiesto; // deve seguire il seme
    }

    /** Ritorna true se c1 batte c2 considerando il seme richiesto. */
    public boolean vinceTra(Carta c1, Carta c2, Seme semeRichiesto) {
        if (c1.getSeme() == c2.getSeme()) {
            return c1.getValore() > c2.getValore();
        }
        // Carte del seme richiesto battono sempre le altre
        if (c1.getSeme() == semeRichiesto && c2.getSeme() != semeRichiesto) return true;
        if (c2.getSeme() == semeRichiesto && c1.getSeme() != semeRichiesto) return false;
        // Nessuna del seme richiesto: la prima giocata resta vincente
        return false;
    }

    /** Determina indice della carta vincente nella lista giocate. */
    public int indiceVincente(List<Carta> carteGiocate, Seme semeRichiesto) {
        if (carteGiocate == null || carteGiocate.isEmpty()) return -1;
        int idx = 0;
        Carta vincente = carteGiocate.get(0);
        for (int i = 1; i < carteGiocate.size(); i++) {
            Carta curr = carteGiocate.get(i);
            if (vinceTra(curr, vincente, semeRichiesto)) {
                idx = i;
                vincente = curr;
            }
        }
        return idx;
    }
}
