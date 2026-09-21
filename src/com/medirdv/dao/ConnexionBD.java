package com.medirdv.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connexion à la base MySQL de XAMPP.
 *
 * Valeurs par défaut de XAMPP :
 *   - hôte        : localhost
 *   - port        : 3306
 *   - utilisateur : root
 *   - mot de passe: (vide)
 *
 * Si vous avez changé ces valeurs dans XAMPP / phpMyAdmin,
 * modifiez simplement les constantes ci-dessous.
 */
public class ConnexionBD {

    private static final String HOTE         = "localhost";
    private static final int    PORT         = 3306;
    private static final String BASE         = "medirdv";
    private static final String UTILISATEUR  = "root";
    private static final String MOT_DE_PASSE = "";

    private static final String URL = "jdbc:mysql://" + HOTE + ":" + PORT + "/" + BASE
            + "?useUnicode=true&characterEncoding=UTF-8"
            + "&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private ConnexionBD() {}

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "Connecteur MySQL introuvable : ajoutez mysql-connector-j-x.x.x.jar au Build Path du projet serveur.", e);
        }
        return DriverManager.getConnection(URL, UTILISATEUR, MOT_DE_PASSE);
    }
}
