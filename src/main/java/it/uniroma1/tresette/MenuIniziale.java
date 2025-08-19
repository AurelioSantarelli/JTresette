package it.uniroma1.tresette;

/**
 * Wrapper main class used by the Maven exec task. It forwards to
 * the real UI class in `it.uniroma1.tresette.view.windows`.
 */
public class MenuIniziale {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            it.uniroma1.tresette.view.windows.MenuIniziale menu = new it.uniroma1.tresette.view.windows.MenuIniziale();
            menu.setLocationRelativeTo(null);
            menu.setVisible(true);
        });
    }
}
