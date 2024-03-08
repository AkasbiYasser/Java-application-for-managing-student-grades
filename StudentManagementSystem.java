package test;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
// Cette classe gère la logique d'interface utilisateur et d'interaction avec la base de données
public class StudentManagementSystem {

    // Paramètres de connexion à la base de données
		String BDD = "test1"; 
		String url = "jdbc:mysql://localhost:3306/" + BDD;
		String user = "root";
		String passwd = "";
		Connection conn=null;
		
		 // Constructeur
		public StudentManagementSystem() {
	        // Enregistrement du pilote JDBC
	        try {
	            Class.forName("com.mysql.cj.jdbc.Driver");
	            conn = DriverManager.getConnection(url, user, passwd);
	            System.out.println("Connexion à la base de données réussie");
	        } catch (ClassNotFoundException e) {
	            JOptionPane.showMessageDialog(null, "MySQL Driver not found: " + e.getMessage());
	        } catch (SQLException e) {
	            JOptionPane.showMessageDialog(null, "Erreur de connexion à la base de données: " + e.getMessage());
	        }
	    }

    public JPanel createStudentFormPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));

        panel.add(new JLabel("Numéro Apogée:"));
        panel.add(new JTextField());

        panel.add(new JLabel("Nom:"));
        panel.add(new JTextField());

        panel.add(new JLabel("Prénom:"));
        panel.add(new JTextField());

        JButton addButton = new JButton("Ajouter");
        //h
        addButton.addActionListener(e -> {
            String apogeeNumber = ((JTextField) panel.getComponent(1)).getText();
            String lastName = ((JTextField) panel.getComponent(3)).getText();
            String firstName = ((JTextField) panel.getComponent(5)).getText();
            addStudent(apogeeNumber, lastName, firstName);
        });
        panel.add(addButton);
        
        //h
        JButton clearButton = new JButton("Effacer");
        panel.add(addButton);
        panel.add(clearButton);
        
        

        // Add action listeners for buttons if needed
        // addButton.addActionListener(...);
        // clearButton.addActionListener(...);

        return panel;
    }

    public JPanel createStudentModificationPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));

        panel.add(new JLabel("Choisir un étudiant:"));
        JComboBox<String> studentDropdown = new JComboBox<>();
        loadStudentsIntoDropdown(studentDropdown); // Chargement des étudiants
        panel.add(studentDropdown);

        panel.add(new JLabel("Numéro Apogée:"));
        JTextField apogeeNumberField = new JTextField();
        panel.add(apogeeNumberField);

        panel.add(new JLabel("Nom:"));
        JTextField nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Prénom:"));
        JTextField firstNameField = new JTextField();
        panel.add(firstNameField);

        JButton modifyButton = new JButton("Modifier");
        modifyButton.addActionListener(e -> {
            String selectedItem = (String) studentDropdown.getSelectedItem();
            if (selectedItem != null && selectedItem.contains(" - ")) {
                int studentId = Integer.parseInt(selectedItem.split(" - ")[0]);
                String apogeeNumber = apogeeNumberField.getText();
                String lastName = nameField.getText();
                String firstName = firstNameField.getText();
                updateStudent(studentId, apogeeNumber, lastName, firstName);
            }
        });
        panel.add(modifyButton);

        JButton cancelButton = new JButton("Annuler");
        // Ajoutez un ActionListener pour cancelButton si nécessaire
        panel.add(cancelButton);

        return panel;
    }

 
    public void loadStudentsIntoDropdown(JComboBox<String> studentDropdown) {
        String sql = "SELECT student_id, apogee_number, last_name, first_name FROM students";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            studentDropdown.removeAllItems(); // Clear existing items
            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                String studentName = rs.getString("last_name") + " " + rs.getString("first_name");
                String studentInfo = studentId + " - " + studentName; // Store both ID and name
                studentDropdown.addItem(studentInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur de chargement des étudiants: " + e.getMessage());
        }
    }


    
    
    
    
    public void loadGradesForStudent(JTextField[] gradeFields, int studentId) {
        String sql = "SELECT module_code, grade FROM grades WHERE student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String moduleCode = rs.getString("module_code");
                float grade = rs.getFloat("grade");
                int moduleIndex = Integer.parseInt(moduleCode.substring(1)) - 1; // M1 -> index 0
                gradeFields[moduleIndex].setText(String.valueOf(grade));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur de chargement des notes: " + e.getMessage());
        }
    }

    
   
 // Méthode pour ajouter un nouvel étudiant dans la base de données
    public void addStudent(String apogeeNumber, String lastName, String firstName) {
        String sql = "INSERT INTO students (apogee_number, last_name, first_name) VALUES (?, ?, ?);";
        try (Connection conn = DriverManager.getConnection(url, user, passwd);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            System.out.println("Connexion établie");

            pstmt.setString(1, apogeeNumber);
            pstmt.setString(2, lastName);
            pstmt.setString(3, firstName);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Étudiant ajouté avec succès.");
            } else {
                System.out.println("Aucune ligne affectée.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL: " + e.getMessage());
        }
    }


// Méthode pour modifier un étudiant existant dans la base de données
public void updateStudent(int studentId, String apogeeNumber, String lastName, String firstName) {
    String sql = "UPDATE students SET apogee_number = ?, last_name = ?, first_name = ? WHERE student_id = ?;";
    try (Connection conn = DriverManager.getConnection(url, user, passwd);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, apogeeNumber);
        pstmt.setString(2, lastName);
        pstmt.setString(3, firstName);
        pstmt.setInt(4, studentId);
        pstmt.executeUpdate();
        JOptionPane.showMessageDialog(null, "Student updated successfully.");
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error updating student: " + e.getMessage());
    }
}

// Méthode pour ajouter une note pour un étudiant dans la base de données
public void addGrade(int studentId, String moduleCode, float grade) {
    String sql = "INSERT INTO grades (student_id, module_code, grade) VALUES (?, ?, ?);";
    try (Connection conn = DriverManager.getConnection(url, user, passwd);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, studentId);
        pstmt.setString(2, moduleCode);
        pstmt.setFloat(3, grade);
        pstmt.executeUpdate();
        JOptionPane.showMessageDialog(null, "Grade added successfully.");
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error adding grade: " + e.getMessage());
    }
}


//Méthode pour ajouter une note pour un étudiant dans la base de données
public void UPGrade(int studentId, String moduleCode, float grade) {
 String sql = "INSERT INTO grades (student_id, module_code, grade) VALUES (?, ?, ?);";
 try (Connection conn = DriverManager.getConnection(url, user, passwd);
      PreparedStatement pstmt = conn.prepareStatement(sql)) {
     pstmt.setInt(1, studentId);
     pstmt.setString(2, moduleCode);
     pstmt.setFloat(3, grade);
    pstmt.executeUpdate();
    JOptionPane.showMessageDialog(null, "Grade added successfully.");
 } catch (SQLException e) {
     e.printStackTrace();
    JOptionPane.showMessageDialog(null, "Error adding grade: " + e.getMessage());
 }
}






//Méthode pour modifier une note pour un étudiant dans la base de données
public void updateGrade(int studentId, String moduleCode, float grade) throws SQLException {
 String sql = "UPDATE grades SET grade = ? WHERE student_id = ? AND module_code = ?;";
 try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
     pstmt.setFloat(1, grade);
     pstmt.setInt(2, studentId);
     pstmt.setString(3, moduleCode);
     int affectedRows = pstmt.executeUpdate();
     if (affectedRows == 0) {
         throw new SQLException("Updating grade failed, no rows affected.");
     }
 }
}

public boolean addOrUpdateGrade(int studentId, String moduleCode, float grade) {
    try {
        if (checkGradeExists(studentId, moduleCode)) {
            // Update existing grade
            updateGrade(studentId, moduleCode, grade);
        } else {
            // Insert new grade
            addGrade(studentId, moduleCode, grade);
        }
        // If everything went well, return true
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        // If there was an exception, return false
        return false;
    }
}

private boolean checkGradeExists(int studentId, String moduleCode) throws SQLException {
 String sql = "SELECT count(*) FROM grades WHERE student_id = ? AND module_code = ?";
 try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
     pstmt.setInt(1, studentId);
     pstmt.setString(2, moduleCode);
     ResultSet rs = pstmt.executeQuery();
     if (rs.next()) {
         return rs.getInt(1) > 0;
     }
     return false;
 }
}

public JPanel createGradesModificationPanel() {
    // Use a GridBagLayout for more control over the layout
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    panel.add(new JLabel("Étudiants:"), gbc);
    
    JComboBox<String> studentDropdown = new JComboBox<>();
    gbc.gridx = 2;
    gbc.gridwidth = 2;
    panel.add(studentDropdown, gbc);
    loadStudentsIntoDropdown(studentDropdown); // Load students

    // Create fields for M1 to M12

    JTextField[] gradeFields = new JTextField[12];
    gbc.gridwidth = 1;
    for (int i = 0; i < gradeFields.length; i++) {
        gbc.gridx = i % 2 * 2; // This will alternate between 0 and 2
        gbc.gridy = i / 2 + 1; // This will increment every two modules
        panel.add(new JLabel("M" + (i + 1) + ":"), gbc);

        gbc.gridx = i % 2 * 2 + 1; // This will alternate between 1 and 3
        gradeFields[i] = new JTextField(10);
        panel.add(gradeFields[i], gbc);
    }


    gbc.gridx = 0;
    gbc.gridy = gradeFields.length / 2 + 1;
    gbc.gridwidth = 2;
    JButton updateButton = new JButton("Update Grades");
    panel.add(updateButton, gbc);

    // Clear button
    gbc.gridx = 2;
    JButton clearButton = new JButton("Clear");
    panel.add(clearButton, gbc);
    
    // Add action listeners
    updateButton.addActionListener(e -> {
        String selectedStudent = (String) studentDropdown.getSelectedItem();
        if (selectedStudent != null && selectedStudent.contains(" - ")) {
            int studentId = Integer.parseInt(selectedStudent.split(" - ")[0]);
            boolean success = true;
            for (int i = 0; i < gradeFields.length; i++) {
                try {
                    String gradeText = gradeFields[i].getText().trim();
                    if (!gradeText.isEmpty()) {
                        float grade = Float.parseFloat(gradeText);
                        success = addOrUpdateGrade(studentId, "M" + (i + 1), grade);
                        if (!success) {
                            JOptionPane.showMessageDialog(panel, "Error updating grade for M" + (i + 1), "Update Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        }
                    } // else ignore empty fields or handle them as needed
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Invalid grade format for M" + (i + 1), "Input Error", JOptionPane.ERROR_MESSAGE);
                    success = false;
                    break;
                }
            }
            if (success) {
                JOptionPane.showMessageDialog(panel, "Grades updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    });




    // Listener to load grades when a student is selected
    studentDropdown.addActionListener(e -> {
        String selectedStudent = (String) studentDropdown.getSelectedItem();
        if (selectedStudent != null && selectedStudent.contains(" - ")) {
            int studentId = Integer.parseInt(selectedStudent.split(" - ")[0]);
            loadGradesForStudent(gradeFields, studentId);
        }
    });
    

    return panel;
}

public JPanel createGradesFormPanel() {
    // Use a GridBagLayout for more control over the layout
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    panel.add(new JLabel("Étudiants:"), gbc);
    
    JComboBox<String> studentDropdown = new JComboBox<>();
    gbc.gridx = 2;
    gbc.gridwidth = 2;
    panel.add(studentDropdown, gbc);
    loadStudentsIntoDropdown(studentDropdown); // Load students

    // Create fields for M1 to M12
    JTextField[] gradeFields = new JTextField[12];
    gbc.gridwidth = 1;
    for (int i = 0; i < gradeFields.length; i++) {
        gbc.gridx = i % 2 * 2; // This will alternate between 0 and 2
        gbc.gridy = i / 2 + 1; // This will increment every two modules
        panel.add(new JLabel("M" + (i + 1) + ":"), gbc);

        gbc.gridx = i % 2 * 2 + 1; // This will alternate between 1 and 3
        gradeFields[i] = new JTextField(10);
        panel.add(gradeFields[i], gbc);
    }

    JButton addButton = new JButton("Ajouter");
    addButton.addActionListener(e -> {
        String selectedStudent = (String) studentDropdown.getSelectedItem();
        if (selectedStudent != null && selectedStudent.contains(" - ")) {
            int studentId = Integer.parseInt(selectedStudent.split(" - ")[0]);
            try {
                for (int i = 0; i < gradeFields.length; i++) {
                    String gradeText = gradeFields[i].getText().trim();
                    float grade;
                    if (!gradeText.isEmpty()) {
                        grade = Float.parseFloat(gradeText);
                        String moduleCode = "M" + (i + 1);
                        addOrUpdateGrade(studentId, moduleCode, grade);
                    }
                }
                JOptionPane.showMessageDialog(panel, "Grades added/updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid grade format. Please enter a numeric value.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    });

    gbc.gridx = 0;
    gbc.gridy = gradeFields.length / 2 + 1;
    gbc.gridwidth = 2;
    panel.add(addButton, gbc);
    
    JButton clearButton = new JButton("Effacer");
    clearButton.addActionListener(e -> {
        for (JTextField gradeField : gradeFields) {
            gradeField.setText("");
        }
    });
    gbc.gridx = 2;
    panel.add(clearButton, gbc);

    // Add action listener to the student dropdown to load grades when a student is selected
    studentDropdown.addActionListener(e -> {
        String selectedStudent = (String) studentDropdown.getSelectedItem();
        if (selectedStudent != null && selectedStudent.contains(" - ")) {
            int studentId = Integer.parseInt(selectedStudent.split(" - ")[0]);
            loadGradesForStudent(gradeFields, studentId);
        }
    });

    return panel;
}









//Dans la classe StudentManagementSystem

public DefaultTableModel getResultsTableModel() {
 String url = "jdbc:mysql://localhost:3306/test1";
 String user = "root";
 String passwd = "";
 
 // Initialisez votre DefaultTableModel avec les colonnes appropriées
 DefaultTableModel model = new DefaultTableModel(
     new String[] {
         "Numéro Apogée", "Nom", "Prénom",
         "M1", "M2", "M3", "M4", "M5", "M6",
         "M7", "M8", "M9", "M10", "M11", "M12",
         "Moyenne"
     },
     0 // Indique le nombre initial de lignes
 );

 Connection conn = null;
 Statement stmt = null;
 ResultSet rs = null;

 try {
     conn = DriverManager.getConnection(url, user, passwd);
     stmt = conn.createStatement();

     String sql = "SELECT s.apogee_number, s.last_name, s.first_name, " +
                  "AVG(CASE WHEN g.module_code = 'M1' THEN g.grade ELSE NULL END) AS M1, " +
                  "AVG(CASE WHEN g.module_code = 'M2' THEN g.grade ELSE NULL END) AS M2, " +
                  "AVG(CASE WHEN g.module_code = 'M2' THEN g.grade ELSE NULL END) AS M3, " +
                  "AVG(CASE WHEN g.module_code = 'M2' THEN g.grade ELSE NULL END) AS M4, " +
                  "AVG(CASE WHEN g.module_code = 'M2' THEN g.grade ELSE NULL END) AS M5, " +
                  "AVG(CASE WHEN g.module_code = 'M2' THEN g.grade ELSE NULL END) AS M6, " +
                  "AVG(CASE WHEN g.module_code = 'M2' THEN g.grade ELSE NULL END) AS M7, " +
                  "AVG(CASE WHEN g.module_code = 'M2' THEN g.grade ELSE NULL END) AS M8, " +
                  "AVG(CASE WHEN g.module_code = 'M2' THEN g.grade ELSE NULL END) AS M9, " +
                  "AVG(CASE WHEN g.module_code = 'M2' THEN g.grade ELSE NULL END) AS M10, " +
                  "AVG(CASE WHEN g.module_code = 'M2' THEN g.grade ELSE NULL END) AS M11, " +
                  "AVG(CASE WHEN g.module_code = 'M12' THEN g.grade ELSE NULL END) AS M12, " +
                  "AVG(g.grade) AS Moyenne " +
                  "FROM students s " +
                  "LEFT JOIN grades g ON s.student_id = g.student_id " +
                  "GROUP BY s.student_id " +
                  "ORDER BY s.student_id";

     rs = stmt.executeQuery(sql);
     while (rs.next()) {
         model.addRow(new Object[]{
             rs.getString("apogee_number"),
             rs.getString("last_name"),
             rs.getString("first_name"),
             rs.getObject("M1"),
             rs.getObject("M2"),
             rs.getObject("M3"),
             rs.getObject("M4"),
             rs.getObject("M5"),
             rs.getObject("M6"),
             rs.getObject("M7"),
             rs.getObject("M8"),
             rs.getObject("M9"),
             rs.getObject("M10"),
             rs.getObject("M11"),
             rs.getObject("M12"),
             rs.getFloat("Moyenne")
         });
     }
 } catch (SQLException e) {
     e.printStackTrace(); // Gestion des exceptions
 } finally {
     try {
         if (rs != null) rs.close();
         if (stmt != null) stmt.close();
         if (conn != null) conn.close();
     } catch (SQLException e) {
         e.printStackTrace();
     }
 }

 return model;
}














public JPanel createLoginPanel(JFrame parentFrame) {
    JPanel loginPanel = new JPanel(new GridLayout(3, 2, 5, 5));

    // Username field
    loginPanel.add(new JLabel("Nom d'utilisateur:"));
    JTextField usernameField = new JTextField();
    loginPanel.add(usernameField);

    // Password field
    loginPanel.add(new JLabel("Mot de passe:"));
    JPasswordField passwordField = new JPasswordField();
    loginPanel.add(passwordField);

    // Login button
    JButton loginButton = new JButton("Connexion");
    loginButton.addActionListener(e -> {
        String username = usernameField.getText();
        char[] password = passwordField.getPassword();
        if (authenticateUser(username, password)) {
            JOptionPane.showMessageDialog(loginPanel, "Connexion réussie!");
            parentFrame.dispose(); // Close the login window
            new ManagementSystem().setVisible(true); // Open the main application window
        } else {
            JOptionPane.showMessageDialog(loginPanel, "Identifiants incorrects.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    });
    loginPanel.add(new JLabel()); // Placeholder
    loginPanel.add(loginButton);

    return loginPanel;
}

private boolean authenticateUser(String username, char[] password) {

    return "admin".equals(username) && "password".equals(new String(password));
}


}





