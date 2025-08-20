package it.uniroma1.tresette.view.components;

import it.uniroma1.tresette.view.utils.PaletteColori;
import it.uniroma1.tresette.view.sound.SoundManager;
import it.uniroma1.tresette.model.Carta;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Factory per la creazione di componenti UI personalizzati per il gioco.
 * Crea bottoni, pannelli e altri componenti con stili consistenti.
 */
public class UIComponentFactory {

    /**
     * Crea un bottone personalizzato con stile arrotondato
     * @param testo il testo del bottone
     * @param colore il colore di sfondo
     * @param larghezza larghezza del bottone
     * @param altezza altezza del bottone
     * @param listener listener per l'azione del bottone
     * @return JButton personalizzato
     */
    public static JButton creaBottonePersonalizzato(String testo, Color colore, 
                                                   int larghezza, int altezza, 
                                                   ActionListener listener) {
        JButton bottone = new JButton(testo) {
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

        bottone.setBackground(colore);
        bottone.setForeground(Color.WHITE);
        bottone.setOpaque(false);
        bottone.setBorderPainted(false);
        bottone.setFocusPainted(false);
        bottone.setContentAreaFilled(false);
        bottone.setPreferredSize(new Dimension(larghezza, altezza));
        bottone.setFont(new Font("Arial", Font.BOLD, 15));
        bottone.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (listener != null) {
            bottone.addActionListener(e -> {
                SoundManager.riproduciSuonoClick();
                listener.actionPerformed(e);
            });
        }

        aggiungiEffettiMouse(bottone, colore);
        return bottone;
    }

    /**
     * Crea il bottone "Nuova Partita"
     * @param listener listener per l'azione
     * @return bottone configurato
     */
    public static JButton creaBottoneNuovaPartita(ActionListener listener) {
        return creaBottonePersonalizzato("Nuova Partita", PaletteColori.ROSSO_BOTTONE, 
                                        160, 45, listener);
    }

    /**
     * Crea il bottone "Interrompi Partita"
     * @param listener listener per l'azione
     * @return bottone configurato
     */
    public static JButton creaBottoneInterrompiPartita(ActionListener listener) {
        return creaBottonePersonalizzato("Interrompi Partita", new Color(139, 69, 19), 
                                        160, 45, listener);
    }

    /**
     * Crea il bottone pausa/riprendi
     * @param listener listener per l'azione
     * @return bottone configurato
     */
    public static JButton creaBottonePausa(ActionListener listener) {
        JButton bottone = new JButton("⏸") {
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

        bottone.setFont(new Font("Arial", Font.BOLD, 12));
        bottone.setBackground(PaletteColori.BOTTONE_STATS);
        bottone.setForeground(Color.WHITE);
        bottone.setOpaque(false);
        bottone.setBorderPainted(false);
        bottone.setFocusPainted(false);
        bottone.setContentAreaFilled(false);
        bottone.setPreferredSize(new Dimension(70, 35));
        bottone.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (listener != null) {
            bottone.addActionListener(listener);
        }

        aggiungiEffettiMouse(bottone, PaletteColori.BOTTONE_STATS);
        return bottone;
    }

    /**
     * Crea un bottone per le carte con effetti hover
     * @param indice indice della carta
     * @param listener listener per l'azione
     * @return bottone configurato per le carte
     */
    public static JButton creaBottoneCarta(int indice, ActionListener listener) {
        JButton bottone = new JButton();
        bottone.setPreferredSize(new Dimension(Carta.LARGHEZZA_CARTA, Carta.ALTEZZA_CARTA)); // Dimensioni originali carta
        bottone.setVisible(false);
        bottone.setBorderPainted(false);
        bottone.setContentAreaFilled(false);
        bottone.setFocusPainted(false);
        bottone.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (listener != null) {
            bottone.addActionListener(listener);
        }

        return bottone;
    }

    /**
     * Crea una label con stile personalizzato
     * @param testo testo della label
     * @param allineamento allineamento del testo
     * @param dimensioneFont dimensione del font
     * @return label configurata
     */
    public static JLabel creaLabelPersonalizzata(String testo, int allineamento, int dimensioneFont) {
        JLabel label = new JLabel(testo, allineamento);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, dimensioneFont));
        return label;
    }

    /**
     * Crea un pannello per le carte giocate con bordo
     * @param nomeGiocatore nome del giocatore
     * @return pannello configurato
     */
    public static JPanel creaPannelloCartaGiocata(String nomeGiocatore) {
        JPanel pannello = new JPanel(new BorderLayout());
        pannello.setOpaque(false);
        pannello.setBorder(BorderFactory.createRaisedBevelBorder());

        // Label con il nome del giocatore
        JLabel labelNome = new JLabel(nomeGiocatore, SwingConstants.CENTER);
        labelNome.setForeground(Color.WHITE);
        labelNome.setFont(new Font("Arial", Font.BOLD, 12));
        pannello.add(labelNome, BorderLayout.NORTH);

        // Label per la carta
        JLabel labelCarta = new JLabel("", SwingConstants.CENTER);
        labelCarta.setOpaque(true);
        labelCarta.setBackground(PaletteColori.COLORE_TAVOLO);
        labelCarta.setPreferredSize(new Dimension(120, 192)); // Dimensioni carta scalate
        pannello.add(labelCarta, BorderLayout.CENTER);

        return pannello;
    }

    /**
     * Crea l'area di log del gioco
     * @return JScrollPane contenente l'area di log
     */
    public static JScrollPane creaAreaLog() {
        JTextArea areaLog = new JTextArea(15, 40);
        areaLog.setEditable(false);
        areaLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        areaLog.setBackground(PaletteColori.COLORE_TAVOLO);
        areaLog.setForeground(Color.WHITE);
        
        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), "Log di gioco",
            0, 0, new Font("Arial", Font.BOLD, 14), Color.WHITE));
        scrollLog.setBackground(PaletteColori.COLORE_TAVOLO);
        
        return scrollLog;
    }

    /**
     * Aggiunge effetti mouse a un bottone
     * @param bottone il bottone a cui aggiungere gli effetti
     * @param coloreOriginale il colore originale del bottone
     */
    private static void aggiungiEffettiMouse(JButton bottone, Color coloreOriginale) {
        Color coloreHover = coloreOriginale.brighter();
        Color colorePremuto = coloreOriginale.darker();

        bottone.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                bottone.setBackground(coloreHover);
                bottone.setBorder(BorderFactory.createRaisedBevelBorder());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                bottone.setBackground(coloreOriginale);
                bottone.setBorder(null);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bottone.setBackground(colorePremuto);
                bottone.setBorder(BorderFactory.createLoweredBevelBorder());
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                bottone.setBackground(coloreHover);
                bottone.setBorder(BorderFactory.createRaisedBevelBorder());
            }
        });
    }

    /**
     * Crea un pannello con sfondo personalizzato
     * @param layout layout manager da usare
     * @param opaco se il pannello deve essere opaco
     * @return pannello configurato
     */
    public static JPanel creaPannelloPersonalizzato(LayoutManager layout, boolean opaco) {
        JPanel pannello = new JPanel(layout);
        pannello.setOpaque(opaco);
        if (opaco) {
            pannello.setBackground(PaletteColori.COLORE_TAVOLO);
        }
        return pannello;
    }

    /**
     * Crea un pannello con sfondo personalizzato.
     * Se viene passata un’immagine, la disegna; altrimenti usa un colore di
     * default.
     * 
     * @param layout         layout manager del pannello
     * @param opaco          se il pannello deve essere opaco
     * @param sfondoImmagine immagine di sfondo (può essere null)
     * @param coloreDefault  colore di sfondo se l’immagine è null
     * @return pannello configurato
     */
    public static JPanel creaPannelloConSfondo(LayoutManager layout, boolean opaco, Image sfondoImmagine,
            Color coloreDefault) {
        JPanel pannello = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (sfondoImmagine != null) {
                    g.drawImage(sfondoImmagine, 0, 0, getWidth(), getHeight(), this);
                } else if (opaco) {
                    g.setColor(coloreDefault);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        pannello.setOpaque(opaco);
        return pannello;
    }

    /**
     * Crea un JTextField personalizzato
     * @param larghezza larghezza campo
     * @param altezza altezza campo
     * @param columns numero di colonne
     * @return JTextField personalizzato
     */
    public static JTextField creaTextField(int larghezza, int altezza, int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setPreferredSize(new Dimension(larghezza, altezza));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return textField;
    }

    /**
     * Crea una JComboBox personalizzata
     * @param elementi array di elementi da inserire
     * @param larghezza larghezza componente
     * @param altezza altezza componente
     * @return JComboBox personalizzata
     */
    public static <T> JComboBox<T> creaComboBox(T[] elementi, int larghezza, int altezza) {
        JComboBox<T> comboBox = new JComboBox<>(elementi);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(larghezza, altezza));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        return comboBox;
    }
}
