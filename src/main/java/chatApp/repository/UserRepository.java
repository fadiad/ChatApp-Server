package chatApp.repository;

import chatApp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;

@Repository

public interface UserRepository extends JpaRepository<User, Long> {
        User findByEmail(String email);
}
