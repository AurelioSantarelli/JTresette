package it.uniroma1.tresette.view.panels;

import it.uniroma1.tresette.controller.GameController;
import it.uniroma1.tresette.view.components.UIComponentFactory;
import it.uniroma1.tresette.view.layout.GameLayoutManager;

import javax.swing.*;
import java.awt.*;

/**
 * Pannello superiore contenente controlli e informazioni di gioco.
 * Include pulsante pausa, punteggi, turno e punteggio vittoria.
 */
public class TopGamePanel extends JPanel {
    
    private final JLabel labelPunteggi;
    private final JLabel labelTurno;
    private final JLabel labelPunteggioVittoria;
    private final JButton btnPausaRiprendi;
    private final GameController gameController;
    
    public TopGamePanel(GameController gameController, double punteggioVittoria) {
        super(new BorderLayout());
        this.gameController = gameController;
        setOpaque(false);
        
        // Inizializza componenti
        btnPausaRiprendi = UIComponentFactory.creaBottonePausa(e -> gameController.togglePausa());
        labelPunteggi = UIComponentFactory.creaLabelPersonalizzata(
            "Punteggi: Caricamento...", SwingConstants.CENTER, 14);
        labelTurno = UIComponentFactory.creaLabelPersonalizzata(
            "Turno: Preparazione...", SwingConstants.CENTER, 14);
        labelPunteggioVittoria = UIComponentFactory.creaLabelPersonalizzata(
            "Partita fino a " + (int)punteggioVittoria + " punti", 
            SwingConstants.RIGHT, 14);
        
        setupLayout();
    }
    
    /**
     * Configura il layout del pannello superiore
     */
    private void setupLayout() {
        // Pannello pulsante pausa (sinistra)
        JPanel panelPausa = GameLayoutManager.createTransparentPanel(new FlowLayout(FlowLayout.LEFT));
        panelPausa.add(btnPausaRiprendi);
        add(panelPausa, BorderLayout.WEST);
        
        // Pannello punteggio vittoria (destra)
        JPanel panelPunteggioVittoria = GameLayoutManager.createTransparentPanel(new FlowLayout(FlowLayout.RIGHT));
        panelPunteggioVittoria.add(labelPunteggioVittoria);
        add(panelPunteggioVittoria, BorderLayout.EAST);
        
        // Pannello centrale punteggi e turno
        JPanel panelPunteggiTurno = GameLayoutManager.createTransparentPanel(new GridLayout(2, 1));
        panelPunteggiTurno.add(labelPunteggi);
        panelPunteggiTurno.add(labelTurno);
        add(panelPunteggiTurno, BorderLayout.CENTER);
    }
    
    /**
     * Aggiorna i punteggi delle coppie
     */
    public void aggiornaPunteggi(double punteggioCoppia1, double punteggioCoppia2) {
        SwingUtilities.invokeLater(() -> {
            it.uniroma1.tresette.model.Giocatore[] giocatori = gameController.getGiocatori();
            labelPunteggi.setText(String.format("Punteggi Totali: " + giocatori[0].getNome() + " - " + giocatori[2].getNome() +
                    " : %.1f | " + giocatori[1].getNome() + " - " + giocatori[3].getNome() + " : %.1f",
                    punteggioCoppia1, punteggioCoppia2));
            repaint();
        });
    }
    
    /**
     * Aggiorna il turno corrente e lo stato della pausa
     */
    public void aggiornaTurno(String nomeGiocatore, int indiceGiocatore) {
        SwingUtilities.invokeLater(() -> {
            if (gameController.isGiocoInPausa()) {
                labelTurno.setText("GIOCO IN PAUSA - Premi '▶' per continuare");
                btnPausaRiprendi.setText("▶");
                btnPausaRiprendi.setBackground(new Color(34, 139, 34)); // Verde
            } else {
                labelTurno.setText("Turno: " + nomeGiocatore);
                btnPausaRiprendi.setText("⏸");
                btnPausaRiprendi.setBackground(it.uniroma1.tresette.view.utils.PaletteColori.BOTTONE_STATS); // Arancione
            }
        });
    }
}
