package com.carely.backend.controller;

import com.carely.backend.code.SuccessCode;
import com.carely.backend.controller.docs.NewsAPI;
import com.carely.backend.dto.group.GetGroupDTO;
import com.carely.backend.dto.news.NewsResponseDTO;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsController implements NewsAPI {
    private final NewsService newsService;

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
}
