package com.demo.service.impl;

import com.demo.dao.MessageDao;
import com.demo.dao.UserDao;
import com.demo.entity.Message;
import com.demo.entity.User;
import com.demo.entity.vo.MessageVo;
import com.demo.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageVoServiceImplTest {
    @Mock
    private MessageDao messageDao;
    @Mock
    private UserDao userDao;
    @InjectMocks
    private MessageVoServiceImpl messageVoService;

    @Test
    void shouldConvertSingleMessageToViewObject() {
        Message message = TestDataFactory.message(1, "tester", 2, LocalDateTime.now());
        User user = TestDataFactory.user(9, "tester", "pwd", 0, "Tester");
        when(messageDao.findByMessageID(1)).thenReturn(message);
        when(userDao.findByUserID("tester")).thenReturn(user);

        MessageVo messageVo = messageVoService.returnMessageVoByMessageID(1);

        assertThat(messageVo.getMessageID()).isEqualTo(1);
        assertThat(messageVo.getUserID()).isEqualTo("tester");
        assertThat(messageVo.getUserName()).isEqualTo("Tester");
        assertThat(messageVo.getState()).isEqualTo(2);
    }

    @Test
    void shouldConvertMessageListToViewObjects() {
        Message message1 = TestDataFactory.message(1, "tester", 2, LocalDateTime.now());
        Message message2 = TestDataFactory.message(2, "tester2", 1, LocalDateTime.now().minusHours(1));
        when(messageDao.findByMessageID(1)).thenReturn(message1);
        when(messageDao.findByMessageID(2)).thenReturn(message2);
        when(userDao.findByUserID("tester")).thenReturn(TestDataFactory.user(1, "tester", "pwd", 0, "Tester"));
        when(userDao.findByUserID("tester2")).thenReturn(TestDataFactory.user(2, "tester2", "pwd", 0, "Tester2"));

        List<MessageVo> vos = messageVoService.returnVo(Arrays.asList(message1, message2));

        assertThat(vos).hasSize(2);
        assertThat(vos).extracting(MessageVo::getUserName).containsExactly("Tester", "Tester2");
    }
}
