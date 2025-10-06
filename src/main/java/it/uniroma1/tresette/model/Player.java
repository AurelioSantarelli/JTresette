package it.uniroma1.tresette.model;

import it.uniroma1.tresette.model.Carta;
import java.util.List;

/**
 * Player abstraction to allow multiple implementations (Human, AI, Remote).
 * This is an initial skeleton designed to be adopted incrementally without
 * breaking existing code that uses {@link Giocatore} directly.
 */
public interface Player {
    /** Nome visualizzato del giocatore. */
    String getNome();

    /** Mano corrente del giocatore (immutabile o mutabile a seconda dell'implementazione). */
    List<Carta> getMano();

    /** Indica se il giocatore è umano (UI-driven) o meno. */
    boolean isUmano();

    /** Riceve una carta nella mano del giocatore. */
    void riceviCarta(Carta carta);

    /** Resetta la mano (inizio nuova partita/mano). */
    void resetMano();
}
