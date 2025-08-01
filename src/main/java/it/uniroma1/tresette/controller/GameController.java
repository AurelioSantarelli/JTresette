package it.uniroma1.tresette.controller;

import it.uniroma1.tresette.model.Carta;
import it.uniroma1.tresette.model.Seme;
import it.uniroma1.tresette.model.Giocatore;
import it.uniroma1.tresette.model.StatisticheGiocatore;
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
        void aggiornaTurno(String nomeGiocatore, int indiceGiocatore);
        void mostraVittoria(String messaggioVittoria);
        void log(String messaggio);
        void abilitaBottoniCarte(boolean abilita);
        void aggiornaCarteGiocate();
        void aggiornaManiGiocatori();
    }

    // Stato del gioco
    private final Giocatore[] giocatori; // Array fisso con le posizioni originali
    private final List<Carta> mazzo;
    private final List<Carta> carteGiocate;
    private final Carta[] cartePerPosizione; // Array per tracciare le carte per posizione fisica
    private final GameStateObservable gameObservable;
    private final GameView view;
    private final double PUNTEGGIO_VITTORIA;
    private final StatisticheGiocatore statisticheGiocatore;
    
    private boolean giocoInCorso;
    private boolean giocoInPausa;
    private int giocatoreCorrente; // Indice del giocatore corrente (posizione fissa)
    private int primoGiocatoreMano; // Chi ha iniziato/deve iniziare la mano corrente
    private int carteGiocateInMano; // Numero di carte giocate nella mano corrente
    private int mano;
    private int giocata; // Numero della giocata corrente (ogni set di 4 carte)
    private double punteggioCoppia1Totale;
    private double punteggioCoppia2Totale;
    private Seme semeRichiesto; // Il seme della prima carta giocata nella mano

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
        this.statisticheGiocatore = new StatisticheGiocatore(nomeGiocatore);
        
        // Inizializza i giocatori
        giocatori = new Giocatore[4];
        giocatori[0] = new Giocatore(nomeGiocatore, true);
        giocatori[1] = new Giocatore("Marcovaldo", false);
        giocatori[2] = new Giocatore("Viligelmo", false);
        giocatori[3] = new Giocatore("Astolfo", false);
        
        // Inizializza le strutture dati
        mazzo = new ArrayList<>();
        carteGiocate = new ArrayList<>();
        cartePerPosizione = new Carta[4]; // Array per 4 posizioni
        
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
        primoGiocatoreMano = 0; // Il primo giocatore inizia sempre il gioco
        carteGiocateInMano = 0;
        mano = 0;
        giocata = 0; // Inizializza il numero di giocata
        punteggioCoppia1Totale = 0;
        punteggioCoppia2Totale = 0;
        semeRichiesto = null; // Inizializza il seme richiesto
        
        // Pulisce le carte dei giocatori
        for (Giocatore giocatore : giocatori) {
            giocatore.azzeraCarte();
        }
        
        carteGiocate.clear();
        
        // Azzera le carte per posizione
        for (int i = 0; i < 4; i++) {
            cartePerPosizione[i] = null;
        }
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
        
        // Notifica l'avvio del gioco (suono carte mischiate)
        gameObservable.notifyGameStateChanged(GameState.AVVIO_GIOCO);
        
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
        giocata = 0; // Azzera il contatore delle giocate per la nuova mano
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
        // Se stiamo iniziando una nuova mano (carteGiocateInMano == 0), 
        // il giocatore corrente è quello che ha vinto l'ultima mano
        if (carteGiocateInMano == 0) {
            giocatoreCorrente = primoGiocatoreMano;
            // Log della nuova giocata
            view.log("--- Giocata " + (giocata + 1) + " ---");
        }
        
        gameObservable.notifyTurnoCambiato(giocatori[giocatoreCorrente].getNome(), giocatoreCorrente);
        view.aggiornaTurno(giocatori[giocatoreCorrente].getNome(), giocatoreCorrente);
        
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
        
        // Controlla se la carta è giocabile secondo le regole del seme
        if (!isCartaGiocabile(carta, giocatoreCorrente)) {
            // Mostra messaggio di errore al giocatore
            if (semeRichiesto != null) {
                view.log("ERRORE: Devi rispondere al seme " + semeRichiesto.getSimbolo() + " se ce l'hai!");
            }
            return false;
        }
        
        return giocaCartaEffettiva(carta);
    }
    
    /**
     * Controlla se una carta può essere giocata secondo le regole del seme
     * @param carta la carta da controllare
     * @param indiceGiocatore l'indice del giocatore che vuole giocare la carta
     * @return true se la carta è giocabile
     */
    public boolean isCartaGiocabile(Carta carta, int indiceGiocatore) {
        // Se non c'è un seme richiesto (prima carta della mano), tutte le carte sono giocabili
        if (semeRichiesto == null) {
            return true;
        }
        
        // Controlla se il giocatore ha carte del seme richiesto
        List<Carta> manoGiocatore = giocatori[indiceGiocatore].getMano();
        boolean haSemeRichiesto = manoGiocatore.stream().anyMatch(c -> c.getSeme() == semeRichiesto);
        
        // Se non ha carte del seme richiesto, può giocare qualsiasi carta
        if (!haSemeRichiesto) {
            return true;
        }
        
        // Se ha carte del seme richiesto, deve giocare una carta di quel seme
        return carta.getSeme() == semeRichiesto;
    }

    /**
     * Esegue effettivamente il gioco di una carta
     * @param carta la carta da giocare
     * @return true se la carta è stata giocata con successo
     */
    private boolean giocaCartaEffettiva(Carta carta) {
        // Rimuove la carta dalla mano del giocatore
        giocatori[giocatoreCorrente].rimuoviCarta(carta);
        
        // Se è la prima carta della mano, imposta il seme richiesto
        if (carteGiocate.isEmpty()) {
            semeRichiesto = carta.getSeme();
        }
        
        // Aggiunge la carta alle carte giocate
        carteGiocate.add(carta);
        cartePerPosizione[giocatoreCorrente] = carta; // Salva la carta nella posizione del giocatore
        carteGiocateInMano++; // Incrementa il contatore delle carte giocate nella mano
        
        // Notifica l'evento
        gameObservable.notifyCartaGiocata(carta, giocatori[giocatoreCorrente].getNome());
        
        // Controlla se la mano è finita
        if (carteGiocate.size() == 4) {
            // Aggiorna la visualizzazione delle carte prima di valutare la mano
            view.aggiornaCarteGiocate();
            gameObservable.notifyGameStateChanged(GameState.VALUTAZIONE_MANO);
            valutaMano();
        } else {
            // Passa al giocatore successivo
            giocatoreCorrente = (giocatoreCorrente + 1) % 4;
            gameObservable.notifyTurnoCambiato(giocatori[giocatoreCorrente].getNome(), giocatoreCorrente);
            view.aggiornaTurno(giocatori[giocatoreCorrente].getNome(), giocatoreCorrente);
            
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
        
        // Strategia AI che rispetta le regole del seme
        Carta cartaScelta = scegliCartaAI(manoAI);
        
        if (cartaScelta != null) {
            giocaCartaEffettiva(cartaScelta);
        }
    }
    
    /**
     * Implementa la logica di scelta della carta per l'AI
     * @param manoAI la mano del giocatore AI
     * @return la carta scelta dall'AI
     */
    private Carta scegliCartaAI(List<Carta> manoAI) {
        if (manoAI.isEmpty()) {
            return null;
        }
        
        // Se non c'è un seme richiesto (prima carta), gioca una carta casuale
        if (semeRichiesto == null) {
            Random random = new Random();
            return manoAI.get(random.nextInt(manoAI.size()));
        }
        
        // Filtra le carte del seme richiesto
        List<Carta> carteSemeRichiesto = manoAI.stream()
                .filter(c -> c.getSeme() == semeRichiesto)
                .toList();
        
        // Se ha carte del seme richiesto, deve giocare una di quelle
        if (!carteSemeRichiesto.isEmpty()) {
            // Strategia semplice: gioca la carta di forza media per il seme richiesto
            Random random = new Random();
            return carteSemeRichiesto.get(random.nextInt(carteSemeRichiesto.size()));
        }
        
        // Se non ha carte del seme richiesto, può giocare qualsiasi carta
        Random random = new Random();
        return manoAI.get(random.nextInt(manoAI.size()));
    }

    /**
     * Valuta la mano appena giocata e determina il vincitore
     */
    private void valutaMano() {
        if (carteGiocate.size() != 4) {
            return;
        }
        
        // Incrementa il numero di giocata
        giocata++;
        
        // Determina il vincitore della mano
        int vincitore = determinaVincitoreMano();
        
        // Calcola i punti della mano
        double puntiMano = 0;
        for (Carta carta : carteGiocate) {
            puntiMano += carta.getPunti();
        }
        
        // Converte i punti in punti di gioco (da centesimi a punti)
        puntiMano = puntiMano / 100.0;
        
        // Controlla se questa è l'ultima giocata della mano (10ª giocata)
        // Nel Tresette ogni giocatore ha 10 carte, quindi ci sono 10 giocate per mano
        boolean isUltimaGiocata = (giocata == 10);
        
        // Se è l'ultima giocata, aggiungi il punto bonus alla squadra vincitrice
        if (isUltimaGiocata) {
            puntiMano += 1.0; // Punto bonus per l'ultima giocata
            view.log("ULTIMA GIOCATA! " + giocatori[vincitore].getNome() + " ottiene +1 punto bonus!");
        }
        
        // Aggiunge le carte alle carte prese del vincitore
        giocatori[vincitore].getCartePrese().addAll(carteGiocate);
        
        // Notifica la fine della mano
        gameObservable.notifyFineMano(giocatori[vincitore].getNome(), puntiMano);
        
        // Aggiorna i punteggi
        aggiornaPunteggi();
        
        // Il vincitore inizia la prossima mano (mantiene la posizione fisica)
        primoGiocatoreMano = vincitore;
        carteGiocateInMano = 0; // Reset del contatore per la nuova mano
        
        // Aspetta 2 secondi prima di pulire le carte e continuare
        javax.swing.Timer timer = new javax.swing.Timer(2000, e -> {
            // Pulisce le carte giocate dopo il delay
            carteGiocate.clear();
            semeRichiesto = null; // Azzera il seme richiesto per la nuova mano
            
            // Azzera le carte per posizione per la nuova mano
            for (int i = 0; i < 4; i++) {
                cartePerPosizione[i] = null;
            }
            
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
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Determina il vincitore di una mano
     * @return indice del giocatore vincitore
     */
    private int determinaVincitoreMano() {
        if (carteGiocate.isEmpty()) {
            return primoGiocatoreMano;
        }
        
        int vincitore = primoGiocatoreMano; // Il primo giocatore vince di default
        int forzaMassima = -1;
        
        // Controlla tutte le carte giocate
        for (int i = 0; i < carteGiocate.size(); i++) {
            Carta carta = carteGiocate.get(i);
            int giocatoreIdx = (primoGiocatoreMano + i) % 4;
            
            // Solo le carte del seme richiesto possono vincere
            if (carta.getSeme() == semeRichiesto) {
                if (carta.getForzaPerPresa() > forzaMassima) {
                    forzaMassima = carta.getForzaPerPresa();
                    vincitore = giocatoreIdx;
                }
            }
        }
        
        return vincitore;
    }

    /**
     * Aggiorna i punteggi delle coppie
     */
    private void aggiornaPunteggi() {
        // Calcola punteggi totali coppia 1 (giocatori 0 e 2)
        double punteggioCoppia1Totale = 0;
        for (Carta carta : giocatori[0].getCartePrese()) {
            punteggioCoppia1Totale += carta.getPunti();
        }
        for (Carta carta : giocatori[2].getCartePrese()) {
            punteggioCoppia1Totale += carta.getPunti();
        }
        
        // Calcola punteggi totali coppia 2 (giocatori 1 e 3)
        double punteggioCoppia2Totale = 0;
        for (Carta carta : giocatori[1].getCartePrese()) {
            punteggioCoppia2Totale += carta.getPunti();
        }
        for (Carta carta : giocatori[3].getCartePrese()) {
            punteggioCoppia2Totale += carta.getPunti();
        }
        
        // Converte in punti di gioco (divide per 100)
        this.punteggioCoppia1Totale = punteggioCoppia1Totale / 100.0;
        this.punteggioCoppia2Totale = punteggioCoppia2Totale / 100.0;
        
        gameObservable.notifyPunteggiAggiornati(this.punteggioCoppia1Totale, this.punteggioCoppia2Totale);
        view.aggiornaPunteggi(this.punteggioCoppia1Totale, this.punteggioCoppia2Totale);
    }

    /**
     * Controlla se la partita è finita
     */
    private void controllaFinePartita() {
        // Fine di una mano (10 giocate completate)
        view.log("=== FINE MANO " + mano + " ===");
        view.log(String.format("Punteggio: %.2f - %.2f", punteggioCoppia1Totale, punteggioCoppia2Totale));
        
        // Notifica la fine di una mano completa
        gameObservable.notifyFineManoCompleta(mano, punteggioCoppia1Totale, punteggioCoppia2Totale);
        
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
            
            // Salva le statistiche del giocatore
            String nomeGiocatoreUmano = giocatori[0].getNome();
            if (punteggioCoppia1Totale >= PUNTEGGIO_VITTORIA) {
                // La coppia del giocatore umano ha vinto (giocatori[0] e giocatori[2])
                statisticheGiocatore.aggiungiVittoria();
                view.log("Statistiche aggiornate: VITTORIA per " + nomeGiocatoreUmano);
            } else {
                // La coppia avversaria ha vinto
                statisticheGiocatore.aggiungiSconfitta();
                view.log("Statistiche aggiornate: SCONFITTA per " + nomeGiocatoreUmano);
            }
            
            view.mostraVittoria(messaggioVittoria);
            view.abilitaBottoniCarte(false);
        } else {
            // Continua con la prossima mano (nuove 40 carte)
            view.log("=== NUOVA MANO ===");
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
            view.aggiornaTurno("GIOCO IN PAUSA", -1);
            view.abilitaBottoniCarte(false);
        } else {
            // Riprende il gioco
            view.aggiornaTurno(giocatori[giocatoreCorrente].getNome(), giocatoreCorrente);
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
    
    /**
     * Restituisce la carta giocata dal giocatore in una specifica posizione fisica
     * @param posizione posizione fisica del giocatore (0-3)
     * @return la carta giocata o null se non ha ancora giocato
     */
    public Carta getCartaPerPosizione(int posizione) {
        if (posizione >= 0 && posizione < 4) {
            return cartePerPosizione[posizione];
        }
        return null;
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
    
    public Seme getSemeRichiesto() {
        return semeRichiesto;
    }
    
    public double getPunteggioCoppia1Totale() {
        return punteggioCoppia1Totale;
    }
    
    public double getPunteggioCoppia2Totale() {
        return punteggioCoppia2Totale;
    }
}
