package ru.office;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class OfficeTests {
    @Test
    @DisplayName("Проверка наличия информации о сотрудниках в БД после удаления отдела")
    void testEmployeeDataRemovalAfterDepartmentDeletion() {
        try (Connection con = DriverManager.getConnection("jdbc:h2:.\\Office")) {
            PreparedStatement pStm = con.prepareStatement("SELECT EXISTS(SELECT 1 FROM Employee WHERE DepartmentId = ?)");
            pStm.setInt(1, 2);
            pStm.executeQuery();
            ResultSet rs = pStm.getResultSet();
            rs.next();
            assertThat("Система не соответствует требованиям, т.к после удаления отдела информация о сотрудниках " +
                    "отдела не удалена из таблицы Employee,", rs.getBoolean(1), equalTo(false));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
