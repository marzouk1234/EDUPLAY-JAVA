# Chatbot AI Web (Flask)

## Installation

1. Placez-vous dans le dossier `web_chatbot` :
   ```bash
   cd web_chatbot
   ```
2. Installez les dépendances :
   ```bash
   pip install -r requirements.txt
   ```

## Lancement

```bash
python app.py
```

Ouvrez ensuite [http://localhost:5000/](http://localhost:5000/) dans votre navigateur.

## Configuration de la clé API

Par défaut, la clé Groq est codée en dur. Pour plus de sécurité, définissez la variable d'environnement `GROQ_API_KEY` :

```bash
set GROQ_API_KEY=VOTRE_CLE_GROQ
``` 