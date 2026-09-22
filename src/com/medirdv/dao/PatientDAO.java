package com.medirdv.dao;
import com.medirdv.model.Patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PatientDAO {
	  public Patient getPatientParEmail(String email) {
	        String sql = "SELECT id, nom, prenom, email, telephone FROM patients WHERE LOWER(email) = LOWER(?)";
	        try (Connection c = ConnexionBD.getConnection();
	             PreparedStatement ps = c.prepareStatement(sql)) {
	            ps.setString(1, email);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    return new Patient(
	                        rs.getInt("id"),
	                        rs.getString("nom"),
	                        rs.getString("prenom"),
	                        rs.getString("email"),
	                        rs.getString("telephone"));
	                }
	            }
	        } catch (SQLException e) {
	            throw new RuntimeException("Erreur base de données : " + e.getMessage(), e);
	        }
	        return null;
	    }

	   
	    public Patient enregistrerPatient(Patient p) {
	        Patient existant = getPatientParEmail(p.getEmail());
	        if (existant != null) {
	            
	            String sql = "UPDATE patients SET nom = ?, prenom = ?, telephone = ? WHERE id = ?";
	            try (Connection c = ConnexionBD.getConnection();
	                 PreparedStatement ps = c.prepareStatement(sql)) {
	                ps.setString(1, p.getNom());
	                ps.setString(2, p.getPrenom());
	                ps.setString(3, p.getTelephone());
	                ps.setInt(4, existant.getId());
	                ps.executeUpdate();
	                p.setId(existant.getId());
	                return p;
	            } catch (SQLException e) {
	                throw new RuntimeException("Erreur base de données : " + e.getMessage(), e);
	            }
	        } else {
	           
	            String sql = "INSERT INTO patients (nom, prenom, email, telephone) VALUES (?, ?, ?, ?)";
	            try (Connection c = ConnexionBD.getConnection();
	                 PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	                ps.setString(1, p.getNom());
	                ps.setString(2, p.getPrenom());
	                ps.setString(3, p.getEmail());
	                ps.setString(4, p.getTelephone());
	                ps.executeUpdate();
	                try (ResultSet cles = ps.getGeneratedKeys()) {
	                    if (cles.next()) {
	                        p.setId(cles.getInt(1));
	                    }
	                }
	                return p;
	            } catch (SQLException e) {
	                throw new RuntimeException("Erreur base de données : " + e.getMessage(), e);
	            }
	        }
	    }

}
