package com.medirdv.model;
import java.io.Serializable;

public class CreneauRequete implements Serializable {
	 private static final long serialVersionUID = 1L;

	    private int medecinId;
	    private String date; 

	    public CreneauRequete(int medecinId, String date) {
	        this.medecinId = medecinId;
	        this.date = date;
	    }

	    public int getMedecinId() { return medecinId; }
	    public String getDate()   { return date; }

}
