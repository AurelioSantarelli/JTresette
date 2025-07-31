# 🚀 Istruzioni per GitHub Setup

## Passi Successivi per Pubblicare su GitHub

### 1. Crea Repository su GitHub
1. Vai su [GitHub.com](https://github.com)
2. Clicca su "New repository" o il pulsante "+"
3. Nome repository: `jtresette` (o nome a tua scelta)
4. Descrizione: `🃏 JTresette - Italian Tresette card game in Java`
5. **NON** selezionare "Initialize with README" (abbiamo già tutto)
6. Clicca "Create repository"

### 2. Collega Repository Locale a GitHub
```bash
# Aggiungi il remote (sostituisci USERNAME con il tuo username GitHub)
git remote add origin https://github.com/USERNAME/jtresette.git

# Verifica la connessione
git remote -v

# Push del codice e dei tag
git push -u origin main
git push origin --tags
```

### 3. Verifica Upload
Dopo il push, il tuo repository GitHub dovrebbe contenere:
- Codice sorgente completo
- README.md con documentazione
- .gitignore configurato per Java/Maven
- Tag v1.0.0 per la release

## 🔄 Workflow Futuro per Nuove Versioni

### Sviluppo Feature
```bash
# Crea branch per nuova feature
git checkout -b feature/nome-feature

# Sviluppa e testa
# ... modifiche al codice ...

# Commit delle modifiche
git add .
git commit -m "✨ feat: descrizione feature"

# Merge nel main
git checkout main
git merge feature/nome-feature
git branch -d feature/nome-feature
```

### Release Nuova Versione
```bash
# Aggiorna versione nel pom.xml se necessario
# Commit finale
git add .
git commit -m "🔖 prep: prepare for v1.1.0 release"

# Crea tag versione
git tag -a v1.1.0 -m "🚀 Release v1.1.0: descrizione features"

# Push tutto
git push origin main
git push origin --tags
```

### Tipi di Commit (Conventional Commits)
- `feat:` ✨ Nuove feature
- `fix:` 🐛 Bug fix
- `docs:` 📚 Documentazione
- `style:` 💄 Modifiche UI/CSS
- `refactor:` ♻️ Refactoring codice
- `test:` 🧪 Test
- `chore:` 🔧 Manutenzione

## 📋 Template Issues/PR per GitHub

### Issue Template
```markdown
## 🐛 Bug Report / 💡 Feature Request

### Descrizione
Breve descrizione del problema o feature richiesta

### Passi per Riprodurre (per bug)
1. Vai a...
2. Clicca su...
3. Risultato atteso vs attuale

### Informazioni Sistema
- OS: [es. macOS, Windows, Linux]
- Java Version: [es. 17]
- Versione JTresette: [es. v1.0.0]
```

## 🔐 Gestione Accessi

### SSH (Raccomandato per push frequenti)
```bash
# Genera chiave SSH (se non presente)
ssh-keygen -t ed25519 -C "tua-email@example.com"

# Aggiungi chiave a GitHub
cat ~/.ssh/id_ed25519.pub
# Copia output e aggiungilo su GitHub > Settings > SSH Keys

# Cambia remote da HTTPS a SSH
git remote set-url origin git@github.com:USERNAME/jtresette.git
```

## 📊 Monitoraggio Repository

### Branches Raccomandati
- `main`: Codice stabile e rilasciato
- `develop`: Codice in sviluppo
- `feature/*`: Feature specifiche
- `hotfix/*`: Fix urgenti

### GitHub Actions (Opzionale)
Considera l'aggiunta di CI/CD per:
- Test automatici su push
- Build automatico delle release
- Controllo qualità codice

## 🎯 Best Practices

1. **Commit Frequenti**: Piccoli commit logici
2. **Messaggi Chiari**: Descrivere il "cosa" e "perché"
3. **Branching**: Usa branch per feature/fix
4. **Tagging**: Versiona le release
5. **Documentation**: Mantieni README aggiornato
6. **.gitignore**: Non includere file generati/temporanei

---

**Importante**: Sostituisci `USERNAME` con il tuo vero username GitHub nei comandi sopra!
