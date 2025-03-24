# Budget Management Application

Description

This is a simple budget management application built using JavaFX. It allows users to log in and manage their expenses by adding, deleting, filtering, and calculating total expenses.

## Features

- **User Authentication**: Secure login system with username and password.
- **Expense Management**:
  - Add new expenses with name, category, amount, date, and description.
  - Delete existing expenses.
  - Filter expenses by category or date range.
  - Calculate total expenses.
- **User Interface**:
  - Intuitive UI with clearly labeled buttons for actions.
  - Responsive design with a structured layout.

Screenshots

![Login Window](images/loginview.png)


![Main Window](images/mainview.png)



## Technologies Used

- Java
- JavaFX
- SQLite (for database)
- Log4j2 (for logging)

## Installation & Usage

- Clone the repository:
```bash
git clone https://github.com/your-user/repo-name.git
```

- Open the project in your preferred Java IDE (e.g., IntelliJ IDEA, Eclipse).
- Ensure you have JavaFX configured.
- Run the Main class to start the application.
- Log in with your credentials.
- Add expenses and categorize them.
- Filter expenses by category or date.
- Calculate your total spending.

## Configuration

- Modify log4j2.xml for logging settings.
- Update database connection details if required.
- The database (`expenses.db`) contains a table `expenses` with columns:
  - `id` (INTEGER, PRIMARY KEY)
  - `name` (TEXT)
  - `category` (TEXT)
  - `amount` (REAL)
  - `date` (TEXT)
  - `description` (TEXT)



