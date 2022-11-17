package chatApp.repository;

import chatApp.Entities.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface GuestRepository extends JpaRepository<Guest, String> {
    Guest findByNickName(String nickName);
}
