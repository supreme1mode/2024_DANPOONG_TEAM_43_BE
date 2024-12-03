package com.carely.backend.controller;

import com.carely.backend.code.SuccessCode;
import com.carely.backend.controller.docs.NewsAPI;
import com.carely.backend.dto.news.CreateCommentDTO;
import com.carely.backend.dto.news.CreateNewsDTO;
import com.carely.backend.dto.news.NewsResponseDTO;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.service.NewsService;
import com.carely.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsController implements NewsAPI {
    private final NewsService newsService;
    private final UserService userService;

    @GetMapping("/group/{groupId}")
    public ResponseEntity<ResponseDTO> getGroupNewsList(@PathVariable("groupId") Long groupId) {

        List<NewsResponseDTO.List> res = newsService.getGroupNewsList(groupId);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_NEWS.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_NEWS, res));
    }

    @GetMapping("/detail/{newsId}")
    public ResponseEntity<ResponseDTO> getNewsDetail(@PathVariable("newsId") Long newsId) {
        NewsResponseDTO.Detail res = newsService.getNewsDetail(newsId);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_NEWS.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_NEWS, res));
    }

    @PostMapping("/create/news")
    public ResponseEntity<ResponseDTO<CreateNewsDTO.Res>> createNews(@Valid @RequestBody CreateNewsDTO createNewsDTO) {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();
        CreateNewsDTO.Res res = newsService.createNews(createNewsDTO, kakaoId);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_CREATE_NEWS.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_CREATE_NEWS, res));
    }

    @PostMapping("/create/news/{newsId}/comment")
    public ResponseEntity<ResponseDTO<CreateCommentDTO.Res>> createNewsComment(@PathVariable("newsId") Long newsId, @Valid @RequestBody CreateCommentDTO createNewsDTO) {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();
        CreateCommentDTO.Res res = newsService.createComment(newsId, createNewsDTO, kakaoId);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_CREATE_NEWS_COMMENT.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_CREATE_NEWS_COMMENT, res));
    }

}
