package it.uniroma1.tresette.view.windows;

import it.uniroma1.tresette.model.observer.*;
import it.uniroma1.tresette.view.icons.IconFactory;
import it.uniroma1.tresette.view.layout.GameLayoutManager;
import it.uniroma1.tresette.view.panels.*;
import it.uniroma1.tresette.controller.GameController;

import javax.swing.*;
import java.awt.*;

/**
 * Classe principale per l'interfaccia grafica del gioco Tresette.
 * Ora completamente modulare - delega la gestione UI ai pannelli specializzati.
 * La logica di gioco è delegata al GameController.
 */
public class TresetteGame extends JFrame implements GameController.GameView {

    // Pannelli modulari specializzati
    private final TopGamePanel topPanel;
    private final PlayedCardsPanel playedCardsPanel;
    private final PlayerCardsPanel playerCardsPanel;
    private final SideControlPanel sidePanel;
    
    // Layout manager
    private final GameLayoutManager layoutManager;
    
    // Controller e sistema Observer
    private final GameController gameController;
    private final GameStateObservable gameObservable;
    private final AudioObserver audioObserver;
    private final LoggingObserver loggingObserver;
    private final DebugObserver debugObserver;
    
    // Configurazione
    private final double PUNTEGGIO_VITTORIA;

    /**
     * Costruttore del gioco con nome giocatore e punteggio predefinito
     * @param nomeGiocatore nome del giocatore umano
     */
    public TresetteGame(String nomeGiocatore) {
        this(nomeGiocatore, 41); // Default a 41 punti
    }

    /**
     * Costruttore completo del gioco
     * @param nomeGiocatore nome del giocatore umano
     * @param punteggioVittoria punteggio necessario per vincere
     */
    public TresetteGame(String nomeGiocatore, int punteggioVittoria) {
        this(nomeGiocatore, punteggioVittoria, false); // Default a 4 giocatori
    }

    /**
     * Costruttore completo del gioco con modalità
     * @param nomeGiocatore nome del giocatore umano
     * @param punteggioVittoria punteggio necessario per vincere
     * @param modalitaDueGiocatori true per modalità 1v1, false per modalità 4 giocatori
     */
    public TresetteGame(String nomeGiocatore, int punteggioVittoria, boolean modalitaDueGiocatori) {
        super("JTresette!");
        
        this.PUNTEGGIO_VITTORIA = punteggioVittoria;

        // Inizializza il sistema Observer
        gameObservable = new GameStateObservable();
        audioObserver = new AudioObserver(true); // Audio abilitato di default
        loggingObserver = new LoggingObserver(this::log);
        debugObserver = new DebugObserver(false); // Debug disabilitato
        
        // Configura il logging
        loggingObserver.setLoggingEnabled(true);
        loggingObserver.setLoggingDettagliato(false);
        
        // Registra gli observer
        gameObservable.addObserver(audioObserver);
        gameObservable.addObserver(loggingObserver);

        // Inizializza il controller
        gameController = new GameController(nomeGiocatore, punteggioVittoria, modalitaDueGiocatori, gameObservable, this);

        // Inizializza layout manager
        layoutManager = new GameLayoutManager(this);
        
        // Inizializza pannelli modulari
        topPanel = new TopGamePanel(gameController, PUNTEGGIO_VITTORIA);
        playedCardsPanel = new PlayedCardsPanel(gameController);
        playerCardsPanel = new PlayerCardsPanel(gameController);
        sidePanel = new SideControlPanel(gameController, this);

        // Costruisce l'interfaccia grafica
        inizializzaGUI();
        
        // Avvia una nuova partita
        gameController.nuovaPartita();
    }

    /**
     * Inizializza l'interfaccia grafica principale usando i pannelli modulari
     */
    private void inizializzaGUI() {
        // Configura layout principale
        layoutManager.setupMainLayout();
        
        // Imposta l'icona dell'applicazione
        ImageIcon appIcon = IconFactory.creaIconaApp();
        setIconImage(appIcon.getImage());
        
        // Assembla i pannelli
        assemblaPannelli();
    }
    
    /**
     * Assembla tutti i pannelli modulari nel layout principale
     */
    private void assemblaPannelli() {
        // Pannello superiore
        layoutManager.addComponent(topPanel, BorderLayout.NORTH);
        
        // Pannello centrale (carte giocate + carte del giocatore)
        JPanel pannelloCentrale = GameLayoutManager.createTransparentPanel(new BorderLayout());
        pannelloCentrale.add(playedCardsPanel, BorderLayout.CENTER);
        pannelloCentrale.add(playerCardsPanel, BorderLayout.SOUTH);
        layoutManager.addComponent(pannelloCentrale, BorderLayout.CENTER);
        
        // Pannello laterale destro
        layoutManager.addComponent(sidePanel, BorderLayout.EAST);
    }

    // =================== IMPLEMENTAZIONE GAMEVIEW INTERFACE ===================
    
    @Override
    public void aggiornaInterfaccia() {
        SwingUtilities.invokeLater(() -> {
            playedCardsPanel.aggiornaNomiGiocatori();
            playerCardsPanel.aggiornaManiGiocatori();
            playedCardsPanel.aggiornaCarteGiocate();
            repaint();
        });
    }
    
    @Override
    public void aggiornaPunteggi(double punteggioCoppia1, double punteggioCoppia2) {
        topPanel.aggiornaPunteggi(punteggioCoppia1, punteggioCoppia2);
    }
    
    @Override
    public void aggiornaTurno(String nomeGiocatore, int indiceGiocatore) {
        topPanel.aggiornaTurno(nomeGiocatore, indiceGiocatore);
    }
    
    @Override
    public void mostraVittoria(String messaggioVittoria) {
        SwingUtilities.invokeLater(() -> {
            it.uniroma1.tresette.model.Giocatore[] giocatori = gameController.getGiocatori();
            boolean haVintoGiocatore = messaggioVittoria.contains(giocatori[0].getNome());
            it.uniroma1.tresette.view.dialogs.DialogManager.mostraVittoria(this, messaggioVittoria, haVintoGiocatore);
        });
    }
    
    @Override
    public void abilitaBottoniCarte(boolean abilita) {
        playerCardsPanel.abilitaBottoniCarte(abilita);
    }
    
    @Override
    public void aggiornaCarteGiocate() {
        playedCardsPanel.aggiornaCarteGiocate();
    }
    
    @Override
    public void aggiornaManiGiocatori() {
        playerCardsPanel.aggiornaManiGiocatori();
    }

    @Override
    public void log(String messaggio) {
        sidePanel.log(messaggio);
    }

    // =================== METODI PUBBLICI PER OBSERVER ===================
    
    public void addGameStateObserver(GameStateObserver observer) {
        gameObservable.addObserver(observer);
    }
    
    public void removeGameStateObserver(GameStateObserver observer) {
        gameObservable.removeObserver(observer);
    }
    
    public it.uniroma1.tresette.model.observer.GameState getCurrentGameState() {
        return gameObservable.getCurrentState();
    }
    
    public void setAudioAbilitato(boolean abilitato) {
        audioObserver.setAudioAbilitato(abilitato);
    }
    
    public boolean isAudioAbilitato() {
        return audioObserver.isAudioAbilitato();
    }
    
    public void setLoggingDettagliato(boolean dettagliato) {
        loggingObserver.setLoggingDettagliato(dettagliato);
    }
    
    public void setLoggingAbilitato(boolean abilitato) {
        loggingObserver.setLoggingEnabled(abilitato);
    }
    
    public void setDebugMode(boolean abilitato) {
        debugObserver.setDebugAbilitato(abilitato);
    }
    
    public boolean isDebugMode() {
        return debugObserver.isDebugAbilitato();
    }
    
    public void resetDebugStats() {
        debugObserver.resetStats();
    }

    // =================== MAIN METHOD ===================
    
    /**
     * Metodo principale - avvia l'applicazione
     * @param args argomenti da riga di comando
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Lancia il menu iniziale
            new MenuIniziale().setVisible(true);
        });
    }
}
