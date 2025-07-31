package it.uniroma1.tresette;

import java.io.*;
import java.util.Properties;

public class StatisticheGiocatore {
    private static final String STATS_DIR = "player_stats";
    private final String statsFile;
    private final String nomeGiocatore;
    private Properties stats;
    
    public StatisticheGiocatore(String nomeGiocatore) {
        this.nomeGiocatore = sanitizeFileName(nomeGiocatore);
        this.statsFile = STATS_DIR + File.separator + "stats_" + this.nomeGiocatore + ".properties";
        this.stats = new Properties();
        creaDirectorySeNecessaria();
        caricaStatistiche();
    }
    
    // Costruttore di default per retrocompatibilità (usa "Giocatore")
    public StatisticheGiocatore() {
        this("Giocatore");
    }
    
    private String sanitizeFileName(String nome) {
        // Rimuovi caratteri non validi per i nomi file e limita la lunghezza
        return nome.replaceAll("[^a-zA-Z0-9_-]", "_")
                  .substring(0, Math.min(nome.length(), 20));
    }
    
    private void creaDirectorySeNecessaria() {
        File dir = new File(STATS_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                System.err.println("Impossibile creare la directory delle statistiche: " + STATS_DIR);
            }
        }
    }
    
    private void caricaStatistiche() {
        File file = new File(statsFile);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                stats.load(fis);
            } catch (IOException e) {
                System.err.println("Errore nel caricamento delle statistiche per " + nomeGiocatore + ": " + e.getMessage());
                // Inizializza con valori di default
                inizializzaStatisticheDefault();
            }
        } else {
            // File non esiste, inizializza con valori di default
            inizializzaStatisticheDefault();
        }
    }
    
    private void inizializzaStatisticheDefault() {
        stats.setProperty("partite_giocate", "0");
        stats.setProperty("partite_vinte", "0");
        stats.setProperty("partite_perse", "0");
        salvaStatistiche();
    }
    
    public void salvaStatistiche() {
        try (FileOutputStream fos = new FileOutputStream(statsFile)) {
            stats.store(fos, "Statistiche giocatore JTresette - " + nomeGiocatore);
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio delle statistiche per " + nomeGiocatore + ": " + e.getMessage());
        }
    }
    
    public int getPartiteGiocate() {
        return Integer.parseInt(stats.getProperty("partite_giocate", "0"));
    }
    
    public int getPartiteVinte() {
        return Integer.parseInt(stats.getProperty("partite_vinte", "0"));
    }
    
    public int getPartitePerse() {
        return Integer.parseInt(stats.getProperty("partite_perse", "0"));
    }
    
    public void aggiungiVittoria() {
        int vinte = getPartiteVinte() + 1;
        int giocate = getPartiteGiocate() + 1;
        stats.setProperty("partite_vinte", String.valueOf(vinte));
        stats.setProperty("partite_giocate", String.valueOf(giocate));
        salvaStatistiche();
    }
    
    public void aggiungiSconfitta() {
        int perse = getPartitePerse() + 1;
        int giocate = getPartiteGiocate() + 1;
        stats.setProperty("partite_perse", String.valueOf(perse));
        stats.setProperty("partite_giocate", String.valueOf(giocate));
        salvaStatistiche();
    }
    
    public double getPercentualeVittorie() {
        int giocate = getPartiteGiocate();
        if (giocate == 0) return 0.0;
        return (double) getPartiteVinte() / giocate * 100.0;
    }
    
    public void resetStatistiche() {
        inizializzaStatisticheDefault();
    }
    
    public String getNomeGiocatore() {
        return nomeGiocatore;
    }
    
    // Metodo statico per ottenere la lista di tutti i giocatori con statistiche
    public static String[] getGiocatoriConStatistiche() {
        File dir = new File(STATS_DIR);
        if (!dir.exists()) {
            return new String[0];
        }
        
        File[] files = dir.listFiles((d, name) -> name.startsWith("stats_") && name.endsWith(".properties"));
        if (files == null) {
            return new String[0];
        }
        
        String[] nomi = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            // Estrae il nome dal formato "stats_NOME.properties"
            nomi[i] = fileName.substring(6, fileName.length() - 11);
        }
        return nomi;
    }
}
