package com.carely.backend.service;

import com.carely.backend.domain.Group;
import com.carely.backend.domain.News;
import com.carely.backend.domain.NewsComment;
import com.carely.backend.domain.User;
import com.carely.backend.dto.news.CreateCommentDTO;
import com.carely.backend.dto.news.CreateNewsDTO;
import com.carely.backend.dto.news.NewsResponseDTO;
import com.carely.backend.exception.GroupNotFoundException;
import com.carely.backend.exception.NewsNotFoundException;
import com.carely.backend.repository.GroupRepository;
import com.carely.backend.repository.NewsCommentRepository;
import com.carely.backend.repository.NewsRepository;
import com.carely.backend.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;
    private final GroupRepository groupRepository;
    private final NewsCommentRepository newsCommentRepository;
    private final UserRepository userRepository;

    public List<NewsResponseDTO.List> getGroupNewsList(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("그룹을 찾을 수 없습니다."));

        List<News> newsList;
        newsList = newsRepository.findAllByGroupOrderByCreatedAtDesc(group);

        return newsList.stream()
                .map(NewsResponseDTO.List::toDTO)
                .collect(Collectors.toList());
    }

    public NewsResponseDTO.Detail getNewsDetail(Long newsId) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new NewsNotFoundException("그룹을 찾을 수 없습니다."));

        return NewsResponseDTO.Detail.toDTO(news);
    }

    @Transactional
    public CreateNewsDTO.Res createNews(Long groupId, @Valid CreateNewsDTO createNewsDTO, String kakaoId) {
        User writer = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("그룹을 찾을 수 없습니다."));

        News news = News.builder()
                .content(createNewsDTO.getContent())
                .title(createNewsDTO.getTitle())
                .writer(writer)
                .group(group)
                .build();

        News newNews = newsRepository.save(news);
        return CreateNewsDTO.Res.toDTO(newNews);
    }

    @Transactional
    public CreateCommentDTO.Res createComment(Long newsId, @Valid CreateCommentDTO createNewsDTO, String kakaoId) {
        User writer = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new NewsNotFoundException("소식을 찾을 수 없습니다."));

        NewsComment comment = NewsComment.builder()
                .content(createNewsDTO.getContent())
                .writer(writer)
                .news(news)
                .build();

        NewsComment newsComment = newsCommentRepository.save(comment);
        return CreateCommentDTO.Res.toDTO(newsComment);
    }
}
