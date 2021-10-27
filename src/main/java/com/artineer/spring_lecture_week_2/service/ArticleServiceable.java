package com.artineer.spring_lecture_week_2.service;

import com.artineer.spring_lecture_week_2.domain.Article;

public interface ArticleServiceable {
    Long save(Article request);

    Article findById(Long id);

    Article update(Article request);

    void delete(Long id);
}
