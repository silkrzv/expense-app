package service;

import model.Expense;
import repository.ExpenseRepository;
import repository.UserRepository;

import java.util.Date;
import java.util.List;

public class Service {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public Service(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    public boolean authenticateUser(String username, String password) {
        return userRepository.authenticate(username, password);
    }

    public void addExpense(Expense expense) {
        expenseRepository.add(expense);
    }

    public void deleteExpenseById(Integer id) {
        expenseRepository.deleteById(id);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.getAll();
    }

    public List<Expense> getExpensesByCategory(String category) {
        return expenseRepository.findByCategory(category);
    }

    public double getTotalExpenseAmount() {
        return expenseRepository.getTotalAmount();
    }

    public List<Expense> getExpensesByDateRange(Date startDate, Date endDate) {
        java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
        java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
        return expenseRepository.filterByDateRange(sqlStartDate, sqlEndDate);
    }
    public void deleteAllExpenses() {
        expenseRepository.deleteAllExpenses();
    }
}
