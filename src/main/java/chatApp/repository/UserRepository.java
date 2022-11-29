package chatApp.repository;

import chatApp.Entities.Guest;
import chatApp.Entities.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.transaction.Transactional;
import java.util.List;

@Repository
//@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);

    @Modifying
    @Query("update User u set u.status =:status where u.id =:id")
    @Transactional
    int updateUserSetStatusForId(@Param("status") String status,
                                 @Param("id") int id);

    @Modifying
    @Query("update User u set u.isMuted =true where u.id =:id")
    @Transactional
    int mute(@Param("id") int id);

    @Modifying
    @Query("update User u set u.isMuted =false where u.id =:id")
    @Transactional
    int unMute(@Param("id") int id);

    @Modifying
    @Query("update User u set u.id = :oldId where u.email =:email")
    @Transactional
    int updateId(@Param("oldId") int id,@Param("email") String email);

    User findUserById(Integer myid);


}
