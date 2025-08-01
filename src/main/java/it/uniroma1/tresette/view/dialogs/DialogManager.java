package it.uniroma1.tresette.view.dialogs;

import it.uniroma1.tresette.view.icons.IconFactory;
import it.uniroma1.tresette.view.sound.SoundManager;

import javax.swing.*;

/**
 * Gestisce tutte le finestre di dialogo del gioco.
 * Centralizza la creazione e gestione dei messaggi per l'utente.
 */
public class DialogManager {

    /**
     * Mostra una finestra di vittoria
     * @param parent componente padre
     * @param messaggio messaggio di vittoria
     * @param haVintoGiocatore true se ha vinto il giocatore umano
     */
    public static void mostraVittoria(JFrame parent, String messaggio, boolean haVintoGiocatore) {
        ImageIcon icona;
        String titolo;
        
        if (haVintoGiocatore) {
            icona = IconFactory.creaIconaVittoria();
            titolo = "Vittoria!";
            SoundManager.riproduciSuonoVittoria();
        } else {
            icona = IconFactory.creaIconaSconfitta();
            titolo = "Sconfitta!";
            SoundManager.riproduciSuonoSconfitta();
        }
        
        JOptionPane.showMessageDialog(
            parent, 
            messaggio, 
            titolo, 
            JOptionPane.PLAIN_MESSAGE, 
            icona
        );
    }

    /**
     * Mostra una finestra di pareggio
     * @param parent componente padre
     * @param messaggio messaggio di pareggio
     */
    public static void mostraPareggio(JFrame parent, String messaggio) {
        JOptionPane.showMessageDialog(
            parent,
            messaggio,
            "Pareggio!",
            JOptionPane.PLAIN_MESSAGE,
            IconFactory.creaIconaPareggio()
        );
    }

    /**
     * Mostra una finestra di conferma per nuova partita
     * @param parent componente padre
     * @return true se l'utente ha confermato
     */
    public static boolean confermaInizioNuovaPartita(JFrame parent) {
        SoundManager.riproduciSuonoClick();
        
        int risposta = JOptionPane.showConfirmDialog(
            parent,
            "Sei sicuro di voler iniziare una nuova partita?\nLa partita corrente andrà persa.",
            "Conferma Nuova Partita",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            IconFactory.creaIconaConferma()
        );
        
        return risposta == JOptionPane.YES_OPTION;
    }

    /**
     * Mostra una finestra di conferma per interrompere la partita
     * @param parent componente padre
     * @return true se l'utente ha confermato
     */
    public static boolean confermaInterruzione(JFrame parent) {
        SoundManager.riproduciSuonoClick();
        
        int risposta = JOptionPane.showConfirmDialog(
            parent,
            "Sei sicuro di voler interrompere la partita e tornare al menu principale?",
            "Conferma Interruzione",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            IconFactory.creaIconaConferma()
        );
        
        return risposta == JOptionPane.YES_OPTION;
    }

    /**
     * Mostra un messaggio di errore per carta non giocabile
     * @param parent componente padre
     * @param nomeSemerRichiesto nome del seme richiesto
     */
    public static void mostraErroreCartaNonGiocabile(JFrame parent, String nomeSemerRichiesto) {
        JOptionPane.showMessageDialog(
            parent,
            "Devi rispondere al seme " + nomeSemerRichiesto + " se ce l'hai!",
            "Carta non giocabile",
            JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Mostra un messaggio di avviso per pausa impossibile
     * @param parent componente padre
     */
    public static void mostraErrorePausaImpossibile(JFrame parent) {
        JOptionPane.showMessageDialog(
            parent,
            "Non puoi mettere in pausa un gioco che non è in corso!",
            "Impossibile mettere in pausa",
            JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Mostra un messaggio generico di informazione
     * @param parent componente padre
     * @param messaggio messaggio da mostrare
     * @param titolo titolo della finestra
     */
    public static void mostraInformazione(JFrame parent, String messaggio, String titolo) {
        JOptionPane.showMessageDialog(
            parent,
            messaggio,
            titolo,
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Mostra un messaggio generico di errore
     * @param parent componente padre
     * @param messaggio messaggio di errore
     * @param titolo titolo della finestra
     */
    public static void mostraErrore(JFrame parent, String messaggio, String titolo) {
        JOptionPane.showMessageDialog(
            parent,
            messaggio,
            titolo,
            JOptionPane.ERROR_MESSAGE
        );
    }
}
