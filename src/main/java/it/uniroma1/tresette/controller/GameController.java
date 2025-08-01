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
    private final boolean modalitaDueGiocatori; // true per 1v1, false per 4 giocatori
    private final int numeroGiocatori; // 2 o 4 giocatori
    
    private boolean giocoInCorso;
    private boolean giocoInPausa;
    private boolean valutazioneInCorso; // Flag per prevenire chiamate multiple durante valutazione
    private boolean aiInEsecuzione; // Flag per prevenire AI concorrenti
    private int giocatoreCorrente; // Indice del giocatore corrente (posizione fissa)
    private int primoGiocatoreMano; // Chi ha iniziato/deve iniziare la mano corrente
    private int carteGiocateInMano; // Numero di carte giocate nella mano corrente
    private int mano;
    private int giocata; // Numero della giocata corrente (ogni set di carte)
    private double punteggioCoppia1Totale;
    private double punteggioCoppia2Totale;
    private double puntiBonus1; // Punti bonus per giocatore/coppia 1
    private double puntiBonus2; // Punti bonus per giocatore/coppia 2
    private Seme semeRichiesto; // Il seme della prima carta giocata nella mano

    /**
     * Costruttore del controller del gioco (modalità 4 giocatori)
     * @param nomeGiocatore nome del giocatore umano
     * @param punteggioVittoria punteggio necessario per vincere
     * @param gameObservable observable per notificare gli eventi
     * @param view interfaccia di visualizzazione
     */
    public GameController(String nomeGiocatore, int punteggioVittoria, 
                         GameStateObservable gameObservable, GameView view) {
        this(nomeGiocatore, punteggioVittoria, false, gameObservable, view);
    }

    /**
     * Costruttore completo del controller del gioco
     * @param nomeGiocatore nome del giocatore umano
     * @param punteggioVittoria punteggio necessario per vincere
     * @param modalitaDueGiocatori true per modalità 1v1, false per modalità 4 giocatori
     * @param gameObservable observable per notificare gli eventi
     * @param view interfaccia di visualizzazione
     */
    public GameController(String nomeGiocatore, int punteggioVittoria, boolean modalitaDueGiocatori,
                         GameStateObservable gameObservable, GameView view) {
        this.PUNTEGGIO_VITTORIA = punteggioVittoria;
        this.gameObservable = gameObservable;
        this.view = view;
        this.statisticheGiocatore = new StatisticheGiocatore(nomeGiocatore);
        this.modalitaDueGiocatori = modalitaDueGiocatori;
        this.numeroGiocatori = modalitaDueGiocatori ? 2 : 4;
        
        // Inizializza i giocatori in base alla modalità
        if (modalitaDueGiocatori) {
            giocatori = new Giocatore[2];
            giocatori[0] = new Giocatore(nomeGiocatore, true);
            giocatori[1] = new Giocatore("Marcovaldo", false);
        } else {
            giocatori = new Giocatore[4];
            giocatori[0] = new Giocatore(nomeGiocatore, true);
            giocatori[1] = new Giocatore("Marcovaldo", false);
            giocatori[2] = new Giocatore("Viligelmo", false);
            giocatori[3] = new Giocatore("Astolfo", false);
        }
        
        // Inizializza le strutture dati
        mazzo = new ArrayList<>();
        carteGiocate = new ArrayList<>();
        cartePerPosizione = new Carta[numeroGiocatori]; // Array per numero giocatori
        
        // Inizializza lo stato del gioco
        reset();
    }

    /**
     * Resetta lo stato del gioco per una nuova partita
     */
    private void reset() {
        giocoInCorso = false;
        giocoInPausa = false;
        valutazioneInCorso = false;
        aiInEsecuzione = false; // Reset AI lock
        giocatoreCorrente = 0;
        primoGiocatoreMano = 0; // Il primo giocatore inizia sempre il gioco
        carteGiocateInMano = 0;
        mano = 0;
        giocata = 0; // Inizializza il numero di giocata
        punteggioCoppia1Totale = 0;
        punteggioCoppia2Totale = 0;
        puntiBonus1 = 0;
        puntiBonus2 = 0;
        semeRichiesto = null; // Inizializza il seme richiesto
        
        // Pulisce le carte dei giocatori
        for (Giocatore giocatore : giocatori) {
            giocatore.azzeraCarte();
        }
        
        carteGiocate.clear();
        
        // Azzera le carte per posizione
        for (int i = 0; i < numeroGiocatori; i++) {
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
        
        // Log dei giocatori in base alla modalità
        if (modalitaDueGiocatori) {
            view.log("Giocatori: " + giocatori[0].getNome() + ", " + giocatori[1].getNome());
        } else {
            view.log("Giocatori: " + giocatori[0].getNome() + ", " + giocatori[1].getNome() + 
                     ", " + giocatori[2].getNome() + ", " + giocatori[3].getNome());
        }
        
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
        
        // In modalità 2 giocatori: 20 carte a testa
        // In modalità 4 giocatori: 10 carte a testa
        int cartePerGiocatore = modalitaDueGiocatori ? 20 : 10;
        
        // Distribuisce le carte a ogni giocatore
        for (int i = 0; i < cartePerGiocatore; i++) {
            for (int j = 0; j < numeroGiocatori; j++) {
                giocatori[j].aggiungiCarta(mazzo.get(i * numeroGiocatori + j));
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
        // Controlli di sicurezza
        if (carta == null || indiceGiocatore < 0 || indiceGiocatore >= giocatori.length) {
            return false;
        }
        
        // CONTROLLO DI SICUREZZA: Se le carte giocate sono vuote, siamo all'inizio di una nuova mano
        // In questo caso, il seme richiesto dovrebbe essere null
        if (carteGiocate.isEmpty()) {
            if (semeRichiesto != null) {
                System.out.println("DEBUG: INCONSISTENZA RILEVATA! carteGiocate vuote ma semeRichiesto=" + semeRichiesto + " -> RESETTO");
                semeRichiesto = null; // Forza il reset
            }
        }
        
        // Se non c'è un seme richiesto (prima carta della mano), tutte le carte sono giocabili
        if (semeRichiesto == null) {
            return true;
        }
        
        // Ottieni la mano del giocatore
        List<Carta> manoGiocatore = giocatori[indiceGiocatore].getMano();
        if (manoGiocatore == null || manoGiocatore.isEmpty()) {
            return false;
        }
        
        // Conta le carte del seme richiesto nella mano
        int carteDelSemeRichiesto = 0;
        for (Carta c : manoGiocatore) {
            if (c != null && c.getSeme() == semeRichiesto) {
                carteDelSemeRichiesto++;
            }
        }
        
        // REGOLA DEL TRESETTE:
        if (carteDelSemeRichiesto == 0) {
            // Se NON ha carte del seme richiesto, può giocare QUALSIASI carta
            return true;
        } else {
            // Se HA carte del seme richiesto, DEVE giocare una di quelle
            return carta.getSeme() == semeRichiesto;
        }
    }

    /**
     * Esegue effettivamente il gioco di una carta
     * @param carta la carta da giocare
     * @return true se la carta è stata giocata con successo
     */
    private boolean giocaCartaEffettiva(Carta carta) {
        if (valutazioneInCorso) {
            return false;
        }
        
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
        if (carteGiocate.size() == numeroGiocatori) {
            // Imposta il flag per prevenire altre chiamate AI
            valutazioneInCorso = true;
            
            // Aggiorna la visualizzazione delle carte prima di valutare la mano
            view.aggiornaCarteGiocate();
            gameObservable.notifyGameStateChanged(GameState.VALUTAZIONE_MANO);
            valutaMano();
        } else {
            // Passa al giocatore successivo
            giocatoreCorrente = (giocatoreCorrente + 1) % numeroGiocatori;
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
    private synchronized void turnoAI() {
        if (!giocoInCorso || giocoInPausa || giocatori[giocatoreCorrente].isUmano() || valutazioneInCorso || aiInEsecuzione) {
            return;
        }
        
        // Imposta il flag per prevenire chiamate concorrenti
        aiInEsecuzione = true;
        
        try {
            List<Carta> manoAI = giocatori[giocatoreCorrente].getMano();
            if (manoAI.isEmpty()) {
                return;
            }
            
            // Strategia AI che rispetta le regole del seme
            Carta cartaScelta = scegliCartaAI(manoAI);
            
            if (cartaScelta != null) {
                giocaCartaEffettiva(cartaScelta);
            }
        } finally {
            // Libera il lock AI sempre, anche in caso di errore
            aiInEsecuzione = false;
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
        if (carteGiocate.size() != numeroGiocatori) {
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
        
        // Determina il numero di giocate per mano in base alla modalità
        int giocatePerMano = modalitaDueGiocatori ? 20 : 10;
        
        // Controlla se questa è l'ultima giocata della mano
        boolean isUltimaGiocata = (giocata == giocatePerMano);
        
        // Se è l'ultima giocata, aggiungi il punto bonus alla squadra vincitrice
        if (isUltimaGiocata) {
            puntiMano += 1.0; // Punto bonus per l'ultima giocata
            
            // Registra il punto bonus per il giocatore/coppia corretti
            if (modalitaDueGiocatori) {
                // Modalità 1v1: assegna al giocatore vincitore
                if (vincitore == 0) {
                    puntiBonus1 += 1.0;
                } else {
                    puntiBonus2 += 1.0;
                }
            } else {
                // Modalità 4 giocatori: assegna alla coppia del vincitore
                if (vincitore == 0 || vincitore == 2) {
                    puntiBonus1 += 1.0; // Coppia 1 (giocatori 0 e 2)
                } else {
                    puntiBonus2 += 1.0; // Coppia 2 (giocatori 1 e 3)
                }
            }
            
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
        
        // IMPORTANTE: Azzera immediatamente il seme richiesto per la nuova giocata
        semeRichiesto = null;
        
        // Aspetta 2 secondi prima di pulire le carte e continuare
        javax.swing.Timer timer = new javax.swing.Timer(2000, e -> {
            // Azzera il flag di valutazione
            valutazioneInCorso = false;
            
            // Pulisce le carte giocate dopo il delay
            carteGiocate.clear();
            
            // Azzera le carte per posizione per la nuova mano
            for (int i = 0; i < numeroGiocatori; i++) {
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
        
        int vincitore = -1;
        int forzaMassima = -1;
        
        // Controlla tutte le carte per posizione giocatore
        for (int giocatoreIdx = 0; giocatoreIdx < numeroGiocatori; giocatoreIdx++) {
            Carta carta = cartePerPosizione[giocatoreIdx];
            if (carta == null) continue; // Salta se il giocatore non ha giocato
            
            // Solo le carte del seme richiesto possono vincere
            if (carta.getSeme() == semeRichiesto) {
                if (carta.getForzaPerPresa() > forzaMassima) {
                    forzaMassima = carta.getForzaPerPresa();
                    vincitore = giocatoreIdx;
                }
            }
        }
        
        // Se nessuno ha giocato una carta del seme richiesto, 
        // vince chi ha giocato la prima carta (che ha stabilito il seme)
        if (vincitore == -1) {
            vincitore = primoGiocatoreMano;
        }
        
        return vincitore;
    }

    /**
     * Aggiorna i punteggi delle coppie
     */
    private void aggiornaPunteggi() {
        if (modalitaDueGiocatori) {
            // Modalità 1v1: calcola punteggi individuali
            double punteggioGiocatore1 = 0;
            for (Carta carta : giocatori[0].getCartePrese()) {
                punteggioGiocatore1 += carta.getPunti();
            }
            
            double punteggioGiocatore2 = 0;
            for (Carta carta : giocatori[1].getCartePrese()) {
                punteggioGiocatore2 += carta.getPunti();
            }
            
            // Converte in punti di gioco (divide per 100) e aggiunge i punti bonus
            this.punteggioCoppia1Totale = (punteggioGiocatore1 / 100.0) + puntiBonus1;
            this.punteggioCoppia2Totale = (punteggioGiocatore2 / 100.0) + puntiBonus2;
        } else {
            // Modalità 4 giocatori: calcola punteggi per coppie
            double punteggioCoppia1Totale = 0;
            for (Carta carta : giocatori[0].getCartePrese()) {
                punteggioCoppia1Totale += carta.getPunti();
            }
            for (Carta carta : giocatori[2].getCartePrese()) {
                punteggioCoppia1Totale += carta.getPunti();
            }
            
            double punteggioCoppia2Totale = 0;
            for (Carta carta : giocatori[1].getCartePrese()) {
                punteggioCoppia2Totale += carta.getPunti();
            }
            for (Carta carta : giocatori[3].getCartePrese()) {
                punteggioCoppia2Totale += carta.getPunti();
            }
            
            // Converte in punti di gioco (divide per 100) e aggiunge i punti bonus
            this.punteggioCoppia1Totale = (punteggioCoppia1Totale / 100.0) + puntiBonus1;
            this.punteggioCoppia2Totale = (punteggioCoppia2Totale / 100.0) + puntiBonus2;
        }
        
        gameObservable.notifyPunteggiAggiornati(this.punteggioCoppia1Totale, this.punteggioCoppia2Totale);
        view.aggiornaPunteggi(this.punteggioCoppia1Totale, this.punteggioCoppia2Totale);
    }

    /**
     * Controlla se la partita è finita
     */
    private void controllaFinePartita() {
        // Fine di una mano (10 o 20 giocate completate in base alla modalità)
        view.log("=== FINE MANO " + mano + " ===");
        view.log(String.format("Punteggio: %.2f - %.2f", punteggioCoppia1Totale, punteggioCoppia2Totale));
        
        // Notifica la fine di una mano completa
        gameObservable.notifyFineManoCompleta(mano, punteggioCoppia1Totale, punteggioCoppia2Totale);
        
        boolean partitaFinita = false;
        String messaggioVittoria = "";
        
        if (modalitaDueGiocatori) {
            // Modalità 1v1: verifica vincitore individuale
            if (punteggioCoppia1Totale >= PUNTEGGIO_VITTORIA) {
                partitaFinita = true;
                messaggioVittoria = "Vittoria di " + giocatori[0].getNome() + "!";
            } else if (punteggioCoppia2Totale >= PUNTEGGIO_VITTORIA) {
                partitaFinita = true;
                messaggioVittoria = "Vittoria di " + giocatori[1].getNome() + "!";
            }
        } else {
            // Modalità 4 giocatori: verifica vincitore per coppie
            if (punteggioCoppia1Totale >= PUNTEGGIO_VITTORIA) {
                partitaFinita = true;
                messaggioVittoria = "Vittoria della coppia " + giocatori[0].getNome() + " - " + giocatori[2].getNome() + "!";
            } else if (punteggioCoppia2Totale >= PUNTEGGIO_VITTORIA) {
                partitaFinita = true;
                messaggioVittoria = "Vittoria della coppia " + giocatori[1].getNome() + " - " + giocatori[3].getNome() + "!";
            }
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
                // Il giocatore umano (sempre giocatori[0]) ha vinto
                statisticheGiocatore.aggiungiVittoria();
                view.log("Statistiche aggiornate: VITTORIA per " + nomeGiocatoreUmano);
            } else {
                // Il giocatore umano ha perso
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
            // Reset AI lock quando si mette in pausa
            aiInEsecuzione = false;
        } else {
            // Riprende il gioco
            aiInEsecuzione = false; // Reset AI lock quando si riprende
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
    
    public int getNumeroGiocatori() {
        return numeroGiocatori;
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
    
    /**
     * Reset manuale del flag AI per debugging
     */
    public void resetAILock() {
        aiInEsecuzione = false;
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
