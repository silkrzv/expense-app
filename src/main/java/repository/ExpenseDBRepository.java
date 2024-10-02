package repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import model.Expense;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ExpenseDBRepository implements ExpenseRepository {

    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();
    private final Connection connection;

    public ExpenseDBRepository(Properties props)
    {
        logger.info("Initializing ArtistDBRepository with properties: {}", props);
        dbUtils = new JdbcUtils(props);
        this.connection = dbUtils.getConnection();
    }

    public Expense add(Expense expense) {
        logger.info("Adding expense: {}", expense);
        String sql = "INSERT INTO Expense (name, category, amount, date, description) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, expense.getName());
            pstmt.setString(2, expense.getCategory());
            pstmt.setDouble(3, expense.getAmount());
            pstmt.setDate(4, new java.sql.Date(expense.getDate().getTime()));
            pstmt.setString(5, expense.getDescription());
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                expense.setID(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Failed to retrieve auto-generated ID.");
            }
        } catch (SQLException e) {
            logger.error("Error adding expense: {}", e.getMessage());
        }
        return expense;
    }

    @Override
    public Expense findById(Integer id) {
        logger.info("Finding expense by ID: {}", id);
        Expense expense = null;
        String sql = "SELECT * FROM Expense WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                expense = new Expense(
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getDate("date"),
                        rs.getString("description")
                );
                expense.setID(rs.getInt("id"));
            }
        } catch (SQLException e) {
            logger.error("Error finding expense by ID: {}", e.getMessage());
        }
        return expense;
    }

    @Override
    public List<Expense> getAll() {
        logger.info("Getting all expenses");
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM Expense";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Expense expense = new Expense(
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getDate("date"),
                        rs.getString("description")
                );
                expense.setID(rs.getInt("id"));
                expenses.add(expense);
            }
        } catch (SQLException e) {
            logger.error("Error getting all expenses: {}", e.getMessage());
        }
        return expenses;
    }

    @Override
    public Expense update(Expense entity) {
        logger.info("Updating expense: {}", entity);
        String sql = "UPDATE Expense SET name = ?, category = ?, amount = ?, date = ?, description = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, entity.getName());
            pstmt.setString(2, entity.getCategory());
            pstmt.setDouble(3, entity.getAmount());
            pstmt.setDate(4, new java.sql.Date(entity.getDate().getTime()));
            pstmt.setString(5, entity.getDescription());
            pstmt.setInt(6, entity.getID());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating expense: {}", e.getMessage());
        }
        return entity;
    }

    @Override
    public void deleteById(Integer id) {
        logger.info("Deleting expense by ID: {}", id);
        String sql = "DELETE FROM Expense WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting expense by ID: {}", e.getMessage());
        }
    }

    public List<Expense> findByCategory(String category) {
        logger.info("Finding expenses by category: {}", category);
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM Expense WHERE category = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Expense expense = new Expense(
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getDate("date"),
                        rs.getString("description")
                );
                expense.setID(rs.getInt("id"));
                expenses.add(expense);
            }
        } catch (SQLException e) {
            logger.error("Error finding expenses by category: {}", e.getMessage());
        }
        return expenses;
    }

    public double getTotalAmount() {
        logger.info("Calculating total amount of expenses");
        double totalAmount = 0;
        String sql = "SELECT SUM(amount) FROM Expense";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                totalAmount = rs.getDouble(1);
            }
        } catch (SQLException e) {
            logger.error("Error calculating total amount of expenses: {}", e.getMessage());
        }
        return totalAmount;
    }

    public List<Expense> filterByDateRange(Date startDate, Date endDate) {
        logger.info("Filtering expenses by date range: {} - {}", startDate, endDate);
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM Expense WHERE date BETWEEN ? AND ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(2, new java.sql.Date(endDate.getTime()));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Expense expense = new Expense(
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getDate("date"),
                        rs.getString("description")
                );
                expense.setID(rs.getInt("id"));
                expenses.add(expense);
            }
        } catch (SQLException e) {
            logger.error("Error filtering expenses by date range: {}", e.getMessage());
        }
        return expenses;
    }

    public void deleteAllExpenses() {
        try (Statement statement = connection.createStatement()) {
            String sql = "DELETE FROM expenses";
            statement.executeUpdate(sql);
        } catch (SQLException exception) {
            logger.error("Error deleting all expenses: {}", exception.getMessage());
        }
    }

}
