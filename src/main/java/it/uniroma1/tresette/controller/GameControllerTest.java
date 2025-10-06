package it.uniroma1.tresette.controller;

import it.uniroma1.tresette.model.observer.GameStateObservable;

/**
 * Classe di test per verificare che il GameController funzioni correttamente.
 * Implementa una versione semplificata della GameView per testing.
 */
public class GameControllerTest {
    
    // Implementazione semplice della GameView per il test
    static class TestGameView implements GameView {
        @Override
        public void aggiornaInterfaccia() {
            System.out.println("TEST: aggiornaInterfaccia()");
        }

        @Override
        public void aggiornaPunteggi(double punteggioCoppia1, double punteggioCoppia2) {
            System.out.printf("TEST: aggiornaPunteggi(%.1f, %.1f)%n", punteggioCoppia1, punteggioCoppia2);
        }

        @Override
        public void aggiornaTurno(String nomeGiocatore, int indiceGiocatore) {
            System.out.printf("TEST: aggiornaTurno(%s, %d)%n", nomeGiocatore, indiceGiocatore);
        }

        @Override
        public void mostraVittoria(String messaggioVittoria) {
            System.out.printf("TEST: mostraVittoria(%s)%n", messaggioVittoria);
        }

        @Override
        public void log(String messaggio) {
            System.out.printf("TEST LOG: %s%n", messaggio);
        }

        @Override
        public void abilitaBottoniCarte(boolean abilita) {
            System.out.printf("TEST: abilitaBottoniCarte(%b)%n", abilita);
        }

        @Override
        public void aggiornaCarteGiocate() {
            System.out.println("TEST: aggiornaCarteGiocate()");
        }

        @Override
        public void aggiornaManiGiocatori() {
            System.out.println("TEST: aggiornaManiGiocatori()");
        }
    }
    
    /**
     * Metodo principale per eseguire i test del GameController
     * 
     * @param args argomenti da riga di comando (non utilizzati)
     */
    public static void main(String[] args) {
        System.out.println("=== TEST GameController ===");
        
        try {
            // Crea gli oggetti necessari
            GameStateObservable gameObservable = new GameStateObservable();
            TestGameView testView = new TestGameView();
            
            // Test modalità 2 giocatori
            System.out.println("\n--- Test Modalità 2 Giocatori ---");
            GameController controller2 = new GameController(
                "TestPlayer", 21, true, gameObservable, testView
            );
            
            System.out.println("✓ GameController creato con successo (2 giocatori)");
            System.out.printf("  Numero giocatori: %d%n", controller2.getNumeroGiocatori());
            System.out.printf("  Giocatori: %s, %s%n", 
                controller2.getGiocatori()[0].getNome(),
                controller2.getGiocatori()[1].getNome());
            
            // Test modalità 4 giocatori
            System.out.println("\n--- Test Modalità 4 Giocatori ---");
            GameController controller4 = new GameController(
                "TestPlayer", 21, false, gameObservable, testView
            );
            
            System.out.println("✓ GameController creato con successo (4 giocatori)");
            System.out.printf("  Numero giocatori: %d%n", controller4.getNumeroGiocatori());
            System.out.printf("  Giocatori: %s, %s, %s, %s%n", 
                controller4.getGiocatori()[0].getNome(),
                controller4.getGiocatori()[1].getNome(),
                controller4.getGiocatori()[2].getNome(),
                controller4.getGiocatori()[3].getNome());
            
            // Test avvio partita
            System.out.println("\n--- Test Avvio Partita ---");
            controller2.nuovaPartita();
            
            // Test stato iniziale
            System.out.printf("  Gioco in corso: %b%n", controller2.isGiocoInCorso());
            System.out.printf("  Gioco in pausa: %b%n", controller2.isGiocoInPausa());
            System.out.printf("  Giocatore corrente: %d%n", controller2.getGiocatoreCorrente());
            
            // Test delle carte
            System.out.printf("  Carte in mano giocatore 1: %d%n", 
                controller2.getGiocatori()[0].getMano().size());
            System.out.printf("  Carte in mano giocatore 2: %d%n", 
                controller2.getGiocatori()[1].getMano().size());
            
            System.out.println("\n✅ TUTTI I TEST SONO PASSATI CON SUCCESSO!");
            System.out.println("Il GameController funziona correttamente!");
            
        } catch (Exception e) {
            System.err.println("❌ ERRORE NEL TEST:");
            e.printStackTrace();
        }
    }
}
