package it.uniroma1.tresette.view.panels;

import it.uniroma1.tresette.controller.GameController;
import it.uniroma1.tresette.model.Carta;
import it.uniroma1.tresette.view.components.UIComponentFactory;
import it.uniroma1.tresette.view.icons.IconFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Pannello per le carte del giocatore umano.
 * Gestisce la visualizzazione e l'interazione con le 10 carte del giocatore.
 */
public class PlayerCardsPanel extends JPanel {
    
    private final JButton[] bottoniCarte;
    private final GameController gameController;
    
    public PlayerCardsPanel(GameController gameController) {
        super(new FlowLayout(FlowLayout.CENTER, 10, 10));
        this.gameController = gameController;
        
        // Inizializza array bottoni
        bottoniCarte = new JButton[10];
        
        setupPanel();
        createCardButtons();
    }
    
    /**
     * Configura il pannello principale
     */
    private void setupPanel() {
        setOpaque(false);
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), "Le tue carte",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14), Color.WHITE));
    }
    
    /**
     * Crea i bottoni per le 10 carte del giocatore
     */
    private void createCardButtons() {
        for (int i = 0; i < 10; i++) {
            final int indice = i;
            bottoniCarte[i] = UIComponentFactory.creaBottoneCarta(i, e -> {
                // Disabilita immediatamente TUTTE le carte per evitare doppi click
                disabilitaTutteLeCarte();
                // Poi esegue la giocata
                gameController.giocaCarta(indice);
            });
            aggiungiEffettoHoverCarta(bottoniCarte[i], indice);
            add(bottoniCarte[i]);
        }
    }
    
    /**
     * Disabilita immediatamente tutte le carte per evitare click multipli
     */
    private void disabilitaTutteLeCarte() {
        for (JButton bottone : bottoniCarte) {
            if (bottone != null) {
                bottone.setEnabled(false);
            }
        }
    }
    
    /**
     * Aggiunge l'effetto hover a una carta
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
     * Aggiorna le carte in mano del giocatore
     */
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
    
    /**
     * Abilita o disabilita i bottoni delle carte
     * @param abilita true per abilitare, false per disabilitare
     */
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
            
            // Verifica stati di base del gioco
            boolean giocoInCorso = gameController.isGiocoInCorso();
            boolean giocoInPausa = gameController.isGiocoInPausa();
            int giocatoreCorrente = gameController.getGiocatoreCorrente();
            
            // Se non è il turno del giocatore umano (indice 0), disabilita tutto
            if (!giocoInCorso || giocoInPausa || giocatoreCorrente != 0) {
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
            
            int carteAbilitate = 0;
            for (int i = 0; i < bottoniCarte.length; i++) {
                if (bottoniCarte[i] != null && i < manoGiocatore.size()) {
                    Carta carta = manoGiocatore.get(i);
                    boolean isCartaGiocabile = gameController.isCartaGiocabile(carta, 0);
                    
                    bottoniCarte[i].setEnabled(isCartaGiocabile);
                    
                    if (isCartaGiocabile) {
                        carteAbilitate++;
                    }
                } else if (bottoniCarte[i] != null) {
                    bottoniCarte[i].setEnabled(false);
                }
            }
            
            // CONTROLLO CRITICO: Se nessuna carta è abilitata ma il gioco è in corso, c'è un problema
            if (carteAbilitate == 0 && manoGiocatore.size() > 0) {
                System.err.println("ERRORE: Nessuna carta abilitata ma giocatore ha " + manoGiocatore.size() + " carte!");
                System.err.println("ERRORE: Questo non dovrebbe mai succedere nel Tresette!");
                
                // In caso di emergenza, abilita tutte le carte
                System.err.println("EMERGENZA: Abilitando tutte le carte");
                for (int i = 0; i < bottoniCarte.length && i < manoGiocatore.size(); i++) {
                    if (bottoniCarte[i] != null) {
                        bottoniCarte[i].setEnabled(true);
                    }
                }
            }
        });
    }
}
