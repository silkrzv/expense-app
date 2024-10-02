package repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

public class UserDBRepository implements UserRepository
{
    private static final Logger logger = LogManager.getLogger();
    private final JdbcUtils dbUtils;
    private final Connection connection;

    public UserDBRepository(Properties props)
    {
        logger.info("Initializing UserDBRepository with properties: {}", props);
        this.dbUtils = new JdbcUtils(props);
        this.connection = dbUtils.getConnection();
    }

    @Override
    public User findById(Integer id)
    {
        logger.traceEntry("Finding user by id: {}", id);
        String query = "SELECT * FROM User WHERE id=?";
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
            {
                String username = resultSet.getString("username");
                String passwordHash = resultSet.getString("password");
                return new User(username, passwordHash);
            }
        }
        catch (SQLException e)
        {
            logger.error("Error finding user by id: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public List<User> getAll()
    {
        logger.traceEntry("Finding all users");
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM User";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query))
        {
            while (resultSet.next())
            {
                String username = resultSet.getString("username");
                String passwordHash = resultSet.getString("password");
                User user = new User(username, passwordHash);
                users.add(user);
            }
        }
        catch (SQLException e)
        {
            logger.error("Error finding all users: {}", e.getMessage());
        }
        return logger.traceExit(users);
    }

    @Override
    public User add(User user)
    {
        logger.traceEntry("Saving user: {}", user);
        String query = "INSERT INTO User (username, password) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
        {
            statement.setString(1, user.getUsername());
            statement.setString(2, hashPassword(user.getPassword()));
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0)
            {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys())
            {
                if (generatedKeys.next())
                {
                    int id = generatedKeys.getInt(1);
                    user.setID(id);
                }
                else
                {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Error saving user: {}", e.getMessage());
        }
        return logger.traceExit(user);
    }

    @Override
    public User update(User user)
    {
        logger.traceEntry("Updating user: {}", user);
        String query = "UPDATE User SET username=?, password=? WHERE id=?";
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setInt(3, user.getID());
            statement.executeUpdate();
            return user;
        }
        catch (SQLException e)
        {
            logger.error("Error occurred while updating user", e);
            return null;
        }
    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public List<User> findByUsername(String username)
    {
        logger.traceEntry("Finding user by username: {}", username);
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM User WHERE username=?";
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                int id = resultSet.getInt("id");
                String fetchedUsername = resultSet.getString("username");
                String passwordHash = resultSet.getString("password");
                User user = new User(fetchedUsername, passwordHash);
                user.setID(id);
                users.add(user);
            }
        }
        catch (SQLException e)
        {
            logger.error("Error finding user by username: {}", e.getMessage());
        }
        return logger.traceExit(users);
    }

    @Override
    public boolean authenticate(String username, String password)
    {
        List<User> users = findByUsername(username);
        for (User user : users)
        {
            if (user.getPassword().equals(hashPassword(password)))
            {
                return true;
            }
        }
        return false;
    }

    private String hashPassword(String password)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            return Base64.getEncoder().encodeToString(digest);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }

}