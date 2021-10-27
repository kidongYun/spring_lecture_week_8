package com.artineer.spring_lecture_week_2.service;

import com.artineer.spring_lecture_week_2.domain.Article;
import com.artineer.spring_lecture_week_2.domain.ArticleRepository;
import com.artineer.spring_lecture_week_2.exception.ApiException;
import com.artineer.spring_lecture_week_2.exception.Asserts;
import com.artineer.spring_lecture_week_2.vo.ApiCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ArticleService implements ArticleServiceable {
    private final ArticleRepository articleRepository;

    @Override
    public Long save(Article request) {
        return articleRepository.save(request).getId();
    }

    @Override
    public Article findById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ApiException(ApiCode.DATA_IS_NOT_FOUND, "article is not found"));
    }

    @Override
    @Transactional
    public Article update(Article request) {
        Article article = this.findById(request.getId());
        article.update(request.getTitle(), request.getContent());

        return article;
    }

    @Override
    public void delete(Long id) {
        articleRepository.deleteById(id);
    }
}
