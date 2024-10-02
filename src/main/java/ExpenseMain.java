import model.User;
import repository.ExpenseDBRepository;
import repository.UserDBRepository;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ExpenseMain {

    public static void main(String[] args) {
        Properties props = new Properties();
        try
        {
            props.load(new FileReader("bd.config"));
        }
        catch (IOException e)
        {
            System.out.println("Cannot find bd.config "+e);
        }

        ExpenseDBRepository expenseRepository = new ExpenseDBRepository(props);
        UserDBRepository userRepository = new UserDBRepository(props);

//        User newUser = new User("Razvan", "psd123");
//        userRepository.add(newUser);

    }
}