package it.uniroma1.tresette.view.panels;

import it.uniroma1.tresette.controller.GameController;
import it.uniroma1.tresette.model.Carta;
import it.uniroma1.tresette.view.utils.PaletteColori;

import javax.swing.*;
import java.awt.*;

/**
 * Pannello centrale per visualizzare le carte giocate dai 4 giocatori.
 * Organizzato in una griglia 2x2 con mapping delle posizioni.
 */
public class PlayedCardsPanel extends JPanel {
    
    private final JLabel[] labelCarteGiocate;
    private final JLabel[] labelNomiGiocatori;
    private final GameController gameController;
    
    public PlayedCardsPanel(GameController gameController) {
        super(new GridLayout(2, 2, 8, 8));
        this.gameController = gameController;
        
        // Inizializza arrays
        labelCarteGiocate = new JLabel[4];
        labelNomiGiocatori = new JLabel[4];
        
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
     * Crea i 4 pannelli per i giocatori
     */
    private void createPlayerPanels() {
        for (int i = 0; i < 4; i++) {
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
            if (giocatori != null && giocatori.length >= 4) {
                labelNomiGiocatori[0].setText(giocatori[2].getNome()); // Viligelmo
                labelNomiGiocatori[1].setText(giocatori[0].getNome()); // Giocatore umano
                labelNomiGiocatori[2].setText(giocatori[3].getNome()); // Astolfo
                labelNomiGiocatori[3].setText(giocatori[1].getNome()); // Marcovaldo
            }
        }
    }
    
    /**
     * Aggiorna le carte giocate sul tavolo
     */
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
}
