package it.uniroma1.tresette;

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

public class TresetteGame extends JFrame {

    // Enum per i semi
    enum Seme {
        COPPE("[coppe]", Color.RED), DENARI("[denari]", Color.RED),
        SPADE("[spade]", Color.BLACK), BASTONI("[bastoni]", Color.BLACK);

        private final String simbolo;
        private final Color colore;

        Seme(String simbolo, Color colore) {
            this.simbolo = simbolo;
            this.colore = colore;
        }

        public String getSimbolo() {
            return simbolo;
        }

        public Color getColore() {
            return colore;
        }
    }

    // Classe Carta
    static class Carta {
        // Nuove dimensioni per le carte
        public static final int LARGHEZZA_CARTA = 105;
        public static final int ALTEZZA_CARTA = 142;

        private final int valore; // 1-10
        private final Seme seme;
        private final String nome;
        private final int punti;
        private ImageIcon immagineCarta;
        private static final ImageIcon CARTA_RETRO = caricaImmagineRetro();

        public Carta(int valore, Seme seme) {
            this.valore = valore;
            this.seme = seme;

            switch (valore) {
                case 1:
                    nome = "A";
                    break;
                case 2:
                    nome = "2";
                    break;
                case 3:
                    nome = "3";
                    break;
                case 4:
                    nome = "4";
                    break;
                case 5:
                    nome = "5";
                    break;
                case 6:
                    nome = "6";
                    break;
                case 7:
                    nome = "7";
                    break;
                case 8:
                    nome = "F";
                    break;
                case 9:
                    nome = "C";
                    break;
                case 10:
                    nome = "R";
                    break;
                default:
                    nome = String.valueOf(valore);
            }

            if (valore == 1) {
                punti = 100;
            } else if (valore == 2 || valore == 3 || valore == 8 || valore == 9 || valore == 10) {
                punti = 33;
            } else {
                punti = 0;
            }
            caricaImmagine();
        }

        private void caricaImmagine() {
            try {
                String nomeRisorsa = String.format("/images/%s_%d.png", seme.name().toLowerCase(), valore);
                java.net.URL imageUrl = getClass().getResource(nomeRisorsa);

                if (imageUrl != null) {
                    BufferedImage originalImg = ImageIO.read(imageUrl);

                    // Ridimensiona l'immagine
                    Image scaledImg = originalImg.getScaledInstance(LARGHEZZA_CARTA, ALTEZZA_CARTA, Image.SCALE_SMOOTH);

                    // Crea una nuova immagine con angoli smussati
                    BufferedImage roundedImg = new BufferedImage(LARGHEZZA_CARTA, ALTEZZA_CARTA,
                            BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = roundedImg.createGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Crea la forma con angoli smussati
                    int raggio = 15;
                    RoundRectangle2D roundRect = new RoundRectangle2D.Float(0, 0, LARGHEZZA_CARTA, ALTEZZA_CARTA,
                            raggio, raggio);

                    // Applica il clipping per tagliare l'immagine con angoli smussati
                    g2d.setClip(roundRect);
                    g2d.drawImage(scaledImg, 0, 0, null);
                    g2d.dispose();

                    immagineCarta = new ImageIcon(roundedImg);
                } else {
                    System.err.println("Immagine della carta non trovata nel classpath: " + nomeRisorsa);
                    immagineCarta = creaImmagineFallback();
                }
            } catch (IOException e) {
                System.err.println("Errore I/O durante il caricamento dell'immagine: " + e.getMessage());
                immagineCarta = creaImmagineFallback();
            }
        }

        private static ImageIcon caricaImmagineRetro() {
            try {
                java.net.URL imageUrl = TresetteGame.class.getResource("/images/retro.png");
                if (imageUrl != null) {
                    BufferedImage img = ImageIO.read(imageUrl);
                    // Ridimensiona con le nuove dimensioni
                    Image scaledImg = img.getScaledInstance(LARGHEZZA_CARTA, ALTEZZA_CARTA, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaledImg);
                } else {
                    System.err.println("Immagine del retro della carta non trovata nel classpath: /images/retro.png");
                }
            } catch (IOException e) {
                System.err.println("Errore I/O durante il caricamento dell'immagine del retro: " + e.getMessage());
            }

            BufferedImage img = new BufferedImage(LARGHEZZA_CARTA, ALTEZZA_CARTA, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = img.createGraphics();
            g2d.setColor(new Color(0, 0, 139));
            g2d.fillRect(0, 0, LARGHEZZA_CARTA, ALTEZZA_CARTA);
            g2d.setColor(Color.WHITE);
            g2d.drawRect(0, 0, LARGHEZZA_CARTA - 1, ALTEZZA_CARTA - 1);
            for (int i = 5; i < LARGHEZZA_CARTA - 5; i += 10) {
                for (int j = 5; j < ALTEZZA_CARTA - 5; j += 10) {
                    g2d.fillOval(i, j, 3, 3);
                }
            }
            g2d.dispose();
            return new ImageIcon(img);
        }

        private ImageIcon creaImmagineFallback() {
            BufferedImage img = new BufferedImage(LARGHEZZA_CARTA, ALTEZZA_CARTA, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = img.createGraphics();

            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, LARGHEZZA_CARTA, ALTEZZA_CARTA);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(0, 0, LARGHEZZA_CARTA - 1, ALTEZZA_CARTA - 1);

            g2d.setColor(seme.getColore());
            g2d.setFont(new Font("Arial", Font.BOLD, 40));
            FontMetrics fm = g2d.getFontMetrics();
            String simbolo = seme.getSimbolo();
            int xSimbolo = (LARGHEZZA_CARTA - fm.stringWidth(simbolo)) / 2;
            g2d.drawString(simbolo, xSimbolo, 50);

            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 28));
            fm = g2d.getFontMetrics();
            int xNome = (LARGHEZZA_CARTA - fm.stringWidth(nome)) / 2;
            g2d.drawString(nome, xNome, 95);

            g2d.dispose();
            return new ImageIcon(img);
        }

        public int getValore() {
            return valore;
        }

        public Seme getSeme() {
            return seme;
        }

        public String getNome() {
            return nome;
        }

        public int getPunti() {
            return punti;
        }

        public ImageIcon getImmagine() {
            return immagineCarta;
        }

        public static ImageIcon getCartaRetro() {
            return CARTA_RETRO;
        }

        public int getForzaPerPresa() {
            switch (valore) {
                case 3:
                    return 10;
                case 2:
                    return 9;
                case 1:
                    return 8;
                case 10:
                    return 7;
                case 9:
                    return 6;
                case 8:
                    return 5;
                case 7:
                    return 4;
                case 6:
                    return 3;
                case 5:
                    return 2;
                case 4:
                    return 1;
                default:
                    return 0;
            }
        }

        @Override
        public String toString() {
            return nome + seme.getSimbolo();
        }
    }

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
    private final JButton[] bottoniCarte;
    private final JLabel[] labelCarteGiocate;
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

        // Inizializza il sistema Observer
        gameObservable = new GameStateObservable();
        audioObserver = new AudioObserver(true); // Audio abilitato di default
        loggingObserver = new LoggingObserver(this::log); // Usa il metodo log esistente
        
        // Disabilita il logging dettagliato per ridurre i messaggi nel log
        loggingObserver.setLoggingDettagliato(false);
        
        // Inizializza il debug observer (COMPLETAMENTE DISABILITATO)
        debugObserver = new DebugObserver(false); // Debug DISABILITATO
        
        // Registra gli observer (debug observer NON registrato)
        gameObservable.addObserver(audioObserver);
        gameObservable.addObserver(loggingObserver);
        // gameObservable.addObserver(debugObserver); // DEBUG COMPLETAMENTE DISABILITATO

        inizializzaGUI();
        nuovaPartita();
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
            SoundManager.riproduciSuonoClick();
            togglePausa();
        });
        
        panelPausa.add(btnPausaRiprendi);
        panelSuperiore.add(panelPausa, BorderLayout.WEST);
        
        // Pannello centrale per i punteggi e turno
        JPanel panelPunteggiTurno = new JPanel(new GridLayout(2, 1));
        panelPunteggiTurno.setOpaque(false);
        
        labelPunteggi = new JLabel("Punteggi:" + giocatori[0].getNome() + " - " + giocatori[2].getNome() +
                " : 0 | " + giocatori[1].getNome() + " - " + giocatori[3].getNome() + " : 0", SwingConstants.CENTER);

        labelPunteggi.setForeground(Color.WHITE);
        labelTurno = new JLabel("Turno: Tu", SwingConstants.CENTER);
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
            JLabel labelNome = new JLabel(giocatori[i].getNome(), SwingConstants.CENTER);
            labelNome.setForeground(Color.WHITE);
            labelNome.setFont(new Font("Arial", Font.BOLD, 12));
            panelGiocatore.add(labelNome, BorderLayout.NORTH);

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
            bottoniCarte[i].addActionListener(e -> giocaCarta(indice));
            
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
                    if (bottoniCarte[indice].isVisible() && indice < giocatori[0].getMano().size()) {
                        // Ripristina l'immagine originale
                        Carta carta = giocatori[0].getMano().get(indice);
                        bottoniCarte[indice].setIcon(carta.getImmagine());
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
            // Oppure usa il click sintetico se non hai file audio:
            // SoundManager.riproduciClickSintetico();

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
                    nuovaPartita();
                }
            } else {
                nuovaPartita();
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
        if (!giocoInCorso || giocoInPausa || giocatoreCorrente != 0 || indice >= giocatori[0].getMano().size())
            return;

        Carta carta = giocatori[0].getMano().get(indice);
        if (!cartaGiocabile(carta)) {
            JOptionPane.showMessageDialog(this,
                    "Devi rispondere al seme " + semeRichiesto.getSimbolo() + " se ce l'hai!");
            return;
        }
        giocaCartaEffettiva(carta);
    }

    private void giocaCartaEffettiva(Carta carta) {
        giocatori[giocatoreCorrente].rimuoviCarta(carta);
        carteGiocate.add(carta);
        
        if (carteGiocate.size() == 1) {
            semeRichiesto = carta.getSeme();
        }
        
        labelCarteGiocate[giocatoreCorrente].setIcon(carta.getImmagine());
        
        // Notifica che una carta è stata giocata
        gameObservable.notifyCartaGiocata(carta, giocatori[giocatoreCorrente].getNome());
        
        giocatoreCorrente = (giocatoreCorrente + 1) % 4;
        
        if (carteGiocate.size() == 4) {
            gameObservable.notifyGameStateChanged(GameState.VALUTAZIONE_MANO);
            javax.swing.Timer timer = new javax.swing.Timer(1500, e -> fineMano());
            timer.setRepeats(false);
            timer.start();
        } else {
            // Notifica cambio turno
            gameObservable.notifyTurnoCambiato(giocatori[giocatoreCorrente].getNome(), giocatoreCorrente);
            
            aggiornaGUI();
            if (!giocatori[giocatoreCorrente].isUmano()) {
                gameObservable.notifyGameStateChanged(GameState.TURNO_AI);
                javax.swing.Timer timer = new javax.swing.Timer(1000, e -> turnoAI());
                timer.setRepeats(false);
                timer.start();
            } else {
                gameObservable.notifyGameStateChanged(GameState.TURNO_UMANO);
            }
        }
    }

    private void turnoAI() {
        if (carteGiocate.size() == 4 || !giocoInCorso || giocoInPausa)
            return;

        Carta carta = giocatori[giocatoreCorrente].scegliCarta(semeRichiesto, carteGiocate);
        if (carta != null) {
            giocaCartaEffettiva(carta);
        }
    }

    private void fineMano() {
        int vincitore = determinaVincitore();
        giocatori[vincitore].getCartePrese().addAll(carteGiocate);

        double puntiRealiMano = carteGiocate.stream().mapToInt(Carta::getPunti).sum() / 100.0;
        
        // Controlla se è l'ultima mano (mano 10) e assegna il punto dell'ultima
        if (giocatori[0].getMano().isEmpty()) {
            // È l'ultima mano, assegna il punto dell'ultima alla squadra vincitrice
            if (vincitore == 0 || vincitore == 2) {
                // Squadra 1 (giocatore umano + Viligelmo) vince l'ultima mano
                // Aggiungi una carta con 100 punti (1 punto) alle carte prese del vincitore
                Carta puntoUltima = new Carta(1, Seme.COPPE); // Carta fittizia per rappresentare il punto
                giocatori[vincitore].getCartePrese().add(puntoUltima);
                log("*** " + giocatori[vincitore].getNome() + " vince l'ULTIMA MANO e guadagna 1 punto extra! ***");
                puntiRealiMano += 1.0; // Aggiungi il punto dell'ultima
            } else {
                // Squadra 2 (Marcovaldo + Astolfo) vince l'ultima mano
                Carta puntoUltima = new Carta(1, Seme.COPPE); // Carta fittizia per rappresentare il punto
                giocatori[vincitore].getCartePrese().add(puntoUltima);
                log("*** " + giocatori[vincitore].getNome() + " vince l'ULTIMA MANO e guadagna 1 punto extra! ***");
                puntiRealiMano += 1.0; // Aggiungi il punto dell'ultima
            }
        }

        // Notifica la fine della mano
        gameObservable.notifyFineMano(giocatori[vincitore].getNome(), puntiRealiMano);

        carteGiocate.clear();
        semeRichiesto = null;
        giocatoreCorrente = vincitore;
        primoDiMano = vincitore;

        for (JLabel label : labelCarteGiocate) {
            label.setIcon(null);
            label.setBackground(PaletteColori.COLORE_TAVOLO);
        }

        mano++;

        if (giocatori[0].getMano().isEmpty()) {
            finePartita();
        } else {
            aggiornaGUI();
            log("--- Mano " + mano + " - Inizia " + giocatori[giocatoreCorrente].getNome() + " ---");
            
            // Notifica cambio turno
            gameObservable.notifyTurnoCambiato(giocatori[giocatoreCorrente].getNome(), giocatoreCorrente);
            
            if (!giocatori[giocatoreCorrente].isUmano()) {
                gameObservable.notifyGameStateChanged(GameState.TURNO_AI);
                javax.swing.Timer timer = new javax.swing.Timer(1000, e -> turnoAI());
                timer.setRepeats(false);
                timer.start();
            } else {
                gameObservable.notifyGameStateChanged(GameState.TURNO_UMANO);
            }
        }
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

    private void log(String messaggio) {
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
    static class SoundManager {
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