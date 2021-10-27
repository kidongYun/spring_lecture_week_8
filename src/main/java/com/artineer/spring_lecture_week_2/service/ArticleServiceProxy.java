package com.artineer.spring_lecture_week_2.service;

import com.artineer.spring_lecture_week_2.domain.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Primary
@Service
public class ArticleServiceProxy implements ArticleServiceable {
    private final ArticleService articleService;

    @Override
    public Long save(Article request) {
        return articleService.save(request);
    }

    @Override
    public Article findById(Long id) {
        return articleService.findById(id);
    }

    @Override
    public Article update(Article request) {
        return articleService.update(request);
    }

    @Override
    public void delete(Long id) {
        articleService.delete(id);
    }
}
