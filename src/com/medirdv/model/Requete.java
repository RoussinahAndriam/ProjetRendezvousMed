package com.medirdv.model;

import java.io.Serializable;

/**
 * Objet envoyé du Client → Serveur
 * Le type définit l'action demandée.
 */
public class Requete implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        GET_MEDECINS,  // Récupérer tous les médecins         
        GET_MEDECINS_SPECIALITE, // Filtrer par spécialité
        GET_CRENEAUX,    // Récupérer créneaux d'un médecin       
        GET_CRENEAUX_DATE, // Créneaux d'un médecin à une date donnée
        CREER_RDV,               // Créer un rendez-vous
        GET_RDV_PATIENT,       // Historique RDV d'un patient
        ANNULER_RDV ,             // Annuler un RDV
        GET_SPECIALITES,  // Récupérer toutes les spécialités
        GET_PATIENT_PAR_EMAIL,       // Chercher un patient par email
        ENREGISTRER_PATIENT // Créer ou mettre à jour un patient
    }

    private Type type;
    private Object donnees; // Paramètre flexible (String, RendezVous, Integer...)

    public Requete(Type type, Object donnees) {
        this.type = type;
        this.donnees = donnees;
    }

    public Type getType()    { return type; }
    public Object getDonnees() { return donnees; }
}
