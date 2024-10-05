package frgp.utn.edu.ar.quepasa.repository;

import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.auth.Phone;
import frgp.utn.edu.ar.quepasa.model.auth.PhoneSerializable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, PhoneSerializable> {
    Optional<Phone> findByPhoneAndUser(String phone, User user);
    @Query("SELECT p FROM Phone p WHERE p.phone = :phone AND p.verified AND p.user.username = :username")
    Optional<Phone> findByPhone(String phone, String username);
}
