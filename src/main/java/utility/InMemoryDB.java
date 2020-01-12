package utility;

import model.Role;
import model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class InMemoryDB {


    public static List<User> users = new ArrayList<>(
            Arrays.asList(
                    new User("m", "m",
                            new HashSet<>(Arrays.asList(Role.ROLE_ADMIN, Role.ROLE_USER)),
                            LocalDateTime.now(),
                            true,
                            3

                    ),
                    new User("k", "k",
                            new HashSet<>(Arrays.asList(Role.ROLE_USER)),
                            LocalDateTime.now(),
                            true,
                            3

                    )
            )
    );
}
