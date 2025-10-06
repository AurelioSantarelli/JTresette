package it.uniroma1.tresette.controller.ai;

import it.uniroma1.tresette.model.Carta;
import it.uniroma1.tresette.model.Giocatore;
import it.uniroma1.tresette.model.Seme;
import java.util.Comparator;
import java.util.List;

/**
 * Basic AI strategy emulating current heuristics: follow suit when possible,
 * try to win if feasible, otherwise discard minimal value cards.
 */
public class BasicAIStrategy implements AIStrategy {

    @Override
    public int selezionaCarta(Giocatore giocatore, Seme semeRichiesto, List<Carta> carteGiocate) {
        List<Carta> mano = giocatore.getMano();
        if (mano == null || mano.isEmpty()) return -1;

        if (semeRichiesto == null) {
            // Apertura: gioca una carta media per non scoprire troppo
            return indiceCartaConValoreMediano(mano);
        }

        // Cerca carte del seme richiesto
        int idxSeguendoSeme = indiceCartaMinimaDelSeme(mano, semeRichiesto);
        if (idxSeguendoSeme >= 0) {
            // Se può seguire il seme, valuta se può vincere
            int idxVincente = indiceCartaMinimaCheVince(mano, semeRichiesto, carteGiocate);
            return idxVincente >= 0 ? idxVincente : idxSeguendoSeme;
        }

        // Non può seguire: scarta la carta più bassa
        return indiceCartaMinima(mano);
    }

    private int indiceCartaConValoreMediano(List<Carta> mano) {
        if (mano.size() <= 2) return 0;
        List<Carta> sorted = mano.stream().sorted(Comparator.comparingInt(Carta::getValore)).toList();
        Carta mediana = sorted.get(sorted.size() / 2);
        return mano.indexOf(mediana);
    }

    private int indiceCartaMinimaDelSeme(List<Carta> mano, Seme seme) {
        int idx = -1;
        int minVal = Integer.MAX_VALUE;
        for (int i = 0; i < mano.size(); i++) {
            Carta c = mano.get(i);
            if (c.getSeme() == seme && c.getValore() < minVal) {
                minVal = c.getValore();
                idx = i;
            }
        }
        return idx;
    }

    private int indiceCartaMinima(List<Carta> mano) {
        int idx = 0;
        int minVal = Integer.MAX_VALUE;
        for (int i = 0; i < mano.size(); i++) {
            int v = mano.get(i).getValore();
            if (v < minVal) {
                minVal = v;
                idx = i;
            }
        }
        return idx;
    }

    private int indiceCartaMinimaCheVince(List<Carta> mano, Seme semeRichiesto, List<Carta> carteGiocate) {
        // Semplice euristica: se mano contiene una carta del seme richiesto con valore maggiore
        // della migliore giocata finora, prova a vincere con la minima che supera.
        int bestValue = -1;
        for (Carta c : carteGiocate) {
            if (c.getSeme() == semeRichiesto && c.getValore() > bestValue) {
                bestValue = c.getValore();
            }
        }
        int idx = -1;
        int minOver = Integer.MAX_VALUE;
        for (int i = 0; i < mano.size(); i++) {
            Carta c = mano.get(i);
            if (c.getSeme() == semeRichiesto && c.getValore() > bestValue && c.getValore() < minOver) {
                minOver = c.getValore();
                idx = i;
            }
        }
        return idx;
    }
}
