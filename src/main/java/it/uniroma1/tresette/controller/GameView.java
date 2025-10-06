package it.uniroma1.tresette.controller;

/**
 * Interfaccia comune per la view del gioco.
 * Definisce i metodi necessari per comunicare con l'interfaccia utente.
 */
public interface GameView {
    void aggiornaInterfaccia();
    void aggiornaPunteggi(double punteggioCoppia1, double punteggioCoppia2);
    void aggiornaTurno(String nomeGiocatore, int indiceGiocatore);
    void mostraVittoria(String messaggioVittoria);
    void log(String messaggio);
    void abilitaBottoniCarte(boolean abilita);
    void aggiornaCarteGiocate();
    void aggiornaManiGiocatori();
}
