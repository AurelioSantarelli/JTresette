package it.uniroma1.tresette.controller;

import it.uniroma1.tresette.model.Carta;
import it.uniroma1.tresette.model.Seme;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Gestisce la logica dell'intelligenza artificiale nel gioco di Tresette.
 * Implementa la strategia di gioco per i giocatori automatici.
 */
public class AIPlayer {
    private final Random random;
    
    public AIPlayer() {
        this.random = new Random();
    }
    
    /**
     * Sceglie una carta per l'AI in base alla strategia implementata
     * @param manoAI la mano del giocatore AI
     * @param semeRichiesto il seme richiesto per questa giocata (null se prima carta)
     * @return la carta scelta dall'AI
     */
    public Carta scegliCarta(List<Carta> manoAI, Seme semeRichiesto) {
        if (manoAI.isEmpty()) {
            return null;
        }
        
        // Se non c'è un seme richiesto, può giocare qualsiasi carta
        if (semeRichiesto == null) {
            return scegliCartaCasuale(manoAI);
        }
        
        // Filtra le carte del seme richiesto
        List<Carta> carteSemeRichiesto = manoAI.stream()
                .filter(carta -> carta.getSeme() == semeRichiesto)
                .collect(Collectors.toList());
        
        // Se ha carte del seme richiesto, deve giocare una di quelle
        if (!carteSemeRichiesto.isEmpty()) {
            return scegliCartaDelSemeRichiesto(carteSemeRichiesto);
        }
        
        // Se non ha carte del seme richiesto, può giocare qualsiasi carta
        return scegliCartaCasuale(manoAI);
    }
    
    /**
     * Sceglie una carta casuale dalla mano
     * @param carte le carte tra cui scegliere
     * @return una carta scelta casualmente
     */
    private Carta scegliCartaCasuale(List<Carta> carte) {
        return carte.get(random.nextInt(carte.size()));
    }
    
    /**
     * Sceglie una carta del seme richiesto usando una strategia semplice
     * @param carteSemeRichiesto le carte del seme richiesto
     * @return la carta scelta
     */
    private Carta scegliCartaDelSemeRichiesto(List<Carta> carteSemeRichiesto) {
        // Strategia semplice: gioca una carta casuale tra quelle del seme richiesto
        // In futuro qui si potrebbero implementare strategie più sofisticate
        return scegliCartaCasuale(carteSemeRichiesto);
    }
    
    /**
     * Strategia avanzata: cerca di giocare la carta migliore per vincere la mano
     * @param carteSemeRichiesto le carte del seme richiesto disponibili
     * @param carteGiocate le carte già giocate in questa mano
     * @return la carta scelta strategicamente
     */
    private Carta scegliCartaStrategica(List<Carta> carteSemeRichiesto, List<Carta> carteGiocate) {
        // Implementazione strategica più avanzata (per future migliorie)
        // Per ora usa la strategia semplice
        return scegliCartaDelSemeRichiesto(carteSemeRichiesto);
    }
    
    /**
     * Valuta se conviene vincere la mano corrente o meno
     * @param carteGiocate le carte già giocate
     * @return true se conviene vincere la mano
     */
    private boolean convieneVincereMano(List<Carta> carteGiocate) {
        // Calcola i punti totali delle carte giocate
        int puntiMano = carteGiocate.stream()
                .mapToInt(Carta::getPunti)
                .sum();
        
        // Conviene vincere se ci sono molti punti in gioco
        return puntiMano >= 100; // Soglia configurabile
    }
    
    /**
     * Trova la carta più forte tra quelle disponibili
     * @param carte le carte tra cui cercare
     * @return la carta più forte
     */
    private Carta trovaCartaPiuForte(List<Carta> carte) {
        return carte.stream()
                .max((c1, c2) -> Integer.compare(c1.getForzaPerPresa(), c2.getForzaPerPresa()))
                .orElse(carte.get(0));
    }
    
    /**
     * Trova la carta più debole tra quelle disponibili
     * @param carte le carte tra cui cercare
     * @return la carta più debole
     */
    private Carta trovaCartaPiuDebole(List<Carta> carte) {
        return carte.stream()
                .min((c1, c2) -> Integer.compare(c1.getForzaPerPresa(), c2.getForzaPerPresa()))
                .orElse(carte.get(0));
    }
}
