package chatApp.repository;

import chatApp.Entities.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
//@EnableJpaRepositories
public interface GuestRepository extends JpaRepository<Guest, String> {
    Guest findByNickName(String nickName);

    @Modifying
    @Query("delete from Guest g where g.nickName = :nickName")
    @Transactional
    int deleteUserByNickName(@Param("nickName") String nickName);
}
