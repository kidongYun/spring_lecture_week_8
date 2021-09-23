package com.artineer.spring_lecture_week_2.conroller;

import com.artineer.spring_lecture_week_2.domain.Article;
import com.artineer.spring_lecture_week_2.dto.ArticleDto;
import com.artineer.spring_lecture_week_2.dto.Response;
import com.artineer.spring_lecture_week_2.service.ArticleService;
import com.artineer.spring_lecture_week_2.vo.ApiCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
@RestController
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping
    public Response<Long> post(@RequestBody ArticleDto.ReqPost request) {
        Article article = Article.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        Long id = articleService.save(article);

        return Response.<Long>builder()
                .code(ApiCode.SUCCESS)
                .data(id)
                .build();
    }

    @GetMapping("/{id}")
    public Response<ArticleDto.Res> get(@PathVariable Long id) {
        Article article = articleService.findById(id);

        ArticleDto.Res response = ArticleDto.Res.builder()
                .id(String.valueOf(article.getId()))
                .title(article.getTitle())
                .content(article.getContent())
                .build();

        return Response.<ArticleDto.Res>builder()
                .code(ApiCode.SUCCESS)
                .data(response)
                .build();
    }
}
