package com.question.services;

import com.question.entity.Question;

import java.util.List;

public interface QuestionService {

    Question create(Question question);

    List<Question> get();

    Question get(Long id);

    List<Question> getByQuizId(Long quizId);
}
