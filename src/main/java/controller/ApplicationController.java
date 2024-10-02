package controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Expense;
import service.Service;

import java.io.IOException;
import java.net.HttpCookie;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ApplicationController {

    @FXML
    private Label messageLabel;
    @FXML
    private TableView<Expense> expenseTableView;
    @FXML
    private TableColumn<Expense, String> nameColumn;
    @FXML
    private TableColumn<Expense, String> categoryColumn;
    @FXML
    private TableColumn<Expense, Double> amountColumn;
    @FXML
    private TableColumn<Expense, Date> dateColumn;
    @FXML
    private TableColumn<Expense, String> descriptionColumn;
    private ApplicationController applicationController;
    private Service service;
    @FXML
    private TextField nameField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField amountField;
    @FXML
    private TextField descriptionField;
//    @FXML
//    private TextField categoryFilterField;
//    @FXML
//    private TextField startDateField;
//    @FXML
//    private TextField endDateField;
//    @FXML
//    private Label totalExpenseLabel;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private ComboBox<String> categoryFilterComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;

    public void initialize(Service service)
    {
        this.service = service;
        initializeColumns();
        populateExpenseTable();

        ObservableList<String> categories = FXCollections.observableArrayList("Food", "Clothes", "Utilities", "Rent", "Others");
        categoryComboBox.setItems(categories);
        categoryFilterComboBox.setItems(categories);

        loadAllExpenses();
    }

    private void initializeColumns()
    {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

//    @FXML
//    private void showExpenseDetails(Expense expense)
//    {
//        nameField.setText(expense.getName());
//        categoryField.setText(expense.getCategory());
//        LocalDate expenseDate = expense.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//        datePicker.setValue(expenseDate);
//        amountField.setText(String.valueOf(expense.getAmount()));
//        descriptionField.setText(String.valueOf(expense.getDescription()));
//    }

    private void populateExpenseTable()
    {
        ObservableList<Expense> expenses = getAllExpenses();
        expenseTableView.setItems(expenses);
    }

    public ObservableList<Expense> getAllExpenses()
    {
        try
        {
            List<Expense> expenseList = service.getAllExpenses();
            showAlert("Success", "Expenses Loaded", "All expenses have been successfully loaded.");
            return FXCollections.observableArrayList(expenseList);
        }
        catch (Exception e)
        {
            showAlert("Error", "Failed to Load Expenses", "An error occurred while loading expenses: " + e.getMessage());
            return FXCollections.observableArrayList();
        }
    }


    @FXML
    private void logoutButtonClicked() throws IOException
    {
        Stage stage = (Stage) messageLabel.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
        Stage loginStage = new Stage();
        loginStage.setScene(new Scene(fxmlLoader.load()));
        LoginController loginController = fxmlLoader.getController();
        loginController.setService(service);
        loginStage.show();
    }

    @FXML
    private void addExpenseButtonClicked()
    {
        String name = nameField.getText();
        String category = categoryComboBox.getValue();
        LocalDate date = datePicker.getValue();
        String amountText = amountField.getText();
        String description = descriptionField.getText();
        if (name.isEmpty() || category == null || date == null || amountText.isEmpty() || description.isEmpty()) {
            showAlert("Warning", "Incomplete Fields", "Fill in all fields");
            return;
        }
        try
        {
            double amount = Double.parseDouble(amountText);
            Date utilDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            service.addExpense(new Expense(name, category, amount, utilDate, description));
            showAlert("Success", "Expense Added", "Expense has been successfully added.");
            populateExpenseTable();
            nameField.setText("");
            categoryComboBox.setValue(null);
            datePicker.setValue(null);
            amountField.setText("");
            descriptionField.setText("");
        }
        catch (NumberFormatException e)
        {
            showAlert("Error", "Invalid Amount", "Please enter a valid amount.");
        }
        catch (Exception e)
        {
            showAlert("Error", "Failed to Add Expense", "An error occurred while adding expense: " + e.getMessage());
        }
    }


    @FXML
    private void deleteExpenseButtonClicked()
    {
        Expense selectedExpense = expenseTableView.getSelectionModel().getSelectedItem();
        if (selectedExpense != null)
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Delete Expense");
            alert.setContentText("Do you want to delete only the selected expense or all expenses?");

            ButtonType deleteSingleButton = new ButtonType("Delete Selected Expense");
            ButtonType deleteAllButton = new ButtonType("Delete All Expenses");
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(deleteSingleButton, deleteAllButton, cancelButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent())
            {
                if (result.get() == deleteSingleButton)
                {
                    deleteSelectedExpense(selectedExpense);
                }
                else if (result.get() == deleteAllButton)
                {
                    deleteAllExpenses();
                }
            }
        }
        else
        {
            showAlert("Error", "No Expense Selected", "Please select an expense to delete.");
        }
    }

    private void deleteSelectedExpense(Expense selectedExpense)
    {
        try
        {
            service.deleteExpenseById(selectedExpense.getID());
            showAlert("Success", "Expense Deleted", "Expense has been successfully deleted.");
            populateExpenseTable();
        }
        catch (Exception e)
        {
            showAlert("Error", "Failed to Delete Expense", "An error occurred while deleting expense: " + e.getMessage());
        }
    }

    private void deleteAllExpenses()
    {
        try
        {
            service.deleteAllExpenses();
            showAlert("Success", "Expenses Deleted", "All expenses have been successfully deleted.");
            populateExpenseTable();
        }
        catch (Exception e)
        {
            showAlert("Error", "Failed to Delete Expenses", "An error occurred while deleting expenses: " + e.getMessage());
        }
    }

    @FXML
    private void getExpensesByCategoryButtonClicked()
    {
        String category = categoryFilterComboBox.getValue();
        try
        {
            List<Expense> expenses = service.getExpensesByCategory(category);
            expenseTableView.setItems(FXCollections.observableArrayList(expenses));
        }
        catch (Exception e)
        {
            showAlert("Error", "Failed to Retrieve Expenses by Category", "An error occurred while retrieving expenses: " + e.getMessage());
        }
    }

    @FXML
    private void getTotalExpenseAmountButtonClicked()
    {
        try
        {
            double totalAmount = service.getTotalExpenseAmount();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Total Expense Amount");
            alert.setHeaderText("The total amount of money spent:");
            alert.setContentText(String.valueOf(totalAmount));
            alert.showAndWait();
        }
        catch (Exception e)
        {
            showAlert("Error", "Failed to Retrieve Total Expense Amount", "An error occurred while retrieving total expense amount: " + e.getMessage());
        }
    }


    @FXML
    private void getExpensesByDateRangeButtonClicked()
    {
        try
        {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (startDate != null && endDate != null)
            {
                java.util.Date sqlStartDate = java.sql.Date.valueOf(startDate);
                java.util.Date sqlEndDate = java.sql.Date.valueOf(endDate);

                List<Expense> expenses = service.getExpensesByDateRange(sqlStartDate, sqlEndDate);

                expenseTableView.setItems(FXCollections.observableArrayList(expenses));
            }
            else
            {
                showAlert("Error", "Missing Date", "Please select both start and end dates.");
            }
        }
        catch (Exception e)
        {
            showAlert("Error", "Failed to Retrieve Expenses by Date Range", "An error occurred while retrieving expenses: " + e.getMessage());
        }
    }

    private void loadAllExpenses() {
        List<Expense> expenses = getAllExpenses();
        expenseTableView.getItems().setAll(expenses);
    }

    @FXML
    public void refreshExpensesButtonClicked() {
        loadAllExpenses();
    }

    private void showAlert(String title, String header, String content)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
