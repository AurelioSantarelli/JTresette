# JTresette! 🃏

Un gioco di Tresette implementato in Java con interfaccia grafica Swing.

## 📋 Descrizione

JTresette! è un'implementazione del classico gioco di carte italiano Tresette per 4 giocatori. Il gioco presenta un'interfaccia grafica intuitiva, effetti sonori e un sistema di statistiche per tracciare le partite.

## ✨ Caratteristiche

- **Interfaccia Grafica**: GUI moderna realizzata con Java Swing
- **Gioco per 4 Giocatori**: Tu vs 3 AI con nomi italiani classici (Marcovaldo, Viligelmo, Astolfo)
- **Sistema Audio**: Effetti sonori per migliorare l'esperienza di gioco
- **Statistiche Giocatore**: Tracciamento automatico di vittorie, sconfitte e percentuali
- **Pattern Observer**: Architettura modulare con sistema di eventi
- **Pausa/Riprendi**: Possibilità di mettere in pausa il gioco
- **Punteggio Personalizzabile**: Configurazione del punteggio di vittoria
- **Log di Gioco**: Sistema di logging dettagliato degli eventi

## 🚀 Come Eseguire

### Prerequisiti
- Java 17 o superiore
- Maven 3.6 o superiore

### Esecuzione
```bash
# Compila il progetto
mvn compile

# Esegui il gioco
mvn exec:java -Dexec.mainClass="it.uniroma1.tresette.MenuIniziale"

# Oppure compila e crea JAR
mvn package
java -jar target/tresette_2-1.0-SNAPSHOT.jar
```

## 🎮 Come Giocare

1. **Avvio**: Inserisci il tuo nome e seleziona il punteggio di vittoria (default 41 punti)
2. **Obiettivo**: Raggiungere per primi il punteggio di vittoria insieme al tuo partner (Viligelmo)
3. **Regole Tresette**: Segui le regole tradizionali del Tresette italiano
4. **Controlli**: 
   - Clicca sulle carte per giocarle
   - Usa il pulsante ⏸/▶ per mettere in pausa
   - "Nuova Partita" per ricominciare
   - "Interrompi Partita" per tornare al menu

## 🏗️ Architettura

Il progetto utilizza diversi pattern di design:

- **Observer Pattern**: Per la gestione degli eventi di gioco
- **MVC Elements**: Separazione logica tra presentazione e business logic
- **Strategy Pattern**: Per l'AI dei giocatori automatici

### Componenti Principali

- `TresetteGame`: Classe principale del gioco
- `MenuIniziale`: Menu di avvio e configurazione
- `StatisticheGiocatore`: Gestione persistente delle statistiche
- `GameStateObserver`: Sistema di notifiche eventi
- `AudioObserver`: Gestione effetti sonori
- `PaletteColori`: Tema colori dell'interfaccia

## 📊 Sistema di Statistiche

Le statistiche dei giocatori vengono salvate automaticamente in file `.properties` nella directory `player_stats/`:
- Partite giocate
- Partite vinte
- Partite perse  
- Percentuale di vittorie

## 🎨 Personalizzazione

- **Colori**: Modifica `PaletteColori.java` per cambiare il tema
- **Suoni**: Sostituisci i file audio in `src/main/resources/sounds/`
- **Immagini**: Aggiorna le carte in `src/main/resources/images/`

## 🛠️ Sviluppo

### Struttura del Progetto
```
src/main/java/it/uniroma1/tresette/
├── TresetteGame.java          # Gioco principale
├── MenuIniziale.java          # Menu di avvio
├── StatisticheGiocatore.java  # Gestione statistiche
├── PaletteColori.java         # Tema colori
├── GameState.java             # Stati del gioco
├── GameStateObserver.java     # Interfaccia Observer
├── GameStateObservable.java   # Classe Observable
├── AudioObserver.java         # Observer audio
├── LoggingObserver.java       # Observer logging
└── DebugObserver.java         # Observer debug

src/main/resources/
├── images/                    # Immagini carte e icone
└── sounds/                    # Effetti sonori
```

### Aggiungere Nuove Funzionalità

1. **Nuovi Observer**: Implementa `GameStateObserver`
2. **Nuovi Suoni**: Aggiungi file WAV in `resources/sounds/`
3. **Nuove Statistiche**: Estendi `StatisticheGiocatore`

## 📝 Cronologia Versioni

- **v1.0**: Versione base con Observer pattern e statistiche
- **Pre-MVC**: Implementazione MVC temporanea (rimossa)
- **Observer Implementation**: Sistema completo di notifiche eventi

## 🤝 Contributi

Questo è un progetto educativo. Le modifiche sono benvenute per:
- Miglioramenti all'interfaccia utente
- Ottimizzazioni delle performance
- Nuove funzionalità di gioco
- Correzioni di bug

## 📜 Licenza

Progetto educativo - Università di Roma "La Sapienza"

## 🙏 Crediti

- **Sviluppo**: Implementazione Java/Swing
- **Design Pattern**: Observer, Strategy
- **Risorse Audio**: Effetti sonori di gioco
- **Grafica**: Carte da gioco tradizionali italiane
