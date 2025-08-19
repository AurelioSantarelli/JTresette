package it.uniroma1.tresette.view.components;

import it.uniroma1.tresette.model.Carta;
import it.uniroma1.tresette.view.icons.CardImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Componente Swing che disegna una carta usando CardImageLoader.
 */
public class CardComponent extends JComponent {
    private Carta carta;
    private BufferedImage image;
    private boolean selezionata;

    public CardComponent(Carta carta) {
        this.carta = carta;
        setPreferredSize(new Dimension(Carta.LARGHEZZA_CARTA, Carta.ALTEZZA_CARTA));
        loadImage();
    }

    public void setCarta(Carta c) {
        this.carta = c;
        loadImage();
        repaint();
    }

    public Carta getCarta() { return carta; }

    public void setSelezionata(boolean s) { this.selezionata = s; repaint(); }

    private void loadImage() {
        if (carta == null) {
            image = null;
            return;
        }
        image = CardImageLoader.load(carta.getRisorsaNome());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            String text = carta != null ? carta.toString() : "N/A";
            g.drawString(text, 6, 16);
        }

        if (selezionata) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(255,255,0,80));
            g2.fillRect(0,0,getWidth(),getHeight());
            g2.dispose();
        }
    }
}
