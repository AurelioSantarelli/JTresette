package it.uniroma1.tresette;

import it.uniroma1.tresette.model.Carta;
import it.uniroma1.tresette.model.observer.*;
import it.uniroma1.tresette.view.utils.PaletteColori;
import it.uniroma1.tresette.view.components.UIComponentFactory;
import it.uniroma1.tresette.view.dialogs.DialogManager;
import it.uniroma1.tresette.view.icons.IconFactory;
import it.uniroma1.tresette.controller.GameController;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

/**
 * Classe principale per l'interfaccia grafica del gioco Tresette.
 * Gestisce solo la visualizzazione e l'interazione con l'utente.
 * La logica di gioco è delegata al GameController.
 */
public class TresetteGame extends JFrame implements GameController.GameView {

    // Componenti UI principali
    private final JPanel pannelloGioco = new JPanel();
    private JLabel labelPunteggi;
    private JLabel labelTurno;
    private JLabel labelPunteggioVittoria;
    private final JButton[] bottoniCarte;
    private final JLabel[] labelCarteGiocate;
    private final JLabel[] labelNomiGiocatori;
    private JTextArea areaLog;
    private JButton btnPausaRiprendi;
    
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
        super("JTresette!");
        
        this.PUNTEGGIO_VITTORIA = punteggioVittoria;

        // Inizializza array per componenti UI
        bottoniCarte = new JButton[10];
        labelCarteGiocate = new JLabel[4];
        labelNomiGiocatori = new JLabel[4];

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
        gameController = new GameController(nomeGiocatore, punteggioVittoria, gameObservable, this);

        // Costruisce l'interfaccia grafica
        inizializzaGUI();
        
        // Avvia una nuova partita
        gameController.nuovaPartita();
    }

    /**
     * Inizializza l'interfaccia grafica principale
     */
    private void inizializzaGUI() {
        configuraFinestra();
        creaPannelloSuperiore();
        creaPannelloGioco();
        creaPannelloDestro();
        
        // Finalizza la configurazione della finestra
        setSize(1300, 900);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /**
     * Configura le proprietà base della finestra
     */
    private void configuraFinestra() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(PaletteColori.COLORE_TAVOLO);
        
        // Imposta l'icona dell'applicazione
        ImageIcon appIcon = IconFactory.creaIconaApp();
        setIconImage(appIcon.getImage());
    }

    /**
     * Crea il pannello superiore con controlli e informazioni
     */
    private void creaPannelloSuperiore() {
        JPanel panelSuperiore = new JPanel(new BorderLayout());
        panelSuperiore.setOpaque(false);
        
        // Pannello pulsante pausa (sinistra)
        JPanel panelPausa = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelPausa.setOpaque(false);
        
        btnPausaRiprendi = UIComponentFactory.creaBottonePausa(e -> gameController.togglePausa());
        panelPausa.add(btnPausaRiprendi);
        panelSuperiore.add(panelPausa, BorderLayout.WEST);
        
        // Pannello punteggio vittoria (destra)
        JPanel panelPunteggioVittoria = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelPunteggioVittoria.setOpaque(false);
        
        labelPunteggioVittoria = UIComponentFactory.creaLabelPersonalizzata(
            "Partita fino a " + (int)PUNTEGGIO_VITTORIA + " punti", 
            SwingConstants.RIGHT, 14);
        panelPunteggioVittoria.add(labelPunteggioVittoria);
        panelSuperiore.add(panelPunteggioVittoria, BorderLayout.EAST);
        
        // Pannello centrale punteggi e turno
        JPanel panelPunteggiTurno = new JPanel(new GridLayout(2, 1));
        panelPunteggiTurno.setOpaque(false);
        
        labelPunteggi = UIComponentFactory.creaLabelPersonalizzata(
            "Punteggi: Caricamento...", SwingConstants.CENTER, 14);
        labelTurno = UIComponentFactory.creaLabelPersonalizzata(
            "Turno: Preparazione...", SwingConstants.CENTER, 14);
        
        panelPunteggiTurno.add(labelPunteggi);
        panelPunteggiTurno.add(labelTurno);
        panelSuperiore.add(panelPunteggiTurno, BorderLayout.CENTER);
        
        add(panelSuperiore, BorderLayout.NORTH);
    }

    /**
     * Crea il pannello principale di gioco
     */
    private void creaPannelloGioco() {
        pannelloGioco.setLayout(new BorderLayout());
        pannelloGioco.setOpaque(false);

        // Pannello centrale per le carte giocate
        creaPannelloCentro();
        
        // Pannello inferiore per le carte del giocatore
        creaPannelloCarte();
        
        add(pannelloGioco, BorderLayout.CENTER);
    }

    /**
     * Crea il pannello centrale per visualizzare le carte giocate
     */
    private void creaPannelloCentro() {
        JPanel panelCentro = new JPanel(new GridLayout(2, 2, 8, 8));
        panelCentro.setOpaque(false);
        panelCentro.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), "Carte Giocate",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14), Color.WHITE));

        for (int i = 0; i < 4; i++) {
            JPanel panelGiocatore = creaPannelloPerGiocatore(i);
            panelCentro.add(panelGiocatore);
        }
        
        pannelloGioco.add(panelCentro, BorderLayout.CENTER);
    }

    /**
     * Crea un pannello per visualizzare la carta di un giocatore
     * @param indice indice del giocatore
     * @return pannello configurato
     */
    private JPanel creaPannelloPerGiocatore(int indice) {
        JPanel panelGiocatore = new JPanel(new BorderLayout());
        panelGiocatore.setOpaque(false);
        panelGiocatore.setBorder(BorderFactory.createRaisedBevelBorder());

        // Label con il nome del giocatore
        labelNomiGiocatori[indice] = new JLabel("Giocatore " + (indice + 1), SwingConstants.CENTER);
        labelNomiGiocatori[indice].setForeground(Color.WHITE);
        labelNomiGiocatori[indice].setFont(new Font("Arial", Font.BOLD, 12));
        panelGiocatore.add(labelNomiGiocatori[indice], BorderLayout.NORTH);

        // Label per la carta
        labelCarteGiocate[indice] = new JLabel("", SwingConstants.CENTER);
        labelCarteGiocate[indice].setOpaque(true);
        labelCarteGiocate[indice].setBackground(PaletteColori.COLORE_TAVOLO);
        labelCarteGiocate[indice].setPreferredSize(new Dimension(120, 192));
        panelGiocatore.add(labelCarteGiocate[indice], BorderLayout.CENTER);

        return panelGiocatore;
    }

    /**
     * Crea il pannello per le carte del giocatore umano
     */
    private void creaPannelloCarte() {
        JPanel panelCarte = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelCarte.setOpaque(false);
        panelCarte.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), "Le tue carte",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14), Color.WHITE));

        for (int i = 0; i < 10; i++) {
            final int indice = i;
            bottoniCarte[i] = UIComponentFactory.creaBottoneCarta(i, e -> gameController.giocaCarta(indice));
            aggiungiEffettoHoverCarta(bottoniCarte[i], indice);
            panelCarte.add(bottoniCarte[i]);
        }
        
        pannelloGioco.add(panelCarte, BorderLayout.SOUTH);
    }

    /**
     * Aggiunge l'effetto hover alle carte
     * @param bottone bottone della carta
     * @param indice indice della carta
     */
    private void aggiungiEffettoHoverCarta(JButton bottone, int indice) {
        bottone.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (bottone.isEnabled() && bottone.getIcon() != null) {
                    ImageIcon iconaOriginale = (ImageIcon) bottone.getIcon();
                    ImageIcon iconaSchiarita = IconFactory.creaIconaSchiarita(iconaOriginale);
                    bottone.setIcon(iconaSchiarita);
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (bottone.isVisible() && gameController != null) {
                    ripristinaCartaOriginale(indice);
                }
            }
        });
    }

    /**
     * Ripristina l'immagine originale di una carta
     * @param indice indice della carta
     */
    private void ripristinaCartaOriginale(int indice) {
        it.uniroma1.tresette.model.Giocatore[] giocatori = gameController.getGiocatori();
        if (giocatori != null && giocatori.length > 0) {
            List<Carta> manoGiocatore = giocatori[0].getMano();
            if (indice < manoGiocatore.size()) {
                Carta carta = manoGiocatore.get(indice);
                bottoniCarte[indice].setIcon(carta.getImmagine());
            }
        }
    }

    /**
     * Crea il pannello destro con log e controlli
     */
    private void creaPannelloDestro() {
        JPanel panelDestro = new JPanel(new BorderLayout());
        panelDestro.setOpaque(false);
        
        // Area di log
        JScrollPane scrollLog = creaAreaLog();
        panelDestro.add(scrollLog, BorderLayout.CENTER);
        
        // Pannello bottoni
        JPanel panelBottoni = creaBottoniControllo();
        panelDestro.add(panelBottoni, BorderLayout.SOUTH);
        
        add(panelDestro, BorderLayout.EAST);
    }

    /**
     * Crea l'area di log
     * @return JScrollPane con l'area di log
     */
    private JScrollPane creaAreaLog() {
        areaLog = new JTextArea(15, 40);
        areaLog.setEditable(false);
        areaLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        areaLog.setBackground(PaletteColori.COLORE_TAVOLO);
        areaLog.setForeground(Color.WHITE);
        
        JScrollPane scrollLog = new JScrollPane(areaLog);
        TitledBorder border = BorderFactory.createTitledBorder("Log di gioco");
        border.setTitleColor(Color.WHITE);
        scrollLog.setBorder(border);
        scrollLog.setBackground(PaletteColori.COLORE_TAVOLO);
        
        return scrollLog;
    }

    /**
     * Crea i bottoni di controllo
     * @return pannello con i bottoni
     */
    private JPanel creaBottoniControllo() {
        JPanel panelBottoni = new JPanel(new GridLayout(2, 1, 5, 5));
        panelBottoni.setOpaque(false);
        
        // Bottone Nuova Partita
        JButton btnNuovaPartita = UIComponentFactory.creaBottoneNuovaPartita(e -> {
            if (gameController.isGiocoInCorso()) {
                if (DialogManager.confermaInizioNuovaPartita(this)) {
                    gameController.nuovaPartita();
                }
            } else {
                gameController.nuovaPartita();
            }
        });
        
        // Bottone Interrompi Partita
        JButton btnInterrompiPartita = UIComponentFactory.creaBottoneInterrompiPartita(e -> {
            if (gameController.isGiocoInCorso()) {
                // Prima mette in pausa il gioco per bloccare il flusso
                if (!gameController.isGiocoInPausa()) {
                    gameController.togglePausa();
                }
                // Poi chiede conferma per l'interruzione
                if (DialogManager.confermaInterruzione(this)) {
                    tornareAlMenu();
                }
            } else {
                // Se il gioco non è in corso, torna direttamente al menu
                tornareAlMenu();
            }
        });
        
        panelBottoni.add(btnNuovaPartita);
        panelBottoni.add(btnInterrompiPartita);
        
        return panelBottoni;
    }

    /**
     * Torna al menu iniziale
     */
    private void tornareAlMenu() {
        String nomeGiocatore = gameController.getGiocatori()[0].getNome();
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            MenuIniziale menu = new MenuIniziale();
            menu.setUltimoNomeUsato(nomeGiocatore);
            menu.setVisible(true);
        });
    }

    // =================== IMPLEMENTAZIONE GAMEVIEW INTERFACE ===================
    
    @Override
    public void aggiornaInterfaccia() {
        SwingUtilities.invokeLater(() -> {
            aggiornaNomiGiocatori();
            aggiornaManiGiocatori();
            aggiornaCarteGiocate();
            repaint();
        });
    }
    
    @Override
    public void aggiornaPunteggi(double punteggioCoppia1, double punteggioCoppia2) {
        SwingUtilities.invokeLater(() -> {
            it.uniroma1.tresette.model.Giocatore[] giocatori = gameController.getGiocatori();
            labelPunteggi.setText(String.format("Punteggi Totali: " + giocatori[0].getNome() + " - " + giocatori[2].getNome() +
                    " : %.1f | " + giocatori[1].getNome() + " - " + giocatori[3].getNome() + " : %.1f",
                    punteggioCoppia1, punteggioCoppia2));
            repaint();
        });
    }
    
    @Override
    public void aggiornaTurno(String nomeGiocatore, int indiceGiocatore) {
        SwingUtilities.invokeLater(() -> {
            if (gameController.isGiocoInPausa()) {
                labelTurno.setText("GIOCO IN PAUSA - Premi '▶' per continuare");
                btnPausaRiprendi.setText("▶");
                btnPausaRiprendi.setBackground(new Color(34, 139, 34)); // Verde
            } else {
                labelTurno.setText("Turno: " + nomeGiocatore);
                btnPausaRiprendi.setText("⏸");
                btnPausaRiprendi.setBackground(PaletteColori.BOTTONE_STATS); // Arancione
            }
        });
    }
    
    @Override
    public void mostraVittoria(String messaggioVittoria) {
        SwingUtilities.invokeLater(() -> {
            it.uniroma1.tresette.model.Giocatore[] giocatori = gameController.getGiocatori();
            boolean haVintoGiocatore = messaggioVittoria.contains(giocatori[0].getNome());
            DialogManager.mostraVittoria(this, messaggioVittoria, haVintoGiocatore);
        });
    }
    
    @Override
    public void abilitaBottoniCarte(boolean abilita) {
        SwingUtilities.invokeLater(() -> {
            if (!abilita) {
                for (JButton bottone : bottoniCarte) {
                    if (bottone != null) {
                        bottone.setEnabled(false);
                    }
                }
                return;
            }
            
            // Abilita solo le carte giocabili
            it.uniroma1.tresette.model.Giocatore[] giocatori = gameController.getGiocatori();
            List<Carta> manoGiocatore = giocatori[0].getMano();
            
            for (int i = 0; i < bottoniCarte.length; i++) {
                if (bottoniCarte[i] != null && i < manoGiocatore.size()) {
                    Carta carta = manoGiocatore.get(i);
                    boolean abilitaBottone = gameController.isGiocoInCorso() && 
                                           !gameController.isGiocoInPausa() && 
                                           gameController.getGiocatoreCorrente() == 0 && 
                                           gameController.isCartaGiocabile(carta, 0);
                    bottoniCarte[i].setEnabled(abilitaBottone);
                } else if (bottoniCarte[i] != null) {
                    bottoniCarte[i].setEnabled(false);
                }
            }
        });
    }
    
    @Override
    public void aggiornaCarteGiocate() {
        SwingUtilities.invokeLater(() -> {
            // Mapping delle posizioni: giocatore[i] -> riquadro[mappingPosizioni[i]]
            int[] mappingPosizioni = {1, 3, 0, 2};
            
            for (int giocatore = 0; giocatore < 4; giocatore++) {
                int riquadroUI = mappingPosizioni[giocatore];
                Carta carta = gameController.getCartaPerPosizione(giocatore);
                
                if (carta != null) {
                    labelCarteGiocate[riquadroUI].setIcon(carta.getImmagine());
                    labelCarteGiocate[riquadroUI].setText("");
                } else {
                    labelCarteGiocate[riquadroUI].setIcon(null);
                    labelCarteGiocate[riquadroUI].setText("");
                }
            }
        });
    }
    
    @Override
    public void aggiornaManiGiocatori() {
        SwingUtilities.invokeLater(() -> {
            it.uniroma1.tresette.model.Giocatore[] giocatori = gameController.getGiocatori();
            List<Carta> manoGiocatore = giocatori[0].getMano();
            
            for (int i = 0; i < 10; i++) {
                if (i < manoGiocatore.size()) {
                    Carta carta = manoGiocatore.get(i);
                    bottoniCarte[i].setIcon(carta.getImmagine());
                    bottoniCarte[i].setText("");
                    bottoniCarte[i].setVisible(true);
                } else {
                    bottoniCarte[i].setVisible(false);
                }
            }
        });
    }

    @Override
    public void log(String messaggio) {
        if (areaLog != null) {
            areaLog.append(messaggio + "\n");
            areaLog.setCaretPosition(areaLog.getDocument().getLength());
        }
    }

    /**
     * Aggiorna i nomi dei giocatori nei riquadri
     */
    private void aggiornaNomiGiocatori() {
        if (gameController != null) {
            it.uniroma1.tresette.model.Giocatore[] giocatori = gameController.getGiocatori();
            if (giocatori != null && giocatori.length >= 4) {
                labelNomiGiocatori[0].setText(giocatori[2].getNome()); // Viligelmo
                labelNomiGiocatori[1].setText(giocatori[0].getNome()); // Giocatore umano
                labelNomiGiocatori[2].setText(giocatori[3].getNome()); // Astolfo
                labelNomiGiocatori[3].setText(giocatori[1].getNome()); // Marcovaldo
            }
        }
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
