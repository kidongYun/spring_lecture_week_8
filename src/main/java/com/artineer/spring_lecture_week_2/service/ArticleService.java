package com.artineer.spring_lecture_week_2.service;

import com.artineer.spring_lecture_week_2.domain.Article;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleService {
    private Long id = 0L;
    final List<Article> database = new ArrayList<>();

    private Long getId() {
        return ++id;
    }

    public Long save(Article request) {
        Article domain = Article.builder()
                .id(getId())
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        database.add(domain);
        return domain.getId();
    }

    public Article findById(Long id) {
        return database.stream().filter(article -> article.getId().equals(id)).findFirst().get();
    }
}
