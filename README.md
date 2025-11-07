# SecureChat

**Messagerie sécurisée en Java (client-serveur, chiffrement RSA + AES)**

## Description  
SecureChat est une application de messagerie temps réel, développée en Java, conçue pour permettre à plusieurs clients de communiquer via un serveur central tout en assurant la **confidentialité** des messages. Grâce à l’échange de clé RSA pour établir une clé AES symétrique, puis l’utilisation de l’AES pour chiffrer tous les échanges, SecureChat garantit que les messages ne peuvent être lus que par les participants autorisés.


## Fonctionnalités  
- Connexion client-serveur multi-utilisateurs.  
- Échange sécurisé de clé AES via RSA dès la connexion.  
- Chiffrement symétrique des messages.  
- Multi-threading : chaque client est géré dans un thread côté serveur.  
- Diffusion des messages à tous les clients connectés.  
- Commande de sortie (`/quit` ou `/exit`) pour déconnexion.  
- Architecture modulaire pour faciliter l’évolution (ex : passage à TLS, GUI, persistance).

## Architecture & Arborescence projet  
```

SecureChat/
└── src/
└── securechat/
├── model/
│   └── Message.java
├── crypto/
│   ├── AESUtils.java
│   ├── RSAUtils.java
│   └── CryptoUtils.java
├── server/
│   ├── SecureChatServer.java
│   ├── ClientHandler.java
│   └── ServerMain.java
├── client/
│   ├── SecureChatClient.java
│   ├── ClientListener.java
│   └── ClientMain.java
└── utils/
└── LoggerUtils.java

````
### Schéma fonctionnel  
- Le serveur écoute les connexions clients.  
- Le client récupère la clé publique RSA du serveur.  
- Le client génère une clé AES et l’envoie chiffrée (RSA) au serveur.  
- Tous les échanges de messages se font via AES.  
- Le serveur relaie les messages à tous les clients connectés.

## Technologies utilisées  
- Java 11+ (ou Java 17 recommandé)  
- `javax.crypto` pour chiffrement AES/RSA  
- Sockets TCP `ServerSocket`, `Socket`  
- Threads Java pour gestion multi-clients  
- Git & GitHub pour versionning  
- IntelliJ IDEA (ou tout IDE Java) pour développement  

## Prérequis  
- JDK installé (ex : OpenJDK 11 ou 17)  
- Git installé  
- IDE (IntelliJ, Eclipse, VS Code + extension Java)  
- Environnement réseau local pour tests (localhost ou réseau)  

## Installation & exécution  
1. Clone le dépôt :  
  bash
   git clone https://github.com/TON_UTILISATEUR/SecureChat.git

2. Dans le terminal, va dans le répertoire du projet :

   ```bash
   cd SecureChat/src
   ```
3. Compile le projet :

   ```bash
   javac securechat/**/*.java
   ```
4. Lance d’abord le serveur :

   ```bash
   java securechat.server.ServerMain
   ```
5. Lance un ou plusieurs clients (dans d’autres terminaux) :

   ```bash
   java securechat.client.ClientMain localhost 12345
   ```
6. Sur chaque client, entre un pseudo, puis écris un message et valide.
7. Pour quitter un client : tape `/quit` ou `/exit`.

## Usage

* Après connexion, écris un message et appuie sur Entrée : ton message est chiffré, envoyé au serveur, puis diffusé aux autres clients.
* Exemple :

  ```
  Enter your nickname: Alice  
  Connected. Type messages, '/quit' to exit.  
  Bonjour tout le monde !
  ```
* Le serveur affiche chaque message reçu avec horodatage.
* Exemple côté serveur :

  ```
  [2025-11-07T15:24:12] Alice : Bonjour tout le monde !
  ```

## Tests
Pas encore de suite de tests automatisés.
À venir : tests unitaires pour `AESUtils`, `RSAUtils`, tests d’intégration client-serveur.







