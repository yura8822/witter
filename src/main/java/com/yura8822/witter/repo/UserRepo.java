package com.yura8822.witter.repo;

import com.yura8822.witter.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepo extends JpaRepository<User, Long> {
    @Query("select u from User u where u.username = ?1")
    User findByUsername(String username);

    @Query("select u from User u where u.id = ?1")
    User findByUserID(Long id);

    @Query("select u from User u where u.activationCode=?1")
    User findByUserActivateCode(String uuid);
}
