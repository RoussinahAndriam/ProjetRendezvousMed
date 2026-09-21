package com.medirdv.client;

import com.medirdv.model.Reponse;
import com.medirdv.model.Requete;

import java.io.*;
import java.net.Socket;

/**
 * Gère la connexion TCP au serveur.
 * Méthode : envoyer(Requete) → Reponse
 */
public class ConnexionServeurclient  {

    private static final String HOTE = "localhost";
    private static final int PORT = 5000;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void connecter() throws IOException {
        socket = new Socket(HOTE, PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        in  = new ObjectInputStream(socket.getInputStream());
        System.out.println("[CLIENT] Connecté au serveur " + HOTE + ":" + PORT);
    }

    public Reponse envoyer(Requete requete) throws IOException, ClassNotFoundException {
        out.writeObject(requete);
        out.flush();
        out.reset();
        return (Reponse) in.readObject();
    }

    public void fermer() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("[CLIENT] Erreur fermeture : " + e.getMessage());
        }
    }
}
