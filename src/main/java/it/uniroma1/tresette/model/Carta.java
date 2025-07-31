package it.uniroma1.tresette.model;

import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * Rappresenta una carta del gioco del Tresette.
 * Contiene informazioni su valore, seme, punti e immagine della carta.
 */
public class Carta {
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
            java.net.URL imageUrl = Carta.class.getResource("/images/retro.png");
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
