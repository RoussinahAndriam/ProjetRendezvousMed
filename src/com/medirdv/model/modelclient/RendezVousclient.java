package com.medirdv.model.modelclient;

import java.io.Serializable;

public class RendezVousclient implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nomPatient;
    private String prenomPatient;
    private String telephone;
    private String email;
    private String motif;
    private String typeConsultation;
    private int medecinId;
    private String nomMedecin;
    private String date;
    private String heure;
    private String statut; // "CONFIRME", "ANNULE", "EN_ATTENTE"

    public RendezVousclient() {}

    public RendezVousclient(String nomPatient, String prenomPatient, String telephone,
                      String email, String motif, String typeConsultation,
                      int medecinId, String nomMedecin, String date, String heure) {
        this.nomPatient = nomPatient;
        this.prenomPatient = prenomPatient;
        this.telephone = telephone;
        this.email = email;
        this.motif = motif;
        this.typeConsultation = typeConsultation;
        this.medecinId = medecinId;
        this.nomMedecin = nomMedecin;
        this.date = date;
        this.heure = heure;
        this.statut = "CONFIRME";
    }

    public int getId()                  { return id; }
    public void setId(int id)           { this.id = id; }
    public String getNomPatient()       { return nomPatient; }
    public String getPrenomPatient()    { return prenomPatient; }
    public String getTelephone()        { return telephone; }
    public String getEmail()            { return email; }
    public String getMotif()            { return motif; }
    public String getTypeConsultation() { return typeConsultation; }
    public int getMedecinId()           { return medecinId; }
    public String getNomMedecin()       { return nomMedecin; }
    public String getDate()             { return date; }
    public String getHeure()            { return heure; }
    public String getStatut()           { return statut; }
    public void setStatut(String s)     { this.statut = s; }
}
