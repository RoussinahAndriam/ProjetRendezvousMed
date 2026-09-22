package com.medirdv.server;

import com.medirdv.dao.MedecinDAO;

import com.medirdv.model.*;
import com.medirdv.dao.PatientDAO;

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
    private final PatientDAO patientDAO = new PatientDAO();

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
                case GET_SPECIALITES:
                    return new Reponse(true, "OK", dao.getToutesSpecialites());

                case GET_CRENEAUX:
                    int medecinId = (Integer) req.getDonnees();
                    return new Reponse(true, "OK", dao.getCreneaux(medecinId));
                case GET_CRENEAUX_DATE:
                    CreneauRequete cr = (CreneauRequete) req.getDonnees();
                    return new Reponse(true, "OK",
                        dao.getCreneauxDisponibles(cr.getMedecinId(), cr.getDate()));
                case CREER_RDV:
                    RendezVous rdv = (RendezVous) req.getDonnees();
                    if (dao.creneauEstDejaPris(rdv.getMedecinId(), rdv.getDate(), rdv.getHeure())) {
                        return new Reponse(false,
                            "Ce créneau vient d'être réservé. Veuillez en choisir un autre.", null);
                    }
                    RendezVous cree = dao.creerRDV(rdv);
                    return new Reponse(true, "Rendez-vous confirmé !", cree);

                case GET_RDV_PATIENT:
                    String email = (String) req.getDonnees();
                    return new Reponse(true, "OK", dao.getRDVParEmail(email));

                case ANNULER_RDV:
                    int idRdv = (Integer) req.getDonnees();
                    boolean ok = dao.annulerRDV(idRdv);
                    return new Reponse(ok, ok ? "RDV annulé." : "RDV introuvable.", null);
                case GET_PATIENT_PAR_EMAIL:
                    String emailPatient = (String) req.getDonnees();
                    Patient patientTrouve = patientDAO.getPatientParEmail(emailPatient);
                    if (patientTrouve == null) {
                        return new Reponse(false, "Aucun patient trouvé pour cet email.", null);
                    }
                    return new Reponse(true, "OK", patientTrouve);

                case ENREGISTRER_PATIENT:
                    Patient patientAEnregistrer = (Patient) req.getDonnees();
                    Patient patientEnregistre = patientDAO.enregistrerPatient(patientAEnregistrer);
                    return new Reponse(true, "Profil enregistré.", patientEnregistre);

                default:
                    return new Reponse(false, "Requête inconnue.", null);
            }
        } catch (Exception e) {
            return new Reponse(false, "Erreur serveur : " + e.getMessage(), null);
        }
    }
}
