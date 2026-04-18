package com.demo.service.impl;

import com.demo.dao.MessageDao;
import com.demo.entity.Message;
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
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {
    @Mock
    private MessageDao messageDao;
    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
    void shouldFindMessageById() {
        Message message = TestDataFactory.message(1, "tester", 1, LocalDateTime.now());
        when(messageDao.getOne(1)).thenReturn(message);

        assertThat(messageService.findById(1)).isSameAs(message);
    }

    @Test
    void shouldFindMessagesByUser() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Message> page = new PageImpl<>(Collections.singletonList(
                TestDataFactory.message(1, "tester", 1, LocalDateTime.now())));
        when(messageDao.findAllByUserID("tester", pageable)).thenReturn(page);

        assertThat(messageService.findByUser("tester", pageable)).isSameAs(page);
    }

    @Test
    void shouldReturnIdWhenMessageCreated() {
        Message message = TestDataFactory.message(8, "tester", 1, LocalDateTime.now());
        when(messageDao.save(message)).thenReturn(message);

        assertThat(messageService.create(message)).isEqualTo(8);
    }

    @Test
    void shouldDeleteMessageById() {
        messageService.delById(4);

        verify(messageDao).deleteById(4);
    }

    @Test
    void shouldUpdateMessage() {
        Message message = TestDataFactory.message(4, "tester", 1, LocalDateTime.now());

        messageService.update(message);

        verify(messageDao).save(message);
    }

    @Test
    void shouldConfirmExistingMessage() {
        Message message = TestDataFactory.message(5, "tester", 1, LocalDateTime.now());
        when(messageDao.findByMessageID(5)).thenReturn(message);

        messageService.confirmMessage(5);

        verify(messageDao).updateState(MessageServiceImpl.STATE_PASS, 5);
    }

    @Test
    void shouldThrowWhenConfirmingMissingMessage() {
        when(messageDao.findByMessageID(5)).thenReturn(null);

        assertThatThrownBy(() -> messageService.confirmMessage(5))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("留言不存在");
    }

    @Test
    void shouldRejectExistingMessage() {
        Message message = TestDataFactory.message(6, "tester", 1, LocalDateTime.now());
        when(messageDao.findByMessageID(6)).thenReturn(message);

        messageService.rejectMessage(6);

        verify(messageDao).updateState(MessageServiceImpl.STATE_REJECT, 6);
    }

    @Test
    void shouldThrowWhenRejectingMissingMessage() {
        when(messageDao.findByMessageID(6)).thenReturn(null);

        assertThatThrownBy(() -> messageService.rejectMessage(6))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("留言不存在");
    }

    @Test
    void shouldFindWaitingMessages() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Message> page = new PageImpl<>(Collections.singletonList(
                TestDataFactory.message(2, "tester", 1, LocalDateTime.now())));
        when(messageDao.findAllByState(MessageServiceImpl.STATE_NO_AUDIT, pageable)).thenReturn(page);

        assertThat(messageService.findWaitState(pageable)).isSameAs(page);
    }

    @Test
    void shouldFindApprovedMessages() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Message> page = new PageImpl<>(Collections.singletonList(
                TestDataFactory.message(2, "tester", 2, LocalDateTime.now())));
        when(messageDao.findAllByState(MessageServiceImpl.STATE_PASS, pageable)).thenReturn(page);

        assertThat(messageService.findPassState(pageable)).isSameAs(page);
    }
}
