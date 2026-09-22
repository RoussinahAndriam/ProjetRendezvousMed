package com.medirdv.ui;

import com.medirdv.client.ConnexionServeurclient;
import com.medirdv.model.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;

/**
 * Interface graphique MédiRDV — style identique aux maquettes.
 * Couleur principale : #1A56DB (bleu médical)
 */
public class AppMediRDV extends JFrame {

    // ── Palette couleurs (maquette) ─────────────────
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

    // Données de session
    private Medecin medecinSelectionne;
    private String creneauSelectionne;
    private String dateSelectionnee = "18 sept. 2026";

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
        panneauPrincipal.add(creerEcranCreneaux(),     "CRENEAUX");
        panneauPrincipal.add(creerEcranFormulaire(),   "FORMULAIRE");
        panneauPrincipal.add(creerEcranConfirmation(), "CONFIRMATION");

        add(panneauPrincipal);
        cardLayout.show(panneauPrincipal, "ACCUEIL");
        setVisible(true);
    }

    // ── CONNEXION AU SERVEUR ─────────────────────────
    private void connecterAuServeur() {
        try {
            connexion.connecter();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Impossible de se connecter au serveur.\nVérifiez que le serveur est lancé sur le port 5000.",
                "Erreur de connexion", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ── ÉCRAN 1 : ACCUEIL ──────────────────────────
    private JPanel creerEcranAccueil() {
        JPanel ecran = new JPanel(new BorderLayout());
        ecran.setBackground(FOND);

        // HERO bleu
        JPanel hero = new JPanel();
        hero.setBackground(BLEU);
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBorder(new EmptyBorder(24, 20, 20, 20));

        JLabel logo = new JLabel("MédiRDV");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setForeground(BLANC);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sous = new JLabel("Prenez rendez-vous en ligne");
        sous.setFont(FONT_SOUS);
        sous.setForeground(new Color(0xBFDBFE));
        sous.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Barre de recherche
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBackground(new Color(0x2563EB));
        searchPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(0x3B82F6), 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        JTextField searchField = new JTextField("Médecin ou spécialité...");
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
        searchPanel.add(new JLabel("🔍"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        hero.add(logo);
        hero.add(Box.createVerticalStrut(4));
        hero.add(sous);
        hero.add(Box.createVerticalStrut(14));
        hero.add(searchPanel);

        // Corps
        JPanel corps = new JPanel();
        corps.setBackground(FOND);
        corps.setLayout(new BoxLayout(corps, BoxLayout.Y_AXIS));
        corps.setBorder(new EmptyBorder(16, 16, 16, 16));

        corps.add(labelSection("Spécialités"));
        corps.add(Box.createVerticalStrut(8));

        // Grille spécialités 2x2
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

        // 2 médecins du jour
        String[][] medecinsDuJour = {
            {"DR", "Dr. Rakoto Jean", "Généraliste", "Disponible à 14h00", "Dispo"},
            {"DS", "Dr. Soa Marie", "Cardiologue", "Disponible à 15h30", "Dispo"}
        };
        for (String[] m : medecinsDuJour) {
            corps.add(carteMedecinSimple(m[0], m[1], m[2], m[3], m[4]));
            corps.add(Box.createVerticalStrut(8));
        }

        JScrollPane scroll = new JScrollPane(corps);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        ecran.add(hero, BorderLayout.NORTH);
        ecran.add(scroll, BorderLayout.CENTER);
        ecran.add(barreNavigation("ACCUEIL"), BorderLayout.SOUTH);
        return ecran;
    }

    // ── ÉCRAN 2 : LISTE MÉDECINS ────────────────────
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

        // Titre
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
            // Mode démo sans serveur
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

    // ── ÉCRAN 3 : CRÉNEAUX ──────────────────────────
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

        // Carte médecin
        panneauCreneaux.add(carteMedecinResume(medecin));
        panneauCreneaux.add(Box.createVerticalStrut(14));

        JLabel lblDate = new JLabel("Choisir une date");
        lblDate.setFont(FONT_BOLD);
        lblDate.setForeground(TEXTE);
        panneauCreneaux.add(lblDate);
        panneauCreneaux.add(Box.createVerticalStrut(6));

        // Mini calendrier (simulé)
        panneauCreneaux.add(miniCalendrier());
        panneauCreneaux.add(Box.createVerticalStrut(12));

        JLabel lblCreneaux = new JLabel("Créneaux disponibles — " + dateSelectionnee);
        lblCreneaux.setFont(FONT_BOLD);
        lblCreneaux.setForeground(TEXTE);
        panneauCreneaux.add(lblCreneaux);
        panneauCreneaux.add(Box.createVerticalStrut(8));

        // Récupérer créneaux depuis le serveur
        try {
            Requete req = new Requete(Requete.Type.GET_CRENEAUX, medecin.getId());
            Reponse rep = connexion.envoyer(req);
            @SuppressWarnings("unchecked")
            List<String> creneauxList = (List<String>) rep.getDonnees();
            panneauCreneaux.add(grilleCreneaux(creneauxList));
        } catch (Exception e) {
            // Mode démo
            panneauCreneaux.add(grilleCreneaux(
                java.util.Arrays.asList("09:00", "10:30", "11:00", "14:00", "15:30")));
        }

        panneauCreneaux.revalidate();
        panneauCreneaux.repaint();
        cardLayout.show(panneauPrincipal, "CRENEAUX");
    }

    // ── ÉCRAN 4 : FORMULAIRE ───────────────────────
    private JTextField fNom, fPrenom, fTel, fEmail, fMotif;
    private JComboBox<String> fType;

    private JPanel creerEcranFormulaire() {
        JPanel ecran = new JPanel(new BorderLayout());
        ecran.setBackground(FOND);
        ecran.add(barreRetour("Vos informations", "CRENEAUX", null), BorderLayout.NORTH);

        JPanel corps = new JPanel();
        corps.setLayout(new BoxLayout(corps, BoxLayout.Y_AXIS));
        corps.setBackground(FOND);
        corps.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Bannière médecin+créneau (mise à jour dynamiquement)
        JPanel banniere = new JPanel(new BorderLayout());
        banniere.setBackground(BLEU_CLAIR);
        banniere.setBorder(new CompoundBorder(
            new LineBorder(new Color(0x93C5FD), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        banniere.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JLabel banTitre = new JLabel("Dr. Soa Marie — Cardiologue");
        banTitre.setFont(FONT_BOLD);
        banTitre.setForeground(BLEU_TEXTE);
        JLabel banDate = new JLabel("📅  " + dateSelectionnee + " à 10:30");
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

        // Type consultation
        corps.add(labelChamp("Type de consultation"));
        fType = new JComboBox<>(new String[]{"Présentiel", "Téléconsultation"});
        fType.setFont(FONT_BODY);
        fType.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        fType.setBackground(BLANC);
        corps.add(fType);
        corps.add(Box.createVerticalStrut(16));

        // Bouton Confirmer
        JButton btnConfirmer = boutonPrimaire("✓  Confirmer le rendez-vous");
        btnConfirmer.addActionListener(e -> soumettreFormulaire(banTitre, banDate));
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
        cardLayout.show(panneauPrincipal, "FORMULAIRE");
    }

    private void soumettreFormulaire(JLabel banTitre, JLabel banDate) {
        // Validation
        if (fNom.getText().isBlank() || fPrenom.getText().isBlank()
                || fTel.getText().isBlank() || fEmail.getText().isBlank()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez remplir tous les champs obligatoires (*).",
                "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Créer le RDV
        String nomMedecin = medecinSelectionne != null ? medecinSelectionne.getNomComplet() : "Dr. Soa Marie";
        int idMedecin     = medecinSelectionne != null ? medecinSelectionne.getId() : 1;
        String creneau    = creneauSelectionne != null ? creneauSelectionne : "10:30";

        RendezVous rdv = new RendezVous(
            fNom.getText().trim(),
            fPrenom.getText().trim(),
            fTel.getText().trim(),
            fEmail.getText().trim(),
            fMotif.getText().trim(),
            (String) fType.getSelectedItem(),
            idMedecin, nomMedecin,
            dateSelectionnee, creneau
        );

        try {
            Requete req = new Requete(Requete.Type.CREER_RDV, rdv);
            Reponse rep = connexion.envoyer(req);
            if (rep.isSucces()) {
                afficherConfirmation((RendezVous) rep.getDonnees());
            } else {
                JOptionPane.showMessageDialog(this, "Erreur : " + rep.getMessage());
            }
        } catch (Exception e) {
            // Mode démo sans serveur
            rdv.setId(999);
            afficherConfirmation(rdv);
        }
    }

    // ── ÉCRAN 5 : CONFIRMATION ─────────────────────
    private JLabel confPatient, confMedecin, confDate, confEmail;

    private JPanel creerEcranConfirmation() {
        JPanel ecran = new JPanel(new BorderLayout());
        ecran.setBackground(FOND);

        // Petite barre bleu
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

        // Icône succès
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

        // Récapitulatif
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

    // ── COMPOSANTS RÉUTILISABLES ────────────────────

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
        btn.setFont(FONT_SMALL);
        btn.setBackground(BLANC);
        btn.setForeground(TEXTE);
        btn.setBorder(new CompoundBorder(
            new LineBorder(GRIS_BORD, 1, true),
            new EmptyBorder(10, 6, 10, 6)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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

        // Avatar
        JLabel avatar = creerAvatar(initiales, BLEU_CLAIR, BLEU_TEXTE);
        panel.add(avatar, BorderLayout.WEST);

        // Infos
        JPanel infos = new JPanel();
        infos.setBackground(BLANC);
        infos.setLayout(new BoxLayout(infos, BoxLayout.Y_AXIS));
        infos.add(bold(nom));
        infos.add(muted(spec));
        infos.add(new JLabel("<html><font color='#059669'>⏰ " + dispo + "</font></html>"));
        panel.add(infos, BorderLayout.CENTER);

        // Badge
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
        JPanel p = new JPanel(new GridLayout(5, 7, 3, 3));
        p.setBackground(FOND);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        String[] jours = {"L","M","M","J","V","S","D"};
        for (String j : jours) {
            JLabel l = new JLabel(j, SwingConstants.CENTER);
            l.setFont(FONT_SMALL); l.setForeground(TEXTE_MUTED);
            p.add(l);
        }
        // Semaine exemple
        int[] nums = {0,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26};
        boolean[] dispos = {false,false,true,false,true,false,false,true,false,true,true,false,false,false,true,false,true,true,false,false,false,false,false,true,true,false,false,false};
        for (int i = 0; i < 28; i++) {
            if (nums[i] == 0) { p.add(new JLabel("")); continue; }
            JButton btn = new JButton("" + nums[i]);
            btn.setFont(FONT_SMALL);
            btn.setMargin(new Insets(2,2,2,2));
            btn.setFocusPainted(false);
            if (nums[i] == 18) {
                btn.setBackground(BLEU); btn.setForeground(BLANC);
            } else if (dispos[i]) {
                btn.setBackground(BLEU_CLAIR); btn.setForeground(BLEU_TEXTE);
            } else {
                btn.setBackground(FOND); btn.setForeground(TEXTE_MUTED);
                btn.setEnabled(false);
            }
            btn.setBorder(new LineBorder(GRIS_BORD, 1, true));
            final int jour = nums[i];
            btn.addActionListener(e -> dateSelectionnee = jour + " sept. 2026");
            p.add(btn);
        }
        return p;
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

        // Bouton Continuer
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
        // Chaque entrée : icône + label + nom de l'écran cible
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
    }/**
     * Navigation depuis la barre du bas.
     * Pour l'instant, seuls ACCUEIL existe vraiment ; les autres affichent un message.
     * On les créera dans les prochaines étapes.
     */
    private void naviguerVers(String nomEcran) {
        switch (nomEcran) {
            case "ACCUEIL":
                cardLayout.show(panneauPrincipal, "ACCUEIL");
                break;
            case "RECHERCHE":
                JOptionPane.showMessageDialog(this,
                    "L'écran de recherche sera disponible dans une prochaine version.",
                    "Bientôt disponible", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "MES_RDV":
                JOptionPane.showMessageDialog(this,
                    "L'écran 'Mes rendez-vous' sera disponible dans une prochaine version.",
                    "Bientôt disponible", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "PROFIL":
                JOptionPane.showMessageDialog(this,
                    "L'écran 'Profil' sera disponible dans une prochaine version.",
                    "Bientôt disponible", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }

    // Helpers visuels
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

    // ── MAIN ───────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception e) { /* ignore */ }
            new AppMediRDV();
        });
    }
}
