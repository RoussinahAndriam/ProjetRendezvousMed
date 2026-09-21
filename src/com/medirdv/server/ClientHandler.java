package com.medirdv.server;

import com.medirdv.dao.MedecinDAO;
import com.medirdv.model.*;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Gère la connexion d'UN client dans un thread séparé.
 * Pattern : reçoit une Requete, renvoie une Reponse.
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final MedecinDAO dao;

    public ClientHandler(Socket socket, MedecinDAO dao) {
        this.socket = socket;
        this.dao = dao;
    }

    @Override
    public void run() {
        try (
            ObjectInputStream in  = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
        ) {
            System.out.println("[SERVEUR] Client connecté : " + socket.getInetAddress());

            while (true) {
                try {
                    Requete req = (Requete) in.readObject();
                    Reponse rep = traiter(req);
                    out.writeObject(rep);
                    out.flush();
                    out.reset(); // Important : évite le cache de sérialisation
                } catch (EOFException e) {
                    System.out.println("[SERVEUR] Client déconnecté.");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("[SERVEUR] Connexion fermée : " + e.getMessage());
        }
    }

    private Reponse traiter(Requete req) {
        try {
            switch (req.getType()) {

                case GET_MEDECINS:
                    List<com.medirdv.model.Medecin> tous = dao.getTousMedecins();
                    return new Reponse(true, "OK", tous);

                case GET_MEDECINS_SPECIALITE:
                    String spec = (String) req.getDonnees();
                    return new Reponse(true, "OK", dao.getMedecinsBySpecialite(spec));

                case GET_CRENEAUX:
                    int medecinId = (Integer) req.getDonnees();
                    return new Reponse(true, "OK", dao.getCreneaux(medecinId));

                case CREER_RDV:
                    RendezVous rdv = (RendezVous) req.getDonnees();
                    RendezVous cree = dao.creerRDV(rdv);
                    return new Reponse(true, "Rendez-vous confirmé !", cree);

                case GET_RDV_PATIENT:
                    String email = (String) req.getDonnees();
                    return new Reponse(true, "OK", dao.getRDVParEmail(email));

                case ANNULER_RDV:
                    int idRdv = (Integer) req.getDonnees();
                    boolean ok = dao.annulerRDV(idRdv);
                    return new Reponse(ok, ok ? "RDV annulé." : "RDV introuvable.", null);

                default:
                    return new Reponse(false, "Requête inconnue.", null);
            }
        } catch (Exception e) {
            return new Reponse(false, "Erreur serveur : " + e.getMessage(), null);
        }
    }
}
