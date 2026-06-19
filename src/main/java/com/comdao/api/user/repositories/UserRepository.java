package com.comdao.api.user.repositories;

import com.comdao.api.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByEmail(String email);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND (:id IS NULL OR u.id <> :id)")
    public Boolean existsByEmailAndIdIsNot(String email, Long id);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.phone = :phone AND (:id IS NULL OR u.id <> :id)")
    public Boolean existsByPhoneAndIdIsNot(String phone, Long id);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND (:id IS NULL OR u.id <> :id)")
    public Boolean existsByUsernameAndIdIsNot(String username, Long id);

    @Query("SELECT u FROM User u WHERE u.username = :id OR u.email = :id OR u.phone = :id")
    public Optional<User> findByLoginId(String id);

    Boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
