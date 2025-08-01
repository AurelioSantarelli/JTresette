package it.uniroma1.tresette.view.panels;

import it.uniroma1.tresette.controller.GameController;
import it.uniroma1.tresette.model.Carta;
import it.uniroma1.tresette.view.utils.PaletteColori;

import javax.swing.*;
import java.awt.*;

/**
 * Pannello centrale per visualizzare le carte giocate.
 * Organizzato in una griglia 2x2 per 4 giocatori o 1x2 per 2 giocatori.
 */
public class PlayedCardsPanel extends JPanel {
    
    private final JLabel[] labelCarteGiocate;
    private final JLabel[] labelNomiGiocatori;
    private final GameController gameController;
    private final int numeroGiocatori;
    
    public PlayedCardsPanel(GameController gameController) {
        this.gameController = gameController;
        this.numeroGiocatori = gameController.getNumeroGiocatori();
        
        // Configura il layout in base al numero di giocatori
        if (numeroGiocatori == 2) {
            setLayout(new GridLayout(1, 2, 15, 8));
        } else {
            setLayout(new GridLayout(2, 2, 8, 8));
        }
        
        // Inizializza arrays
        labelCarteGiocate = new JLabel[numeroGiocatori];
        labelNomiGiocatori = new JLabel[numeroGiocatori];
        
        setupPanel();
        createPlayerPanels();
    }
    
    /**
     * Configura il pannello principale
     */
    private void setupPanel() {
        setOpaque(false);
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), "Carte Giocate",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14), Color.WHITE));
    }
    
    /**
     * Crea i pannelli per i giocatori (2 o 4 in base alla modalità)
     */
    private void createPlayerPanels() {
        for (int i = 0; i < numeroGiocatori; i++) {
            JPanel panelGiocatore = createSinglePlayerPanel(i);
            add(panelGiocatore);
        }
    }
    
    /**
     * Crea un pannello per un singolo giocatore
     * @param indice indice del giocatore
     * @return pannello configurato
     */
    private JPanel createSinglePlayerPanel(int indice) {
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
     * Aggiorna i nomi dei giocatori nei riquadri con mapping corretto
     */
    public void aggiornaNomiGiocatori() {
        if (gameController != null) {
            it.uniroma1.tresette.model.Giocatore[] giocatori = gameController.getGiocatori();
            if (giocatori != null && giocatori.length >= numeroGiocatori) {
                if (numeroGiocatori == 2) {
                    // Modalità 1v1: giocatore umano a sinistra, AI a destra
                    labelNomiGiocatori[0].setText(giocatori[0].getNome()); // Giocatore umano
                    labelNomiGiocatori[1].setText(giocatori[1].getNome()); // AI
                } else {
                    // Modalità 4 giocatori: mapping tradizionale
                    labelNomiGiocatori[0].setText(giocatori[2].getNome()); // Viligelmo
                    labelNomiGiocatori[1].setText(giocatori[0].getNome()); // Giocatore umano
                    labelNomiGiocatori[2].setText(giocatori[3].getNome()); // Astolfo
                    labelNomiGiocatori[3].setText(giocatori[1].getNome()); // Marcovaldo
                }
            }
        }
    }
    
    /**
     * Aggiorna le carte giocate sul tavolo
     */
    public void aggiornaCarteGiocate() {
        SwingUtilities.invokeLater(() -> {
            if (numeroGiocatori == 2) {
                // Modalità 1v1: mapping diretto
                for (int giocatore = 0; giocatore < 2; giocatore++) {
                    Carta carta = gameController.getCartaPerPosizione(giocatore);
                    
                    if (carta != null) {
                        labelCarteGiocate[giocatore].setIcon(carta.getImmagine());
                        labelCarteGiocate[giocatore].setText("");
                    } else {
                        labelCarteGiocate[giocatore].setIcon(null);
                        labelCarteGiocate[giocatore].setText("");
                    }
                }
            } else {
                // Modalità 4 giocatori: mapping tradizionale
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
            }
        });
    }
}
