package com.okami.server.repository;

import com.okami.server.domain.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Integer> {

    Optional<Note> findByUserUsernameAndName(String username, String name);

    boolean existsByUserUsernameAndName(String username, String name);

    @Query(value = """
                SELECT n.name
                FROM Note n
                JOIN n.user u
                WHERE u.username = :username
            """)
    List<String> getNamesByUsername(String username);

    void deleteByNameAndUserUsername(String name, String username);
}
