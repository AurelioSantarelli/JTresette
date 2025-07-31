package it.uniroma1.tresette.controller;

import it.uniroma1.tresette.model.Carta;
import it.uniroma1.tresette.model.Seme;
import it.uniroma1.tresette.model.Giocatore;
import it.uniroma1.tresette.model.observer.*;

import java.util.*;
import java.util.List;

/**
 * Controller principale che gestisce la logica del gioco di Tresette.
 * Si occupa di orchestrare le partite, distribuire le carte, gestire i turni
 * e calcolare i punteggi.
 */
public class GameController {
    
    // Interfaccia per comunicare con la View
    public interface GameView {
        void aggiornaInterfaccia();
        void aggiornaPunteggi(double punteggioCoppia1, double punteggioCoppia2);
        void mostraVittoria(String messaggioVittoria);
        void log(String messaggio);
        void abilitaBottoniCarte(boolean abilita);
        void aggiornaCarteGiocate();
        void aggiornaManiGiocatori();
    }

    // Stato del gioco
    private final Giocatore[] giocatori;
    private final List<Carta> mazzo;
    private final List<Carta> carteGiocate;
    private final GameStateObservable gameObservable;
    private final GameView view;
    private final double PUNTEGGIO_VITTORIA;
    
    private boolean giocoInCorso;
    private boolean giocoInPausa;
    private int giocatoreCorrente;
    private int mano;
    private double punteggioCoppia1Totale;
    private double punteggioCoppia2Totale;

    /**
     * Costruttore del controller del gioco
     * @param nomeGiocatore nome del giocatore umano
     * @param punteggioVittoria punteggio necessario per vincere
     * @param gameObservable observable per notificare gli eventi
     * @param view interfaccia di visualizzazione
     */
    public GameController(String nomeGiocatore, int punteggioVittoria, 
                         GameStateObservable gameObservable, GameView view) {
        this.PUNTEGGIO_VITTORIA = punteggioVittoria;
        this.gameObservable = gameObservable;
        this.view = view;
        
        // Inizializza i giocatori
        giocatori = new Giocatore[4];
        giocatori[0] = new Giocatore(nomeGiocatore, true);
        giocatori[1] = new Giocatore("Marcovaldo", false);
        giocatori[2] = new Giocatore("Viligelmo", false);
        giocatori[3] = new Giocatore("Astolfo", false);
        
        // Inizializza le strutture dati
        mazzo = new ArrayList<>();
        carteGiocate = new ArrayList<>();
        
        // Inizializza lo stato del gioco
        reset();
    }

    /**
     * Resetta lo stato del gioco per una nuova partita
     */
    private void reset() {
        giocoInCorso = false;
        giocoInPausa = false;
        giocatoreCorrente = 0;
        mano = 0;
        punteggioCoppia1Totale = 0;
        punteggioCoppia2Totale = 0;
        
        // Pulisce le carte dei giocatori
        for (Giocatore giocatore : giocatori) {
            giocatore.azzeraCarte();
        }
        
        carteGiocate.clear();
    }

    /**
     * Inizia una nuova partita
     */
    public void nuovaPartita() {
        reset();
        gameObservable.notifyGameStateChanged(GameState.NON_INIZIATO);
        view.log("=== NUOVA PARTITA ===");
        view.log("Punteggio per vincere: " + (int)PUNTEGGIO_VITTORIA + " punti");
        view.log("Giocatori: " + giocatori[0].getNome() + ", " + giocatori[1].getNome() + 
                 ", " + giocatori[2].getNome() + ", " + giocatori[3].getNome());
        
        // Inizia il gioco
        giocoInCorso = true;
        gameObservable.notifyGameStateChanged(GameState.IN_CORSO);
        
        iniziaNuovaMano();
    }

    /**
     * Inizia una nuova mano di gioco
     */
    private void iniziaNuovaMano() {
        mano++;
        view.log("--- Mano " + mano + " ---");
        gameObservable.notifyGameStateChanged(GameState.NUOVA_MANO);
        
        // Pulisce le carte giocate
        carteGiocate.clear();
        
        distribuisciCarte();
        iniziaTurno();
    }

    /**
     * Distribuisce le carte ai giocatori
     */
    private void distribuisciCarte() {
        gameObservable.notifyGameStateChanged(GameState.DISTRIBUZIONE_CARTE);
        
        // Crea il mazzo
        creaMazzo();
        
        // Mescola il mazzo
        Collections.shuffle(mazzo);
        
        // Distribuisce 10 carte a ogni giocatore
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 4; j++) {
                giocatori[j].aggiungiCarta(mazzo.get(i * 4 + j));
            }
        }
        
        view.aggiornaManiGiocatori();
        view.aggiornaInterfaccia();
    }

    /**
     * Crea un mazzo completo di carte
     */
    private void creaMazzo() {
        mazzo.clear();
        for (Seme seme : Seme.values()) {
            for (int valore = 1; valore <= 10; valore++) {
                mazzo.add(new Carta(valore, seme));
            }
        }
    }

    /**
     * Inizia il turno di gioco
     */
    private void iniziaTurno() {
        gameObservable.notifyTurnoCambiato(giocatori[giocatoreCorrente].getNome(), giocatoreCorrente);
        
        if (giocatori[giocatoreCorrente].isUmano()) {
            gameObservable.notifyGameStateChanged(GameState.TURNO_UMANO);
            view.abilitaBottoniCarte(true);
        } else {
            gameObservable.notifyGameStateChanged(GameState.TURNO_AI);
            view.abilitaBottoniCarte(false);
            // Esegue il turno AI dopo un breve delay
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Delay per simulare il "pensiero" dell'AI
                    if (giocoInCorso && !giocoInPausa) {
                        turnoAI();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    /**
     * Gestisce il gioco di una carta da parte del giocatore umano
     * @param indiceCarta indice della carta nella mano del giocatore
     * @return true se la carta è stata giocata con successo
     */
    public boolean giocaCarta(int indiceCarta) {
        if (!giocoInCorso || giocoInPausa || !giocatori[giocatoreCorrente].isUmano()) {
            return false;
        }
        
        List<Carta> manoGiocatore = giocatori[giocatoreCorrente].getMano();
        if (indiceCarta < 0 || indiceCarta >= manoGiocatore.size()) {
            return false;
        }
        
        Carta carta = manoGiocatore.get(indiceCarta);
        return giocaCartaEffettiva(carta);
    }

    /**
     * Esegue effettivamente il gioco di una carta
     * @param carta la carta da giocare
     * @return true se la carta è stata giocata con successo
     */
    private boolean giocaCartaEffettiva(Carta carta) {
        // Rimuove la carta dalla mano del giocatore
        giocatori[giocatoreCorrente].rimuoviCarta(carta);
        
        // Aggiunge la carta alle carte giocate
        carteGiocate.add(carta);
        
        // Notifica l'evento
        gameObservable.notifyCartaGiocata(carta, giocatori[giocatoreCorrente].getNome());
        
        // Controlla se la mano è finita
        if (carteGiocate.size() == 4) {
            gameObservable.notifyGameStateChanged(GameState.VALUTAZIONE_MANO);
            valutaMano();
        } else {
            // Passa al giocatore successivo
            giocatoreCorrente = (giocatoreCorrente + 1) % 4;
            gameObservable.notifyTurnoCambiato(giocatori[giocatoreCorrente].getNome(), giocatoreCorrente);
            
            if (giocatori[giocatoreCorrente].isUmano()) {
                gameObservable.notifyGameStateChanged(GameState.TURNO_UMANO);
                view.abilitaBottoniCarte(true);
            } else {
                gameObservable.notifyGameStateChanged(GameState.TURNO_AI);
                // Continua con l'AI
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        if (giocoInCorso && !giocoInPausa) {
                            turnoAI();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
        }
        
        view.aggiornaCarteGiocate();
        view.aggiornaManiGiocatori();
        view.aggiornaInterfaccia();
        
        return true;
    }

    /**
     * Gestisce il turno dell'AI
     */
    private void turnoAI() {
        if (!giocoInCorso || giocoInPausa || giocatori[giocatoreCorrente].isUmano()) {
            return;
        }
        
        List<Carta> manoAI = giocatori[giocatoreCorrente].getMano();
        if (manoAI.isEmpty()) {
            return;
        }
        
        // Strategia AI semplice: gioca una carta casuale
        Random random = new Random();
        int indiceCartaScelta = random.nextInt(manoAI.size());
        Carta cartaScelta = manoAI.get(indiceCartaScelta);
        
        giocaCartaEffettiva(cartaScelta);
    }

    /**
     * Valuta la mano appena giocata e determina il vincitore
     */
    private void valutaMano() {
        if (carteGiocate.size() != 4) {
            return;
        }
        
        // Determina il vincitore della mano
        int vincitore = determinaVincitoreMano();
        
        // Calcola i punti della mano
        double puntiMano = 0;
        for (Carta carta : carteGiocate) {
            puntiMano += carta.getPunti();
        }
        
        // Aggiunge le carte alle carte prese del vincitore
        giocatori[vincitore].getCartePrese().addAll(carteGiocate);
        
        // Notifica la fine della mano
        gameObservable.notifyFineMano(giocatori[vincitore].getNome(), puntiMano);
        
        view.log(giocatori[vincitore].getNome() + " vince la mano (" + String.format("%.2f", puntiMano) + " punti)");
        
        // Aggiorna i punteggi
        aggiornaPunteggi();
        
        // Il vincitore inizia la prossima mano
        giocatoreCorrente = vincitore;
        
        // Pulisce le carte giocate
        carteGiocate.clear();
        
        // Controlla se ci sono ancora carte da giocare
        if (giocatori[0].getMano().isEmpty()) {
            // Fine della mano, controlla se la partita è finita
            controllaFinePartita();
        } else {
            // Continua con la prossima giocata
            iniziaTurno();
        }
        
        view.aggiornaCarteGiocate();
        view.aggiornaManiGiocatori();
        view.aggiornaInterfaccia();
    }

    /**
     * Determina il vincitore di una mano
     * @return indice del giocatore vincitore
     */
    private int determinaVincitoreMano() {
        int vincitore = (giocatoreCorrente - carteGiocate.size() + 4) % 4;
        int forzaMassima = carteGiocate.get(0).getForzaPerPresa();
        
        for (int i = 1; i < carteGiocate.size(); i++) {
            int forzaCarta = carteGiocate.get(i).getForzaPerPresa();
            if (forzaCarta > forzaMassima) {
                forzaMassima = forzaCarta;
                vincitore = (giocatoreCorrente - carteGiocate.size() + i + 4) % 4;
            }
        }
        
        return vincitore;
    }

    /**
     * Aggiorna i punteggi delle coppie
     */
    private void aggiornaPunteggi() {
        // Calcola punteggi coppia 1 (giocatori 0 e 2)
        double punteggioCoppia1 = 0;
        for (Carta carta : giocatori[0].getCartePrese()) {
            punteggioCoppia1 += carta.getPunti();
        }
        for (Carta carta : giocatori[2].getCartePrese()) {
            punteggioCoppia1 += carta.getPunti();
        }
        
        // Calcola punteggi coppia 2 (giocatori 1 e 3)
        double punteggioCoppia2 = 0;
        for (Carta carta : giocatori[1].getCartePrese()) {
            punteggioCoppia2 += carta.getPunti();
        }
        for (Carta carta : giocatori[3].getCartePrese()) {
            punteggioCoppia2 += carta.getPunti();
        }
        
        punteggioCoppia1 /= 100.0;
        punteggioCoppia2 /= 100.0;
        
        punteggioCoppia1Totale += punteggioCoppia1;
        punteggioCoppia2Totale += punteggioCoppia2;
        
        gameObservable.notifyPunteggiAggiornati(punteggioCoppia1Totale, punteggioCoppia2Totale);
        view.aggiornaPunteggi(punteggioCoppia1Totale, punteggioCoppia2Totale);
    }

    /**
     * Controlla se la partita è finita
     */
    private void controllaFinePartita() {
        boolean partitaFinita = false;
        String messaggioVittoria = "";
        
        if (punteggioCoppia1Totale >= PUNTEGGIO_VITTORIA) {
            partitaFinita = true;
            messaggioVittoria = "Vittoria della coppia " + giocatori[0].getNome() + " - " + giocatori[2].getNome() + "!";
        } else if (punteggioCoppia2Totale >= PUNTEGGIO_VITTORIA) {
            partitaFinita = true;
            messaggioVittoria = "Vittoria della coppia " + giocatori[1].getNome() + " - " + giocatori[3].getNome() + "!";
        }
        
        if (partitaFinita) {
            giocoInCorso = false;
            gameObservable.notifyGameStateChanged(GameState.TERMINATO);
            view.log("=== PARTITA TERMINATA ===");
            view.log(messaggioVittoria);
            view.log(String.format("Punteggio finale: %.2f - %.2f", punteggioCoppia1Totale, punteggioCoppia2Totale));
            view.mostraVittoria(messaggioVittoria);
            view.abilitaBottoniCarte(false);
        } else {
            // Continua con la prossima mano
            iniziaNuovaMano();
        }
    }

    /**
     * Gestisce la pausa/ripresa del gioco
     */
    public void togglePausa() {
        if (!giocoInCorso) {
            return;
        }
        
        giocoInPausa = !giocoInPausa;
        gameObservable.notifyPausaToggled(giocoInPausa);
        
        if (giocoInPausa) {
            gameObservable.notifyGameStateChanged(GameState.IN_PAUSA);
            view.abilitaBottoniCarte(false);
        } else {
            // Riprende il gioco
            if (giocatori[giocatoreCorrente].isUmano()) {
                gameObservable.notifyGameStateChanged(GameState.TURNO_UMANO);
                view.abilitaBottoniCarte(true);
            } else {
                gameObservable.notifyGameStateChanged(GameState.TURNO_AI);
                // Riprende l'AI
                new Thread(() -> {
                    try {
                        Thread.sleep(500);
                        if (giocoInCorso && !giocoInPausa) {
                            turnoAI();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
        }
    }

    // Getters per la view
    public Giocatore[] getGiocatori() {
        return giocatori;
    }
    
    public List<Carta> getCarteGiocate() {
        return carteGiocate;
    }
    
    public boolean isGiocoInCorso() {
        return giocoInCorso;
    }
    
    public boolean isGiocoInPausa() {
        return giocoInPausa;
    }
    
    public int getGiocatoreCorrente() {
        return giocatoreCorrente;
    }
    
    public double getPunteggioCoppia1Totale() {
        return punteggioCoppia1Totale;
    }
    
    public double getPunteggioCoppia2Totale() {
        return punteggioCoppia2Totale;
    }
}
