package com.medirdv.model.modelclient;

import java.io.Serializable;

/**
 * Objet envoyé du Client → Serveur
 * Le type définit l'action demandée.
 */
public class Requeteclient implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        GET_MEDECINS,          // Récupérer tous les médecins
        GET_MEDECINS_SPECIALITE, // Filtrer par spécialité
        GET_CRENEAUX,          // Récupérer créneaux d'un médecin
        CREER_RDV,             // Créer un rendez-vous
        GET_RDV_PATIENT,       // Historique RDV d'un patient
        ANNULER_RDV            // Annuler un RDV
    }

    private Type type;
    private Object donnees; // Paramètre flexible (String, RendezVous, Integer...)

    public Requeteclient(Type type, Object donnees) {
        this.type = type;
        this.donnees = donnees;
    }

    public Type getType()    { return type; }
    public Object getDonnees() { return donnees; }
}
