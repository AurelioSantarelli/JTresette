# CHECKLIST GIORNALIERA - STUDIO PROGETTO TRESETTE

## 📅 GIORNO 1: OVERVIEW E ORIENTAMENTO (2 ore)

### ⏰ Sessione Mattina/Sera (2 ore totali)

#### 🔍 Comprensione del Dominio (45 min)
- [ ] **Regole Tresette** (15 min)
  - [ ] Letto regole complete su Wikipedia/fonti
  - [ ] Capito modalità 2 vs 4 giocatori
  - [ ] Compreso sistema punteggio
  - [ ] **Autotest**: Scritte 10 domande sulle regole e risposte

- [ ] **Requisiti Progetto** (15 min)  
  - [ ] Letta sezione "Analisi Requisiti" della relazione
  - [ ] Creata mappa mentale requisiti
  - [ ] Distinti must-have vs nice-to-have

- [ ] **Demo Live** (15 min)
  - [ ] Avviata applicazione: `mvn exec:java -Dexec.mainClass=it.uniroma1.tresette.view.windows.Tresette`
  - [ ] Giocata partita completa 4 giocatori
  - [ ] Testate funzionalità: pausa, audio, menu
  - [ ] **Note**: Annotazioni complete del gameplay

#### 🗂️ Struttura Progetto (45 min)
- [ ] **Directory Exploration** (15 min)
  - [ ] Eseguiti comandi: `find src/ -name "*.java" | head -20`
  - [ ] Contate classi per package
  - [ ] Identificati file principali (>200 linee)
  - [ ] **Output**: Mappa struttura progetto

- [ ] **Build e Dipendenze** (15 min)
  - [ ] Analizzato `pom.xml`
  - [ ] Eseguito `mvn dependency:tree`
  - [ ] Compilato con `mvn clean compile`
  - [ ] **Risposte**: Dipendenze esterne, perché Maven, build process

- [ ] **Documentazione** (15 min)
  - [ ] Letto README.md
  - [ ] Generato Javadoc: `mvn javadoc:javadoc`
  - [ ] Overview relazione tecnica
  - [ ] **Output**: 5 domande non chiare, schema progetto

#### ✅ CHECKPOINT GIORNO 1
- [ ] So spiegare regole Tresette in 3 minuti
- [ ] Navigo progetto senza perdermi
- [ ] Ho identificato 5 file Java più importanti
- [ ] Applicazione funziona perfettamente

**Se manca qualche ✅ → Rivedi quella sezione prima di continuare**

---

## 📅 GIORNO 2: ARCHITETTURA E DESIGN (3 ore)

### ⏰ Sessione 1: Pattern MVC (60 min)

#### 🏗️ Componenti MVC (20 min)
- [ ] **Controller**: Letto `GameController.java` completo
- [ ] **View**: Letto `Tresette.java` (main window)  
- [ ] **Model**: Esplorato package `model/`
- [ ] **Schema creato**:
  ```
  MODEL: [Lista classi] → Cosa rappresentano
  VIEW: [Lista classi] → Come mostrano i dati
  CONTROLLER: [Lista classi] → Come gestiscono logica
  ```

#### 🔄 Flusso Comunicazione (20 min)
- [ ] **Tracciato flusso**: Click carta → Risposta UI
  - [ ] `Tresette.java` → cattura click
  - [ ] `GameController.java` → processa azione  
  - [ ] `Model classes` → aggiornano stato
  - [ ] `View` → riflette cambiamenti
- [ ] **Diagramma disegnato** del flusso

#### 💡 Vantaggi MVC (20 min)
- [ ] **Risposto a domande**:
  - [ ] Cambio UI Swing→JavaFX: cosa modifichierei?
  - [ ] Nuova regola gioco: dove la aggiungerei?
  - [ ] Testing controller: perché è facile?
- [ ] **Scritte risposte** 2-3 frasi per domanda

### ⏰ Sessione 2: Observer Pattern (45 min)

#### 👁️ Implementazione Observer (25 min)
- [ ] **File studiati**:
  - [ ] `GameStateObservable.java` (Subject)
  - [ ] `AudioObserver.java` (Observer concreto)  
  - [ ] `GameController.java` (uso observer)
- [ ] **Catalogazione**: 
  - [ ] Contati observer diversi
  - [ ] Per ogni observer: cosa osserva + cosa fa quando notificato
  - [ ] Disegnato pattern Observer per progetto

#### 🔗 Vantaggi Disaccoppiamento (20 min)
- [ ] **Scenario test**: StatisticsObserver immaginario
  - [ ] Dove lo aggiungerei?
  - [ ] Quali classi da modificare?
  - [ ] Come si integrerebbe?
- [ ] **Scritto pseudocodice** per nuovo observer

### ⏰ Sessione 3: Altri Pattern (75 min)

#### 🏭 Factory Pattern (25 min)
- [ ] **File analizzati**: `UIComponentFactory.java`, `IconFactory.java`
- [ ] **Identificati** tutti metodi factory
- [ ] **Compreso** perché meglio di creazione diretta oggetti
- [ ] **Coding exercise**: Scritto factory method per carte

#### 🎯 Strategy Pattern (25 min)  
- [ ] **Analizzato**: `AIPlayer.java` - strategie diverse
- [ ] **Tracciato strategie**:
  - [ ] Primo turno → Quale strategia?
  - [ ] Può vincere → Quale strategia?  
  - [ ] Deve perdere → Quale strategia?

#### 🏠 Singleton Pattern (25 min)
- [ ] **File studiato**: `SoundManager.java` + altri singleton
- [ ] **Domande critiche risposte**:
  - [ ] Perché singleton per audio?
  - [ ] Rischi pattern Singleton?
  - [ ] Come testare classe Singleton?

#### ✅ CHECKPOINT GIORNO 2  
- [ ] Disegnata da memoria architettura MVC
- [ ] Spiegato in 2 min Observer pattern  
- [ ] Identificati tutti pattern + loro scopo
- [ ] Critica costruttiva: cosa migliorerei nell'architettura

---

## 📅 GIORNO 3: IMPLEMENTAZIONE CORE (4 ore)

### ⏰ Sessione 1: GameController Deep Dive (90 min)

#### 🔍 Anatomia Controller (30 min)
- [ ] **Analizzato metodi pubblici**: `grep -n "public.*(" GameController.java`
- [ ] **Per ogni metodo pubblico annotato**:
  - [ ] Cosa fa?
  - [ ] Chi lo chiama?
  - [ ] Cosa modifica?  
  - [ ] Quali observer notifica?

#### ⚙️ Manager Specializzati (30 min)
- [ ] **GameStateManager**:
  - [ ] Stati gestiti identificati
  - [ ] Capito come garantisce consistenza
  - [ ] Compreso perché synchronized

- [ ] **DeckManager**:
  - [ ] Algoritmo distribuzione carte
  - [ ] Gestione mescolamento
  - [ ] Logica distribuzione

- [ ] **ScoreCalculator**:  
  - [ ] Logica calcolo punti
  - [ ] Determinazione vincitore mano
  - [ ] Gestione casi speciali

- [ ] **TurnManager**:
  - [ ] Rotazione turni
  - [ ] Modalità 2 vs 4 giocatori
  - [ ] Stati di attesa

#### 🔄 Coordinazione Manager (30 min)
- [ ] **Seguita partita completa nel codice**:
  - [ ] Inizializzazione gioco
  - [ ] Distribuzione carte  
  - [ ] Giocata mano completa
  - [ ] Calcolo punteggi
  - [ ] Determinazione vincitore
- [ ] **Creato diagramma sequenza** (semplificato)

### ⏰ Sessione 2: Intelligenza Artificiale (75 min)

#### 🧠 Algoritmo AI Principale (35 min)
- [ ] **File `AIPlayer.java` - Ogni metodo analizzato**:
  - [ ] `selezionaCarta()` → Strategia principale
  - [ ] `selezionaCartaPrimaTurno()` → Apertura
  - [ ] `selezionaCartaConSeme()` → Risposta seme richiesto

- [ ] **Per ogni strategia definito**:
  - [ ] Input: Cosa considera?
  - [ ] Logica: Come decide?
  - [ ] Output: Perché quella carta?

#### ⚖️ Bilanciamento AI (25 min)
- [ ] **Test AI eseguito**: 3 partite giocate
- [ ] **Annotate**: Mosse "stupide" o "troppo furbe"
- [ ] **Trovata nel codice** logica di quelle mosse  
- [ ] **Valutato**: AI è bilanciata?

#### 🚀 Miglioramenti Possibili (15 min)
- [ ] **Brainstorming fatto**:
  - [ ] Come rendere AI più intelligente?
  - [ ] Come implementare livelli difficoltà?
  - [ ] Quali info aggiuntive darle?

### ⏰ Sessione 3: Gestione dello Stato (75 min)

#### 🔄 Ciclo Vita Gioco (25 min)
- [ ] **Stati mappati**: `MENU → DISTRIBUZIONE → GIOCO → VALUTAZIONE → FINE`
- [ ] **Per ogni stato definito**:
  - [ ] Quando si entra?
  - [ ] Cosa è permesso fare?
  - [ ] Come si esce?
  - [ ] Quali dati sono validi?

#### 🔄 Sincronizzazione UI-Model (25 min)  
- [ ] **Tracciati aggiornamenti**:
  - [ ] Giocata carta → Aggiornamento UI
  - [ ] Cambio turno → Indicatori UI
  - [ ] Fine mano → Popup punteggi
  - [ ] Fine partita → Schermata finale

- [ ] **Domande risposte**:
  - [ ] Se UI lenta nell'aggiornare?
  - [ ] Come gestita concorrenza?

#### ❌ Gestione Errori (25 min)
- [ ] **Cercato nel codice**:
  - [ ] Validazioni input: dove e come?
  - [ ] Exception handling: cosa catturato?
  - [ ] Recovery: come riprende da errori?

- [ ] **Test errori fatto**:
  - [ ] Cliccate carte non giocabili
  - [ ] Testati edge cases

#### ✅ CHECKPOINT GIORNO 3
- [ ] Spiegato passo-passo cosa succede giocando carta
- [ ] Descritto algoritmo AI in pseudocodice  
- [ ] Tracciato flusso da click utente a aggiornamento UI
- [ ] Identificati 3 possibili bug e dove potrebbero verificarsi

---

## 📅 GIORNO 4: PATTERN E BEST PRACTICES (3 ore)

### ⏰ Sessione 1: Principi SOLID (75 min)

#### 📝 Single Responsibility Principle (15 min)
- [ ] **Analizzate classi principali**:
  - [ ] `GameController` → Una sola responsabilità?
  - [ ] `DeckManager` → Cosa fa esattamente?
  - [ ] `ScoreCalculator` → Perché separato?
- [ ] **Esercizio**: Trovata classe che viola SRP + proposto refactoring

#### 🔓 Open/Closed Principle (15 min)
- [ ] **Scenari estensione valutati**:
  - [ ] Nuova modalità gioco
  - [ ] Cambio regole punteggio
  - [ ] Nuovo tipo AI
- [ ] **Per ogni scenario**: Parti da modificare vs estendere identificate

#### 🔄 Liskov Substitution Principle (15 min)
- [ ] **Verificate gerarchie**:
  - [ ] Ereditarietà nel progetto?
  - [ ] Oggetti derivati sostituibili?
  - [ ] Interfacce rispettate?

#### 🧩 Interface Segregation Principle (15 min)
- [ ] **Analizzate interfacce**:
  - [ ] `GameView` interface → Troppo grande?
  - [ ] Observer interfaces → Ben segmentate?
  - [ ] Dependency interfaces → Appropriate?

#### 🔄 Dependency Inversion Principle (15 min)
- [ ] **Dipendenze analizzate**:
  - [ ] GameController dipende da implementazioni concrete?
  - [ ] Come iniettate dipendenze?
  - [ ] Se cambio implementazione UI?

### ⏰ Sessione 2: Code Quality Analysis (60 min)

#### 📊 Metriche Codice (20 min)
- [ ] **Calcolate metriche**:
  - [ ] `find src/ -name "*.java" -exec wc -l {} + | sort -n`
  - [ ] Complessità metodi (branch points)
  - [ ] Ratio commenti vs codice
  
- [ ] **Domande risposte**:
  - [ ] Classi più complesse?
  - [ ] Distribuzione linee bilanciata?
  - [ ] Documentazione sufficiente?

#### 👃 Code Smells Detection (20 min)
- [ ] **Cercati problemi**:
  - [ ] Metodi troppo lunghi (>20 linee)
  - [ ] Classi troppo grandi (>300 linee)
  - [ ] Parametri eccessivi (>4)
  - [ ] Codice duplicato
  - [ ] Commenti obsoleti
- [ ] **Per ogni problema**: Soluzione proposta

#### ✅ Best Practices Verification (20 min)
- [ ] **Checklist qualità**:
  - [ ] Nomi variabili/metodi descrittivi
  - [ ] Costanti vs magic numbers
  - [ ] Exception handling appropriato
  - [ ] Resource management corretto
  - [ ] Thread safety dove necessaria

### ⏰ Sessione 3: Design Patterns Advanced (45 min)

#### 🏭 Pattern Creazionali (15 min)
- [ ] **Identificati e analizzati**:
  - [ ] Factory methods utilizzati
  - [ ] Builder pattern (se presente)
  - [ ] Singleton implementations
- [ ] **Domanda**: Dove aggiungere altri pattern creazionali?

#### 🏗️ Pattern Strutturali (15 min)  
- [ ] **Cercati**:
  - [ ] Adapter pattern (UI adapters?)
  - [ ] Facade pattern (librerie esterne?)
  - [ ] Decorator pattern (UI components?)

#### 🎭 Pattern Comportamentali (15 min)
- [ ] **Analizzati**:
  - [ ] Observer (già studiato)
  - [ ] Strategy (AI behavior)  
  - [ ] Command pattern (user actions?)
  - [ ] State pattern (game states?)
- [ ] **Esercizio**: Disegnato class diagram pattern principali

#### ✅ CHECKPOINT GIORNO 4
- [ ] Giudicata architettura (voto 1-10 + motivazione)
- [ ] Proposti 3 miglioramenti concreti con giustificazione
- [ ] Identificato pattern più importante + spiegazione
- [ ] Scritta code review 200 parole sul progetto

---

## 📅 GIORNO 5: TESTING E QUALITÀ (2 ore)

### ⏰ Sessione 1: Analisi Testing Esistente (45 min)

#### 🔍 Test Discovery (15 min)
- [ ] **Esplorati test**:
  - [ ] `find . -path "*/test/*" -name "*.java"`
  - [ ] `mvn test` eseguito
  - [ ] Test specifici provati
- [ ] **Catalogazione**:
  - [ ] Numero test contati
  - [ ] Tipi test identificati (unit, integration, UI)
  - [ ] Coverage approssimativa stimata

#### 🎯 Test Quality Analysis (15 min)
- [ ] **Per ogni test analizzato**:
  - [ ] Cosa testa? (functionality)
  - [ ] Come testa? (methodology)  
  - [ ] È un good test? (AAA pattern, isolamento)

#### 📋 Gap Analysis (15 min)
- [ ] **Cosa NON testato identificato**:
  - [ ] Componenti senza test
  - [ ] Edge cases non coperti
  - [ ] Error conditions non verificate
  - [ ] Integration points critici
- [ ] **Prioritizzati**: Test da aggiungere per primi

### ⏰ Sessione 2: Testabilità del Design (30 min)

#### 🎯 Design for Testability (15 min)
- [ ] **Analizzata architettura**:
  - [ ] GameController facilmente testabile?
  - [ ] Dipendenze mockabili?
  - [ ] Stati ispezionabili?
  - [ ] Side effects controllabili?

#### 🚧 Testing Challenges (15 min)
- [ ] **Identificate sfide**:
  - [ ] UI Testing: come testare Swing?
  - [ ] AI Testing: come verificare intelligenza?
  - [ ] Timing Testing: aspetti temporali?
  - [ ] Audio Testing: sound system?
- [ ] **Per ogni sfida**: Strategia testing proposta

### ⏰ Sessione 3: Quality Metrics (45 min)

#### 📚 Javadoc Quality (15 min)
- [ ] **Verificata documentazione**:
  - [ ] `mvn javadoc:javadoc` eseguito
  - [ ] Warnings/errors controllati
  - [ ] Sample HTML files aperti
- [ ] **Valutazione**:
  - [ ] Completezza commenti
  - [ ] Qualità descrizioni
  - [ ] Esempi d'uso
  - [ ] Tag appropriati

#### 📊 Code Coverage (15 min)
- [ ] **Stima manuale**:
  - [ ] 3 classi principali prese
  - [ ] Branch/path identificati
  - [ ] Coverage verificata
  - [ ] Percentuale calcolata
- [ ] **Domanda**: Target coverage per progetto?

#### ⚡ Performance Analysis (15 min)
- [ ] **Misurate performance**:
  - [ ] Tempo avvio: `time mvn exec:java...`
  - [ ] Memory profiling basic
- [ ] **Bottleneck identification**:
  - [ ] Operazioni più costose
  - [ ] Memory leaks potenziali  
  - [ ] UI responsiveness issues

#### ✅ CHECKPOINT GIORNO 5
- [ ] Stimata qualità complessiva codice (1-10)
- [ ] Identificati 3 rischi principali manutenibilità
- [ ] Proposta strategia per aumentare test coverage
- [ ] Scritto piano monitoraggio qualità nel tempo

---

## 📅 GIORNO 6: PREPARAZIONE FINALE (2 ore)

### ⏰ Sessione 1: Synthesis & Integration (60 min)

#### 🧠 Mind Map del Progetto (20 min)  
- [ ] **Creata mappa mentale completa**:
  ```
  TRESETTE PROJECT
      |
  ARCHITECTURE - IMPLEMENTATION - QUALITY
      |              |              |
  [Pattern]      [Components]    [Testing]
  [Principles]   [Algorithms]    [Metrics]  
  [Design]       [Features]      [Docs]
  ```
- [ ] **Espanso ogni ramo** con dettagli specifici

#### ⚠️ Critical Path Analysis (20 min)
- [ ] **Identificati componenti critici**:
  - [ ] Se si rompe X, cosa succede?
  - [ ] Single point of failure?
  - [ ] Dove concentrati i rischi?

#### 📖 Evolution Story (20 min)
- [ ] **Scritta storia evoluzione**:
  - [ ] Problema iniziale (Controller monolitico)
  - [ ] Decisioni architetturali  
  - [ ] Refactoring implementati
  - [ ] Risultati ottenuti
  - [ ] Lezioni apprese
- [ ] **Storia in 300 parole** per il professore

### ⏰ Sessione 2: Q&A Preparation (60 min)

#### 🎯 Domande Tecniche Probabili (30 min)
- [ ] **Risposte dettagliate preparate**:

**Architettura:**
- [ ] "Perché MVC invece di MVP o MVVM?"
- [ ] "Come gestisci comunicazione View-Controller?"  
- [ ] "Quali pattern implementati e perché?"

**Implementazione:**
- [ ] "Come funziona algoritmo AI?"
- [ ] "Come risolto problema concorrenza?"
- [ ] "Perché Swing invece JavaFX?"

**Qualità:**
- [ ] "Come garantita qualità codice?"
- [ ] "Quali metriche utilizzate?"
- [ ] "Come approcceresti testing sistema?"

#### 😈 Domande Critiche/Provocatorie (15 min)
- [ ] **Preparato a rispondere**:
  - [ ] "Design over-engineered per semplice gioco?"
  - [ ] "Perché non database per statistiche?"
  - [ ] "AI troppo semplice?"
  - [ ] "Come scala per 1000 utenti simultanei?"

#### 🤔 Domande su Scelte Alternative (15 min)  
- [ ] **"Cosa diversamente se..." preparato**:
  - [ ] "...multiplayer online?"
  - [ ] "...target mobile?"
  - [ ] "...più tempo?"
  - [ ] "...ricominciare da capo?"

### ⏰ CHECKPOINT FINALE: Autotest Completo (80 min)

#### 📝 Test di Conoscenza (30 min) - SENZA GUARDARE CODICE
- [ ] **Elencati** tutti design pattern utilizzati
- [ ] **Disegnata** architettura MVC sistema
- [ ] **Descritto** algoritmo AI in pseudocodice  
- [ ] **Spiegato** come funziona Observer pattern
- [ ] **Identificati** componenti principali + responsabilità

#### 🧠 Test di Comprensione (30 min) - SCENARI IPOTETICI
- [ ] **Undo system**: Come implementeresti?
- [ ] **Test AI**: Come verificare rispetto regole?
- [ ] **Varianti Tresette**: Cosa cambieresti?
- [ ] **Performance**: Come ottimizzare per dispositivi lenti?

#### 🎤 Presentazione Mock (20 min) - SIMULAZIONE
- [ ] **5 min**: Overview progetto
- [ ] **10 min**: Deep dive tecnico  
- [ ] **5 min**: Q&A simulation

#### 🎉 RISULTATO FINALE
**Se hai completato tutto senza bloccarti → SEI PRONTO! 🎓**

---

## 📋 CHECKLIST MATERIALI FINALI

### 📚 Documenti Pronti
- [ ] **Relazione Universitaria** (28 pagine) stampata/PDF
- [ ] **Slide Presentazione** (16 slide) in HTML/PowerPoint
- [ ] **Codice sorgente** accessibile e funzionante
- [ ] **Javadoc generata** consultabile
- [ ] **Note personali** per presentazione

### 💻 Setup Tecnico  
- [ ] **Applicazione testata** e funzionante al 100%
- [ ] **Backup progetto** su USB/cloud
- [ ] **IDE configurato** per mostrare codice
- [ ] **Demo preparata** e provata
- [ ] **Proiettore/schermo** testato

### 🧠 Preparazione Mentale
- [ ] **Timeline rispettata** (20 minuti presentazione)
- [ ] **Transizioni fluide** tra slide provate
- [ ] **Collegamenti teoria-pratica** chiari
- [ ] **Risposte Q&A** memorizzate
- [ ] **Backup plan** se qualcosa va storto

### 🏆 CONFIDENZA
- [ ] **Conosco ogni componente** del mio progetto
- [ ] **So giustificare ogni scelta** architettuale
- [ ] **Posso rispondere domande avanzate** sui pattern
- [ ] **Ho alternative e miglioramenti** pronti
- [ ] **Sono sicuro della qualità** del mio lavoro

## 🎯 OBIETTIVO RAGGIUNTO!
**Hai una padronanza completa del tuo progetto Tresette. Sei pronto per impressionare il professore e dimostrare le tue competenze ingegneristiche! 🚀**

*Buona fortuna con la presentazione! 🍀*
