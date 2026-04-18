package com.demo.controller;

import com.demo.entity.Message;
import com.demo.exception.LoginException;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.NestedServletException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class MessageControllerIntegrationTest extends AbstractControllerIntegrationTest {
    @Test
    void shouldRequireLoginForMessagePage() {
        assertThatThrownBy(() -> mockMvc.perform(get("/message_list")))
                .isInstanceOf(NestedServletException.class)
                .hasCauseInstanceOf(LoginException.class);
    }

    @Test
    void shouldRenderMessagePageForLoggedUser() throws Exception {
        mockMvc.perform(get("/message_list").session(userSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("message_list"))
                .andExpect(model().attributeExists("total", "user_total"));
    }

    @Test
    void shouldReturnApprovedMessagesOnly() throws Exception {
        mockMvc.perform(get("/message/getMessageList").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].state").value(2))
                .andExpect(jsonPath("$[1].state").value(2));
    }

    @Test
    void shouldReturnCurrentUsersAllMessages() throws Exception {
        mockMvc.perform(get("/message/findUserList").session(userSession()).param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldRequireLoginForUserMessageListApi() {
        assertThatThrownBy(() -> mockMvc.perform(get("/message/findUserList").param("page", "1")))
                .isInstanceOf(NestedServletException.class)
                .hasCauseInstanceOf(LoginException.class);
    }

    @Test
    void shouldCreatePendingMessage() throws Exception {
        mockMvc.perform(post("/sendMessage")
                        .param("userID", normalUser.getUserID())
                        .param("content", "新的留言"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/message_list"));

        Message created = messageDao.findAll().stream()
                .filter(message -> "新的留言".equals(message.getContent()))
                .findFirst()
                .orElse(null);
        assertThat(created).isNotNull();
        assertThat(created.getState()).isEqualTo(1);
    }

    @Test
    void shouldModifyMessageAndResetAuditState() throws Exception {
        mockMvc.perform(post("/modifyMessage.do")
                        .param("messageID", String.valueOf(approvedMessage.getMessageID()))
                        .param("content", "修改后的留言"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        Message updated = messageDao.findByMessageID(approvedMessage.getMessageID());
        assertThat(updated.getContent()).isEqualTo("修改后的留言");
        assertThat(updated.getState()).isEqualTo(1);
    }

    @Test
    void shouldDeleteMessageByPostEndpoint() throws Exception {
        mockMvc.perform(post("/delMessage.do").param("messageID", String.valueOf(approvedMessage.getMessageID())))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        assertThat(messageDao.findByMessageID(approvedMessage.getMessageID())).isNull();
    }
}
