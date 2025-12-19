package com.example.quiz.repo;

import com.example.quiz.model.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface QuestionRepository extends MongoRepository<Question, String> {
    List<Question> findByCategoryAndDifficulty(String category, String difficulty);
}
