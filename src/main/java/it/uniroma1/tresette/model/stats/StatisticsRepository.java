package it.uniroma1.tresette.model.stats;

import it.uniroma1.tresette.model.StatisticheGiocatore;

/**
 * Astrazione per la persistenza delle statistiche del giocatore.
 * Consente implementazioni diverse (file system, database, in-memory, remoto).
 */
public interface StatisticsRepository {
    StatisticheGiocatore load();
    void save(StatisticheGiocatore stats);
}
