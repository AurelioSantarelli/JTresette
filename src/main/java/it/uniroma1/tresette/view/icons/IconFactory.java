package it.uniroma1.tresette.view.icons;

import it.uniroma1.tresette.view.utils.PaletteColori;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * Factory per la creazione di icone personalizzate del gioco.
 * Gestisce il caricamento delle icone dalle risorse e la creazione di icone di fallback.
 */
/**
 * Factory per la creazione e gestione delle icone utilizzate nell'applicazione
 */
public class IconFactory {

    /**
     * Crea l'icona di vittoria
     * @return ImageIcon per la vittoria
     */
    public static ImageIcon creaIconaVittoria() {
        try {
            java.net.URL iconUrl = IconFactory.class.getResource("/images/vittoria.png");
            if (iconUrl != null) {
                BufferedImage img = ImageIO.read(iconUrl);
                Image scaledImg = img.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            }
        } catch (IOException e) {
            System.err.println("Errore nel caricamento dell'icona vittoria: " + e.getMessage());
        }

        // Icona di fallback per vittoria
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Sfondo verde
        g2d.setColor(PaletteColori.COLORE_TAVOLO);
        g2d.fillOval(5, 5, 54, 54);

        // Aggiungi simbolo di vittoria
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("✓", 20, 40);

        g2d.dispose();
        return new ImageIcon(img);
    }

    /**
     * Crea l'icona di sconfitta
     * @return ImageIcon per la sconfitta
     */
    public static ImageIcon creaIconaSconfitta() {
        try {
            java.net.URL iconUrl = IconFactory.class.getResource("/images/sconfitta.png");
            if (iconUrl != null) {
                BufferedImage img = ImageIO.read(iconUrl);
                Image scaledImg = img.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            }
        } catch (IOException e) {
            System.err.println("Errore nel caricamento dell'icona sconfitta: " + e.getMessage());
        }

        // Icona di fallback per sconfitta
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Sfondo rosso
        g2d.setColor(PaletteColori.ROSSO_BOTTONE);
        g2d.fillOval(5, 5, 54, 54);

        // Aggiungi simbolo di sconfitta
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("✗", 18, 40);

        g2d.dispose();
        return new ImageIcon(img);
    }

    /**
     * Crea l'icona di pareggio
     * @return ImageIcon per il pareggio
     */
    public static ImageIcon creaIconaPareggio() {
        try {
            java.net.URL iconUrl = IconFactory.class.getResource("/images/pareggio.png");
            if (iconUrl != null) {
                BufferedImage img = ImageIO.read(iconUrl);
                Image scaledImg = img.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            }
        } catch (IOException e) {
            System.err.println("Errore nel caricamento dell'icona pareggio: " + e.getMessage());
        }

        // Icona di fallback per pareggio
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Sfondo giallo
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(5, 5, 54, 54);

        // Aggiungi simbolo di pareggio
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("=", 20, 40);

        g2d.dispose();
        return new ImageIcon(img);
    }

    /**
     * Crea l'icona di conferma
     * @return ImageIcon per la conferma
     */
    public static ImageIcon creaIconaConferma() {
        try {
            java.net.URL iconUrl = IconFactory.class.getResource("/images/conferma.png");
            if (iconUrl != null) {
                BufferedImage img = ImageIO.read(iconUrl);
                Image scaledImg = img.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            }
        } catch (IOException e) {
            System.err.println("Errore nel caricamento dell'icona conferma: " + e.getMessage());
        }

        // Icona di fallback per conferma
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Sfondo verde
        g2d.setColor(new Color(34, 139, 34));
        g2d.fillOval(5, 5, 54, 54);

        // Punto esclamativo
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        g2d.drawString("?", 20, 40);

        g2d.dispose();
        return new ImageIcon(img);
    }

    /**
     * Crea l'icona dell'applicazione
     * @return ImageIcon per l'icona dell'app
     */
    public static ImageIcon creaIconaApp() {
        try {
            java.net.URL iconUrl = IconFactory.class.getResource("/images/app_icon.png");
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                return icon;
            }
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dell'icona app: " + e.getMessage());
        }

        // Icona di fallback
        BufferedImage iconImg = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = iconImg.createGraphics();
        g2d.setColor(PaletteColori.COLORE_TAVOLO); // Verde tavolo
        g2d.fillRect(0, 0, 64, 64);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("T", 22, 40);
        g2d.dispose();
        return new ImageIcon(iconImg);
    }

    /**
     * Crea un'icona schiarita di un'icona esistente (per effetti hover)
     * @param iconaOriginale l'icona da schiarire
     * @return icona schiarita
     */
    public static ImageIcon creaIconaSchiarita(ImageIcon iconaOriginale) {
        if (iconaOriginale == null) return null;
        
        BufferedImage imgOriginale = new BufferedImage(
            iconaOriginale.getIconWidth(),
            iconaOriginale.getIconHeight(),
            BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g2d = imgOriginale.createGraphics();
        g2d.drawImage(iconaOriginale.getImage(), 0, 0, null);
        g2d.dispose();
        
        // Crea un'immagine schiarita
        BufferedImage imgSchiarita = new BufferedImage(
            imgOriginale.getWidth(),
            imgOriginale.getHeight(),
            BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g2dSchiarita = imgSchiarita.createGraphics();
        g2dSchiarita.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Disegna l'immagine originale
        g2dSchiarita.drawImage(imgOriginale, 0, 0, null);
        
        // Applica un overlay bianco semi-trasparente per schiarire
        g2dSchiarita.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g2dSchiarita.setColor(Color.WHITE);
        g2dSchiarita.fillRect(0, 0, imgSchiarita.getWidth(), imgSchiarita.getHeight());
        
        g2dSchiarita.dispose();
        
        return new ImageIcon(imgSchiarita);
    }
}
