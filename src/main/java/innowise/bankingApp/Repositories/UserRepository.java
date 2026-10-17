package innowise.bankingApp.Repositories;

import innowise.bankingApp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
