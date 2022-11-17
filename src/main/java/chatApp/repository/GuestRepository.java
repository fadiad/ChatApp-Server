package chatApp.repository;

import chatApp.Entities.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepository extends JpaRepository<Guest, String> {
    Guest findByNickname(String nickName);
}
