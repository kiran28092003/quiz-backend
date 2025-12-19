package com.example.quiz.repo;

import com.example.quiz.model.Score;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ScoreRepository extends MongoRepository<Score, String> {
    List<Score> findByCategoryAndDifficulty(String category, String difficulty, Pageable pageable);
}
