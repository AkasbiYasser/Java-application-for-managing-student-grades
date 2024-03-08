package test;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

public class ExcelExporter {
    String BDD = "test1";
    String url = "jdbc:mysql://localhost:3306/" + BDD;
    String user = "root";
    String passwd = "";

    public void exportDataToExcel(String fileName) {
        // Query to fetch student details and their grades
        String query = "SELECT s.apogee_number, s.last_name, s.first_name, " +
                "AVG(CASE WHEN g.module_code = 'M1' THEN g.grade ELSE NULL END) AS M1, " +
                "AVG(CASE WHEN g.module_code = 'M2' THEN g.grade ELSE NULL END) AS M2, " +
                "AVG(CASE WHEN g.module_code = 'M3' THEN g.grade ELSE NULL END) AS M3, " +
                "AVG(CASE WHEN g.module_code = 'M4' THEN g.grade ELSE NULL END) AS M4, " +
                "AVG(CASE WHEN g.module_code = 'M5' THEN g.grade ELSE NULL END) AS M5, " +
                "AVG(CASE WHEN g.module_code = 'M6' THEN g.grade ELSE NULL END) AS M6, " +
                "AVG(CASE WHEN g.module_code = 'M7' THEN g.grade ELSE NULL END) AS M7, " +
                "AVG(CASE WHEN g.module_code = 'M8' THEN g.grade ELSE NULL END) AS M8, " +
                "AVG(CASE WHEN g.module_code = 'M9' THEN g.grade ELSE NULL END) AS M9, " +
                "AVG(CASE WHEN g.module_code = 'M10' THEN g.grade ELSE NULL END) AS M10, " +
                "AVG(CASE WHEN g.module_code = 'M11' THEN g.grade ELSE NULL END) AS M11, " +
                "AVG(CASE WHEN g.module_code = 'M12' THEN g.grade ELSE NULL END) AS M12, " +
                "AVG(g.grade) AS Moyenne " +
                "FROM students s " +
                "LEFT JOIN grades g ON s.student_id = g.student_id " +
                "GROUP BY s.apogee_number, s.last_name, s.first_name " +
                "ORDER BY s.apogee_number";

        try (Connection conn = DriverManager.getConnection(url, user, passwd);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery();
             Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Étudiants");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Numéro Apogée");
            headerRow.createCell(1).setCellValue("Nom");
            headerRow.createCell(2).setCellValue("Prénom");
            headerRow.createCell(3).setCellValue("M1");
            headerRow.createCell(4).setCellValue("M2");
            headerRow.createCell(5).setCellValue("M3");
            headerRow.createCell(6).setCellValue("M4");
            headerRow.createCell(7).setCellValue("M5");
            headerRow.createCell(8).setCellValue("M6");
            headerRow.createCell(9).setCellValue("M7");
            headerRow.createCell(10).setCellValue("M8");
            headerRow.createCell(11).setCellValue("M9");
            headerRow.createCell(12).setCellValue("M10");
            headerRow.createCell(13).setCellValue("M11");
            headerRow.createCell(14).setCellValue("M12");
            headerRow.createCell(15).setCellValue("Moyenne");

            // Write data rows
            int rowNum = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rs.getString("apogee_number"));
                row.createCell(1).setCellValue(rs.getString("last_name"));
                row.createCell(2).setCellValue(rs.getString("first_name"));
                row.createCell(3).setCellValue(rs.getFloat("M1"));
                row.createCell(4).setCellValue(rs.getFloat("M2"));
                row.createCell(5).setCellValue(rs.getFloat("M3"));
                row.createCell(6).setCellValue(rs.getFloat("M4"));
                row.createCell(7).setCellValue(rs.getFloat("M5"));
                row.createCell(8).setCellValue(rs.getFloat("M6"));
                row.createCell(9).setCellValue(rs.getFloat("M7"));
                row.createCell(10).setCellValue(rs.getFloat("M8"));
                row.createCell(11).setCellValue(rs.getFloat("M9"));
                row.createCell(12).setCellValue(rs.getFloat("M10"));
                row.createCell(13).setCellValue(rs.getFloat("M11"));
                row.createCell(14).setCellValue(rs.getFloat("M12"));
                row.createCell(15).setCellValue(rs.getFloat("Moyenne"));
            }

            // Write the workbook to a file
            try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                workbook.write(fileOut);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ExcelExporter exporter = new ExcelExporter();
        exporter.exportDataToExcel("Résultats.xlsx");
    }
}
