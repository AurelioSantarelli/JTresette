package it.uniroma1.tresette.view.layout;

import it.uniroma1.tresette.view.utils.PaletteColori;

import javax.swing.*;
import java.awt.*;

/**
 * Gestore del layout principale della finestra di gioco.
 * Centralizza la configurazione e organizzazione dei pannelli.
 */
public class GameLayoutManager {
    
    private final JFrame parentFrame;
    
    /**
     * Costruttore del gestore del layout
     * 
     * @param parentFrame finestra principale del gioco
     */
    public GameLayoutManager(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }
    
    /**
     * Configura il layout principale della finestra
     */
    public void setupMainLayout() {
        parentFrame.setLayout(new BorderLayout());
        parentFrame.getContentPane().setBackground(PaletteColori.COLORE_TAVOLO);
        
        // Configurazione finestra
        parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        parentFrame.setSize(1300, 900);
        parentFrame.setLocationRelativeTo(null);
        parentFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    /**
     * Aggiunge un pannello al layout principale
     * @param component componente da aggiungere
     * @param position posizione nel BorderLayout
     */
    public void addComponent(Component component, String position) {
        parentFrame.add(component, position);
    }
    
    /**
     * Crea un pannello base con sfondo trasparente
     * @param layout layout manager per il pannello
     * @return pannello configurato
     */
    public static JPanel createTransparentPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }
    
    /**
     * Crea un pannello con bordo e titolo
     * @param title titolo del bordo
     * @param layout layout manager
     * @return pannello configurato
     */
    public static JPanel createTitledPanel(String title, LayoutManager layout) {
        JPanel panel = createTransparentPanel(layout);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), title,
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14), Color.WHITE));
        return panel;
    }
}
