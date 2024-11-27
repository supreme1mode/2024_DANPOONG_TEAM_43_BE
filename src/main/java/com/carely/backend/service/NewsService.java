package com.carely.backend.service;

import com.carely.backend.domain.Group;
import com.carely.backend.domain.News;
import com.carely.backend.domain.NewsComment;
import com.carely.backend.dto.news.NewsResponseDTO;
import com.carely.backend.exception.GroupNotFoundException;
import com.carely.backend.exception.NewsNotFoundException;
import com.carely.backend.repository.GroupRepository;
import com.carely.backend.repository.NewsCommentRepository;
import com.carely.backend.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;
    private final GroupRepository groupRepository;
    private final NewsCommentRepository newsCommentRepository;

    public List<NewsResponseDTO.List> getGroupNewsList(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("그룹을 찾을 수 없습니다."));

        List<News> newsList;
        newsList = newsRepository.findAllByGroup(group);

        return newsList.stream()
                .map(NewsResponseDTO.List::toDTO)
                .collect(Collectors.toList());
    }

    public NewsResponseDTO.Detail getNewsDetail(Long newsId) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new NewsNotFoundException("그룹을 찾을 수 없습니다."));

        return NewsResponseDTO.Detail.toDTO(news);
    }
}
