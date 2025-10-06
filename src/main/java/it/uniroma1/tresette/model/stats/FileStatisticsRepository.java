package it.uniroma1.tresette.model.stats;

import it.uniroma1.tresette.model.StatisticheGiocatore;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Implementazione basata su file per la persistenza delle statistiche.
 * Nota: questa è una bozza semplice; si può evolvere per gestire errori e locking.
 */
public class FileStatisticsRepository implements StatisticsRepository {
    private final Path file;

    public FileStatisticsRepository(Path file) {
        this.file = file;
    }

    @Override
    public StatisticheGiocatore load() {
        // Usa la logica interna di StatisticheGiocatore che già gestisce file e default
        // Qui si legge dall'inline path per compatibilità, convertendo le chiavi se necessario.
        Properties props = new Properties();
        StatisticheGiocatore stats = new StatisticheGiocatore();
        if (Files.exists(file)) {
            try (var in = Files.newInputStream(file)) {
                props.load(in);
                // Converte eventuali chiavi diverse in quelle attese da StatisticheGiocatore
                // Nota: StatisticheGiocatore gestisce internamente il proprio file in player_stats.
                // Questa implementazione di load ritorna un'istanza con valori calcolati dal costruttore.
            } catch (IOException e) {
                // In caso di errore, restituisce stats con valori di default
            }
        }
        return stats;
    }

    @Override
    public void save(StatisticheGiocatore stats) {
        Properties props = new Properties();
        props.setProperty("partite_giocate", String.valueOf(stats.getPartiteGiocate()));
        props.setProperty("partite_vinte", String.valueOf(stats.getPartiteVinte()));
        props.setProperty("partite_perse", String.valueOf(stats.getPartitePerse()));
        try (var out = Files.newOutputStream(file)) {
            props.store(out, "Statistiche Giocatore");
        } catch (IOException e) {
            // Loggare tramite LoggingObserver o logger; non propagare per non rompere il gioco
        }
    }
}
