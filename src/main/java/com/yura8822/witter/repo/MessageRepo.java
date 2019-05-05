package com.yura8822.witter.repo;

import com.yura8822.witter.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepo extends JpaRepository<Message, Long> {
    List<Message> findAll();

    @Query("select m from Message m where m.tag = ?1")
    List<Message> findByTag(String tag);
}
