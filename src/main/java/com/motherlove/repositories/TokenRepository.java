package com.motherlove.repositories;

import com.motherlove.models.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Token findByToken(String token);
    List<Token> findAllByUser_UserId(Long userId);
}
