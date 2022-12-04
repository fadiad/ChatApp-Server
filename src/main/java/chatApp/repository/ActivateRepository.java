package chatApp.repository;


import chatApp.Entities.ActiveUser;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;


@Repository
public interface ActivateRepository extends JpaRepository<ActiveUser, String> {

    ActiveUser findByEmail(String email);

    ActiveUser findByCode(String code);


//    @Modifying
//    @Query(value = "Select * from active-user where code = :myCode",nativeQuery = true)
//    @Transactional
//    int findByCode(@Param("myCode") String myCode);
}