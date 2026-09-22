package com.medirdv.ui;

import com.medirdv.client.ConnexionServeurclient;

import com.medirdv.model.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class AppMediRDV extends JFrame {

    
    static final Color BLEU        = new Color(0x1A56DB);
    static final Color BLEU_CLAIR  = new Color(0xDBEAFE);
    static final Color BLEU_TEXTE  = new Color(0x1E40AF);
    static final Color VERT        = new Color(0x059669);
    static final Color VERT_CLAIR  = new Color(0xDCFCE7);
    static final Color ROUGE       = new Color(0xDC2626);
    static final Color FOND        = new Color(0xF8FAFC);
    static final Color BLANC       = Color.WHITE;
    static final Color GRIS_CLAIR  = new Color(0xF1F5F9);
    static final Color GRIS_BORD   = new Color(0xE2E8F0);
    static final Color TEXTE       = new Color(0x1E293B);
    static final Color TEXTE_MUTED = new Color(0x64748B);

    static final Font FONT_TITRE   = new Font("Segoe UI", Font.BOLD, 18);
    static final Font FONT_SOUS    = new Font("Segoe UI", Font.PLAIN, 12);
    static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font FONT_BOLD    = new Font("Segoe UI", Font.BOLD, 13);
    static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);

    private ConnexionServeurclient connexion;
    private CardLayout cardLayout;
    private JPanel panneauPrincipal;

    
    private Medecin medecinSelectionne;
    private String creneauSelectionne;
    private LocalDate dateSelectionnee = LocalDate.now(); 
    private static final DateTimeFormatter FORMAT_DATE =
    	    DateTimeFormatter.ofPattern("d MMM yyyy", Locale.FRENCH);
    private JTextField searchField;
    private JPanel panneauRecherche = new JPanel();
    private JTextField pNom, pPrenom, pEmail, pTel;
    private Patient patientCourant;  
    private JPanel panneauMesRdv = new JPanel();

    public AppMediRDV() {
        super("MédiRDV — Prise de rendez-vous");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 750);
        setLocationRelativeTo(null);
        setResizable(false);

        connexion = new ConnexionServeurclient ();
        connecterAuServeur();

        cardLayout = new CardLayout();
        panneauPrincipal = new JPanel(cardLayout);
        panneauPrincipal.setBackground(FOND);

        panneauPrincipal.add(creerEcranAccueil(),      "ACCUEIL");
        panneauPrincipal.add(creerEcranMedecins(),     "MEDECINS");
        panneauPrincipal.add(creerEcranSpecialites(),  "SPECIALITES");
        panneauPrincipal.add(creerEcranCreneaux(),     "CRENEAUX");
        panneauPrincipal.add(creerEcranFormulaire(),   "FORMULAIRE");
        panneauPrincipal.add(creerEcranConfirmation(), "CONFIRMATION");
        panneauPrincipal.add(creerEcranProfil(),       "PROFIL");
        panneauPrincipal.add(creerEcranRecherche(),    "RECHERCHE");
        panneauPrincipal.add(creerEcranMesRdv(),       "MES_RDV");

        add(panneauPrincipal);
        cardLayout.show(panneauPrincipal, "ACCUEIL");
        setVisible(true);
    }

    
    private void connecterAuServeur() {
        try {
            connexion.connecter();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Impossible de se connecter au serveur.\nVérifiez que le serveur est lancé sur le port 5000.",
                "Erreur de connexion", JOptionPane.WARNING_MESSAGE);
        }
    }

    
    private JPanel creerEcranAccueil() {
        JPanel ecran = new JPanel(new BorderLayout());
        ecran.setBackground(FOND);

        
        JPanel hero = new JPanel();
        hero.setBackground(BLEU);
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBorder(new EmptyBorder(24, 20, 20, 20));

        JLabel logo = new JLabel("MédiRDV");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setForeground(BLANC);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sous = new JLabel("Prenez rendez-vous en ligne en quelques clics");
        sous.setFont(FONT_SOUS);
        sous.setForeground(new Color(0xBFDBFE));
        sous.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBackground(new Color(0x2563EB));
        searchPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(0x3B82F6), 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        searchField = new JTextField("Médecin ou spécialité...");   // ← utilise l'attribut, pas "JTextField" local
        searchField.setFont(FONT_BODY);
        searchField.setForeground(new Color(0xBFDBFE));
        searchField.setBackground(new Color(0x2563EB));
        searchField.setBorder(null);
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Médecin ou spécialité...")) {
                    searchField.setText(""); searchField.setForeground(BLANC);
                }
            }
        });

        
        searchField.addActionListener(e -> {
            String motCle = searchField.getText().trim();
            if (motCle.isEmpty() || motCle.equals("Médecin ou spécialité...")) {
                return;
            }
            afficherRecherche(motCle);
        });
        searchPanel.add(new JLabel("🔍"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        hero.add(logo);
        hero.add(Box.createVerticalStrut(4));
        hero.add(sous);
        hero.add(Box.createVerticalStrut(14));
        hero.add(searchPanel);

        
        JPanel corps = new JPanel();
        corps.setBackground(FOND);
        corps.setLayout(new BoxLayout(corps, BoxLayout.Y_AXIS));
        corps.setBorder(new EmptyBorder(16, 16, 16, 16));
        JPanel ligneTitreSpecs = new JPanel(new BorderLayout());
        ligneTitreSpecs.setBackground(FOND);
        ligneTitreSpecs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        ligneTitreSpecs.setAlignmentX(Component.LEFT_ALIGNMENT);

        ligneTitreSpecs.add(labelSection("Spécialités"), BorderLayout.WEST);

        JButton btnVoirTout = new JButton("Voir tout →");
        btnVoirTout.setFont(FONT_SMALL);
        btnVoirTout.setForeground(BLEU);
        btnVoirTout.setBackground(FOND);
        btnVoirTout.setBorderPainted(false);
        btnVoirTout.setFocusPainted(false);
        btnVoirTout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVoirTout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnVoirTout.setText("<html><u>Voir tout →</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnVoirTout.setText("Voir tout →");
            }
        });
        btnVoirTout.addActionListener(e -> afficherToutesSpecialites());
        ligneTitreSpecs.add(btnVoirTout, BorderLayout.EAST);

        corps.add(ligneTitreSpecs);
        corps.add(Box.createVerticalStrut(8));

        

        
        JPanel grille = new JPanel(new GridLayout(2, 2, 10, 10));
        grille.setBackground(FOND);
        grille.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        String[][] specs = {
            {"❤️", "Cardiologie"},
            {"👁", "Ophtalmologie"},
            {"🧠", "Neurologie"},
            {"👤", "Généraliste"}
        };
        for (String[] s : specs) {
            JButton btn = carteSpecialite(s[0], s[1]);
            btn.addActionListener(e -> afficherMedecins(s[1]));
            grille.add(btn);
        }
        corps.add(grille);
        corps.add(Box.createVerticalStrut(18));

        corps.add(labelSection("Médecins disponibles aujourd'hui"));
        corps.add(Box.createVerticalStrut(8));

     
     try {
         Requete req = new Requete(Requete.Type.GET_MEDECINS, null);
         Reponse rep = connexion.envoyer(req);

         if (rep.isSucces()) {
             @SuppressWarnings("unchecked")
             List<Medecin> tous = (List<Medecin>) rep.getDonnees();

             if (tous == null || tous.isEmpty()) {
                 corps.add(labelInfo("Aucun médecin disponible."));
             } else {
                 // Afficher les 3 premiers (pour ne pas surcharger l'accueil)
                 int limite = Math.min(3, tous.size());
                 for (int i = 0; i < limite; i++) {
                     corps.add(carteMedecinAccueil(tous.get(i)));
                     corps.add(Box.createVerticalStrut(8));
                 }
             }
         } else {
             corps.add(labelInfo("Erreur : " + rep.getMessage()));
         }
     } catch (Exception e) {
         corps.add(labelInfo("Serveur non disponible. Mode démo."));
         
         Medecin demo = new Medecin(1, "Soa", "Marie", "Cardiologie",
                 "Antananarivo", 4.9, 42, "Disponible");
         corps.add(carteMedecinAccueil(demo));
     }

        JScrollPane scroll = new JScrollPane(corps);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        ecran.add(hero, BorderLayout.NORTH);
        ecran.add(scroll, BorderLayout.CENTER);
        ecran.add(barreNavigation("ACCUEIL"), BorderLayout.SOUTH);
        return ecran;
    }

    
    private JPanel panneauMedecins = new JPanel();
    private JLabel titreMedecins;

    private JPanel creerEcranMedecins() {
        JPanel ecran = new JPanel(new BorderLayout());
        ecran.setBackground(FOND);
        ecran.add(barreRetour("Médecins", "ACCUEIL", null), BorderLayout.NORTH);

        panneauMedecins.setLayout(new BoxLayout(panneauMedecins, BoxLayout.Y_AXIS));
        panneauMedecins.setBackground(FOND);
        panneauMedecins.setBorder(new EmptyBorder(12, 16, 12, 16));

        JScrollPane scroll = new JScrollPane(panneauMedecins);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        ecran.add(scroll, BorderLayout.CENTER);
        return ecran;
    }

    private void afficherMedecins(String specialite) {
    	   panneauMedecins.removeAll();
    	
        JLabel titre = new JLabel(specialite + "s");
        titre.setFont(FONT_TITRE);
        titre.setForeground(TEXTE);
        panneauMedecins.add(titre);
        panneauMedecins.add(Box.createVerticalStrut(12));

        try {
            Requete req = new Requete(Requete.Type.GET_MEDECINS_SPECIALITE, specialite);
            Reponse rep = connexion.envoyer(req);

            if (rep.isSucces()) {
                @SuppressWarnings("unchecked")
                List<Medecin> liste = (List<Medecin>) rep.getDonnees();

                if (liste.isEmpty()) {
                    panneauMedecins.add(labelInfo("Aucun médecin trouvé pour cette spécialité."));
                } else {
                    JLabel nb = new JLabel(liste.size() + " médecin(s) trouvé(s)");
                    nb.setFont(FONT_SOUS);
                    nb.setForeground(TEXTE_MUTED);
                    panneauMedecins.add(nb);
                    panneauMedecins.add(Box.createVerticalStrut(10));

                    for (Medecin m : liste) {
                        panneauMedecins.add(carteMedecinDetaille(m));
                        panneauMedecins.add(Box.createVerticalStrut(8));
                    }
                }
            } else {
                panneauMedecins.add(labelInfo("Erreur : " + rep.getMessage()));
            }
        } catch (Exception e) {
            panneauMedecins.add(labelInfo("Serveur non disponible. Mode démo activé."));
           
            afficherDemoMedecins(specialite);
        }

        panneauMedecins.revalidate();
        panneauMedecins.repaint();
        cardLayout.show(panneauPrincipal, "MEDECINS");
    }

    private void afficherDemoMedecins(String specialite) {
        Medecin demo1 = new Medecin(1, "Soa",  "Marie", specialite, "Fianarantsoa", 4.9, 42, "Disponible");
        Medecin demo2 = new Medecin(2, "Rova", "Jean",  specialite, "Fianarantsoa", 4.5, 18, "Disponible");
        panneauMedecins.add(carteMedecinDetaille(demo1));
        panneauMedecins.add(Box.createVerticalStrut(8));
        panneauMedecins.add(carteMedecinDetaille(demo2));
    }
    
    private JPanel panneauSpecialites = new JPanel();

    private JPanel creerEcranSpecialites() {
        JPanel ecran = new JPanel(new BorderLayout());
        ecran.setBackground(FOND);
        ecran.add(barreRetour("Toutes les spécialités", "ACCUEIL", null), BorderLayout.NORTH);

        panneauSpecialites.setLayout(new BoxLayout(panneauSpecialites, BoxLayout.Y_AXIS));
        panneauSpecialites.setBackground(FOND);
        panneauSpecialites.setBorder(new EmptyBorder(12, 16, 12, 16));

        JScrollPane scroll = new JScrollPane(panneauSpecialites);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        ecran.add(scroll, BorderLayout.CENTER);
        return ecran;
    }

 
    private void afficherToutesSpecialites() {
        panneauSpecialites.removeAll();

        JLabel titre = new JLabel("Choisissez une spécialité");
        titre.setFont(FONT_TITRE);
        titre.setForeground(TEXTE);
        titre.setAlignmentX(Component.LEFT_ALIGNMENT);
        panneauSpecialites.add(titre);
        panneauSpecialites.add(Box.createVerticalStrut(12));

        try {
            Requete req = new Requete(Requete.Type.GET_SPECIALITES, null);
            Reponse rep = connexion.envoyer(req);

            if (rep.isSucces()) {
                @SuppressWarnings("unchecked")
                List<String> specs = (List<String>) rep.getDonnees();

                if (specs == null || specs.isEmpty()) {
                    panneauSpecialites.add(labelInfo("Aucune spécialité disponible."));
                } else {
                    JPanel grille = new JPanel(new GridLayout(0, 2, 10, 10));
                    grille.setBackground(FOND);
                    grille.setAlignmentX(Component.LEFT_ALIGNMENT);
                    grille.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000));

                    for (String spec : specs) {
                        String emoji = emojiPourSpecialite(spec);
                        JButton btn = carteSpecialite(emoji, spec);
                        btn.addActionListener(e -> afficherMedecins(spec));
                        grille.add(btn);
                    }
                    panneauSpecialites.add(grille);
                }
            } else {
                panneauSpecialites.add(labelInfo("Erreur : " + rep.getMessage()));
            }
        } catch (Exception e) {
            panneauSpecialites.add(labelInfo("Serveur non disponible."));
        }

        panneauSpecialites.revalidate();
        panneauSpecialites.repaint();
        cardLayout.show(panneauPrincipal, "SPECIALITES");
    }

    
    private String emojiPourSpecialite(String spec) {
        String s = spec.toLowerCase();
        if (s.contains("cardio"))       return "❤️";
        if (s.contains("ophtalmo"))     return "👁";
        if (s.contains("neuro"))        return "🧠";
        if (s.contains("dermato"))      return "🩺";
        if (s.contains("pédia") || s.contains("pedia")) return "👶";
        if (s.contains("gynéco") || s.contains("gyneco")) return "🌸";
        if (s.contains("général") || s.contains("general")) return "👤";
        return "🩺";
    }    
    private JPanel creerEcranRecherche() {
        JPanel ecran = new JPanel(new BorderLayout());
        ecran.setBackground(FOND);
        ecran.add(barreRetour("Résultats de recherche", "ACCUEIL", null), BorderLayout.NORTH);

        panneauRecherche.setLayout(new BoxLayout(panneauRecherche, BoxLayout.Y_AXIS));
        panneauRecherche.setBackground(FOND);
        panneauRecherche.setBorder(new EmptyBorder(12, 16, 12, 16));

        JScrollPane scroll = new JScrollPane(panneauRecherche);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        ecran.add(scroll, BorderLayout.CENTER);
        ecran.add(barreNavigation("RECHERCHE"), BorderLayout.SOUTH);
        return ecran;
    }

   
    private void afficherRecherche(String motCle) {
        panneauRecherche.removeAll();

        
        JLabel titre = new JLabel("Résultats pour : \"" + motCle + "\"");
        titre.setFont(FONT_TITRE);
        titre.setForeground(TEXTE);
        titre.setAlignmentX(Component.LEFT_ALIGNMENT);
        panneauRecherche.add(titre);
        panneauRecherche.add(Box.createVerticalStrut(12));

        try {
            
            Requete req = new Requete(Requete.Type.GET_MEDECINS, null);
            Reponse rep = connexion.envoyer(req);

            if (rep.isSucces()) {
                @SuppressWarnings("unchecked")
                List<Medecin> tous = (List<Medecin>) rep.getDonnees();

                
                String cle = motCle.toLowerCase(Locale.FRENCH);
                java.util.List<Medecin> resultats = new java.util.ArrayList<>();
                for (Medecin m : tous) {
                    String nom    = m.getNom()        != null ? m.getNom().toLowerCase(Locale.FRENCH)        : "";
                    String prenom = m.getPrenom()     != null ? m.getPrenom().toLowerCase(Locale.FRENCH)     : "";
                    String spec   = m.getSpecialite() != null ? m.getSpecialite().toLowerCase(Locale.FRENCH) : "";
                    String ville  = m.getVille()      != null ? m.getVille().toLowerCase(Locale.FRENCH)      : "";

                    if (nom.contains(cle) || prenom.contains(cle)
                            || spec.contains(cle) || ville.contains(cle)) {
                        resultats.add(m);
                    }
                }

                if (resultats.isEmpty()) {
                    panneauRecherche.add(labelInfo("Aucun médecin ne correspond à votre recherche."));
                } else {
                    JLabel nb = new JLabel(resultats.size() + " médecin(s) trouvé(s)");
                    nb.setFont(FONT_SOUS);
                    nb.setForeground(TEXTE_MUTED);
                    nb.setAlignmentX(Component.LEFT_ALIGNMENT);
                    panneauRecherche.add(nb);
                    panneauRecherche.add(Box.createVerticalStrut(10));

                    for (Medecin m : resultats) {
                        panneauRecherche.add(carteMedecinDetaille(m));
                        panneauRecherche.add(Box.createVerticalStrut(8));
                    }
                }
            } else {
                panneauRecherche.add(labelInfo("Erreur : " + rep.getMessage()));
            }
        } catch (Exception e) {
            panneauRecherche.add(labelInfo("Serveur non disponible."));
        }

        panneauRecherche.revalidate();
        panneauRecherche.repaint();
        cardLayout.show(panneauPrincipal, "RECHERCHE");
    }    
    private JPanel creerEcranProfil() {
        JPanel ecran = new JPanel(new BorderLayout());
        ecran.setBackground(FOND);
        ecran.add(barreRetour("Mon profil", "ACCUEIL", null), BorderLayout.NORTH);

        JPanel corps = new JPanel();
        corps.setLayout(new BoxLayout(corps, BoxLayout.Y_AXIS));
        corps.setBackground(FOND);
        corps.setBorder(new EmptyBorder(20, 16, 16, 16));

        
        JLabel avatar = creerAvatar("?", BLEU_CLAIR, BLEU_TEXTE);
        avatar.setPreferredSize(new Dimension(80, 80));
        avatar.setMaximumSize(new Dimension(80, 80));
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 28));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        corps.add(avatar);
        corps.add(Box.createVerticalStrut(16));

       
        JLabel titre = new JLabel("Mes informations");
        titre.setFont(FONT_TITRE);
        titre.setForeground(TEXTE);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);
        corps.add(titre);
        corps.add(Box.createVerticalStrut(4));

        JLabel sous = new JLabel("Ces informations apparaîtront sur vos rendez-vous");
        sous.setFont(FONT_SOUS);
        sous.setForeground(TEXTE_MUTED);
        sous.setAlignmentX(Component.CENTER_ALIGNMENT);
        corps.add(sous);
        corps.add(Box.createVerticalStrut(20));

       
        pPrenom = champTexte(corps, "Prénom *", "");
        pNom    = champTexte(corps, "Nom *", "");
        pEmail  = champTexte(corps, "Email *", "");
        pTel    = champTexte(corps, "Téléphone *", "");

        corps.add(Box.createVerticalStrut(8));

      
        JButton btnSave = boutonPrimaire("💾  Enregistrer");
        btnSave.addActionListener(e -> sauvegarderProfil());
        corps.add(btnSave);
        corps.add(Box.createVerticalStrut(8));

        
        JButton btnLoad = boutonSecondaire("🔄  Charger mon profil");
        btnLoad.addActionListener(e -> chargerProfil());
        corps.add(btnLoad);

        JScrollPane scroll = new JScrollPane(corps);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        ecran.add(scroll, BorderLayout.CENTER);
        return ecran;
    }

  
    private void afficherProfil() {
        if (patientCourant != null) {
            pPrenom.setText(patientCourant.getPrenom());
            pNom.setText(patientCourant.getNom());
            pEmail.setText(patientCourant.getEmail());
            pTel.setText(patientCourant.getTelephone());
        }
        cardLayout.show(panneauPrincipal, "PROFIL");
    }

  
    private void chargerProfil() {
        String email = pEmail.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Entrez votre email pour charger votre profil.",
                "Email manquant", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Requete req = new Requete(Requete.Type.GET_PATIENT_PAR_EMAIL, email);
            Reponse rep = connexion.envoyer(req);
            if (rep.isSucces()) {
                patientCourant = (Patient) rep.getDonnees();
                pPrenom.setText(patientCourant.getPrenom());
                pNom.setText(patientCourant.getNom());
                pTel.setText(patientCourant.getTelephone());
                JOptionPane.showMessageDialog(this,
                    "Profil chargé !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    rep.getMessage(), "Aucun profil trouvé", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Serveur non disponible.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void sauvegarderProfil() {
        // Validation
        if (pNom.getText().isBlank() || pPrenom.getText().isBlank()
                || pEmail.getText().isBlank() || pTel.getText().isBlank()) {
            JOptionPane.showMessageDialog(this,
                "Tous les champs sont obligatoires (*).",
                "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Patient p = new Patient(
            pNom.getText().trim(),
            pPrenom.getText().trim(),
            pEmail.getText().trim(),
            pTel.getText().trim());

        try {
            Requete req = new Requete(Requete.Type.ENREGISTRER_PATIENT, p);
            Reponse rep = connexion.envoyer(req);
            if (rep.isSucces()) {
                patientCourant = (Patient) rep.getDonnees();
                JOptionPane.showMessageDialog(this,
                    "Profil enregistré avec succès !",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erreur : " + rep.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Serveur non disponible.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }    
    private JPanel creerEcranMesRdv() {
        JPanel ecran = new JPanel(new BorderLayout());
        ecran.setBackground(FOND);
        ecran.add(barreRetour("Mes rendez-vous", "ACCUEIL", null), BorderLayout.NORTH);

        panneauMesRdv.setLayout(new BoxLayout(panneauMesRdv, BoxLayout.Y_AXIS));
        panneauMesRdv.setBackground(FOND);
        panneauMesRdv.setBorder(new EmptyBorder(12, 16, 12, 16));

        JScrollPane scroll = new JScrollPane(panneauMesRdv);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        ecran.add(scroll, BorderLayout.CENTER);
        ecran.add(barreNavigation("MES_RDV"), BorderLayout.SOUTH);
        return ecran;
    }

    
    private void afficherMesRdv() {
        panneauMesRdv.removeAll();

        
        String email = (patientCourant != null) ? patientCourant.getEmail() : null;

        if (email == null || email.isBlank()) {
            email = JOptionPane.showInputDialog(this,
                "Entrez votre email pour voir vos rendez-vous :",
                "Mes rendez-vous", JOptionPane.QUESTION_MESSAGE);
            if (email == null || email.isBlank()) {
                
                cardLayout.show(panneauPrincipal, "ACCUEIL");
                return;
            }
            email = email.trim();
        }

   
        JLabel titre = new JLabel("Vos rendez-vous");
        titre.setFont(FONT_TITRE);
        titre.setForeground(TEXTE);
        titre.setAlignmentX(Component.LEFT_ALIGNMENT);
        panneauMesRdv.add(titre);
        panneauMesRdv.add(Box.createVerticalStrut(4));

        JLabel sousTitre = new JLabel("Email : " + email);
        sousTitre.setFont(FONT_SOUS);
        sousTitre.setForeground(TEXTE_MUTED);
        sousTitre.setAlignmentX(Component.LEFT_ALIGNMENT);
        panneauMesRdv.add(sousTitre);
        panneauMesRdv.add(Box.createVerticalStrut(14));

        
        try {
            Requete req = new Requete(Requete.Type.GET_RDV_PATIENT, email);
            Reponse rep = connexion.envoyer(req);

            if (rep.isSucces()) {
                @SuppressWarnings("unchecked")
                List<RendezVous> rdvs = (List<RendezVous>) rep.getDonnees();

                if (rdvs == null || rdvs.isEmpty()) {
                    panneauMesRdv.add(labelInfo("Vous n'avez aucun rendez-vous pour le moment."));
                } else {
                    JLabel nb = new JLabel(rdvs.size() + " rendez-vous");
                    nb.setFont(FONT_SOUS);
                    nb.setForeground(TEXTE_MUTED);
                    nb.setAlignmentX(Component.LEFT_ALIGNMENT);
                    panneauMesRdv.add(nb);
                    panneauMesRdv.add(Box.createVerticalStrut(10));

                    for (RendezVous r : rdvs) {
                        panneauMesRdv.add(carteRdv(r, email));
                        panneauMesRdv.add(Box.createVerticalStrut(8));
                    }
                }
            } else {
                panneauMesRdv.add(labelInfo("Erreur : " + rep.getMessage()));
            }
        } catch (Exception ex) {
            panneauMesRdv.add(labelInfo("Serveur non disponible."));
        }

        panneauMesRdv.revalidate();
        panneauMesRdv.repaint();
        cardLayout.show(panneauPrincipal, "MES_RDV");
    }

    
    private JPanel carteRdv(RendezVous r, String email) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(BLANC);
        panel.setBorder(new CompoundBorder(
            new LineBorder(GRIS_BORD, 1, true),
            new EmptyBorder(12, 14, 12, 14)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

       
        boolean confirme = "CONFIRME".equalsIgnoreCase(r.getStatut());
        JLabel statutBadge = badge(
            confirme ? "Confirmé" : "Annulé",
            confirme ? VERT_CLAIR : new Color(0xFEE2E2),
            confirme ? new Color(0x166534) : ROUGE
        );

        
        JPanel infos = new JPanel();
        infos.setBackground(BLANC);
        infos.setLayout(new BoxLayout(infos, BoxLayout.Y_AXIS));

        JLabel lblMedecin = bold("👨‍⚕️  " + (r.getNomMedecin() != null ? r.getNomMedecin() : "Médecin"));
        infos.add(lblMedecin);
        infos.add(Box.createVerticalStrut(4));

        JLabel lblDate = new JLabel("📅  " + r.getDate() + " à " + r.getHeure());
        lblDate.setFont(FONT_BODY);
        lblDate.setForeground(TEXTE);
        infos.add(lblDate);

        if (r.getMotif() != null && !r.getMotif().isBlank()) {
            JLabel lblMotif = new JLabel("📝  " + r.getMotif());
            lblMotif.setFont(FONT_SMALL);
            lblMotif.setForeground(TEXTE_MUTED);
            infos.add(Box.createVerticalStrut(2));
            infos.add(lblMotif);
        }

        panel.add(infos, BorderLayout.CENTER);

        
        JPanel actions = new JPanel();
        actions.setBackground(BLANC);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        statutBadge.setAlignmentX(Component.RIGHT_ALIGNMENT);
        actions.add(statutBadge);

        if (confirme) {
            actions.add(Box.createVerticalStrut(8));
            JButton btnAnnuler = new JButton("Annuler");
            btnAnnuler.setFont(FONT_SMALL);
            btnAnnuler.setForeground(ROUGE);
            btnAnnuler.setBackground(BLANC);
            btnAnnuler.setBorder(new LineBorder(ROUGE, 1, true));
            btnAnnuler.setFocusPainted(false);
            btnAnnuler.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnAnnuler.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnAnnuler.addActionListener(e -> annulerRdv(r, email));
            actions.add(btnAnnuler);
        }

        panel.add(actions, BorderLayout.EAST);
        return panel;
    }

   
    private void annulerRdv(RendezVous r, String email) {
        int choix = JOptionPane.showConfirmDialog(this,
            "Voulez-vous vraiment annuler ce rendez-vous ?\n\n"
                + r.getNomMedecin() + "\n" + r.getDate() + " à " + r.getHeure(),
            "Confirmer l'annulation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        if (choix != JOptionPane.YES_OPTION) return;

        try {
            Requete req = new Requete(Requete.Type.ANNULER_RDV, r.getId());
            Reponse rep = connexion.envoyer(req);
            if (rep.isSucces()) {
                JOptionPane.showMessageDialog(this,
                    "Rendez-vous annulé.",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                // Recharger la liste
                afficherMesRdv();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erreur : " + rep.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Serveur non disponible.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

   
    private JPanel carteMedecinAccueil(Medecin m) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(BLANC);
        panel.setBorder(new CompoundBorder(
            new LineBorder(GRIS_BORD, 1, true),
            new EmptyBorder(10, 12, 10, 12)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

       
        JLabel avatar = creerAvatar(m.getInitiales(), BLEU_CLAIR, BLEU_TEXTE);
        panel.add(avatar, BorderLayout.WEST);

    
        JPanel infos = new JPanel();
        infos.setBackground(BLANC);
        infos.setLayout(new BoxLayout(infos, BoxLayout.Y_AXIS));
        infos.add(bold(m.getNomComplet()));
        infos.add(muted(m.getSpecialite() + " — " + m.getVille()));
        boolean dispo = "Disponible".equalsIgnoreCase(m.getDisponibilite());
        infos.add(new JLabel("<html><font color='" + (dispo ? "#059669" : "#DC2626") + "'>⏰ "
                + (dispo ? "Disponible" : "Complet") + "</font></html>"));
        panel.add(infos, BorderLayout.CENTER);

        
        panel.add(badge(dispo ? "Dispo" : "Complet",
                dispo ? VERT_CLAIR : new Color(0xFEE2E2),
                dispo ? new Color(0x166534) : ROUGE), BorderLayout.EAST);

        
        if (dispo) {
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    afficherCreneaux(m);
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    panel.setBackground(GRIS_CLAIR);
                    infos.setBackground(GRIS_CLAIR);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    panel.setBackground(BLANC);
                    infos.setBackground(BLANC);
                }
            });
        }
        return panel;
    }

    
    private JPanel panneauCreneaux = new JPanel();

    private JPanel creerEcranCreneaux() {
        JPanel ecran = new JPanel(new BorderLayout());
        ecran.setBackground(FOND);
        ecran.add(barreRetour("Prendre rendez-vous", "MEDECINS", null), BorderLayout.NORTH);

        panneauCreneaux.setLayout(new BoxLayout(panneauCreneaux, BoxLayout.Y_AXIS));
        panneauCreneaux.setBackground(FOND);
        panneauCreneaux.setBorder(new EmptyBorder(12, 16, 12, 16));

        JScrollPane scroll = new JScrollPane(panneauCreneaux);
        scroll.setBorder(null);
        ecran.add(scroll, BorderLayout.CENTER);
        return ecran;
    }

    private void afficherCreneaux(Medecin medecin) {
        this.medecinSelectionne = medecin;
        panneauCreneaux.removeAll();

     
        panneauCreneaux.add(carteMedecinResume(medecin));
        panneauCreneaux.add(Box.createVerticalStrut(14));

        JLabel lblDate = new JLabel("Choisir une date");
        lblDate.setFont(FONT_BOLD);
        lblDate.setForeground(TEXTE);
        panneauCreneaux.add(lblDate);
        panneauCreneaux.add(Box.createVerticalStrut(6));

        
        panneauCreneaux.add(miniCalendrier());
        panneauCreneaux.add(Box.createVerticalStrut(12));

        JLabel lblCreneaux = new JLabel("Créneaux disponibles — " + dateSelectionnee.format(FORMAT_DATE));
        lblCreneaux.setFont(FONT_BOLD);
        lblCreneaux.setForeground(TEXTE);
        panneauCreneaux.add(lblCreneaux);
        panneauCreneaux.add(Box.createVerticalStrut(8));

      
        try {
            String dateStr = dateSelectionnee.format(FORMAT_DATE);
            CreneauRequete cr = new CreneauRequete(medecin.getId(), dateStr);
            Requete req = new Requete(Requete.Type.GET_CRENEAUX_DATE, cr);
            Reponse rep = connexion.envoyer(req);

            if (rep.isSucces()) {
                @SuppressWarnings("unchecked")
                List<String> creneauxList = (List<String>) rep.getDonnees();
                if (creneauxList == null || creneauxList.isEmpty()) {
                    panneauCreneaux.add(labelInfo("Aucun créneau disponible pour cette date."));
                } else {
                    panneauCreneaux.add(grilleCreneaux(creneauxList));
                }
            } else {
                panneauCreneaux.add(labelInfo("Erreur : " + rep.getMessage()));
            }
        } catch (Exception e) {
         
            panneauCreneaux.add(grilleCreneaux(
                java.util.Arrays.asList("09:00", "10:30", "11:00", "14:00", "15:30")));
        }
        panneauCreneaux.revalidate();
        panneauCreneaux.repaint();
        cardLayout.show(panneauPrincipal, "CRENEAUX");
    }

    
    private JTextField fNom, fPrenom, fTel, fEmail, fMotif;
    private JComboBox<String> fType;
    private JLabel banTitre;
    private JLabel banDate;

    private JPanel creerEcranFormulaire() {
        JPanel ecran = new JPanel(new BorderLayout());
        ecran.setBackground(FOND);
        ecran.add(barreRetour("Vos informations", "CRENEAUX", null), BorderLayout.NORTH);

        JPanel corps = new JPanel();
        corps.setLayout(new BoxLayout(corps, BoxLayout.Y_AXIS));
        corps.setBackground(FOND);
        corps.setBorder(new EmptyBorder(12, 16, 12, 16));

      
        JPanel banniere = new JPanel(new BorderLayout());
        banniere.setBackground(BLEU_CLAIR);
        banniere.setBorder(new CompoundBorder(
            new LineBorder(new Color(0x93C5FD), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        banniere.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        banTitre = new JLabel("Dr. — Spécialité");
        banTitre.setFont(FONT_BOLD);
        banTitre.setForeground(BLEU_TEXTE);

        banDate = new JLabel("📅  —");
        banDate.setFont(FONT_SMALL);
        banDate.setForeground(new Color(0x1D4ED8));
        banniere.add(banTitre, BorderLayout.NORTH);
        banniere.add(banDate,  BorderLayout.SOUTH);
        corps.add(banniere);
        corps.add(Box.createVerticalStrut(14));

        fNom    = champTexte(corps, "Nom *", "");
        fPrenom = champTexte(corps, "Prénom *", "");
        fTel    = champTexte(corps, "Téléphone *", "");
        fEmail  = champTexte(corps, "E-mail *", "");
        fMotif  = champTexte(corps, "Motif de consultation *", "");

    
        corps.add(labelChamp("Type de consultation"));
        fType = new JComboBox<>(new String[]{"Présentiel", "Téléconsultation"});
        fType.setFont(FONT_BODY);
        fType.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        fType.setBackground(BLANC);
        corps.add(fType);
        corps.add(Box.createVerticalStrut(16));

    
        JButton btnConfirmer = boutonPrimaire("✓  Confirmer le rendez-vous");
        btnConfirmer.addActionListener(e -> soumettreFormulaire());
        corps.add(btnConfirmer);
        corps.add(Box.createVerticalStrut(8));

        JButton btnAnnuler = boutonSecondaire("Annuler");
        btnAnnuler.addActionListener(e -> cardLayout.show(panneauPrincipal, "CRENEAUX"));
        corps.add(btnAnnuler);

        JScrollPane scroll = new JScrollPane(corps);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        ecran.add(scroll, BorderLayout.CENTER);
        return ecran;
    }

    private void ouvrirFormulaire() {
       
        if (medecinSelectionne != null) {
            banTitre.setText(medecinSelectionne.getNomComplet()
                    + " — " + medecinSelectionne.getSpecialite());
        } else {
            banTitre.setText("Dr. — Spécialité");
        }

        String creneauAffiche = (creneauSelectionne != null ? creneauSelectionne : "--:--");
        banDate.setText("📅  " + dateSelectionnee.format(FORMAT_DATE) + " à " + creneauAffiche);

      
        if (patientCourant != null) {
            fNom.setText(patientCourant.getNom());
            fPrenom.setText(patientCourant.getPrenom());
            fEmail.setText(patientCourant.getEmail());
            fTel.setText(patientCourant.getTelephone());
        }
        
        fMotif.setText("");

        cardLayout.show(panneauPrincipal, "FORMULAIRE");
    }
    private void soumettreFormulaire() {
      
        if (fNom.getText().isBlank() || fPrenom.getText().isBlank()
                || fTel.getText().isBlank() || fEmail.getText().isBlank()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez remplir tous les champs obligatoires (*).",
                "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }

     
        if (medecinSelectionne == null || creneauSelectionne == null) {
            JOptionPane.showMessageDialog(this,
                "Aucun médecin ou créneau sélectionné. Retour à l'accueil.",
                "Erreur", JOptionPane.WARNING_MESSAGE);
            cardLayout.show(panneauPrincipal, "ACCUEIL");
            return;
        }

       
        Patient patientSaisi = new Patient(
            fNom.getText().trim(),
            fPrenom.getText().trim(),
            fEmail.getText().trim(),
            fTel.getText().trim()
        );
        try {
            Requete reqPatient = new Requete(Requete.Type.ENREGISTRER_PATIENT, patientSaisi);
            Reponse repPatient = connexion.envoyer(reqPatient);
            if (repPatient.isSucces()) {
                patientCourant = (Patient) repPatient.getDonnees();
            }
        } catch (Exception ex) {
           
            System.err.println("[CLIENT] Impossible d'enregistrer le profil : " + ex.getMessage());
        }

        
        String nomMedecin = medecinSelectionne.getNomComplet();
        int idMedecin     = medecinSelectionne.getId();
        String creneau    = creneauSelectionne;

        RendezVous rdv = new RendezVous(
            fNom.getText().trim(),
            fPrenom.getText().trim(),
            fTel.getText().trim(),
            fEmail.getText().trim(),
            fMotif.getText().trim(),
            (String) fType.getSelectedItem(),
            idMedecin, nomMedecin,
            dateSelectionnee.format(FORMAT_DATE), creneau
        );

        try {
            Requete req = new Requete(Requete.Type.CREER_RDV, rdv);
            Reponse rep = connexion.envoyer(req);
            if (rep.isSucces()) {
                afficherConfirmation((RendezVous) rep.getDonnees());
            } else {
                JOptionPane.showMessageDialog(this,
                    rep.getMessage() + "\n\nVeuillez choisir un autre créneau.",
                    "Créneau indisponible", JOptionPane.WARNING_MESSAGE);
                cardLayout.show(panneauPrincipal, "CRENEAUX");
            }
        } catch (Exception e) {
            rdv.setId(999);
            afficherConfirmation(rdv);
        }
    }

   
    private JLabel confPatient, confMedecin, confDate, confEmail;

    private JPanel creerEcranConfirmation() {
        JPanel ecran = new JPanel(new BorderLayout());
        ecran.setBackground(FOND);

       
        JPanel topBar = new JPanel();
        topBar.setBackground(BLEU);
        topBar.setPreferredSize(new Dimension(420, 52));
        JLabel logoConf = new JLabel("MédiRDV");
        logoConf.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoConf.setForeground(BLANC);
        topBar.add(logoConf);
        ecran.add(topBar, BorderLayout.NORTH);

        JPanel corps = new JPanel();
        corps.setLayout(new BoxLayout(corps, BoxLayout.Y_AXIS));
        corps.setBackground(FOND);
        corps.setBorder(new EmptyBorder(24, 20, 20, 20));

        
        JLabel icone = new JLabel("✓");
        icone.setFont(new Font("Segoe UI", Font.BOLD, 36));
        icone.setForeground(VERT);
        icone.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel cercleFond = new JPanel();
        cercleFond.setBackground(VERT_CLAIR);
        cercleFond.setPreferredSize(new Dimension(68, 68));
        cercleFond.setMaximumSize(new Dimension(68, 68));
        cercleFond.setLayout(new GridBagLayout());
        cercleFond.add(icone);
        cercleFond.setAlignmentX(Component.CENTER_ALIGNMENT);
        corps.add(cercleFond);
        corps.add(Box.createVerticalStrut(12));

        JLabel titre = new JLabel("Rendez-vous confirmé !");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titre.setForeground(TEXTE);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);
        corps.add(titre);

        JLabel sousTitre = new JLabel("Un e-mail de confirmation vous a été envoyé.");
        sousTitre.setFont(FONT_SOUS);
        sousTitre.setForeground(TEXTE_MUTED);
        sousTitre.setAlignmentX(Component.CENTER_ALIGNMENT);
        corps.add(sousTitre);
        corps.add(Box.createVerticalStrut(16));

     
        JPanel recap = new JPanel();
        recap.setLayout(new BoxLayout(recap, BoxLayout.Y_AXIS));
        recap.setBackground(GRIS_CLAIR);
        recap.setBorder(new CompoundBorder(
            new LineBorder(GRIS_BORD, 1, true),
            new EmptyBorder(12, 14, 12, 14)
        ));
        recap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        confMedecin = ligneRecap("👨‍⚕️", "Dr. Soa Marie — Cardiologue");
        confDate    = ligneRecap("📅", "Jeudi 18 sept. 2026 à 10:30");
        confPatient = ligneRecap("👤", "Fenotiana ANDRIAMANALINA");
        confEmail   = ligneRecap("✉️", "roussinahandriamanalina@gmail.com");

        recap.add(confMedecin);
        recap.add(Box.createVerticalStrut(8));
        recap.add(confDate);
        recap.add(Box.createVerticalStrut(8));
        recap.add(confPatient);
        recap.add(Box.createVerticalStrut(8));
        recap.add(confEmail);
        corps.add(recap);
        corps.add(Box.createVerticalStrut(16));

        JButton btnAccueil = boutonPrimaire("Retour à l'accueil");
        btnAccueil.addActionListener(e -> {
            cardLayout.show(panneauPrincipal, "ACCUEIL");
            viderFormulaire();
        });
        corps.add(btnAccueil);

        JScrollPane scroll = new JScrollPane(corps);
        scroll.setBorder(null);
        ecran.add(scroll, BorderLayout.CENTER);
        return ecran;
    }

    private void afficherConfirmation(RendezVous rdv) {
        confPatient.setText("👤  " + rdv.getPrenomPatient() + " " + rdv.getNomPatient());
        confMedecin.setText("👨‍⚕️  " + rdv.getNomMedecin());
        confDate.setText("📅  " + rdv.getDate() + " à " + rdv.getHeure());
        confEmail.setText("✉️  " + rdv.getEmail());
        cardLayout.show(panneauPrincipal, "CONFIRMATION");
    }

    private void viderFormulaire() {
        fNom.setText(""); fPrenom.setText("");
        fTel.setText(""); fEmail.setText(""); fMotif.setText("");
        creneauSelectionne = null;
    }

    

    private JLabel labelSection(String texte) {
        JLabel lbl = new JLabel(texte);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(TEXTE);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel labelInfo(String texte) {
        JLabel lbl = new JLabel(texte);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(TEXTE_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel labelChamp(String texte) {
        JLabel lbl = new JLabel(texte);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXTE_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField champTexte(JPanel parent, String label, String valeur) {
        parent.add(labelChamp(label));
        JTextField field = new JTextField(valeur);
        field.setFont(FONT_BODY);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBorder(new CompoundBorder(
            new LineBorder(GRIS_BORD, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        parent.add(field);
        parent.add(Box.createVerticalStrut(8));
        return field;
    }

   
    private JButton carteSpecialite(String emoji, String nom) {
        JButton btn = new JButton("<html><center>" + emoji + "<br><b>" + nom + "</b></center></html>");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btn.setBackground(BLANC);
        btn.setForeground(TEXTE);
        btn.setBorder(new CompoundBorder(
            new LineBorder(GRIS_BORD, 1, true),
            new EmptyBorder(10, 6, 10, 6)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);

       
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(BLEU_CLAIR);
                btn.setBorder(new CompoundBorder(
                    new LineBorder(BLEU, 1, true),
                    new EmptyBorder(10, 6, 10, 6)
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(BLANC);
                btn.setBorder(new CompoundBorder(
                    new LineBorder(GRIS_BORD, 1, true),
                    new EmptyBorder(10, 6, 10, 6)
                ));
            }
        });
        return btn;
    }

    private JPanel carteMedecinSimple(String initiales, String nom, String spec,
                                       String dispo, String statut) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(BLANC);
        panel.setBorder(new CompoundBorder(
            new LineBorder(GRIS_BORD, 1, true),
            new EmptyBorder(10, 12, 10, 12)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

   
        JLabel avatar = creerAvatar(initiales, BLEU_CLAIR, BLEU_TEXTE);
        panel.add(avatar, BorderLayout.WEST);

     
        JPanel infos = new JPanel();
        infos.setBackground(BLANC);
        infos.setLayout(new BoxLayout(infos, BoxLayout.Y_AXIS));
        infos.add(bold(nom));
        infos.add(muted(spec));
        infos.add(new JLabel("<html><font color='#059669'>⏰ " + dispo + "</font></html>"));
        panel.add(infos, BorderLayout.CENTER);

     
        panel.add(badge(statut, VERT_CLAIR, new Color(0x166534)), BorderLayout.EAST);
        return panel;
    }

    private JPanel carteMedecinDetaille(Medecin m) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(BLANC);
        panel.setBorder(new CompoundBorder(
            new LineBorder(GRIS_BORD, 1, true),
            new EmptyBorder(12, 14, 12, 14)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel avatar = creerAvatar(m.getInitiales(), BLEU_CLAIR, BLEU_TEXTE);
        panel.add(avatar, BorderLayout.WEST);

        JPanel infos = new JPanel();
        infos.setBackground(BLANC);
        infos.setLayout(new BoxLayout(infos, BoxLayout.Y_AXIS));
        infos.add(bold(m.getNomComplet()));
        infos.add(muted(m.getSpecialite() + " — " + m.getVille()));
        String etoiles = "★".repeat((int)m.getRating()) + "☆".repeat(5-(int)m.getRating());
        infos.add(new JLabel("<html><font color='#F59E0B'>" + etoiles
            + "</font> <font color='#64748B'>(" + m.getNbAvis() + " avis)</font></html>"));

        boolean dispo = m.getDisponibilite().equals("Disponible");
        JLabel dispLbl = new JLabel("<html><font color='" + (dispo ? "#059669" : "#DC2626") + "'>"
            + (dispo ? "✓ Disponible" : "✗ Complet") + "</font></html>");
        infos.add(dispLbl);
        panel.add(infos, BorderLayout.CENTER);

        if (dispo) {
            panel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    afficherCreneaux(m);
                }
                public void mouseEntered(MouseEvent e) { panel.setBackground(GRIS_CLAIR); infos.setBackground(GRIS_CLAIR); }
                public void mouseExited(MouseEvent e)  { panel.setBackground(BLANC); infos.setBackground(BLANC); }
            });
        }
        return panel;
    }

    private JPanel carteMedecinResume(Medecin m) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(BLANC);
        p.setBorder(new CompoundBorder(
            new LineBorder(GRIS_BORD, 1, true),
            new EmptyBorder(10, 12, 10, 12)
        ));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        p.add(creerAvatar(m.getInitiales(), BLEU_CLAIR, BLEU_TEXTE), BorderLayout.WEST);
        JPanel infos = new JPanel();
        infos.setBackground(BLANC);
        infos.setLayout(new BoxLayout(infos, BoxLayout.Y_AXIS));
        infos.add(bold(m.getNomComplet()));
        infos.add(muted(m.getSpecialite()));
        String e = "★".repeat((int)m.getRating());
        infos.add(new JLabel("<html><font color='#F59E0B'>" + e + "</font> " + m.getRating() + "</html>"));
        p.add(infos, BorderLayout.CENTER);
        return p;
    }
        private JPanel miniCalendrier() {
           
            JPanel wrapper = new JPanel();
            wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
            wrapper.setBackground(FOND);
            wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

         
            DateTimeFormatter moisFormat = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH);
            String titreMois = dateSelectionnee.format(moisFormat);
         
            titreMois = titreMois.substring(0, 1).toUpperCase() + titreMois.substring(1);

            JLabel lblMois = new JLabel(titreMois, SwingConstants.CENTER);
            lblMois.setFont(FONT_BOLD);
            lblMois.setForeground(TEXTE);
            lblMois.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblMois.setBorder(new EmptyBorder(0, 0, 4, 0));
            lblMois.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
            wrapper.add(lblMois);
         
            JPanel p = new JPanel(new GridLayout(0, 7, 3, 3));
            p.setBackground(FOND);
            p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
            p.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            String[] joursSemaine = {"L", "M", "M", "J", "V", "S", "D"};
        for (String j : joursSemaine) {
            JLabel l = new JLabel(j, SwingConstants.CENTER);
            l.setFont(FONT_SMALL);
            l.setForeground(TEXTE_MUTED);
            p.add(l);
        }

       
        YearMonth mois = YearMonth.from(dateSelectionnee);
        LocalDate premierJour = mois.atDay(1);
        int totalJours = mois.lengthOfMonth();
        LocalDate aujourdHui = LocalDate.now();

    
        int decalage = premierJour.getDayOfWeek().getValue() - 1;

      
        for (int i = 0; i < decalage; i++) {
            p.add(new JLabel(""));
        }

       
        for (int jour = 1; jour <= totalJours; jour++) {
            LocalDate dateDuJour = mois.atDay(jour);
            JButton btn = new JButton(String.valueOf(jour));
            btn.setFont(FONT_SMALL);
            btn.setMargin(new Insets(2, 2, 2, 2));
            btn.setFocusPainted(false);

            boolean estPasse  = dateDuJour.isBefore(aujourdHui);
            boolean estAujourdHui = dateDuJour.isEqual(aujourdHui);
            boolean estSelectionne = dateDuJour.isEqual(dateSelectionnee);

            if (estAujourdHui || estSelectionne) {
                btn.setBackground(BLEU);
                btn.setForeground(BLANC);
            } else if (estPasse) {
                btn.setBackground(FOND);
                btn.setForeground(TEXTE_MUTED);
                btn.setEnabled(false); 
            } else {
                btn.setBackground(BLEU_CLAIR);
                btn.setForeground(BLEU_TEXTE);
            }
            btn.setBorder(new LineBorder(GRIS_BORD, 1, true));

            
            final LocalDate dateCliquee = dateDuJour;
            if (!estPasse) {
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn.addActionListener(e -> {
                    dateSelectionnee = dateCliquee;
                    if (medecinSelectionne != null) {
                        afficherCreneaux(medecinSelectionne); 
                    }
                });
            }

            p.add(btn);
        }

       
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        wrapper.add(p);
        return wrapper;
    }


    private JPanel grilleCreneaux(List<String> creneauxList) {
        JPanel p = new JPanel(new GridLayout(0, 3, 8, 8));
        p.setBackground(FOND);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        ButtonGroup group = new ButtonGroup();

        for (String h : creneauxList) {
            JToggleButton btn = new JToggleButton(h);
            btn.setFont(FONT_BODY);
            btn.setBackground(BLANC);
            btn.setForeground(TEXTE);
            btn.setBorder(new CompoundBorder(
                new LineBorder(GRIS_BORD, 1, true),
                new EmptyBorder(6, 4, 6, 4)
            ));
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            group.add(btn);
            btn.addActionListener(e -> {
                creneauSelectionne = h;
                btn.setBackground(BLEU);
                btn.setForeground(BLANC);
            });
            p.add(btn);
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(FOND);
        wrapper.add(p, BorderLayout.CENTER);
        wrapper.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);

        
        JButton btnCont = boutonPrimaire("Continuer");
        btnCont.addActionListener(e -> {
            if (creneauSelectionne == null) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un créneau.");
                return;
            }
            ouvrirFormulaire();
        });

        JPanel container = new JPanel();
        container.setBackground(FOND);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.add(wrapper);
        container.add(Box.createVerticalStrut(10));
        container.add(btnCont);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        return container;
    }

    private JLabel ligneRecap(String icone, String texte) {
        JLabel lbl = new JLabel(icone + "  " + texte);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(TEXTE);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel barreRetour(String titre, String destination, Runnable action) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BLEU);
        bar.setPreferredSize(new Dimension(420, 50));
        bar.setBorder(new EmptyBorder(0, 12, 0, 12));

        JButton btnBack = new JButton("←");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnBack.setForeground(BLANC);
        btnBack.setBackground(BLEU);
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> {
            if (action != null) action.run();
            cardLayout.show(panneauPrincipal, destination);
        });
        bar.add(btnBack, BorderLayout.WEST);

        JLabel titreLbl = new JLabel(titre);
        titreLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titreLbl.setForeground(BLANC);
        bar.add(titreLbl, BorderLayout.CENTER);
        return bar;
    }

    private JPanel barreNavigation(String actif) {
        
        String[][] items = {
            {"🏠", "Accueil",   "ACCUEIL"},
            {"🔍", "Chercher",  "RECHERCHE"},
            {"📅", "Mes RDV",   "MES_RDV"},
            {"👤", "Profil",    "PROFIL"}
        };

        JPanel nav = new JPanel(new GridLayout(1, 4));
        nav.setBackground(BLANC);
        nav.setBorder(new MatteBorder(1, 0, 0, 0, GRIS_BORD));
        nav.setPreferredSize(new Dimension(420, 52));

        for (String[] item : items) {
            String icone  = item[0];
            String label  = item[1];
            String ecran  = item[2];
            boolean estActif = ecran.equals(actif);

            JButton btn = new JButton("<html><center>" + icone + "<br>" + label + "</center></html>");
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btn.setForeground(estActif ? BLEU : TEXTE_MUTED);
            btn.setBackground(BLANC);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> naviguerVers(ecran));

            nav.add(btn);
        }
        return nav;
    }
    private void naviguerVers(String nomEcran) {
        switch (nomEcran) {
            case "ACCUEIL":
                cardLayout.show(panneauPrincipal, "ACCUEIL");
                break;
            case "RECHERCHE":
            	 cardLayout.show(panneauPrincipal, "RECHERCHE");
            	    break;
                 case "MES_RDV":
                	 afficherMesRdv();
                break;
            case "PROFIL":
            	afficherProfil();
                break;
        }
    }

    
    private JLabel creerAvatar(String initiales, Color fond, Color texte) {
        JLabel av = new JLabel(initiales, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fond);
                g2.fillOval(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        av.setFont(new Font("Segoe UI", Font.BOLD, 13));
        av.setForeground(texte);
        av.setOpaque(false);
        av.setPreferredSize(new Dimension(42, 42));
        av.setMinimumSize(new Dimension(42, 42));
        av.setMaximumSize(new Dimension(42, 42));
        return av;
    }

    private JLabel bold(String texte) {
        JLabel l = new JLabel(texte);
        l.setFont(FONT_BOLD); l.setForeground(TEXTE);
        return l;
    }

    private JLabel muted(String texte) {
        JLabel l = new JLabel(texte);
        l.setFont(FONT_SMALL); l.setForeground(TEXTE_MUTED);
        return l;
    }

    private JLabel badge(String texte, Color fond, Color couleur) {
        JLabel l = new JLabel(texte);
        l.setFont(FONT_SMALL); l.setForeground(couleur);
        l.setBackground(fond); l.setOpaque(true);
        l.setBorder(new CompoundBorder(
            new LineBorder(fond, 1, true),
            new EmptyBorder(2, 7, 2, 7)
        ));
        return l;
    }

    private JButton boutonPrimaire(String texte) {
        JButton btn = new JButton(texte);
        btn.setFont(FONT_BOLD);
        btn.setBackground(BLEU); btn.setForeground(BLANC);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    private JButton boutonSecondaire(String texte) {
        JButton btn = new JButton(texte);
        btn.setFont(FONT_BODY);
        btn.setBackground(BLANC); btn.setForeground(BLEU);
        btn.setBorder(new LineBorder(BLEU, 1, true));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception e) { /* ignore */ }
            new AppMediRDV();
        });
    }
}
