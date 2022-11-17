package chatApp.repository;

import chatApp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;

@Repository
//@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User, String> {
        User findByEmail(String email);
}
