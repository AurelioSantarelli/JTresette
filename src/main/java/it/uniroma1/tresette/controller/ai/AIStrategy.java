package it.uniroma1.tresette.controller.ai;

import it.uniroma1.tresette.model.Carta;
import it.uniroma1.tresette.model.Giocatore;
import it.uniroma1.tresette.model.Seme;
import java.util.List;

/**
 * Strategy abstraction for AI decision-making.
 * Different implementations can model different difficulty levels or styles.
 */
public interface AIStrategy {
    /**
     * Seleziona l'indice della carta da giocare dalla mano del giocatore.
     *
     * @param giocatore       il giocatore AI
     * @param semeRichiesto   il seme richiesto nella mano corrente (può essere null se apertura)
     * @param carteGiocate    le carte già giocate in questa mano (ordine di gioco)
     * @return indice della carta nella mano del giocatore da giocare
     */
    int selezionaCarta(Giocatore giocatore, Seme semeRichiesto, List<Carta> carteGiocate);
}
