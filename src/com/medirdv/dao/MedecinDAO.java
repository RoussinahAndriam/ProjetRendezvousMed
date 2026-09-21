package com.medirdv.dao;

import com.medirdv.model.Medecin;
import com.medirdv.model.RendezVous;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Accès aux données via MySQL (XAMPP) avec JDBC.
 * Les méthodes publiques sont identiques à l'ancienne version en mémoire :
 * ClientHandler, Serveur et le client n'ont donc pas besoin de changer.
 *
 * Tables utilisées : medecins, creneaux, rendez_vous (voir database/medirdv.sql).
 */
public class MedecinDAO {

    public MedecinDAO() {
        // Simple test de connexion au démarrage du serveur
        try (Connection c = ConnexionBD.getConnection()) {
            System.out.println("[SERVEUR] Connecté à MySQL (base medirdv).");
        } catch (SQLException e) {
            System.err.println("[SERVEUR] Connexion MySQL impossible : " + e.getMessage());
            System.err.println("[SERVEUR] Vérifiez : MySQL démarré dans XAMPP, base 'medirdv' importée, "
                    + "connecteur MySQL ajouté au Build Path.");
        }
    }

    public List<Medecin> getTousMedecins() {
        String sql = "SELECT id, nom, prenom, specialite, ville, rating, nb_avis, disponibilite "
                   + "FROM medecins ORDER BY id";
        List<Medecin> result = new ArrayList<>();
        try (Connection c = ConnexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(lireMedecin(rs));
            }
        } catch (SQLException e) {
            throw erreurBD(e);
        }
        return result;
    }

    public List<Medecin> getMedecinsBySpecialite(String specialite) {
        String sql = "SELECT id, nom, prenom, specialite, ville, rating, nb_avis, disponibilite "
                   + "FROM medecins WHERE LOWER(specialite) = LOWER(?) ORDER BY id";
        List<Medecin> result = new ArrayList<>();
        try (Connection c = ConnexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, specialite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(lireMedecin(rs));
                }
            }
        } catch (SQLException e) {
            throw erreurBD(e);
        }
        return result;
    }

    public List<String> getCreneaux(int medecinId) {
        String sql = "SELECT heure FROM creneaux WHERE medecin_id = ? ORDER BY heure";
        List<String> result = new ArrayList<>();
        try (Connection c = ConnexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, medecinId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getString("heure"));
                }
            }
        } catch (SQLException e) {
            throw erreurBD(e);
        }
        return result;
    }

    public RendezVous creerRDV(RendezVous rdv) {
        String sql = "INSERT INTO rendez_vous (nom_patient, prenom_patient, telephone, email, motif, "
                   + "type_consultation, medecin_id, nom_medecin, date_rdv, heure, statut) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = ConnexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, rdv.getNomPatient());
            ps.setString(2, rdv.getPrenomPatient());
            ps.setString(3, rdv.getTelephone());
            ps.setString(4, rdv.getEmail());
            ps.setString(5, rdv.getMotif());
            ps.setString(6, rdv.getTypeConsultation());
            ps.setInt(7, rdv.getMedecinId());
            ps.setString(8, rdv.getNomMedecin());
            ps.setString(9, rdv.getDate());
            ps.setString(10, rdv.getHeure());
            ps.setString(11, rdv.getStatut() != null ? rdv.getStatut() : "CONFIRME");
            ps.executeUpdate();

            try (ResultSet cles = ps.getGeneratedKeys()) {
                if (cles.next()) {
                    rdv.setId(cles.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw erreurBD(e);
        }

        System.out.println("[SERVEUR] Nouveau RDV créé : ID=" + rdv.getId()
                + " Patient=" + rdv.getPrenomPatient() + " " + rdv.getNomPatient()
                + " Médecin=" + rdv.getNomMedecin()
                + " Date=" + rdv.getDate() + " " + rdv.getHeure());
        return rdv;
    }

    public List<RendezVous> getRDVParEmail(String email) {
        String sql = "SELECT id, nom_patient, prenom_patient, telephone, email, motif, type_consultation, "
                   + "medecin_id, nom_medecin, date_rdv, heure, statut "
                   + "FROM rendez_vous WHERE LOWER(email) = LOWER(?) ORDER BY id";
        List<RendezVous> result = new ArrayList<>();
        try (Connection c = ConnexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RendezVous r = new RendezVous(
                            rs.getString("nom_patient"),
                            rs.getString("prenom_patient"),
                            rs.getString("telephone"),
                            rs.getString("email"),
                            rs.getString("motif"),
                            rs.getString("type_consultation"),
                            rs.getInt("medecin_id"),
                            rs.getString("nom_medecin"),
                            rs.getString("date_rdv"),
                            rs.getString("heure"));
                    r.setId(rs.getInt("id"));
                    r.setStatut(rs.getString("statut"));
                    result.add(r);
                }
            }
        } catch (SQLException e) {
            throw erreurBD(e);
        }
        return result;
    }

    public boolean annulerRDV(int id) {
        String sql = "UPDATE rendez_vous SET statut = 'ANNULE' WHERE id = ?";
        try (Connection c = ConnexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw erreurBD(e);
        }
    }

    // ── Utilitaires ────────────────────────────────

    private Medecin lireMedecin(ResultSet rs) throws SQLException {
        return new Medecin(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("specialite"),
                rs.getString("ville"),
                rs.getDouble("rating"),
                rs.getInt("nb_avis"),
                rs.getString("disponibilite"));
    }

    /** ClientHandler attrape déjà Exception et renvoie "Erreur serveur : ..." au client. */
    private RuntimeException erreurBD(SQLException e) {
        return new RuntimeException("Erreur base de données : " + e.getMessage(), e);
    }
}
