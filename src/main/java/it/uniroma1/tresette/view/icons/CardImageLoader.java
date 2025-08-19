package it.uniroma1.tresette.view.icons;

import it.uniroma1.tresette.model.Carta;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Carica e mette in cache le immagini delle carte da resources (/images/).
 * Le immagini vengono ridimensionate alle dimensioni definite in {@link Carta}
 * e ritagliate con angoli smussati per mantenere l'aspetto precedente.
 */
public final class CardImageLoader {
    private static final Map<String, BufferedImage> CACHE = new ConcurrentHashMap<>();
    private static final String BASE_PATH = "/images/";
    private static final int WIDTH = Carta.LARGHEZZA_CARTA;
    private static final int HEIGHT = Carta.ALTEZZA_CARTA;

    private CardImageLoader() {}

    public static BufferedImage load(String resourceName) {
        if (resourceName == null) return null;
        return CACHE.computeIfAbsent(resourceName, key -> {
            try (InputStream is = CardImageLoader.class.getResourceAsStream(BASE_PATH + key)) {
                if (is == null) return null;
                BufferedImage original = ImageIO.read(is);
                if (original == null) return null;

                // Scale image smoothly
                Image scaled = original.getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);

                // Create rounded image with alpha
                BufferedImage rounded = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = rounded.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Rounded rectangle clip
                int radius = 15;
                RoundRectangle2D roundRect = new RoundRectangle2D.Float(0, 0, WIDTH, HEIGHT, radius, radius);
                g2.setClip(roundRect);
                g2.drawImage(scaled, 0, 0, null);
                g2.dispose();

                return rounded;
            } catch (IOException e) {
                return null;
            }
        });
    }
}
