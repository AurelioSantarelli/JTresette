package it.uniroma1.tresette.view.panels;

import it.uniroma1.tresette.view.windows.MenuIniziale;
import it.uniroma1.tresette.controller.GameController;
import it.uniroma1.tresette.view.components.UIComponentFactory;
import it.uniroma1.tresette.view.dialogs.DialogManager;
import it.uniroma1.tresette.view.layout.GameLayoutManager;
import it.uniroma1.tresette.view.utils.PaletteColori;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Pannello laterale destro contenente area log e bottoni di controllo.
 * Gestisce il logging del gioco e i controlli principali.
 */
public class SideControlPanel extends JPanel {
    
    private final JTextArea areaLog;
    private final GameController gameController;
    private final JFrame parentFrame;
    
    public SideControlPanel(GameController gameController, JFrame parentFrame) {
        super(new BorderLayout());
        this.gameController = gameController;
        this.parentFrame = parentFrame;
        
        setOpaque(false);
        
        // Inizializza area log
        areaLog = new JTextArea(15, 40);
        setupComponents();
    }
    
    /**
     * Configura i componenti del pannello
     */
    private void setupComponents() {
        // Area di log
        JScrollPane scrollLog = createLogArea();
        add(scrollLog, BorderLayout.CENTER);
        
        // Pannello bottoni
        JPanel panelBottoni = createControlButtons();
        add(panelBottoni, BorderLayout.SOUTH);
    }
    
    /**
     * Crea l'area di log scrollabile
     * @return JScrollPane configurato
     */
    private JScrollPane createLogArea() {
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
     * Crea il pannello con i bottoni di controllo
     * @return pannello configurato
     */
    private JPanel createControlButtons() {
        JPanel panelBottoni = GameLayoutManager.createTransparentPanel(new GridLayout(2, 1, 5, 5));
        
        // Bottone Nuova Partita
        JButton btnNuovaPartita = UIComponentFactory.creaBottoneNuovaPartita(e -> {
            if (gameController.isGiocoInCorso()) {
                if (DialogManager.confermaInizioNuovaPartita(parentFrame)) {
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
                if (DialogManager.confermaInterruzione(parentFrame)) {
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
        parentFrame.dispose();
        SwingUtilities.invokeLater(() -> {
            MenuIniziale menu = new MenuIniziale();
            menu.setUltimoNomeUsato(nomeGiocatore);
            menu.setVisible(true);
        });
    }
    
    /**
     * Aggiunge un messaggio al log
     * @param messaggio messaggio da aggiungere
     */
    public void log(String messaggio) {
        if (areaLog != null) {
            areaLog.append(messaggio + "\n");
            areaLog.setCaretPosition(areaLog.getDocument().getLength());
        }
    }
}
