package frgp.utn.edu.ar.quepasa.repository;

import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.auth.Mail;
import frgp.utn.edu.ar.quepasa.model.auth.MailSerializable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MailRepository extends JpaRepository<Mail, MailSerializable> {

    public Optional<Mail> findByMailAndUser(String mail, User user);
    @Query("SELECT m FROM Mail m WHERE m.mail = :mail AND m.user.username = :username AND m.verified")
    Optional<Mail> findByMail(String mail, String username);

}
