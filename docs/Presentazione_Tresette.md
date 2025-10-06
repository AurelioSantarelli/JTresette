# PRESENTAZIONE PROGETTO TRESETTE
**Implementazione di un Gioco di Carte Digitale in Java**

**Studente**: Aurelio Santarelli  
**Corso**: [Nome del Corso]  
**Anno Accademico**: 2024-2025

# SLIDE 1: INTRODUZIONE AL PROGETTO

## Obiettivo
Sviluppare una **versione digitale completa** del gioco tradizionale italiano **Tresette** utilizzando Java e Swing.

## Motivazioni
- **Complessità bilanciata**: Regole sufficientemente articolate per una progettazione significativa
- **Dominio familiare**: Regole ben definite del gioco tradizionale
- **Sfide tecniche diverse**: AI, interfaccia grafica, gestione stato, multimedia
- **Scalabilità**: Architettura estendibile a modalità diverse

## Risultati Attesi
✅ Applicazione desktop completa e stabile  
✅ Implementazione fedele delle regole del Tresette  
✅ Intelligenza artificiale competitiva  
✅ Interfaccia utente intuitiva e responsive

---

# SLIDE 2: ANALISI DEI REQUISITI

## Requisiti Funzionali Principali
### RF01 - Gestione del Gioco
- Modalità 2 e 4 giocatori (1 umano + AI)
- Distribuzione automatica delle carte
- Calcolo accurato dei punteggi

### RF02 - Interfaccia Utente
- Visualizzazione chiara delle carte del giocatore
- Feedback visivo per azioni consentite
- Aggiornamento real-time dei punteggi

### RF03 - Intelligenza Artificiale
- Rispetto delle regole del gioco
- Strategie competitive ma non frustranti
- Timing realistico per le mosse

## Requisiti Non Funzionali
- **Performance**: Tempo di risposta < 100ms
- **Usabilità**: Interfaccia intuitiva per tutte le età
- **Manutenibilità**: Codice ben documentato e modulare
- **Portabilità**: Compatibilità multipiattaforma Java

---

# SLIDE 3: ARCHITETTURA DEL SISTEMA

## Pattern Architetturale: MVC + Observer

```
VIEW LAYER (UI)
    ↕ Observer Pattern
CONTROLLER LAYER (Business Logic)
    ↕
MODEL LAYER (Data & Entities)
```

## Principi SOLID Applicati
- **SRP**: Ogni classe ha una responsabilità singola e ben definita
- **OCP**: Sistema aperto per estensioni, chiuso per modifiche
- **DIP**: Dipendenze verso astrazioni, non implementazioni concrete

## Decomposizione in Package
```java
it.uniroma1.tresette/
├── model/          // Entità di dominio
├── controller/     // Logica di controllo  
├── view/          // Interfaccia utente
└── util/          // Utility generali
```

---

# SLIDE 4: COMPONENTI CHIAVE - CONTROLLER

## GameController: Il Coordinatore Centrale

```java
public class GameController {
    // Componenti specializzati
    private final GameStateManager gameState;
    private final DeckManager deckManager;
    private final ScoreCalculator scoreCalculator;
    private final TurnManager turnManager;
    private final AIPlayer aiPlayer;
}
```

## Evoluzione Architetturale
**Prima**: Controller monolitico (809 linee)
- Tutte le responsabilità in una classe
- Difficile da testare e manutenere

**Dopo**: Controller modulare (495 linee) 
- Responsabilità distribuite in manager specializzati
- Migliore testabilità e manutenibilità
- **Complessità ciclomatica ridotta da 15 a 6**

---

# SLIDE 5: GESTIONE DELLO STATO

## GameStateManager: Centralizzazione dello Stato

```java
public class GameStateManager {
    // Stati booleani del gioco
    private boolean giocoInCorso, giocoInPausa;
    
    // Contatori e indici
    private int giocatoreCorrente, mano, giocata;
    
    // Punteggi
    private double punteggioCoppia1, punteggioCoppia2;
    
    // Operazioni atomiche
    public synchronized void avanzaTurno() {
        giocatoreCorrente = (giocatoreCorrente + 1) % numeroGiocatori;
        carteGiocateInMano++;
    }
}
```

## Vantaggi Ottenuti
- **Consistenza**: Stato sempre coerente
- **Thread-safety**: Operazioni sincronizzate
- **Debug facilitato**: Stato centralizzato e ispezionabile
- **Transazioni atomiche**: Modifiche multiple coerenti

---

# SLIDE 6: INTELLIGENZA ARTIFICIALE

## Strategia AI Multi-livello

```java
public int selezionaCarta(Giocatore giocatore, Seme semeRichiesto, 
                         List<Carta> carteGiocate) {
    if (semeRichiesto == null) {
        return selezionaCartaPrimaTurno(mano);  // Apertura strategica
    } else if (puoVincereMano(semeRichiesto)) {
        return selezionaCartaVincente();        // Gioca per vincere
    } else {
        return selezionaCartaMinima();          // Conserva risorse
    }
}
```

## Caratteristiche dell'AI
- **Analisi del contesto**: Considera seme richiesto e carte già giocate
- **Gestione delle risorse**: Conserva carte forti quando appropriato
- **Esecuzione non bloccante**: Timer per realismo senza bloccare UI
- **Strategia bilanciata**: Competitiva ma non frustrante

---

# SLIDE 7: PATTERN UTILIZZATI

## 1. Observer Pattern - Comunicazione Asincrona
```java
public class GameStateObservable {
    public void notifyCartaGiocata(Carta carta, String giocatore) {
        observers.forEach(obs -> obs.onCartaGiocata(carta, giocatore));
    }
}
```

## 2. Factory Pattern - Creazione Standardizzata
```java
public class UIComponentFactory {
    public static JButton createStyledButton(String text, Color color) {
        // Creazione componente con styling consistente
    }
}
```

## 3. Strategy Pattern (Implicito) - AI Comportamentale
```java
// Diverse strategie in base al contesto
if (primoTurno) return strategiaApertura();
else if (puoVincere) return strategiaOffensiva();  
else return strategiaDifensiva();
```

## 4. Singleton Pattern - Gestione Risorse
```java
public class SoundManager {
    private static SoundManager instance;
    // Gestione centralizzata dell'audio
}
```

---

# SLIDE 8: GESTIONE DELLE SFIDE TECNICHE

## Sfida #1: AI Non Bloccante
**Problema**: L'AI deve "pensare" senza bloccare l'interfaccia

**Soluzione**: 
```java
Timer timer = new Timer(AI_DELAY, e -> {
    int scelta = aiPlayer.selezionaCarta(giocatore);
    SwingUtilities.invokeLater(() -> executeMove(scelta));
});
```

## Sfida #2: Ottimizzazione Risorse Grafiche
**Problema**: 40+ immagini da caricare efficientemente

**Soluzione**: Cache intelligente
```java
private static final Map<String, BufferedImage> CACHE = 
    new ConcurrentHashMap<>();
```

## Sfida #3: Sincronizzazione Vista-Modello
**Problema**: UI sincronizzata senza accoppiamento forte

**Soluzione**: Observer Pattern per aggiornamenti automatici

---

# SLIDE 9: METRICHE E RISULTATI

## Metriche del Codice
- **Linee di codice**: ~3,500 LOC
- **Numero di classi**: 31 classi ben organizzate
- **Complessità ciclomatica**: Media 4.2 (ottimo)
- **Copertura Javadoc**: 100% (0 warning)

## Performance Ottenute
- **Avvio applicazione**: ~2.5 secondi
- **Tempo risposta UI**: <50ms
- **Memoria utilizzata**: ~180MB (target <256MB)
- **Calcolo AI**: ~800ms (con timer realistico)

## Qualità Assurance
✅ **Zero crash** nei test estesi  
✅ **UI sempre responsiva**  
✅ **Tutte le funzionalità implementate**  
✅ **Codice completamente documentato**

---

# SLIDE 10: TESTING E VALIDAZIONE

## Strategia di Testing Multi-livello

### Unit Testing
```java
@Test
public void testCalcoloPuntiCarta() {
    Carta asso = new Carta(1, Seme.COPPE);
    assertEquals(1.0/3.0, asso.getPunti(), 0.001);
}
```

### Integration Testing
- Test delle interazioni GameController ↔ Manager
- Validazione flusso completo di una mano
- Verifica propagazione eventi Observer

### Manual Testing
- Test UI su diverse risoluzioni
- Verifica user experience
- Test di performance con partite lunghe

## Bug Principali Risolti
1. **Concurrency**: AI bloccava UI → Timer asincroni
2. **Memory Leak**: Immagini ricaricate → Cache statica
3. **State Sync**: Stati inconsistenti → GameStateManager centralizzato

---

# SLIDE 11: TECNOLOGIE E TOOLS

## Stack Tecnologico

### Java 17 LTS
- **Portabilità**: "Write once, run anywhere"
- **Stabilità**: Supporto a lungo termine
- **Performance**: JVM ottimizzata
- **Ecosistema maturo**: Librerie consolidate

### Java Swing
- **Nativo**: Nessuna dipendenza esterna
- **Maturo**: Comportamenti prevedibili
- **Controllo completo**: Personalizzazione totale UI
- **Multipiattaforma**: Look & Feel nativo

### Maven
- **Gestione dipendenze**: Risoluzione automatica
- **Build standardizzato**: Ciclo di vita definito
- **Plugin ecosystem**: Javadoc, testing, packaging

## Tools di Sviluppo
- **IDE**: IntelliJ IDEA per produttività
- **VCS**: Git per version control
- **Documentation**: Javadoc per API docs

---

# SLIDE 12: LEZIONI APPRESE

## Competenze Acquisite

### Tecniche
- **Architettura software**: Sistemi modulari e scalabili
- **Design Patterns**: Applicazione pratica di pattern consolidati
- **Java avanzato**: Concorrenza, generics, collections
- **UI Design**: Interfacce responsive con Swing

### Metodologiche  
- **Problem solving**: Decomposizione problemi complessi
- **Testing**: Strategie di quality assurance
- **Documentazione**: Technical writing efficace
- **Performance optimization**: Ottimizzazione sistematica

## Insight Principali

1. **Progettazione upfront**: Tempo investito in architettura paga sempre
2. **Separation of concerns**: MVC facilita manutenibilità enormemente  
3. **Observer pattern**: Eccellente per disaccoppiamento UI/Logic
4. **Documentazione**: Javadoc completo è investimento che ripaga

---

# SLIDE 13: SVILUPPI FUTURI

## Estensioni a Breve Termine

### 1. Miglioramenti AI
- **Livelli di difficoltà**: Easy/Medium/Hard selezionabili
- **Strategie personalizzate**: Comportamenti AI diversi
- **Apprendimento**: AI che si adatta allo stile del giocatore

### 2. Personalizzazione UI
- **Temi grafici**: Skin e colori alternativi
- **Carte personalizzate**: Set di carte diversi
- **Configurabilità**: Opzioni utente estese

### 3. Features Aggiuntive
- **Statistiche avanzate**: Analytics dettagliate partite
- **Replay system**: Rivedere partite precedenti
- **Tutorial interattivo**: Guida per nuovi giocatori

## Estensioni a Lungo Termine

### 1. Multiplayer Online
- **Architettura client-server**: Gioco in rete
- **Matchmaking**: Sistema di pairing giocatori
- **Chat integrata**: Comunicazione tra giocatori

### 2. Mobile Version
- **Android port**: Riutilizzo classi model esistenti
- **Touch interface**: UI ottimizzata per touch
- **Cross-platform sync**: Statistiche condivise

---

# SLIDE 14: DIMOSTRAZIONE LIVE

## Demo delle Funzionalità Principali

### 1. Avvio e Menu Principale
- Selezione modalità di gioco
- Configurazione partita

### 2. Gameplay Core
- Giocata di una mano completa
- Interazione con AI
- Calcolo punteggi automatico

### 3. Features Avanzate
- Sistema di pausa/ripresa
- Audio feedback
- Gestione fine partita

### 4. Aspetti Tecnici
- Responsività interfaccia
- Timing realistico AI
- Aggiornamenti real-time UI

---

# SLIDE 15: CONCLUSIONI

## Obiettivi Raggiunti ✅

### Funzionali
- ✅ **Gioco completo**: Tutte le regole del Tresette implementate
- ✅ **AI competitiva**: Intelligenza artificiale bilanciata e divertente
- ✅ **UI/UX eccellente**: Interfaccia intuitiva e responsive
- ✅ **Performance ottimali**: Tutti i target di performance raggiunti

### Tecnici
- ✅ **Architettura solida**: Pattern appropriati e codice manutenibile
- ✅ **Qualità alta**: Zero warning, documentazione completa
- ✅ **Estensibilità**: Base per sviluppi futuri
- ✅ **Stabilità**: Testing approfondito e bug fixing

## Valore del Progetto

### Didattico
- **Applicazione pratica** di concetti teorici OOP
- **Esperienza completa** di sviluppo software professionale
- **Best practices** implementate concretamente

### Tecnico
- **Software funzionante** e utilizzabile
- **Codebase di qualità** per portfolio
- **Base solida** per estensioni future

---

# SLIDE 16: Q&A

## Domande Frequenti Attese

### "Perché Java Swing invece di JavaFX?"
- **Stabilità**: Swing è maturo e prevedibile
- **Semplicità**: Meno complessità per questo progetto
- **Compatibilità**: Funziona ovunque ci sia Java

### "Come gestite la complessità dell'AI?"
- **Strategia a livelli**: Logica semplice ma efficace
- **Separazione responsabilità**: AIPlayer dedicato
- **Testing empirico**: Bilanciamento attraverso test reali

### "Scalabilità dell'architettura?"
- **Pattern modulari**: Facile aggiungere nuovi componenti
- **Interfacce chiare**: Implementazioni sostituibili
- **Observer pattern**: Nuove funzionalità senza impatto

### "Performance con molte partite?"
- **Cache intelligente**: Risorse riutilizzate
- **Garbage collection**: Gestione memoria ottimizzata  
- **Profiling**: Ottimizzazioni basate su misurazioni

---

## Grazie per l'Attenzione!

### Contatti
- **Email**: [tua-email@università.it]
- **Progetto**: https://github.com/[username]/Tresette

### Materiali
- **Codice sorgente**: Completamente documentato
- **Relazione tecnica**: 28 pagine di analisi dettagliata  
- **Javadoc**: API documentation completa
- **Questa presentazione**: Slide disponibili

**Domande?**
