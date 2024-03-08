package test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManagementSystem extends JFrame {
    private JMenuBar menuBar;
    private JMenu menuAjouter, menuNotes, menuAffichage;
    private JMenuItem menuItemNouveau, menuItemModifier, menuItemSupprimer, menuItemQuitter;
    private JMenuItem menuItemSaisir, menuItemNotesModifier;
    private JMenuItem menuItemResultats, menuItemExporter, menuItemDeliberations, menuItemExportExcel;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private StudentManagementSystem studentManagementSystem;

    public ManagementSystem() {
        // Configuration de la fenêtre principale
        setTitle("Gestion RSSP2");
        setSize(900, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        studentManagementSystem = new StudentManagementSystem();

        // Initialisation des éléments du menu et des actions
        initMenu();

        // Ajout des panneaux de carte
        cardPanel.add(new JLabel("Home Panel"), "Home");
        cardPanel.add(studentManagementSystem.createStudentFormPanel(), "NewStudent");
        cardPanel.add(studentManagementSystem.createStudentModificationPanel(), "ModifyStudent");
        cardPanel.add(studentManagementSystem.createGradesFormPanel(), "EnterGrades");
        cardPanel.add(studentManagementSystem.createGradesModificationPanel(), "ModifyGrades");

        // Ajout du panneau de carte au cadre
        add(cardPanel, BorderLayout.CENTER);

        // Affichage du panneau principal (d'accueil) initialement
        cardLayout.show(cardPanel, "Home");
    }

    private void initMenu() {
        menuBar = new JMenuBar();
        
        // Menu Ajouter
        menuAjouter = new JMenu("Ajouter");
        menuItemNouveau = new JMenuItem("Nouveau");
        menuItemNouveau.addActionListener(e -> cardLayout.show(cardPanel, "NewStudent"));
        menuAjouter.add(menuItemNouveau);

        // Menu Modifier
        menuItemModifier = new JMenuItem("Modifier");
        menuItemModifier.addActionListener(e -> cardLayout.show(cardPanel, "ModifyStudent"));
        menuAjouter.add(menuItemModifier);
        
     // Ajout du bouton "Supprimer"
        menuItemSupprimer = new JMenuItem("Supprimer");
        menuItemSupprimer.addActionListener(e -> {
            // Logique de suppression ici
        });
        menuAjouter.add(menuItemSupprimer);

        // Ajout du bouton "Quitter"
        menuItemQuitter = new JMenuItem("Quitter");
        menuItemQuitter.addActionListener(e -> {
            // Logique pour quitter l'application
            System.exit(0);
        });
        menuAjouter.add(menuItemQuitter);

        // Menu Notes
        menuNotes = new JMenu("Notes");
        menuItemSaisir = new JMenuItem("Saisir");
        menuItemSaisir.addActionListener(e -> cardLayout.show(cardPanel, "EnterGrades"));
        menuNotes.add(menuItemSaisir);

        menuItemNotesModifier = new JMenuItem("Modifier les Notes");
        menuItemNotesModifier.addActionListener(e -> cardLayout.show(cardPanel, "ModifyGrades"));
        menuNotes.add(menuItemNotesModifier);

        
        // Menu Affichage avec option d'exportation vers Excel
        menuAffichage = new JMenu("Affichage");
        menuItemResultats = new JMenuItem("Résultats");
        menuItemExporter = new JMenuItem("Exporter");
        menuItemDeliberations = new JMenuItem("Délibérations");
        menuItemExportExcel = new JMenuItem("Exporter vers Excel");
        menuItemExportExcel.addActionListener(e -> {
            ExcelExporter exporter = new ExcelExporter();
            exporter.exportDataToExcel("Résultats.xlsx");
            JOptionPane.showMessageDialog(this, "Exportation des données vers Excel réussie.");
        });
        menuAffichage.add(menuItemResultats);
        menuAffichage.add(menuItemExporter);
        menuAffichage.add(menuItemDeliberations);
        menuAffichage.add(menuItemExportExcel);

        menuBar.add(menuAjouter);
        menuBar.add(menuNotes);
        menuBar.add(menuAffichage);

        setJMenuBar(menuBar);
        
        
     // Dans la classe ManagementSystem
      
            menuItemResultats.addActionListener(e -> {
                DefaultTableModel tableModel = studentManagementSystem.getResultsTableModel();
                JTable resultsTable = new JTable(tableModel);
                JScrollPane scrollPane = new JScrollPane(resultsTable); // Pour rendre la table déroulante si nécessaire
                cardPanel.add(scrollPane, "Results");
                cardLayout.show(cardPanel, "Results");
            });
            // ...
        }

        
        
        
        
    


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame loginFrame = new JFrame("Connexion");
            StudentManagementSystem sms = new StudentManagementSystem();
            loginFrame.add(sms.createLoginPanel(loginFrame));
            loginFrame.pack();
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setVisible(true);
        });
    }
}

// Note: The 'StudentManagementSystem' class and related methods (like 'createStudentFormPanel', 'createStudentModificationPanel', etc.) must be defined elsewhere in your code.
