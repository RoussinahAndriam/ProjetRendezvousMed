package com.medirdv.model;
import java.io.Serializable;
public class Patient implements Serializable {
	 private static final long serialVersionUID = 1L;

	    private int id;
	    private String nom;
	    private String prenom;
	    private String email;
	    private String telephone;

	    public Patient(int id, String nom, String prenom, String email, String telephone) {
	        this.id = id;
	        this.nom = nom;
	        this.prenom = prenom;
	        this.email = email;
	        this.telephone = telephone;
	    }

	    public Patient(String nom, String prenom, String email, String telephone) {
	        this(0, nom, prenom, email, telephone);
	    }

	  
	    public int getId()           { return id; }
	    public String getNom()       { return nom; }
	    public String getPrenom()    { return prenom; }
	    public String getEmail()     { return email; }
	    public String getTelephone() { return telephone; }


	    public void setId(int id)             { this.id = id; }
	    public void setNom(String nom)        { this.nom = nom; }
	    public void setPrenom(String prenom)  { this.prenom = prenom; }
	    public void setEmail(String email)    { this.email = email; }
	    public void setTelephone(String t)    { this.telephone = t; }

	    
	    public String getInitiales() {
	        String p = (prenom != null && !prenom.isEmpty()) ? prenom.substring(0, 1) : "";
	        String n = (nom != null && !nom.isEmpty()) ? nom.substring(0, 1) : "";
	        return (p + n).toUpperCase();
	    }

	   
	    public String getNomComplet() {
	        return prenom + " " + nom;
	    }

}
