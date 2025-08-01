package it.uniroma1.tresette.view.windows;

import it.uniroma1.tresette.model.StatisticheGiocatore;
import it.uniroma1.tresette.view.sound.SoundManager;
import it.uniroma1.tresette.view.utils.PaletteColori;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class MenuIniziale extends JFrame {

    private BufferedImage sfondoImmagine;
    private JTextField campoNome;
    private JComboBox<String> comboPunteggio;
    private JComboBox<String> comboModalita;
    private final int dimMenuIniziale = 800;
    private String ultimoNomeUsato = ""; // Ricorda l'ultimo nome utilizzato
    
    public MenuIniziale() {
        super("JTresette - Menu Iniziale");
        caricaSfondo();
        inizializzaGUI();
    }
    
    private void caricaSfondo() {
        try {
            java.net.URL imageUrl = getClass().getResource("/images/sfondo_menu.png");
            if (imageUrl != null) {
                sfondoImmagine = ImageIO.read(imageUrl);
            } else {
                System.err.println("Immagine di sfondo non trovata: /images/sfondo_menu.png");
            }
        } catch (IOException e) {
            System.err.println("Errore nel caricamento dello sfondo: " + e.getMessage());
        }
    }
    
    private void inizializzaGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Pannello principale con sfondo personalizzato
        JPanel pannelloPrincipale = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (sfondoImmagine != null) {
                    g.drawImage(sfondoImmagine, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(PaletteColori.COLORE_TAVOLO);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        pannelloPrincipale.setLayout(new BorderLayout());
        
        // Pannello in alto a destra per il pulsante Stats
        JPanel pannelloSuperiore = new JPanel(new BorderLayout());
        pannelloSuperiore.setOpaque(false);
        
        JButton btnStats = new JButton("Stats") {
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
        btnStats.setFont(new Font("Arial", Font.BOLD, 14));
        btnStats.setBackground(PaletteColori.BOTTONE_STATS); // Arancione
        btnStats.setForeground(Color.WHITE);
        btnStats.setOpaque(false);
        btnStats.setBorderPainted(false);
        btnStats.setFocusPainted(false);
        btnStats.setContentAreaFilled(false);
        btnStats.setPreferredSize(new Dimension(80, 35));
        btnStats.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effetti mouse per il pulsante Stats
        btnStats.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnStats.setBackground(PaletteColori.BOTTONE_STATS_PREMUTO); // Arancione più chiaro
                btnStats.setBorder(BorderFactory.createRaisedBevelBorder());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnStats.setBackground(PaletteColori.BOTTONE_STATS); // Arancione originale
                btnStats.setBorder(null);
            }
            
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnStats.setBackground(PaletteColori.BOTTONE_STATS_PREMUTO); // Arancione più scuro
                btnStats.setBorder(BorderFactory.createLoweredBevelBorder());
            }
            
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnStats.setBackground(PaletteColori.BOTTONE_STATS_PREMUTO); // Torna al colore hover
                btnStats.setBorder(BorderFactory.createRaisedBevelBorder());
            }
        });
        
        btnStats.addActionListener(e -> {
            SoundManager.riproduciSuonoClick();
            mostraStatistiche();
        });
        
        // Pannello wrapper per posizionare il pulsante leggermente più a sinistra e in basso
        JPanel wrapperStats = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        wrapperStats.setOpaque(false);
        wrapperStats.setBorder(BorderFactory.createEmptyBorder(150, 45, 30, 70)); // top, left, bottom, right
        wrapperStats.add(btnStats);
        
        pannelloSuperiore.add(wrapperStats, BorderLayout.EAST);
        pannelloPrincipale.add(pannelloSuperiore, BorderLayout.NORTH);
        
        // Pannello per le scritte in basso
        JPanel pannelloInferiore = new JPanel();
        pannelloInferiore.setOpaque(false);
        pannelloInferiore.setLayout(new BoxLayout(pannelloInferiore, BoxLayout.Y_AXIS));
        pannelloInferiore.setBorder(BorderFactory.createEmptyBorder(20, 50, 120, 50)); // Aumentato da 50 a 120
        
        /* // Sottotitolo
        JLabel sottotitolo = new JLabel("Il tradizionale gioco di carte italiano", SwingConstants.CENTER);
        sottotitolo.setFont(new Font("Arial", Font.ITALIC, 18));
        sottotitolo.setForeground(Color.WHITE);
        sottotitolo.setOpaque(true);
        sottotitolo.setBackground(new Color(0, 0, 0, 100));
        sottotitolo.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        sottotitolo.setAlignmentX(Component.CENTER_ALIGNMENT);
        pannelloInferiore.add(sottotitolo); */
        
        pannelloInferiore.add(Box.createVerticalStrut(15)); // Spazio

        // Pannello per nome (orizzontale)
        JPanel pannelloNome = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pannelloNome.setOpaque(false);
        
        // Etichetta per il nome
        JLabel labelNome = new JLabel("Inserisci il tuo nome:", SwingConstants.CENTER);
        labelNome.setFont(new Font("Arial", Font.BOLD, 14));
        labelNome.setForeground(Color.WHITE);
        labelNome.setOpaque(true);
        labelNome.setBackground(new Color(0, 0, 0, 120));
        labelNome.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        pannelloNome.add(labelNome);
        
        // Campo di testo per il nome
        campoNome = new JTextField("", 10);
        campoNome.setFont(new Font("Arial", Font.PLAIN, 14));
        campoNome.setHorizontalAlignment(JTextField.CENTER);
        campoNome.setToolTipText("Inserisci il tuo nome qui");
        campoNome.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        campoNome.addActionListener(e -> avviaGioco());
        pannelloNome.add(campoNome);
        
        pannelloInferiore.add(pannelloNome);
        pannelloInferiore.add(Box.createVerticalStrut(10)); // Spazio        // Pannello per la scelta del punteggio
        JPanel pannelloPunteggio = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pannelloPunteggio.setOpaque(false);
        
        // Etichetta per il punteggio
        JLabel labelPunteggio = new JLabel("Punti per vincere:", SwingConstants.CENTER);
        labelPunteggio.setFont(new Font("Arial", Font.BOLD, 14));
        labelPunteggio.setForeground(Color.WHITE);
        labelPunteggio.setOpaque(true);
        labelPunteggio.setBackground(new Color(0, 0, 0, 120));
        labelPunteggio.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        pannelloPunteggio.add(labelPunteggio);
        
        // Menu a tendina per il punteggio
        String[] opzioniPunteggio = {"21 punti", "31 punti", "41 punti (classico)"};
        comboPunteggio = new JComboBox<>(opzioniPunteggio);
        comboPunteggio.setSelectedIndex(2); // Default: 41 punti (classico)
        comboPunteggio.setFont(new Font("Arial", Font.PLAIN, 14));
        comboPunteggio.setPreferredSize(new Dimension(150, 30));
        comboPunteggio.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        pannelloPunteggio.add(comboPunteggio);
        
        pannelloInferiore.add(pannelloPunteggio);
        pannelloInferiore.add(Box.createVerticalStrut(10)); // Spazio
        
        // Pannello per la scelta della modalità
        JPanel pannelloModalita = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pannelloModalita.setOpaque(false);
        
        // Etichetta per la modalità
        JLabel labelModalita = new JLabel("Modalità di gioco:", SwingConstants.CENTER);
        labelModalita.setFont(new Font("Arial", Font.BOLD, 14));
        labelModalita.setForeground(Color.WHITE);
        labelModalita.setOpaque(true);
        labelModalita.setBackground(new Color(0, 0, 0, 120));
        labelModalita.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        pannelloModalita.add(labelModalita);
        
        // Menu a tendina per la modalità
        String[] opzioniModalita = {"4 Giocatori (Classico)", "2 Giocatori (1v1)"};
        comboModalita = new JComboBox<>(opzioniModalita);
        comboModalita.setSelectedIndex(0); // Default: 4 giocatori
        comboModalita.setFont(new Font("Arial", Font.PLAIN, 14));
        comboModalita.setPreferredSize(new Dimension(150, 30));
        comboModalita.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        pannelloModalita.add(comboModalita);
        
        pannelloInferiore.add(pannelloModalita);
        pannelloInferiore.add(Box.createVerticalStrut(20)); // Spazio
        
        // Pulsante Inizia Gioco
        JButton btnInizia = new JButton("Inizia Gioco!") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
                g2d.dispose();
            }
        };
        btnInizia.setFont(new Font("Arial", Font.BOLD, 16));
        btnInizia.setBackground(PaletteColori.ROSSO_BOTTONE);
        btnInizia.setForeground(Color.WHITE);
        btnInizia.setOpaque(false);
        btnInizia.setBorderPainted(false);
        btnInizia.setFocusPainted(false);
        btnInizia.setContentAreaFilled(false);
        btnInizia.setPreferredSize(new Dimension(160, 40));
        btnInizia.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnInizia.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Aggiungi effetti mouse
        btnInizia.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnInizia.setBackground(PaletteColori.ROSSO_BOTTONE_PREMUTO); // Verde più chiaro
                btnInizia.setBorder(BorderFactory.createRaisedBevelBorder());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnInizia.setBackground(PaletteColori.ROSSO_BOTTONE); // Verde originale
                btnInizia.setBorder(null);
            }
            
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnInizia.setBackground(PaletteColori.ROSSO_BOTTONE_PREMUTO); // Verde più scuro
                btnInizia.setBorder(BorderFactory.createLoweredBevelBorder());
            }
            
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnInizia.setBackground(new Color(50, 170, 50)); // Torna al colore hover
                btnInizia.setBorder(BorderFactory.createRaisedBevelBorder());
            }
        });

        btnInizia.addActionListener(e -> {
            // Riproduci il suono del click
            SoundManager.riproduciSuonoClick();
            // Oppure usa il click sintetico se non hai file audio:
            // SoundManager.riproduciClickSintetico();
            
            avviaGioco();
        });

        // Bottone Istruzioni
        JButton btnIstruzioni = new JButton("Regole") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                super.paintComponent(g);
                g2d.dispose();
            }
        };
        btnIstruzioni.setFont(new Font("Arial", Font.BOLD, 16));
        btnIstruzioni.setBackground(new Color(70, 130, 180)); // Blu acciaio
        btnIstruzioni.setForeground(Color.WHITE);
        btnIstruzioni.setOpaque(false);
        btnIstruzioni.setBorderPainted(false);
        btnIstruzioni.setFocusPainted(false);
        btnIstruzioni.setContentAreaFilled(false);
        btnIstruzioni.setPreferredSize(new Dimension(140, 40));
        btnIstruzioni.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIstruzioni.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Aggiungi effetti mouse per il bottone istruzioni
        btnIstruzioni.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnIstruzioni.setBackground(new Color(100, 149, 237)); // Blu più chiaro
                btnIstruzioni.setBorder(BorderFactory.createRaisedBevelBorder());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnIstruzioni.setBackground(PaletteColori.BOTTONE_REGOLE); // Blu originale
                btnIstruzioni.setBorder(null);
            }
            
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnIstruzioni.setBackground(PaletteColori.BOTTONE_REGOLE_PREMUTO); // Torna al colore hover
                btnIstruzioni.setBorder(BorderFactory.createRaisedBevelBorder());
            }
        });

        btnIstruzioni.addActionListener(e -> {
            // Riproduci il suono del click
            SoundManager.riproduciSuonoClick();
            mostraIstruzioni();
        });

        // Pannello per i bottoni
        JPanel pannelloBottoni = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pannelloBottoni.setOpaque(false);
        pannelloBottoni.add(btnInizia);
        pannelloBottoni.add(btnIstruzioni);
        
        pannelloInferiore.add(pannelloBottoni);
        
        // Aggiungi il pannello inferiore al bottom del BorderLayout
        pannelloPrincipale.add(pannelloInferiore, BorderLayout.SOUTH);
        
        add(pannelloPrincipale, BorderLayout.CENTER);
        
        // Imposta dimensioni 1080x1080 e blocca completamente il ridimensionamento
        setSize(dimMenuIniziale, dimMenuIniziale);
        setLocationRelativeTo(null);
        setResizable(false);
        setMaximumSize(new Dimension(dimMenuIniziale, dimMenuIniziale));
        setMinimumSize(new Dimension(dimMenuIniziale, dimMenuIniziale));
        setPreferredSize(new Dimension(dimMenuIniziale, dimMenuIniziale));

        SwingUtilities.invokeLater(() -> campoNome.requestFocus());
    }
    
    private void avviaGioco() {
        String nomeGiocatore = campoNome.getText().trim();
        if (nomeGiocatore.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inserisci un nome valido!", "Nome richiesto", JOptionPane.WARNING_MESSAGE);
            campoNome.requestFocus(); // Mette il focus sul campo nome
            return;
        }
        
        // Salva l'ultimo nome utilizzato
        ultimoNomeUsato = nomeGiocatore;
        
        // Determina il punteggio di vittoria selezionato
        int punteggioVittoria;
        switch (comboPunteggio.getSelectedIndex()) {
            case 0: punteggioVittoria = 21; break;
            case 1: punteggioVittoria = 31; break;
            case 2: 
            default: punteggioVittoria = 41; break;
        }
        
        // Determina la modalità di gioco selezionata
        boolean modalitaDueGiocatori = comboModalita.getSelectedIndex() == 1;
        
        dispose();
        SwingUtilities.invokeLater(() -> new TresetteGame(nomeGiocatore, punteggioVittoria, modalitaDueGiocatori).setVisible(true));
    }
    
    private void mostraStatistiche() {
        String tempNome = campoNome.getText().trim();
        
        // Se il campo è vuoto, usa l'ultimo nome utilizzato
        if (tempNome.isEmpty()) {
            if (!ultimoNomeUsato.isEmpty()) {
                tempNome = ultimoNomeUsato;
                // Reinserisci il nome nel campo per comodità
                campoNome.setText(ultimoNomeUsato);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Inserisci prima il tuo nome nel campo sopra per vedere le tue statistiche!", 
                    "Nome richiesto", 
                    JOptionPane.WARNING_MESSAGE);
                campoNome.requestFocus();
                return;
            }
        }
        final String nomeGiocatore = tempNome; // Rendi finale per l'uso nelle lambda
        
        StatisticheGiocatore stats = new StatisticheGiocatore(nomeGiocatore);
        
        String statistiche = String.format("""
            STATISTICHE DI %s
            
            Partite Giocate: %d
            Partite Vinte: %d
            Partite Perse: %d
            Percentuale Vittorie: %.1f%%
            
            %s
            """, 
            nomeGiocatore.toUpperCase(),
            stats.getPartiteGiocate(),
            stats.getPartiteVinte(),
            stats.getPartitePerse(),
            stats.getPercentualeVittorie(),
            getMessaggioMotivazionale(stats)
        );
        
        JTextArea areaStats = new JTextArea(statistiche);
        areaStats.setEditable(false);
        areaStats.setFont(new Font("Arial", Font.PLAIN, 16));
        areaStats.setBackground(new Color(245, 245, 245));
        areaStats.setForeground(Color.BLACK);
        areaStats.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel con bottoni per reset e visualizzazione di tutti i giocatori
        JPanel panelStats = new JPanel(new BorderLayout());
        panelStats.add(areaStats, BorderLayout.CENTER);
        
        JPanel panelBottoni = new JPanel(new FlowLayout());
        
        JButton btnReset = new JButton("Reset " + nomeGiocatore);
        btnReset.setFont(new Font("Arial", Font.BOLD, 12));
        btnReset.setBackground(new Color(220, 20, 60));
        btnReset.setForeground(Color.BLACK);
        btnReset.addActionListener(e -> {
            int risposta = JOptionPane.showConfirmDialog(
                this,
                "Sei sicuro di voler resettare le statistiche di " + nomeGiocatore + "?",
                "Conferma Reset",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (risposta == JOptionPane.YES_OPTION) {
                stats.resetStatistiche();
                JOptionPane.showMessageDialog(this, "Statistiche di " + nomeGiocatore + " resettate!", "Reset completato", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton btnTuttiGiocatori = new JButton("Tutti i Giocatori");
        btnTuttiGiocatori.setFont(new Font("Arial", Font.BOLD, 12));
        btnTuttiGiocatori.setBackground(Color.LIGHT_GRAY);
        btnTuttiGiocatori.setForeground(Color.BLACK);
        btnTuttiGiocatori.addActionListener(e -> mostraTuttiGiocatori());
        
        panelBottoni.add(btnReset);
        panelBottoni.add(btnTuttiGiocatori);
        panelStats.add(panelBottoni, BorderLayout.SOUTH);
        
        JOptionPane.showMessageDialog(
            this,
            panelStats,
            "Statistiche di " + nomeGiocatore,
            JOptionPane.PLAIN_MESSAGE,
            creaIconaStatistiche()
        );
    }
    
    private void mostraTuttiGiocatori() {
        String[] giocatori = StatisticheGiocatore.getGiocatoriConStatistiche();
        
        if (giocatori.length == 0) {
            JOptionPane.showMessageDialog(
                this,
                "Nessun giocatore ha ancora giocato partite!",
                "Nessuna Statistica",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("CLASSIFICA GENERALE\n");
        sb.append("═══════════════════════════════\n\n");
        
        // Carica le statistiche di tutti i giocatori e ordina per vittorie
        java.util.List<StatisticheGiocatore> listaStats = new java.util.ArrayList<>();
        for (String nome : giocatori) {
            listaStats.add(new StatisticheGiocatore(nome));
        }
        
        // Ordina per percentuale vittorie (poi per numero partite vinte)
        listaStats.sort((a, b) -> {
            double diffPerc = b.getPercentualeVittorie() - a.getPercentualeVittorie();
            if (Math.abs(diffPerc) < 0.1) {
                return b.getPartiteVinte() - a.getPartiteVinte();
            }
            return diffPerc > 0 ? 1 : -1;
        });
        
        for (int i = 0; i < listaStats.size(); i++) {
            StatisticheGiocatore stat = listaStats.get(i);
            String posizione = "";
            switch (i) {
                case 0: posizione = "[I] "; break;
                case 1: posizione = "[II] "; break;
                case 2: posizione = "[III] "; break;
                default: posizione = String.format("%d° ", i + 1); break;
            }
            
            sb.append(String.format("%s%s\n", posizione, stat.getNomeGiocatore().toUpperCase()));
            sb.append(String.format("   %d partite | %d vinte | %.1f%%\n\n",
                stat.getPartiteGiocate(),
                stat.getPartiteVinte(),
                stat.getPercentualeVittorie()
            ));
        }
        
        JTextArea areaTutti = new JTextArea(sb.toString());
        areaTutti.setEditable(false);
        areaTutti.setFont(new Font("Courier New", Font.PLAIN, 14));
        areaTutti.setBackground(new Color(245, 245, 245));
        areaTutti.setForeground(Color.BLACK);
        areaTutti.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(areaTutti);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Classifica Tutti i Giocatori",
            JOptionPane.PLAIN_MESSAGE,
            creaIconaStatistiche()
        );
    }
    
    // Metodo per impostare l'ultimo nome utilizzato (chiamato dal gioco quando si torna al menu)
    public void setUltimoNomeUsato(String nome) {
        if (nome != null && !nome.trim().isEmpty()) {
            ultimoNomeUsato = nome.trim();
            campoNome.setText(ultimoNomeUsato);
        }
    }

    private String getMessaggioMotivazionale(StatisticheGiocatore stats) {
        double percentuale = stats.getPercentualeVittorie();
        int partite = stats.getPartiteGiocate();
        
        if (partite == 0) {
            return "Inizia a giocare per vedere le tue statistiche!";
        } else if (percentuale >= 75) {
            return "Sei un maestro del Tresette! Ottimo lavoro!";
        } else if (percentuale >= 60) {
            return "Stai andando molto bene! Continua così!";
        } else if (percentuale >= 50) {
            return "Sei sulla buona strada! La pratica rende perfetti!";
        } else if (percentuale >= 25) {
            return "Non mollare! Ogni partita è un'occasione per migliorare!";
        } else {
            return "La fortuna gira! Il prossimo successo è dietro l'angolo!";
        }
    }
    
    private ImageIcon creaIconaStatistiche() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Sfondo arancione
        g2d.setColor(PaletteColori.BOTTONE_STATS);
        g2d.fillOval(5, 5, 54, 54);
        
        // Simbolo grafico statistiche
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        
        // Disegna un grafico a barre stilizzato
        g2d.fillRect(18, 45, 6, 10);
        g2d.fillRect(26, 35, 6, 20);
        g2d.fillRect(34, 25, 6, 30);
        g2d.fillRect(42, 40, 6, 15);
        
        g2d.dispose();
        return new ImageIcon(img);
    }
    
    private void mostraIstruzioni() {
        String istruzioni = """
            REGOLE DEL TRESETTE
            
            OBIETTIVO:
            Raggiungere per primi il punteggio scelto (21, 31 o 41 punti) con il proprio compagno di squadra.
            
            SQUADRE:
            • Tu + Viligelmo (coppia 1)
            • Marcovaldo + Astolfo (coppia 2)
            
            VALORE DELLE CARTE:
            • Asso: 1 punti
            • Due, Tre, Fante, Cavallo, Re: 0.33 punti ciascuno
            • Quattro, Cinque, Sei, Sette: 0 punti
            
            FORZA DELLE CARTE (per prendere la mano):
            Dal più forte al più debole: 3, 2, Asso, Re, Cavallo, Fante, 7, 6, 5, 4
            
            COME SI GIOCA:
            1. Ogni giocatore riceve 10 carte
            2. Il primo di mano gioca una carta
            3. Gli altri devono rispondere al seme se ce l'hanno
            4. Se non hanno il seme, possono giocare qualsiasi carta
            5. Vince la mano chi gioca la carta più forte del seme richiesto
            6. Chi vince la mano inizia quella successiva
            
            VITTORIA:
            La prima coppia che raggiunge o supera il punteggio scelto vince la partita!
            
            STRATEGIA:
            • Cerca di capire quali carte hanno i tuoi avversari
            • Ricorda quali carte sono già uscite
            """;
        
        JTextArea areaIstruzioni = new JTextArea(istruzioni);
        areaIstruzioni.setEditable(false);
        areaIstruzioni.setFont(new Font("Arial", Font.PLAIN, 14));
        areaIstruzioni.setBackground(new Color(245, 245, 245));
        areaIstruzioni.setForeground(Color.BLACK);
        areaIstruzioni.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(areaIstruzioni);
        scrollPane.setPreferredSize(new Dimension(550, 600));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Istruzioni - Come giocare a Tresette",
            JOptionPane.PLAIN_MESSAGE,
            creaIconaIstruzioni()
        );
    }
    
    private ImageIcon creaIconaIstruzioni() {
        // Crea un'icona personalizzata per le istruzioni
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Sfondo blu
        g2d.setColor(PaletteColori.BOTTONE_REGOLE);
        g2d.fillOval(5, 5, 54, 54);
        
        // Punto interrogativo
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        FontMetrics fm = g2d.getFontMetrics();
        String testo = "?";
        int x = (64 - fm.stringWidth(testo)) / 2;
        int y = (64 + fm.getAscent() - fm.getDescent()) / 2;
        g2d.drawString(testo, x, y);
        
        g2d.dispose();
        return new ImageIcon(img);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MenuIniziale().setVisible(true);
        });
    }
}