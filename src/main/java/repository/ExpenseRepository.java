package repository;

import model.Expense;
import repository.IRepository;

import java.sql.Date;
import java.util.List;

public interface ExpenseRepository extends IRepository<Expense, Integer> {
    List<Expense> findByCategory(String category);
    double getTotalAmount();
    List<Expense> filterByDateRange(Date startDate, Date endDate);
    void deleteAllExpenses();
}
