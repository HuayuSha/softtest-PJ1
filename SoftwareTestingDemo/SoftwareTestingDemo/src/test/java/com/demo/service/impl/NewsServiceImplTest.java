package com.demo.service.impl;

import com.demo.dao.NewsDao;
import com.demo.entity.News;
import com.demo.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {
    @Mock
    private NewsDao newsDao;
    @InjectMocks
    private NewsServiceImpl newsService;

    @Test
    void shouldPageNews() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<News> page = new PageImpl<>(Arrays.asList(
                TestDataFactory.news(1, "n1", LocalDateTime.now()),
                TestDataFactory.news(2, "n2", LocalDateTime.now().minusDays(1))));
        when(newsDao.findAll(pageable)).thenReturn(page);

        assertThat(newsService.findAll(pageable)).isSameAs(page);
    }

    @Test
    void shouldFindNewsById() {
        News news = TestDataFactory.news(1, "notice", LocalDateTime.now());
        when(newsDao.getOne(1)).thenReturn(news);

        assertThat(newsService.findById(1)).isSameAs(news);
    }

    @Test
    void shouldReturnIdWhenNewsCreated() {
        News news = TestDataFactory.news(9, "new", LocalDateTime.now());
        when(newsDao.save(news)).thenReturn(news);

        assertThat(newsService.create(news)).isEqualTo(9);
    }

    @Test
    void shouldDeleteNewsById() {
        newsService.delById(7);

        verify(newsDao).deleteById(7);
    }

    @Test
    void shouldUpdateNews() {
        News news = TestDataFactory.news(8, "update", LocalDateTime.now());

        newsService.update(news);

        verify(newsDao).save(news);
    }
}
