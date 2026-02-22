package ru.office;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Office {

    public static void main(String[] args) {
        addAnnToHrDepartment();
        checkFirstCharOfEmployeeNames();
        getCountOfEmployeesInItDepartment();
        System.out.println("Добавляем нового сотрудника с именем 'ann' в отдел IT");
        Service.addEmployee(new Employee(13,"ann",2));
        checkFirstCharOfEmployeeNames();
        addAnnToHrDepartment();
        getCountOfEmployeesInItDepartment();
    }

    static void addAnnToHrDepartment() {
        try (Connection con = DriverManager.getConnection("jdbc:h2:.\\Office")) {
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery("SELECT Id FROM Employee WHERE Name = 'Ann'");
            if (rs.last()) {
                handleSingleAnnFound(con, rs);
            } else {
                System.out.println("Сотрудник с именем Ann не найден.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleSingleAnnFound(Connection con, ResultSet rs) throws SQLException {
        int rowCount = rs.getRow();
        if (rowCount == 1) {
            updateAnnDepartment(con, rs);
        } else {
            System.out.println("Найдено несколько сотрудников с именем Ann. Перевод невозможен.");
        }
    }

    private static void updateAnnDepartment(Connection con, ResultSet rs) throws SQLException {
        int employeeId = rs.getInt("ID");
        PreparedStatement updateStm = con.prepareStatement("UPDATE Employee SET DepartmentId = 3 where Id = ?");
        updateStm.setInt(1, employeeId);
        int rowsUpdated = updateStm.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Ann переведена в отдел кадров.");
        } else {
            System.out.println("Не удалось обновить запись для Ann.");
        }
    }

    static void checkFirstCharOfEmployeeNames() {
        try (Connection con = DriverManager.getConnection("jdbc:h2:.\\Office")) {
            Statement stm = con.createStatement();
            String updateQuery =
                    "UPDATE Employee SET Name = CONCAT(UPPER(SUBSTRING(Name, 1, 1)), SUBSTRING(Name, 2)) " +
                            "WHERE Name REGEXP '^[a-z]'";
            int rowsUpdated = stm.executeUpdate(updateQuery);
            System.out.println("Количество исправленных имен: " + rowsUpdated);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static void getCountOfEmployeesInItDepartment() {
        try (Connection con = DriverManager.getConnection("jdbc:h2:.\\Office")) {
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery("SELECT COUNT(*) FROM Employee WHERE DepartmentId = 2");
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Количество сотрудников в IT-отделе: " + count);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
