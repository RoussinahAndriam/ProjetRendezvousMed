package com.medirdv.model.modelclient;

import java.io.Serializable;

public class Medecinclient implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nom;
    private String prenom;
    private String specialite;
    private String ville;
    private double rating;
    private int nbAvis;
    private String disponibilite; // "Disponible", "Complet"
    private String initiales;

    public Medecinclient(int id, String nom, String prenom, String specialite,
                   String ville, double rating, int nbAvis, String disponibilite) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.specialite = specialite;
        this.ville = ville;
        this.rating = rating;
        this.nbAvis = nbAvis;
        this.disponibilite = disponibilite;
        this.initiales = ("" + prenom.charAt(0) + nom.charAt(0)).toUpperCase();
    }

    public int getId()              { return id; }
    public String getNom()          { return nom; }
    public String getPrenom()       { return prenom; }
    public String getNomComplet()   { return "Dr. " + prenom + " " + nom; }
    public String getSpecialite()   { return specialite; }
    public String getVille()        { return ville; }
    public double getRating()       { return rating; }
    public int getNbAvis()          { return nbAvis; }
    public String getDisponibilite(){ return disponibilite; }
    public String getInitiales()    { return initiales; }

    @Override
    public String toString() {
        return getNomComplet() + " - " + specialite;
    }
}
