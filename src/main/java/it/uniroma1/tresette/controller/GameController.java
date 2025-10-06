package it.uniroma1.tresette.controller;

import it.uniroma1.tresette.model.Carta;
import it.uniroma1.tresette.model.Seme;
import it.uniroma1.tresette.model.Giocatore;
import it.uniroma1.tresette.model.observer.*;

import java.util.*;

/**
 * Controller principale del gioco di Tresette.
 * Coordina le varie componenti del gioco mantenendo la responsabilità di orchestrazione.
 */
public class GameController {
    
    // Componenti del gioco
    private final GameStateManager gameState;
    private final DeckManager deckManager;
    private final ScoreCalculator scoreCalculator;
    private final TurnManager turnManager;
    private final AIPlayer aiPlayer;
    
    // Riferimenti necessari
    private final Giocatore[] giocatori;
    private final GameStateObservable gameObservable;
    private final GameView view;
    
    /**
     * Costruttore del controller del gioco (modalità 4 giocatori)
     * 
     * @param nomeGiocatore nome del giocatore umano
     * @param punteggioVittoria punteggio necessario per vincere la partita
     * @param gameObservable observable per notificare eventi di gioco
     * @param view interfaccia per aggiornare la vista
     */
    public GameController(String nomeGiocatore, int punteggioVittoria, 
                         GameStateObservable gameObservable, GameView view) {
        this(nomeGiocatore, punteggioVittoria, false, gameObservable, view);
    }

    /**
     * Costruttore completo del controller del gioco
     * 
     * @param nomeGiocatore nome del giocatore umano
     * @param punteggioVittoria punteggio necessario per vincere la partita
     * @param modalitaDueGiocatori true per modalità 2 giocatori, false per 4 giocatori
     * @param gameObservable observable per notificare eventi di gioco
     * @param view interfaccia per aggiornare la vista
     */
    public GameController(String nomeGiocatore, int punteggioVittoria, boolean modalitaDueGiocatori,
                         GameStateObservable gameObservable, GameView view) {
        this.gameObservable = gameObservable;
        this.view = view;
        
        // Inizializza i componenti
        this.gameState = new GameStateManager(modalitaDueGiocatori, punteggioVittoria);
        this.deckManager = new DeckManager(modalitaDueGiocatori);
        this.scoreCalculator = new ScoreCalculator(modalitaDueGiocatori);
        this.turnManager = new TurnManager(gameObservable, view);
        this.aiPlayer = new AIPlayer();
        
        // Inizializza i giocatori
        this.giocatori = inizializzaGiocatori(nomeGiocatore, modalitaDueGiocatori);
    }
    
    /**
     * Inizializza l'array dei giocatori in base alla modalità
     */
    private Giocatore[] inizializzaGiocatori(String nomeGiocatore, boolean modalitaDueGiocatori) {
        if (modalitaDueGiocatori) {
            return new Giocatore[] {
                new Giocatore(nomeGiocatore, true),
                new Giocatore("Marcovaldo", false)
            };
        } else {
            return new Giocatore[] {
                new Giocatore(nomeGiocatore, true),
                new Giocatore("Marcovaldo", false),
                new Giocatore("Viligelmo", false),
                new Giocatore("Astolfo", false)
            };
        }
    }

    /**
     * Inizia una nuova partita
     */
    public void nuovaPartita() {
        gameState.reset();
        
        // Pulisce le carte dei giocatori
        for (Giocatore giocatore : giocatori) {
            giocatore.azzeraCarte();
        }
        
        deckManager.pulisciCarteGiocate();
        
        gameObservable.notifyGameStateChanged(GameState.NON_INIZIATO);
        view.log("=== NUOVA PARTITA ===");
        view.log("Punteggio per vincere: " + (int)gameState.getPunteggioVittoria() + " punti");
        
        // Log dei giocatori
        if (gameState.isModalitaDueGiocatori()) {
            view.log("Modalità: 2 giocatori");
            view.log("Giocatore 1: " + giocatori[0].getNome() + " (Umano)");
            view.log("Giocatore 2: " + giocatori[1].getNome() + " (AI)");
        } else {
            view.log("Modalità: 4 giocatori (coppie)");
            view.log("Coppia 1: " + giocatori[0].getNome() + " (Umano) - " + giocatori[2].getNome() + " (AI)");
            view.log("Coppia 2: " + giocatori[1].getNome() + " (AI) - " + giocatori[3].getNome() + " (AI)");
        }
        
        iniziaNuovaMano();
    }

    /**
     * Inizia una nuova mano di gioco
     */
    private void iniziaNuovaMano() {
        gameState.setMano(gameState.getMano() + 1);
        view.log("\n=== MANO " + gameState.getMano() + " ===");
        
        // Distribuzione delle carte
        distribuisciCarte();
        
        gameState.setGiocoInCorso(true);
        turnManager.iniziaTurno(giocatori, gameState);
    }

    /**
     * Distribuisce le carte ai giocatori
     */
    private void distribuisciCarte() {
        gameObservable.notifyGameStateChanged(GameState.DISTRIBUZIONE_CARTE);
        
        deckManager.creaMazzo();
        deckManager.mescolaMazzo();
        deckManager.distribuisciCarte(giocatori);
        
        view.aggiornaManiGiocatori();
        view.aggiornaInterfaccia();
    }

    /**
     * Gestisce il gioco di una carta da parte del giocatore umano
     * 
     * @param indiceCarta indice della carta da giocare nella mano del giocatore
     * @return true se la carta è stata giocata con successo, false altrimenti
     */
    public boolean giocaCarta(int indiceCarta) {
        if (!gameState.isGiocoInCorso() || gameState.isGiocoInPausa() || 
            gameState.isValutazioneInCorso() || gameState.isAiInEsecuzione()) {
            return false;
        }
        
        Giocatore giocatoreCorrente = turnManager.getGiocatoreCorrente(giocatori, gameState);
        
        if (!giocatoreCorrente.isUmano() || indiceCarta < 0 || 
            indiceCarta >= giocatoreCorrente.getMano().size()) {
            return false;
        }
        
        Carta carta = giocatoreCorrente.getMano().get(indiceCarta);
        
        // Controlla se la carta è giocabile secondo le regole
        if (!deckManager.isCartaGiocabile(carta, giocatoreCorrente, gameState.getSemeRichiesto())) {
            view.log("Non puoi giocare questa carta! Devi seguire il seme se possibile.");
            return false;
        }
        
        return eseguiGiocataCarta(carta);
    }
    
    /**
     * Esegue effettivamente il gioco di una carta
     */
    private boolean eseguiGiocataCarta(Carta carta) {
        if (gameState.isValutazioneInCorso()) {
            return false;
        }
        
        Giocatore giocatoreCorrente = turnManager.getGiocatoreCorrente(giocatori, gameState);
        
        // Rimuove la carta dalla mano del giocatore
        giocatoreCorrente.rimuoviCarta(carta);
        
        // Se è la prima carta della mano, imposta il seme richiesto
        if (deckManager.getNumeroCarteGiocate() == 0) {
            gameState.setSemeRichiesto(carta.getSeme());
        }
        
        // Aggiunge la carta alle carte giocate
        deckManager.aggiungiCartaGiocata(carta, gameState.getGiocatoreCorrente());
        gameState.incrementaCarteGiocateInMano();
        
        // Notifica l'evento
        gameObservable.notifyCartaGiocata(carta, giocatoreCorrente.getNome());
        
        // Aggiorna immediatamente la vista
        view.aggiornaCarteGiocate();
        view.aggiornaManiGiocatori();
        
        // Controlla se la mano è finita
        if (deckManager.getNumeroCarteGiocate() == gameState.getNumeroGiocatori()) {
            gameState.setValutazioneInCorso(true);
            view.aggiornaCarteGiocate();
            gameObservable.notifyGameStateChanged(GameState.VALUTAZIONE_MANO);
            valutaMano();
        } else {
            // Passa al giocatore successivo
            turnManager.gestisciCambioTurno(giocatori, gameState);
            
            // Se è il turno dell'AI, esegui automaticamente
            if (!turnManager.isGiocatoreCorrenteUmano(giocatori, gameState)) {
                eseguiTurnoAI();
            }
        }
        
        return true;
    }
    
    /**
     * Esegue il gioco di una carta senza chiamare ricorsivamente eseguiTurnoAI()
     * Usato internamente per evitare loop nell'AI
     */
    private boolean eseguiGiocataCartaInterna(Carta carta) {
        if (gameState.isValutazioneInCorso()) {
            return false;
        }
        
        Giocatore giocatoreCorrente = turnManager.getGiocatoreCorrente(giocatori, gameState);
        
        // Rimuove la carta dalla mano del giocatore
        giocatoreCorrente.rimuoviCarta(carta);
        
        // Se è la prima carta della mano, imposta il seme richiesto
        if (deckManager.getNumeroCarteGiocate() == 0) {
            gameState.setSemeRichiesto(carta.getSeme());
        }
        
        // Aggiunge la carta alle carte giocate
        deckManager.aggiungiCartaGiocata(carta, gameState.getGiocatoreCorrente());
        gameState.incrementaCarteGiocateInMano();
        
        // Notifica l'evento
        gameObservable.notifyCartaGiocata(carta, giocatoreCorrente.getNome());
        
        // Aggiorna immediatamente la vista
        view.aggiornaCarteGiocate();
        view.aggiornaManiGiocatori();
        
        // Controlla se la mano è finita
        if (deckManager.getNumeroCarteGiocate() == gameState.getNumeroGiocatori()) {
            gameState.setValutazioneInCorso(true);
            view.aggiornaCarteGiocate();
            gameObservable.notifyGameStateChanged(GameState.VALUTAZIONE_MANO);
            valutaMano();
        } else {
            // Passa al giocatore successivo
            turnManager.gestisciCambioTurno(giocatori, gameState);
            
            // Se è il turno dell'AI, esegui automaticamente ma con un timer per evitare ricorsione immediata
            if (!turnManager.isGiocatoreCorrenteUmano(giocatori, gameState)) {
                // Programma il prossimo turno AI con un piccolo delay
                javax.swing.Timer nextAITimer = new javax.swing.Timer(500, e -> {
                    if (gameState.isGiocoInCorso() && !gameState.isValutazioneInCorso() && 
                        !gameState.isAiInEsecuzione() && !gameState.isGiocoInPausa()) {
                        eseguiTurnoAI();
                    }
                });
                nextAITimer.setRepeats(false);
                nextAITimer.start();
            }
        }
        
        return true;
    }
    
    /**
     * Gestisce il turno dell'AI
     */
    private void eseguiTurnoAI() {
        // Verifica che l'AI non sia già in esecuzione o che il gioco sia in pausa
        if (gameState.isAiInEsecuzione() || gameState.isGiocoInPausa()) {
            return;
        }
        
        gameState.setAiInEsecuzione(true);
        
        // Usa un Timer invece di Thread.sleep per non bloccare l'UI
        javax.swing.Timer aiTimer = new javax.swing.Timer(1500, e -> {
            try {
                // Controlla di nuovo se il gioco è in pausa prima di eseguire
                if (!gameState.isGiocoInCorso() || gameState.isValutazioneInCorso() || gameState.isGiocoInPausa()) {
                    return;
                }
                
                Giocatore aiGiocatore = turnManager.getGiocatoreCorrente(giocatori, gameState);
                
                // Strategia AI che rispetta le regole del seme
                Carta cartaScelta = aiPlayer.scegliCarta(aiGiocatore.getMano(), gameState.getSemeRichiesto());
                
                if (cartaScelta != null) {
                    // Chiama il metodo interno per evitare ricorsione
                    eseguiGiocataCartaInterna(cartaScelta);
                } else {
                    view.log("ERRORE: AI non riesce a scegliere una carta valida!");
                    // Prova a scegliere la prima carta disponibile come fallback
                    if (!aiGiocatore.getMano().isEmpty()) {
                        eseguiGiocataCartaInterna(aiGiocatore.getMano().get(0));
                    }
                }
            } catch (Exception ex) {
                view.log("ERRORE nell'esecuzione AI: " + ex.getMessage());
                ex.printStackTrace();
            } finally {
                // Libera il lock AI sempre, anche in caso di errore
                gameState.setAiInEsecuzione(false);
            }
        });
        
        aiTimer.setRepeats(false);
        aiTimer.start();
    }

    /**
     * Valuta la mano appena giocata e determina il vincitore
     */
    private void valutaMano() {
        if (deckManager.getNumeroCarteGiocate() != gameState.getNumeroGiocatori()) {
            return;
        }
        
        gameState.incrementaGiocata();
        
        // Determina il vincitore della mano
        int vincitore = scoreCalculator.determinaVincitoreMano(
            deckManager.getCarteGiocate(), 
            gameState.getPrimoGiocatoreMano(), 
            gameState.getSemeRichiesto()
        );
        
        // Calcola i punti della mano
        double puntiMano = scoreCalculator.calcolaPuntiMano(deckManager.getCarteGiocate());
        
        // Assegna le carte al vincitore
        for (Carta carta : deckManager.getCarteGiocate()) {
            giocatori[vincitore].getCartePrese().add(carta);
        }
        
        // Controlla se è l'ultima giocata
        boolean isUltimaGiocata = scoreCalculator.isUltimaGiocata(gameState.getGiocata());
        if (isUltimaGiocata) {
            // Assegna punti bonus per l'ultima presa
            if (gameState.isModalitaDueGiocatori()) {
                if (vincitore == 0) {
                    gameState.setPuntiBonus1(gameState.getPuntiBonus1() + 1);
                } else {
                    gameState.setPuntiBonus2(gameState.getPuntiBonus2() + 1);
                }
            } else {
                if (vincitore == 0 || vincitore == 2) {
                    gameState.setPuntiBonus1(gameState.getPuntiBonus1() + 1);
                } else {
                    gameState.setPuntiBonus2(gameState.getPuntiBonus2() + 1);
                }
            }
        }
        
        gameObservable.notifyFineMano(giocatori[vincitore].getNome(), puntiMano);
        
        // Aggiorna i punteggi
        scoreCalculator.aggiornaPunteggi(giocatori, gameState);
        
        // Aggiorna immediatamente i punteggi nella view
        view.aggiornaPunteggi(gameState.getPunteggioCoppia1Totale(), gameState.getPunteggioCoppia2Totale());
        
        // Prepara la nuova mano
        turnManager.preparaNuovaMano(vincitore, gameState);
        
        // Azzera immediatamente il seme richiesto
        gameState.setSemeRichiesto(null);
        
        // Aspetta 2 secondi prima di continuare
        javax.swing.Timer timer = new javax.swing.Timer(2000, e -> {
            gameState.setValutazioneInCorso(false);
            
            // Pulisci le carte dal tavolo
            deckManager.pulisciCarteGiocate();
            view.aggiornaCarteGiocate(); // Importante: aggiorna la vista per nascondere le carte
            
            // Controlla se ci sono ancora carte da giocare
            if (giocatori[0].getMano().isEmpty()) {
                controllaFinePartita();
            } else {
                gameState.setGiocatoreCorrente(gameState.getPrimoGiocatoreMano());
                turnManager.iniziaTurno(giocatori, gameState);
                
                if (!turnManager.isGiocatoreCorrenteUmano(giocatori, gameState)) {
                    eseguiTurnoAI();
                }
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Controlla se la partita è finita
     */
    private void controllaFinePartita() {
        if (gameState.isVittoriaRaggiunta()) {
            gameState.setGiocoInCorso(false);
            String vincitore = gameState.getVincitorePartita();
            
            // Aggiorna le statistiche (se necessario)
            if (gameState.isModalitaDueGiocatori()) {
                // Le statistiche potrebbero essere gestite in futuro
            }
            
            view.mostraVittoria("Partita terminata! Vincitore: " + vincitore);
        } else {
            // Continua con una nuova mano
            iniziaNuovaMano();
        }
    }

    /**
     * Gestisce la pausa/ripresa del gioco
     */
    public void togglePausa() {
        if (gameState.isGiocoInCorso()) {
            gameState.setGiocoInPausa(!gameState.isGiocoInPausa());
            
            // Notifica il cambio di stato per i suoni
            gameObservable.notifyPausaToggled(gameState.isGiocoInPausa());
            
            if (gameState.isGiocoInPausa()) {
                gameObservable.notifyGameStateChanged(GameState.IN_PAUSA);
                // Disabilita i bottoni delle carte quando in pausa
                view.abilitaBottoniCarte(false);
                view.log("Gioco in pausa");
            } else {
                gameObservable.notifyGameStateChanged(GameState.IN_CORSO);
                // Riprende il turno senza cambiare il giocatore corrente
                turnManager.riprendiTurnoDaPausa(giocatori, gameState);
                
                // Riabilita i bottoni delle carte solo se è il turno del giocatore umano
                if (turnManager.isGiocatoreCorrenteUmano(giocatori, gameState)) {
                    view.abilitaBottoniCarte(true);
                } else {
                    // Se è il turno dell'AI, fallo ripartire
                    eseguiTurnoAI();
                }
                view.log("Gioco ripreso");
            }
        }
    }
    
    // Getters per la view
    
    /**
     * Restituisce l'array dei giocatori della partita
     * 
     * @return array contenente tutti i giocatori
     */
    public Giocatore[] getGiocatori() {
        return giocatori;
    }
    
    /**
     * Restituisce il numero di giocatori nella partita
     * 
     * @return numero di giocatori (2 o 4)
     */
    public int getNumeroGiocatori() {
        return gameState.getNumeroGiocatori();
    }
    
    /**
     * Restituisce la lista delle carte giocate nella mano corrente
     * 
     * @return lista delle carte giocate
     */
    public List<Carta> getCarteGiocate() {
        return deckManager.getCarteGiocate();
    }
    
    /**
     * Restituisce la carta giocata in una specifica posizione
     * 
     * @param posizione posizione della carta (0-3)
     * @return carta nella posizione specificata
     */
    public Carta getCartaPerPosizione(int posizione) {
        return deckManager.getCartaPerPosizione(posizione);
    }
    
    /**
     * Indica se il gioco è attualmente in corso
     * 
     * @return true se il gioco è in corso, false altrimenti
     */
    public boolean isGiocoInCorso() {
        return gameState.isGiocoInCorso();
    }
    
    /**
     * Indica se il gioco è attualmente in pausa
     * 
     * @return true se il gioco è in pausa, false altrimenti
     */
    public boolean isGiocoInPausa() {
        return gameState.isGiocoInPausa();
    }
    
    /**
     * Resetta il flag che indica se l'AI è in esecuzione
     */
    public void resetAILock() {
        gameState.setAiInEsecuzione(false);
    }
    
    /**
     * Restituisce l'indice del giocatore che deve giocare
     * 
     * @return indice del giocatore corrente (0-3)
     */
    public int getGiocatoreCorrente() {
        return gameState.getGiocatoreCorrente();
    }
    
    /**
     * Restituisce il seme richiesto per la mano corrente
     * 
     * @return seme richiesto, null se non c'è un seme richiesto
     */
    public Seme getSemeRichiesto() {
        return gameState.getSemeRichiesto();
    }
    
    /**
     * Controlla se una carta può essere giocata secondo le regole del seme
     * @param carta la carta da controllare
     * @param indiceGiocatore l'indice del giocatore che vuole giocare la carta
     * @return true se la carta è giocabile
     */
    public boolean isCartaGiocabile(Carta carta, int indiceGiocatore) {
        if (indiceGiocatore < 0 || indiceGiocatore >= giocatori.length) {
            return false;
        }
        return deckManager.isCartaGiocabile(carta, giocatori[indiceGiocatore], gameState.getSemeRichiesto());
    }
}
