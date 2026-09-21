package com.medirdv.model;

import java.io.Serializable;

/**
 * Objet envoyé du Serveur → Client
 */
public class Reponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean succes;
    private String message;
    private Object donnees; // Liste médecins, RDV créé, créneaux...

    public Reponse(boolean succes, String message, Object donnees) {
        this.succes = succes;
        this.message = message;
        this.donnees = donnees;
    }

    public boolean isSucces()    { return succes; }
    public String getMessage()   { return message; }
    public Object getDonnees()   { return donnees; }
}
