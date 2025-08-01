package it.uniroma1.tresette;

import it.uniroma1.tresette.model.Carta;
import it.uniroma1.tresette.model.Seme;
import it.uniroma1.tresette.model.Giocatore;
import it.uniroma1.tresette.model.StatisticheGiocatore;
import it.uniroma1.tresette.model.observer.*;
import it.uniroma1.tresette.model.observer.GameState; // Import specifico per risolvere l'ambiguità
import it.uniroma1.tresette.view.utils.PaletteColori;
import it.uniroma1.tresette.controller.GameController;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.IOException;
import java.util.*;
import java.util.List;

public class TresetteGame extends JFrame implements GameController.GameView {

    // Classe Giocatore
    static class Giocatore {
        private final String nome;
        private final List<Carta> mano;
        private final List<Carta> cartePrese;
        private final boolean isUmano;

        public Giocatore(String nome, boolean isUmano) {
            this.nome = nome;
            this.isUmano = isUmano;
            this.mano = new ArrayList<>();
            this.cartePrese = new ArrayList<>();
        }

        public void aggiungiCarta(Carta carta) {
            mano.add(carta);
        }

        public void rimuoviCarta(Carta carta) {
            mano.remove(carta);
        }

        public List<Carta> getMano() {
            return mano;
        }

        public List<Carta> getCartePrese() {
            return cartePrese;
        }

        public String getNome() {
            return nome;
        }

        public boolean isUmano() {
            return isUmano;
        }

        public int calcolaPunti() {
            return cartePrese.stream().mapToInt(Carta::getPunti).sum();
        }

        public double calcolaPuntiReali() {
            return cartePrese.stream().mapToInt(Carta::getPunti).sum() / 100.0;
        }

        public Carta scegliCarta(Seme semeRichiesto, List<Carta> carteGiocate) {
            if (mano.isEmpty())
                return null;

            if (semeRichiesto != null) {
                List<Carta> carteSeme = mano.stream()
                        .filter(c -> c.getSeme() == semeRichiesto)
                        .sorted(Comparator.comparingInt(Carta::getForzaPerPresa).reversed())
                        .toList();

                if (!carteSeme.isEmpty()) {
                    int forzaMassima = carteGiocate.stream()
                            .filter(c -> c.getSeme() == semeRichiesto)
                            .mapToInt(Carta::getForzaPerPresa)
                            .max().orElse(0);

                    for (Carta carta : carteSeme) {
                        if (carta.getForzaPerPresa() > forzaMassima) {
                            return carta;
                        }
                    }
                    return carteSeme.get(carteSeme.size() - 1);
                }
            }
            return mano.get(new Random().nextInt(mano.size()));
        }
    }

    private final Giocatore[] giocatori;
    private final List<Carta> mazzo;
    private final JPanel pannelloGioco = new JPanel();
    private JLabel labelPunteggi = new JLabel();
    private JLabel labelTurno = new JLabel();
    private JLabel labelPunteggioVittoria = new JLabel(); // Label per mostrare il punteggio di vittoria
    private final JButton[] bottoniCarte;
    private final JLabel[] labelCarteGiocate;
    private final JLabel[] labelNomiGiocatori; // Array per le label dei nomi
    private JTextArea areaLog = new JTextArea();

    private int giocatoreCorrente;
    private int primoDiMano;
    private final List<Carta> carteGiocate;
    private Seme semeRichiesto;
    private boolean giocoInCorso;
    private boolean giocoInPausa; // Nuova variabile per gestire la pausa
    private JButton btnPausaRiprendi; // Riferimento al pulsante pausa/riprendi
    private int mano;
    private double punteggioCoppia1Totale; // Punteggio totale coppia 1 (giocatore + Viligelmo)
    private double punteggioCoppia2Totale; // Punteggio totale coppia 2 (Marcovaldo + Astolfo)
    private final double PUNTEGGIO_VITTORIA; // Ora è una variabile invece di una costante
    
    // Sistema Observer
    private final GameStateObservable gameObservable;
    private final AudioObserver audioObserver;
    private final LoggingObserver loggingObserver;
    private final DebugObserver debugObserver;
    
    // Controller per la logica del gioco
    private final GameController gameController;

    public TresetteGame(String nomeGiocatore) {
        this(nomeGiocatore, 41); // Default a 41 punti per retrocompatibilità
    }

    public TresetteGame(String nomeGiocatore, int punteggioVittoria) {
        super("JTresette!");
        
        this.PUNTEGGIO_VITTORIA = punteggioVittoria;

        giocatori = new Giocatore[4];
        giocatori[0] = new Giocatore(nomeGiocatore, true); // Usa il nome inserito
        giocatori[1] = new Giocatore("Marcovaldo", false);
        giocatori[2] = new Giocatore("Viligelmo", false);
        giocatori[3] = new Giocatore("Astolfo", false);

        mazzo = new ArrayList<>();
        carteGiocate = new ArrayList<>();
        bottoniCarte = new JButton[10];
        labelCarteGiocate = new JLabel[4];
        labelNomiGiocatori = new JLabel[4]; // Inizializza l'array per i nomi

        // Inizializza il sistema Observer
        gameObservable = new GameStateObservable();
        audioObserver = new AudioObserver(true); // Audio abilitato di default
        loggingObserver = new LoggingObserver(this::log); // Usa il metodo log esistente
        
        // Abilita il logging essenziale ma disabilita quello dettagliato
        loggingObserver.setLoggingEnabled(true); // LOGGING RIABILITATO per eventi essenziali
        loggingObserver.setLoggingDettagliato(false); // Solo eventi importanti, non debug
        
        // Inizializza il debug observer (COMPLETAMENTE DISABILITATO)
        debugObserver = new DebugObserver(false); // Debug DISABILITATO
        
        // Registra audio e logging observer - debug disabilitato
        gameObservable.addObserver(audioObserver);
        gameObservable.addObserver(loggingObserver); // LOGGING RIABILITATO
        // gameObservable.addObserver(debugObserver); // DEBUG COMPLETAMENTE DISABILITATO

        // Inizializza il controller
        gameController = new GameController(nomeGiocatore, punteggioVittoria, gameObservable, this);

        inizializzaGUI();
        gameController.nuovaPartita(); // Usa il controller invece di nuovaPartita()
    }

    private void inizializzaGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(PaletteColori.COLORE_TAVOLO);

        // Imposta l'icona dell'applicazione
        try {
            java.net.URL iconUrl = getClass().getResource("/images/app_icon.png");
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                setIconImage(icon.getImage());
            } else {
                // Crea un'icona di fallback se il file non esiste
                BufferedImage iconImg = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = iconImg.createGraphics();
                g2d.setColor(PaletteColori.COLORE_TAVOLO); // Verde tavolo
                g2d.fillRect(0, 0, 64, 64);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 24));
                g2d.drawString("T", 22, 40);
                g2d.dispose();
                setIconImage(iconImg);
            }
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dell'icona: " + e.getMessage());
        }

        JPanel panelSuperiore = new JPanel(new BorderLayout());
        panelSuperiore.setOpaque(false);
        
        // Pannello per il pulsante pausa in alto a sinistra
        JPanel panelPausa = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelPausa.setOpaque(false);
        
        // Crea il pulsante Pausa/Riprendi
        btnPausaRiprendi = new JButton("⏸") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
                g2d.dispose();
            }
        };
        btnPausaRiprendi.setFont(new Font("Arial", Font.BOLD, 12));
        btnPausaRiprendi.setBackground(PaletteColori.BOTTONE_STATS); // Arancione
        btnPausaRiprendi.setForeground(Color.WHITE);
        btnPausaRiprendi.setOpaque(false);
        btnPausaRiprendi.setBorderPainted(false);
        btnPausaRiprendi.setFocusPainted(false);
        btnPausaRiprendi.setContentAreaFilled(false);
        btnPausaRiprendi.setPreferredSize(new Dimension(70, 35));
        btnPausaRiprendi.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effetti mouse per il pulsante Pausa/Riprendi
        btnPausaRiprendi.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnPausaRiprendi.setBackground(new Color(255, 140, 0)); // Arancione più scuro
                btnPausaRiprendi.setBorder(BorderFactory.createRaisedBevelBorder());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnPausaRiprendi.setBackground(PaletteColori.BOTTONE_STATS); // Arancione originale
                btnPausaRiprendi.setBorder(null);
            }
            
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnPausaRiprendi.setBackground(new Color(255, 140, 0)); // Arancione più scuro
                btnPausaRiprendi.setBorder(BorderFactory.createLoweredBevelBorder());
            }
            
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnPausaRiprendi.setBackground(new Color(255, 140, 0)); // Torna al colore hover
                btnPausaRiprendi.setBorder(BorderFactory.createRaisedBevelBorder());
            }
        });
        
        btnPausaRiprendi.addActionListener(e -> {
            // Non riproduciamo il suono qui per evitare il doppio click
            // Il suono viene gestito dall'AudioObserver tramite notifyPausaToggled()
            gameController.togglePausa();
        });
        
        panelPausa.add(btnPausaRiprendi);
        panelSuperiore.add(panelPausa, BorderLayout.WEST);
        
        // Pannello per il punteggio di vittoria in alto a destra
        JPanel panelPunteggioVittoria = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelPunteggioVittoria.setOpaque(false);
        
        labelPunteggioVittoria = new JLabel("Partita fino a " + (int)PUNTEGGIO_VITTORIA + " punti", SwingConstants.RIGHT);
        labelPunteggioVittoria.setForeground(Color.WHITE);
        labelPunteggioVittoria.setFont(new Font("Arial", Font.BOLD, 14));
        panelPunteggioVittoria.add(labelPunteggioVittoria);
        panelSuperiore.add(panelPunteggioVittoria, BorderLayout.EAST);
        
        // Pannello centrale per i punteggi e turno
        JPanel panelPunteggiTurno = new JPanel(new GridLayout(2, 1));
        panelPunteggiTurno.setOpaque(false);
        
        labelPunteggi = new JLabel("Punteggi: Caricamento...", SwingConstants.CENTER);

        labelPunteggi.setForeground(Color.WHITE);
        labelTurno = new JLabel("Turno: Preparazione...", SwingConstants.CENTER);
        labelTurno.setForeground(Color.WHITE);
        panelPunteggiTurno.add(labelPunteggi);
        panelPunteggiTurno.add(labelTurno);
        panelSuperiore.add(panelPunteggiTurno, BorderLayout.CENTER);
        add(panelSuperiore, BorderLayout.NORTH);

        pannelloGioco.setLayout(new BorderLayout());
        pannelloGioco.setOpaque(false);

        JPanel panelCentro = new JPanel(new GridLayout(2, 2, 8, 8));
        panelCentro.setOpaque(false);
        panelCentro.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), "Carte Giocate",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14), Color.WHITE));

        for (int i = 0; i < 4; i++) {
            // Crea un pannello container per ogni giocatore
            JPanel panelGiocatore = new JPanel(new BorderLayout());
            panelGiocatore.setOpaque(false);
            panelGiocatore.setBorder(BorderFactory.createRaisedBevelBorder());

            // Label con il nome del giocatore
            labelNomiGiocatori[i] = new JLabel("Giocatore " + (i + 1), SwingConstants.CENTER);
            labelNomiGiocatori[i].setForeground(Color.WHITE);
            labelNomiGiocatori[i].setFont(new Font("Arial", Font.BOLD, 12));
            panelGiocatore.add(labelNomiGiocatori[i], BorderLayout.NORTH);

            // Label per la carta (modificata per non avere bordo)
            labelCarteGiocate[i] = new JLabel("", SwingConstants.CENTER);
            labelCarteGiocate[i].setOpaque(true);
            labelCarteGiocate[i].setBackground(PaletteColori.COLORE_TAVOLO);
            labelCarteGiocate[i].setPreferredSize(new Dimension(Carta.LARGHEZZA_CARTA * 2, Carta.ALTEZZA_CARTA * 2));
            panelGiocatore.add(labelCarteGiocate[i], BorderLayout.CENTER);

            panelCentro.add(panelGiocatore);
        }
        pannelloGioco.add(panelCentro, BorderLayout.CENTER);

        JPanel panelCarte = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelCarte.setOpaque(false);
        panelCarte.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), "Le tue carte",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14), Color.WHITE));

        for (int i = 0; i < 10; i++) {
            bottoniCarte[i] = new JButton();
            bottoniCarte[i].setPreferredSize(new Dimension(Carta.LARGHEZZA_CARTA, Carta.ALTEZZA_CARTA));
            bottoniCarte[i].setVisible(false);
            bottoniCarte[i].setBorderPainted(false);
            bottoniCarte[i].setContentAreaFilled(false);
            bottoniCarte[i].setFocusPainted(false);
            bottoniCarte[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            final int indice = i;
            bottoniCarte[i].addActionListener(e -> gameController.giocaCarta(indice));
            
            // Aggiungi effetto hover per schiarire la carta
            bottoniCarte[i].addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (bottoniCarte[indice].isEnabled() && bottoniCarte[indice].getIcon() != null) {
                        // Crea un'immagine più chiara
                        ImageIcon iconaOriginale = (ImageIcon) bottoniCarte[indice].getIcon();
                        ImageIcon iconaSchiarita = TresetteGame.this.creaIconaSchiarita(iconaOriginale);
                        bottoniCarte[indice].setIcon(iconaSchiarita);
                    }
                }
                
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if (bottoniCarte[indice].isVisible() && gameController != null) {
                        // Ripristina l'immagine originale dal controller
                        it.uniroma1.tresette.model.Giocatore[] giocatoriController = gameController.getGiocatori();
                        if (giocatoriController != null && giocatoriController.length > 0) {
                            List<Carta> manoGiocatore = giocatoriController[0].getMano();
                            if (indice < manoGiocatore.size()) {
                                Carta carta = manoGiocatore.get(indice);
                                bottoniCarte[indice].setIcon(carta.getImmagine());
                            }
                        }
                    }
                }
            });
            
            panelCarte.add(bottoniCarte[i]);
        }
        pannelloGioco.add(panelCarte, BorderLayout.SOUTH);
        add(pannelloGioco, BorderLayout.CENTER);

        JPanel panelDestro = new JPanel(new BorderLayout());
        panelDestro.setOpaque(false);
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
        panelDestro.add(scrollLog, BorderLayout.CENTER);
        JButton btnNuovaPartita = new JButton("Nuova Partita") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
                g2d.dispose();
            }
        };
        btnNuovaPartita.setBackground(Color.RED);
        btnNuovaPartita.setForeground(Color.WHITE);
        btnNuovaPartita.setOpaque(false);
        btnNuovaPartita.setBorderPainted(false);
        btnNuovaPartita.setFocusPainted(false);
        btnNuovaPartita.setContentAreaFilled(false);
        btnNuovaPartita.setPreferredSize(new Dimension(160, 45));
        btnNuovaPartita.setFont(new Font("Arial", Font.BOLD, 15));
        btnNuovaPartita.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Aggiungi effetti mouse
        btnNuovaPartita.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnNuovaPartita.setBackground(PaletteColori.ROSSO_BOTTONE_PREMUTO); // Rosso più chiaro
                btnNuovaPartita.setBorder(BorderFactory.createRaisedBevelBorder());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnNuovaPartita.setBackground(PaletteColori.ROSSO_BOTTONE); // Rosso originale
                btnNuovaPartita.setBorder(null);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnNuovaPartita.setBackground(PaletteColori.ROSSO_BOTTONE_PREMUTO); // Rosso più scuro
                btnNuovaPartita.setBorder(BorderFactory.createLoweredBevelBorder());
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnNuovaPartita.setBackground(new Color(255, 100, 100)); // Torna al colore hover
                btnNuovaPartita.setBorder(BorderFactory.createRaisedBevelBorder());
            }
        });

        btnNuovaPartita.addActionListener(e -> {
            // Riproduci il suono del click
            SoundManager.riproduciSuonoClick();
            
            if (giocoInCorso) {
                int risposta = JOptionPane.showConfirmDialog(
                        this,
                        "Sei sicuro di voler iniziare una nuova partita?\nLa partita corrente andrà persa.",
                        "Conferma Nuova Partita",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        creaIconaConferma() // Aggiungi qui la tua icona personalizzata
                );
                if (risposta == JOptionPane.YES_OPTION) {
                    gameController.nuovaPartita();
                }
            } else {
                gameController.nuovaPartita();
            }
        });
        
        // Bottone Interrompi Partita
        JButton btnInterrompiPartita = new JButton("Interrompi Partita") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
                g2d.dispose();
            }
        };
        btnInterrompiPartita.setBackground(new Color(139, 69, 19)); // Marrone
        btnInterrompiPartita.setForeground(Color.WHITE);
        btnInterrompiPartita.setOpaque(false);
        btnInterrompiPartita.setBorderPainted(false);
        btnInterrompiPartita.setFocusPainted(false);
        btnInterrompiPartita.setContentAreaFilled(false);
        btnInterrompiPartita.setPreferredSize(new Dimension(160, 45));
        btnInterrompiPartita.setFont(new Font("Arial", Font.BOLD, 15));
        btnInterrompiPartita.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Aggiungi effetti mouse per il bottone interrompi
        btnInterrompiPartita.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnInterrompiPartita.setBackground(new Color(160, 82, 45)); // Marrone più chiaro
                btnInterrompiPartita.setBorder(BorderFactory.createRaisedBevelBorder());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnInterrompiPartita.setBackground(new Color(139, 69, 19)); // Marrone originale
                btnInterrompiPartita.setBorder(null);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnInterrompiPartita.setBackground(new Color(101, 67, 33)); // Marrone più scuro
                btnInterrompiPartita.setBorder(BorderFactory.createLoweredBevelBorder());
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnInterrompiPartita.setBackground(new Color(160, 82, 45)); // Torna al colore hover
                btnInterrompiPartita.setBorder(BorderFactory.createRaisedBevelBorder());
            }
        });

        btnInterrompiPartita.addActionListener(e -> {
            // Riproduci il suono del click
            SoundManager.riproduciSuonoClick();
            
            int risposta = JOptionPane.showConfirmDialog(
                    this,
                    "Sei sicuro di voler interrompere la partita e tornare al menu principale?",
                    "Conferma Interruzione",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    creaIconaConferma()
            );
            if (risposta == JOptionPane.YES_OPTION) {
                // Blocca il gioco
                giocoInCorso = false;
                
                // Chiudi la finestra del gioco
                this.dispose();
                // Apri il menu iniziale e ripristina il nome del giocatore
                SwingUtilities.invokeLater(() -> {
                    MenuIniziale menu = new MenuIniziale();
                    menu.setUltimoNomeUsato(giocatori[0].getNome());
                    menu.setVisible(true);
                });
            }
        });
        
        // Pannello per i bottoni
        JPanel panelBottoni = new JPanel(new GridLayout(2, 1, 5, 5));
        panelBottoni.setOpaque(false);
        panelBottoni.add(btnNuovaPartita);
        panelBottoni.add(btnInterrompiPartita);
        
        panelDestro.add(panelBottoni, BorderLayout.SOUTH);
        add(panelDestro, BorderLayout.EAST);
        setSize(1300, 900);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void nuovaPartita() {
        // Notifica l'inizio di una nuova partita
        gameObservable.notifyGameStateChanged(GameState.NON_INIZIATO);
        
        // Reset delle mani dei giocatori
        for (Giocatore g : giocatori) {
            g.getMano().clear();
            g.getCartePrese().clear();
        }
        carteGiocate.clear();

        giocatoreCorrente = 0;
        primoDiMano = 0;
        semeRichiesto = null;
        giocoInCorso = true;
        giocoInPausa = false; // Reset della pausa
        mano = 1;
        
        // Reset dei punteggi totali solo se si inizia una nuova serie
        punteggioCoppia1Totale = 0.0;
        punteggioCoppia2Totale = 0.0;

        // Reset del pulsante pausa
        if (btnPausaRiprendi != null) {
            btnPausaRiprendi.setText("⏸");
            btnPausaRiprendi.setBackground(PaletteColori.BOTTONE_STATS); // Arancione
        }

        for (JLabel label : labelCarteGiocate) {
            label.setIcon(null);
            label.setText("");
            label.setBackground(PaletteColori.COLORE_TAVOLO);
        }

        areaLog.setText("=== NUOVA SERIE DI PARTITE ===\n");
        areaLog.append("Si gioca fino a " + (int)PUNTEGGIO_VITTORIA + " punti!\n\n");

        // Notifica che il gioco è in corso
        gameObservable.notifyGameStateChanged(GameState.IN_CORSO);
        
        iniziaNuovaMano();
    }

    private void iniziaNuovaMano() {
        // Notifica preparazione nuova mano
        gameObservable.notifyGameStateChanged(GameState.NUOVA_MANO);
        
        // Reset delle mani dei giocatori per la nuova mano
        for (Giocatore g : giocatori) {
            g.getMano().clear();
            g.getCartePrese().clear();
        }
        
        // Cambia chi inizia la partita (rotazione) - solo se non è la prima mano della serie
        if (punteggioCoppia1Totale > 0 || punteggioCoppia2Totale > 0) {
            primoDiMano = (primoDiMano + 1) % 4;
        }
        
        // Reset delle variabili di gioco
        giocatoreCorrente = primoDiMano;
        semeRichiesto = null;
        mano = 1;
        carteGiocate.clear();
        
        // Pulisci l'area delle carte giocate
        for (JLabel label : labelCarteGiocate) {
            label.setIcon(null);
            label.setBackground(PaletteColori.COLORE_TAVOLO);
        }
        
        creaMazzo();
        Collections.shuffle(mazzo);
        
        // Notifica distribuzione carte
        gameObservable.notifyGameStateChanged(GameState.DISTRIBUZIONE_CARTE);
        
        distribuisciCarte();
        aggiornaGUI();

        log("=== NUOVA MANO ===");
        log("Carte distribuite. Inizia " + giocatori[primoDiMano].getNome());

        // Notifica il cambio di turno
        gameObservable.notifyTurnoCambiato(giocatori[giocatoreCorrente].getNome(), giocatoreCorrente);
        
        // Notifica lo stato appropriato
        if (giocatori[giocatoreCorrente].isUmano()) {
            gameObservable.notifyGameStateChanged(GameState.TURNO_UMANO);
        } else {
            gameObservable.notifyGameStateChanged(GameState.TURNO_AI);
            javax.swing.Timer timer = new javax.swing.Timer(1000, e -> turnoAI());
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void creaMazzo() {
        mazzo.clear();
        for (Seme seme : Seme.values()) {
            for (int valore = 1; valore <= 10; valore++) {
                mazzo.add(new Carta(valore, seme));
            }
        }
    }

    private void distribuisciCarte() {
        int indice = 0;
        for (int carta = 0; carta < 10; carta++) {
            for (int giocatore = 0; giocatore < 4; giocatore++) {
                giocatori[giocatore].aggiungiCarta(mazzo.get(indice++));
            }
        }

        giocatori[0].getMano().sort((a, b) -> {
            if (a.getSeme() != b.getSeme()) {
                return a.getSeme().compareTo(b.getSeme());
            }
            return Integer.compare(a.getValore(), b.getValore());
        });
    }

    private void aggiornaGUI() {
        // Mostra i punteggi totali accumulati
        labelPunteggi.setText(String.format("Punteggi Totali: " + giocatori[0].getNome() + " - " + giocatori[2].getNome() +
                " : %.2f | " + giocatori[1].getNome() + " - " + giocatori[3].getNome() + " : %.2f (fino a %.0f)", 
                punteggioCoppia1Totale, punteggioCoppia2Totale, PUNTEGGIO_VITTORIA));
        labelTurno.setText("Turno: " + giocatori[giocatoreCorrente].getNome());

        // Notifica aggiornamento punteggi
        gameObservable.notifyPunteggiAggiornati(punteggioCoppia1Totale, punteggioCoppia2Totale);

        List<Carta> manoGiocatore = giocatori[0].getMano();
        for (int i = 0; i < 10; i++) {
            if (i < manoGiocatore.size()) {
                Carta carta = manoGiocatore.get(i);
                bottoniCarte[i].setIcon(carta.getImmagine());
                bottoniCarte[i].setText("");
                bottoniCarte[i].setVisible(true);
                bottoniCarte[i].setEnabled(giocoInCorso && !giocoInPausa && giocatoreCorrente == 0 && cartaGiocabile(carta));
            } else {
                bottoniCarte[i].setVisible(false);
            }
        }
    }

    private boolean cartaGiocabile(Carta carta) {
        if (semeRichiesto == null)
            return true;

        boolean haSeme = giocatori[0].getMano().stream().anyMatch(c -> c.getSeme() == semeRichiesto);

        return !haSeme || carta.getSeme() == semeRichiesto;
    }

    private void giocaCarta(int indice) {
        // METODO LEGACY DISABILITATO - Ora usa solo GameController
        // Il GameController gestisce tutta la logica di gioco
        // I bottoni chiamano direttamente gameController.giocaCarta(indice)
        return;
        
        /*
        if (!giocoInCorso || giocoInPausa || giocatoreCorrente != 0 || indice >= giocatori[0].getMano().size())
            return;

        Carta carta = giocatori[0].getMano().get(indice);
        if (!cartaGiocabile(carta)) {
            JOptionPane.showMessageDialog(this,
                    "Devi rispondere al seme " + semeRichiesto.getSimbolo() + " se ce l'hai!");
            return;
        }
        giocaCartaEffettiva(carta);
        */
    }

    private void giocaCartaEffettiva(Carta carta) {
        // METODO LEGACY DISABILITATO - Ora usa solo GameController
        // Questo metodo modificava le posizioni dei giocatori causando il problema
        return;
    }

    private void turnoAI() {
        // METODO LEGACY DISABILITATO - Ora usa solo GameController
        return;
        /* CODICE LEGACY COMMENTATO
        if (carteGiocate.size() == 4 || !giocoInCorso || giocoInPausa)
            return;

        Carta carta = giocatori[giocatoreCorrente].scegliCarta(semeRichiesto, carteGiocate);
        if (carta != null) {
            giocaCartaEffettiva(carta);
        }
        */
    }

    private void fineMano() {
        // METODO LEGACY COMPLETAMENTE DISABILITATO - Ora usa solo GameController
        return;
    }

    private int determinaVincitore() {
        int vincitore = 0;
        int forzaMassima = -1;

        for (int i = 0; i < 4; i++) {
            Carta carta = carteGiocate.get(i);
            int giocatoreIdx = (primoDiMano + i) % 4;

            if (carta.getSeme() == semeRichiesto) {
                if (carta.getForzaPerPresa() > forzaMassima) {
                    forzaMassima = carta.getForzaPerPresa();
                    vincitore = giocatoreIdx;
                }
            }
        }
        return vincitore;
    }

    private void finePartita() {
        // Calcola i punti della mano appena conclusa
        double punteggioCoppia1Mano = giocatori[0].calcolaPuntiReali() + giocatori[2].calcolaPuntiReali();
        double punteggioCoppia2Mano = giocatori[1].calcolaPuntiReali() + giocatori[3].calcolaPuntiReali();
        
        // Aggiungi i punti ai totali
        punteggioCoppia1Totale += punteggioCoppia1Mano;
        punteggioCoppia2Totale += punteggioCoppia2Mano;
        
        log("=== FINE MANO ===");
        log(String.format("Punti mano - Tu + " + giocatori[2].getNome() + " : %.2f punti", punteggioCoppia1Mano));
        log(String.format("Punti mano - " + giocatori[1].getNome() + " + " + giocatori[3].getNome()+" : %.2f punti", punteggioCoppia2Mano));
        log(String.format("TOTALI - Tu + " + giocatori[2].getNome() + " : %.2f punti", punteggioCoppia1Totale));
        log(String.format("TOTALI - " + giocatori[1].getNome() + " + " + giocatori[3].getNome()+" : %.2f punti", punteggioCoppia2Totale));
        
        // Controlla se qualcuno ha vinto (raggiunto 41 punti)
        if (punteggioCoppia1Totale >= PUNTEGGIO_VITTORIA || punteggioCoppia2Totale >= PUNTEGGIO_VITTORIA) {
            vittoriaFinale();
        } else {
            // Continua con una nuova mano
            log("\nNessuna coppia ha ancora raggiunto " + (int)PUNTEGGIO_VITTORIA + " punti. Si continua!");
            
            // Riproduci suono per continuazione partita
            SoundManager.riproduciSuonoContinuaPartita();
            
            // Aggiorna GUI per mostrare i nuovi punteggi totali
            aggiornaGUI();
            
            // Pausa prima di iniziare la nuova mano
            javax.swing.Timer timer = new javax.swing.Timer(3000, e -> {
                iniziaNuovaMano();
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    private void vittoriaFinale() {
        giocoInCorso = false;
        
        // Notifica che il gioco è terminato
        gameObservable.notifyGameStateChanged(GameState.TERMINATO);
        
        String messaggio;
        ImageIcon iconaPersonalizzata = null;
        boolean giocatoreHaVinto = false;
        
        if (punteggioCoppia1Totale >= PUNTEGGIO_VITTORIA && punteggioCoppia2Totale >= PUNTEGGIO_VITTORIA) {
            // Caso raro: entrambe le coppie superano 41 nella stessa mano, vince chi ha di più
            if (punteggioCoppia1Totale > punteggioCoppia2Totale) {
                messaggio = String.format("Hai vinto! Tu e " + giocatori[2].getNome() + " avete totalizzato %.2f punti!", punteggioCoppia1Totale);
                iconaPersonalizzata = creaIconaVittoria();
                SoundManager.riproduciSuonoVittoria();
                giocatoreHaVinto = true;
            } else if (punteggioCoppia2Totale > punteggioCoppia1Totale) {
                messaggio = String.format("Hai perso! " + giocatori[1].getNome() + " e " + giocatori[3].getNome()+" hanno totalizzato %.2f punti!", punteggioCoppia2Totale);
                iconaPersonalizzata = creaIconaSconfitta();
                SoundManager.riproduciSuonoSconfitta();
                giocatoreHaVinto = false;
            } else {
                messaggio = String.format("Pareggio incredibile! Entrambe le coppie hanno totalizzato %.2f punti!", punteggioCoppia1Totale);
                iconaPersonalizzata = creaIconaPareggio();
                // In caso di pareggio, non aggiorniamo le statistiche
                giocatoreHaVinto = false; // Trattato come sconfitta per semplicità
            }
        } else if (punteggioCoppia1Totale >= PUNTEGGIO_VITTORIA) {
            messaggio = String.format("Hai vinto! Tu e " + giocatori[2].getNome() + " avete raggiunto %.2f punti!", punteggioCoppia1Totale);
            iconaPersonalizzata = creaIconaVittoria();
            SoundManager.riproduciSuonoVittoria();
            giocatoreHaVinto = true;
        } else {
            messaggio = String.format("Hai perso! " + giocatori[1].getNome() + " e " + giocatori[3].getNome()+" hanno raggiunto %.2f punti!", punteggioCoppia2Totale);
            iconaPersonalizzata = creaIconaSconfitta();
            SoundManager.riproduciSuonoSconfitta();
            giocatoreHaVinto = false;
        }
        
        // Aggiorna le statistiche
        StatisticheGiocatore stats = new StatisticheGiocatore(giocatori[0].getNome());
        if (giocatoreHaVinto) {
            stats.aggiungiVittoria();
        } else {
            stats.aggiungiSconfitta();
        }
        
        log("=== VITTORIA FINALE ===");
        log(messaggio);
        
        JOptionPane.showMessageDialog(
            this, 
            messaggio, 
            "Vittoria!", 
            JOptionPane.PLAIN_MESSAGE, 
            iconaPersonalizzata
        );
        
        aggiornaGUI();
    }

    private ImageIcon creaIconaVittoria() {
        try {
            java.net.URL iconUrl = getClass().getResource("/images/vittoria.png");
            if (iconUrl != null) {
                BufferedImage img = ImageIO.read(iconUrl);
                Image scaledImg = img.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            }
        } catch (IOException e) {
            System.err.println("Errore nel caricamento dell'icona vittoria: " + e.getMessage());
        }

        // Icona di fallback per vittoria
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Sfondo verde
        g2d.setColor(PaletteColori.COLORE_TAVOLO);
        g2d.fillOval(5, 5, 54, 54);

        g2d.dispose();
        return new ImageIcon(img);
    }

    private ImageIcon creaIconaSconfitta() {
        try {
            java.net.URL iconUrl = getClass().getResource("/images/sconfitta.png");
            if (iconUrl != null) {
                BufferedImage img = ImageIO.read(iconUrl);
                Image scaledImg = img.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            }
        } catch (IOException e) {
            System.err.println("Errore nel caricamento dell'icona sconfitta: " + e.getMessage());
        }

        // Icona di fallback per sconfitta
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Sfondo rosso
        g2d.setColor(PaletteColori.ROSSO_BOTTONE);
        g2d.fillOval(5, 5, 54, 54);
        g2d.dispose();
        return new ImageIcon(img);
    }

    private ImageIcon creaIconaPareggio() {
        try {
            java.net.URL iconUrl = getClass().getResource("/images/pareggio.png");
            if (iconUrl != null) {
                BufferedImage img = ImageIO.read(iconUrl);
                Image scaledImg = img.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            }
        } catch (IOException e) {
            System.err.println("Errore nel caricamento dell'icona pareggio: " + e.getMessage());
        }

        // Icona di fallback per pareggio
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Sfondo giallo
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(5, 5, 54, 54);

        g2d.dispose();
        return new ImageIcon(img);
    }

    private ImageIcon creaIconaConferma() {
        try {
            java.net.URL iconUrl = getClass().getResource("/images/conferma.png");
            if (iconUrl != null) {
                BufferedImage img = ImageIO.read(iconUrl);
                Image scaledImg = img.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            }
        } catch (IOException e) {
            System.err.println("Errore nel caricamento dell'icona conferma: " + e.getMessage());
        }

        // Icona di fallback per conferma
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Sfondo verde
        g2d.setColor(new Color(34, 139, 34));
        g2d.fillOval(5, 5, 54, 54);

        // Punto esclamativo
        g2d.setColor(new Color(255, 215, 0));
        g2d.fillOval(20, 15, 24, 24);
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        g2d.setColor(Color.WHITE);
        g2d.drawString("⁉️", 20, 40);

        g2d.dispose();
        return new ImageIcon(img);
    }

    public void log(String messaggio) {
        areaLog.append(messaggio + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }
    
    private void togglePausa() {
        if (!giocoInCorso) {
            // Se il gioco non è in corso, non permettere la pausa
            JOptionPane.showMessageDialog(
                this,
                "Non puoi mettere in pausa un gioco che non è in corso!",
                "Impossibile mettere in pausa",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        giocoInPausa = !giocoInPausa;
        
        // Notifica il toggle della pausa
        gameObservable.notifyPausaToggled(giocoInPausa);
        
        if (giocoInPausa) {
            // Gioco in pausa
            gameObservable.notifyGameStateChanged(GameState.IN_PAUSA);
            btnPausaRiprendi.setText("▶");
            btnPausaRiprendi.setBackground(new Color(34, 139, 34)); // Verde
            labelTurno.setText("GIOCO IN PAUSA - Premi '▶' per continuare");
            
            // Disabilita tutti i bottoni delle carte
            for (JButton bottone : bottoniCarte) {
                bottone.setEnabled(false);
            }
            
            log("=== GIOCO IN PAUSA ===");
        } else {
            // Riprendi il gioco
            btnPausaRiprendi.setText("⏸");
            btnPausaRiprendi.setBackground(new Color(255, 165, 0)); // Arancione
            labelTurno.setText("Turno: " + giocatori[giocatoreCorrente].getNome());
            
            log("=== GIOCO RIPRESO ===");
            
            // Determina lo stato appropriato
            if (giocatori[giocatoreCorrente].isUmano()) {
                gameObservable.notifyGameStateChanged(GameState.TURNO_UMANO);
            } else {
                gameObservable.notifyGameStateChanged(GameState.TURNO_AI);
            }
            
            // Aggiorna la GUI per ripristinare lo stato corretto
            aggiornaGUI();
            
            // Se non è il turno dell'utente, riprendi il turno AI
            if (!giocatori[giocatoreCorrente].isUmano() && carteGiocate.size() < 4) {
                javax.swing.Timer timer = new javax.swing.Timer(1000, e -> turnoAI());
                timer.setRepeats(false);
                timer.start();
            }
        }
    }
    
    private ImageIcon creaIconaSchiarita(ImageIcon iconaOriginale) {
        if (iconaOriginale == null) return null;
        
        BufferedImage imgOriginale = new BufferedImage(
            iconaOriginale.getIconWidth(),
            iconaOriginale.getIconHeight(),
            BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g2d = imgOriginale.createGraphics();
        g2d.drawImage(iconaOriginale.getImage(), 0, 0, null);
        g2d.dispose();
        
        // Crea un'immagine schiarita
        BufferedImage imgSchiarita = new BufferedImage(
            imgOriginale.getWidth(),
            imgOriginale.getHeight(),
            BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g2dSchiarita = imgSchiarita.createGraphics();
        g2dSchiarita.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Disegna l'immagine originale
        g2dSchiarita.drawImage(imgOriginale, 0, 0, null);
        
        // Applica un overlay bianco semi-trasparente per schiarire
        g2dSchiarita.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g2dSchiarita.setColor(Color.WHITE);
        g2dSchiarita.fillRect(0, 0, imgSchiarita.getWidth(), imgSchiarita.getHeight());
        
        g2dSchiarita.dispose();
        
        return new ImageIcon(imgSchiarita);
    }
    
    // Metodi pubblici per gestire gli observer
    
    /**
     * Aggiunge un observer personalizzato al sistema
     * @param observer l'observer da aggiungere
     */
    public void addGameStateObserver(GameStateObserver observer) {
        gameObservable.addObserver(observer);
    }
    
    /**
     * Rimuove un observer dal sistema
     * @param observer l'observer da rimuovere
     */
    public void removeGameStateObserver(GameStateObserver observer) {
        gameObservable.removeObserver(observer);
    }
    
    /**
     * Restituisce lo stato corrente del gioco
     * @return lo stato corrente
     */
    public GameState getCurrentGameState() {
        return gameObservable.getCurrentState();
    }
    
    /**
     * Abilita o disabilita l'audio
     * @param abilitato true per abilitare, false per disabilitare
     */
    public void setAudioAbilitato(boolean abilitato) {
        audioObserver.setAudioAbilitato(abilitato);
    }
    
    /**
     * Verifica se l'audio è abilitato
     * @return true se l'audio è abilitato
     */
    public boolean isAudioAbilitato() {
        return audioObserver.isAudioAbilitato();
    }
    
    /**
     * Abilita o disabilita il logging dettagliato
     * @param dettagliato true per logging dettagliato
     */
    public void setLoggingDettagliato(boolean dettagliato) {
        loggingObserver.setLoggingDettagliato(dettagliato);
    }
    
    /**
     * Abilita o disabilita completamente il logging
     * @param abilitato true per abilitare il logging
     */
    public void setLoggingAbilitato(boolean abilitato) {
        loggingObserver.setLoggingEnabled(abilitato);
    }
    
    /**
     * Abilita o disabilita il debug mode
     * @param abilitato true per abilitare il debug
     */
    public void setDebugMode(boolean abilitato) {
        debugObserver.setDebugAbilitato(abilitato);
    }
    
    /**
     * Verifica se il debug mode è abilitato
     * @return true se il debug è abilitato
     */
    public boolean isDebugMode() {
        return debugObserver.isDebugAbilitato();
    }
    
    /**
     * Reset delle statistiche di debug
     */
    public void resetDebugStats() {
        debugObserver.resetStats();
    }

    // =================== IMPLEMENTAZIONE INTERFACE GAMEVIEW ===================
    
    @Override
    public void aggiornaInterfaccia() {
        SwingUtilities.invokeLater(() -> {
            aggiornaNomiGiocatori();
            aggiornaManiGiocatori();
            aggiornaCarteGiocate();
            repaint();
        });
    }
    
    /**
     * Aggiorna i nomi dei giocatori nei riquadri
     */
    private void aggiornaNomiGiocatori() {
        if (gameController != null) {
            it.uniroma1.tresette.model.Giocatore[] giocatoriController = gameController.getGiocatori();
            if (giocatoriController != null && giocatoriController.length >= 4) {
                // Mapping coerente con aggiornaCarteGiocate():
                // giocatore[0] (Umano) -> riquadro[1], giocatore[1] (Marcovaldo) -> riquadro[3]
                // giocatore[2] (Viligelmo) -> riquadro[0], giocatore[3] (Astolfo) -> riquadro[2]
                
                labelNomiGiocatori[0].setText(giocatoriController[2].getNome()); // Riquadro 0: Viligelmo
                labelNomiGiocatori[1].setText(giocatoriController[0].getNome()); // Riquadro 1: Giocatore umano
                labelNomiGiocatori[2].setText(giocatoriController[3].getNome()); // Riquadro 2: Astolfo
                labelNomiGiocatori[3].setText(giocatoriController[1].getNome()); // Riquadro 3: Marcovaldo
            }
        }
    }
    
    @Override
    public void aggiornaPunteggi(double punteggioCoppia1, double punteggioCoppia2) {
        SwingUtilities.invokeLater(() -> {
            this.punteggioCoppia1Totale = punteggioCoppia1;
            this.punteggioCoppia2Totale = punteggioCoppia2;
            
            // Usa i nomi dal controller
            it.uniroma1.tresette.model.Giocatore[] giocatoriController = gameController.getGiocatori();
            labelPunteggi.setText(String.format("Punteggi Totali: " + giocatoriController[0].getNome() + " - " + giocatoriController[2].getNome() +
                    " : %.1f | " + giocatoriController[1].getNome() + " - " + giocatoriController[3].getNome() + " : %.1f",
                    punteggioCoppia1, punteggioCoppia2));
            
            repaint();
        });
    }
    
    @Override
    public void aggiornaTurno(String nomeGiocatore, int indiceGiocatore) {
        SwingUtilities.invokeLater(() -> {
            if (gameController.isGiocoInPausa()) {
                labelTurno.setText("GIOCO IN PAUSA - Premi '▶' per continuare");
            } else {
                labelTurno.setText("Turno: " + nomeGiocatore);
            }
        });
    }
    
    @Override
    public void mostraVittoria(String messaggioVittoria) {
        SwingUtilities.invokeLater(() -> {
            if (messaggioVittoria.contains(giocatori[0].getNome())) {
                // Il giocatore umano ha vinto
                JOptionPane.showMessageDialog(this, messaggioVittoria, "Vittoria!", JOptionPane.INFORMATION_MESSAGE,
                        new ImageIcon(getClass().getResource("/images/vittoria.png")));
            } else {
                // Il giocatore umano ha perso
                JOptionPane.showMessageDialog(this, messaggioVittoria, "Sconfitta!", JOptionPane.INFORMATION_MESSAGE,
                        new ImageIcon(getClass().getResource("/images/sconfitta.png")));
            }
        });
    }
    
    @Override
    public void abilitaBottoniCarte(boolean abilita) {
        SwingUtilities.invokeLater(() -> {
            // Se stiamo disabilitando, disabilita tutto
            if (!abilita) {
                for (JButton bottone : bottoniCarte) {
                    if (bottone != null) {
                        bottone.setEnabled(false);
                    }
                }
                return;
            }
            
            // Altrimenti abilita solo le carte giocabili secondo le regole
            it.uniroma1.tresette.model.Giocatore[] giocatori = gameController.getGiocatori();
            List<Carta> manoGiocatore = giocatori[0].getMano();
            
            for (int i = 0; i < bottoniCarte.length; i++) {
                if (bottoniCarte[i] != null && i < manoGiocatore.size()) {
                    Carta carta = manoGiocatore.get(i);
                    // Abilita solo se è il turno del giocatore umano E la carta è giocabile
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
            // Mapping fisso delle posizioni:
            // Posizione fisica 0 (alto-sx) -> riquadro UI 0 -> Giocatore 1 (AI avversario)
            // Posizione fisica 1 (alto-dx) -> riquadro UI 1 -> Giocatore 3 (AI avversario)  
            // Posizione fisica 2 (basso-sx) -> riquadro UI 2 -> Giocatore 0 (Umano)
            // Posizione fisica 3 (basso-dx) -> riquadro UI 3 -> Giocatore 2 (AI compagno)
            
            // Mapping delle posizioni dei giocatori ai riquadri UI
            int[] mappingPosizioni = {1, 3, 0, 2}; // giocatore[i] -> riquadro[mappingPosizioni[i]]
            
            for (int giocatore = 0; giocatore < 4; giocatore++) {
                int riquadroUI = mappingPosizioni[giocatore];
                Carta carta = gameController.getCartaPerPosizione(giocatore);
                
                if (carta != null) {
                    labelCarteGiocate[riquadroUI].setIcon(carta.getImmagine());
                    labelCarteGiocate[riquadroUI].setText("");
                } else {
                    labelCarteGiocate[riquadroUI].setIcon(null);
                    labelCarteGiocate[riquadroUI].setText(""); // Rimosso il testo "Carta X"
                }
            }
        });
    }
    
    @Override
    public void aggiornaManiGiocatori() {
        SwingUtilities.invokeLater(() -> {
            it.uniroma1.tresette.model.Giocatore[] giocatori = gameController.getGiocatori();
            
            // Aggiorna le carte del giocatore umano
            List<Carta> manoGiocatore = giocatori[0].getMano();
            for (int i = 0; i < 10; i++) {
                if (i < manoGiocatore.size()) {
                    Carta carta = manoGiocatore.get(i);
                    bottoniCarte[i].setIcon(carta.getImmagine());
                    bottoniCarte[i].setText("");
                    bottoniCarte[i].setVisible(true);
                    
                    // Abilita solo le carte giocabili secondo le regole del seme
                    boolean abilitaBottone = gameController.isGiocoInCorso() && 
                                           !gameController.isGiocoInPausa() && 
                                           gameController.getGiocatoreCorrente() == 0 && 
                                           gameController.isCartaGiocabile(carta, 0);
                    bottoniCarte[i].setEnabled(abilitaBottone);
                } else {
                    bottoniCarte[i].setVisible(false);
                }
            }
            
            // Aggiorna il numero di carte degli altri giocatori
            // (questo sarà gestito nel repaint con le informazioni dal controller)
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Lancia il menu iniziale invece del gioco direttamente
            new MenuIniziale().setVisible(true);
        });
    }

    // Classe per gestire i suoni
    public static class SoundManager {
        private static Clip clipCartaGiocata;
        private static Clip clipVittoria;
        private static Clip clipSconfitta;
        private static Clip clipClick; // Nuovo clip per il click
        private static Clip clipCarteMischiate; // Nuovo clip per le carte mischiate
        private static Clip clipContinua; // Nuovo clip per continua partita
        private static Clip clipMano; // Nuovo clip per fine mano

        static {
            caricaSuoni();
        }

        private static void caricaSuoni() {
            clipCartaGiocata = caricaSuono("/sounds/carta_giocata.wav");
            clipVittoria = caricaSuono("/sounds/vittoria.wav");
            clipSconfitta = caricaSuono("/sounds/sconfitta.wav");
            clipClick = caricaSuono("/sounds/click.wav"); // Carica il suono del click
            clipCarteMischiate = caricaSuono("/sounds/carte_mischiate.wav"); // Carica il suono delle carte mischiate
            clipContinua = caricaSuono("/sounds/continua.wav"); // Carica il suono continua partita
            clipMano = caricaSuono("/sounds/mano.wav"); // Carica il suono fine mano
        }

        private static Clip caricaSuono(String percorso) {
            try {
                java.net.URL soundUrl = TresetteGame.class.getResource(percorso);
                if (soundUrl != null) {
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundUrl);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    return clip;
                } else {
                    System.err.println("File audio non trovato: " + percorso);
                }
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.err.println("Errore nel caricamento del suono " + percorso + ": " + e.getMessage());
            }
            return null;
        }

        public static void riproduciSuonoCarta() {
            riproduciSuono(clipCartaGiocata);
        }

        public static void riproduciSuonoVittoria() {
            riproduciSuono(clipVittoria);
        }

        public static void riproduciSuonoSconfitta() {
            riproduciSuono(clipSconfitta);
        }

        public static void riproduciSuonoClick() {
            riproduciSuono(clipClick);
        }

        public static void riproduciSuonoCarteMischiate() {
            riproduciSuono(clipCarteMischiate);
        }

        public static void riproduciSuonoContinuaPartita() {
            riproduciSuono(clipContinua);
        }

        public static void riproduciSuonoFineMano() {
            riproduciSuono(clipMano);
        }

        private static void riproduciSuono(Clip clip) {
            if (clip != null) {
                // Riavvolgi il suono per poterlo riprodurre più volte
                clip.setFramePosition(0);
                clip.start();
            }
        }

        // Metodo per creare un suono di click sintetico se non hai file audio
        public static void riproduciClickSintetico() {
            try {
                // Crea un suono di click breve e acuto
                int sampleRate = 44100;
                int duration = 100; // millisecondi - più breve per il click
                byte[] buffer = new byte[sampleRate * duration / 1000];

                for (int i = 0; i < buffer.length; i++) {
                    double angle = 2.0 * Math.PI * i * 1200 / sampleRate; // 1200 Hz - più acuto
                    buffer[i] = (byte) (Math.sin(angle) * 80 * Math.exp(-5.0 * i / buffer.length)); // Con fade out
                }

                AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();
                line.write(buffer, 0, buffer.length);
                line.drain();
                line.close();
            } catch (LineUnavailableException e) {
                System.err.println("Errore nella riproduzione del click sintetico: " + e.getMessage());
            }
        }

        // Metodo per creare un suono sintetico se non hai file audio
        public static void riproduciSuonoSintetico() {
            try {
                // Crea un suono sintetico breve
                int sampleRate = 44100;
                int duration = 200; // millisecondi
                byte[] buffer = new byte[sampleRate * duration / 1000];

                for (int i = 0; i < buffer.length; i++) {
                    double angle = 2.0 * Math.PI * i * 800 / sampleRate; // 800 Hz
                    buffer[i] = (byte) (Math.sin(angle) * 127);
                }

                AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();
                line.write(buffer, 0, buffer.length);
                line.drain();
                line.close();
            } catch (LineUnavailableException e) {
                System.err.println("Errore nella riproduzione del suono sintetico: " + e.getMessage());
            }
        }
    }
}