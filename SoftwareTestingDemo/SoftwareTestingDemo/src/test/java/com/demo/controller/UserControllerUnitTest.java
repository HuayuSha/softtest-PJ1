package com.demo.controller;

import com.demo.controller.user.UserController;
import com.demo.entity.User;
import com.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerUnitTest {
    @Mock
    private UserService userService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @InjectMocks
    private UserController controller;

    @Test
    void shouldRejectUnexpectedAdminRoleDuringLogin() throws Exception {
        User unexpectedRoleUser = new User();
        unexpectedRoleUser.setUserID("student-x");
        unexpectedRoleUser.setIsadmin(2);
        when(userService.checkLogin("student-x", "pwd")).thenReturn(unexpectedRoleUser);

        String result = controller.login("student-x", "pwd", request);

        assertThat(result).isEqualTo("false");
        verify(request, never()).getSession();
    }

    @Test
    void shouldKeepOldPasswordWhenNewPasswordIsNull() throws Exception {
        User existingUser = new User();
        existingUser.setUserID("student1");
        existingUser.setUserName("原用户名");
        existingUser.setPassword("old-pwd");
        existingUser.setEmail("old@mail.com");
        existingUser.setPhone("13500000000");
        existingUser.setPicture("file/user/original.png");

        when(userService.findByUserID("student1")).thenReturn(existingUser);
        when(request.getSession()).thenReturn(session);

        controller.updateUser(
                "新用户名",
                "student1",
                null,
                "new@mail.com",
                "13600000000",
                new MockMultipartFile("picture", "", "application/octet-stream", new byte[0]),
                request,
                response
        );

        assertThat(existingUser.getUserName()).isEqualTo("新用户名");
        assertThat(existingUser.getPassword()).isEqualTo("old-pwd");
        assertThat(existingUser.getEmail()).isEqualTo("new@mail.com");
        assertThat(existingUser.getPhone()).isEqualTo("13600000000");
        assertThat(existingUser.getPicture()).isEqualTo("file/user/original.png");
        verify(userService).updateUser(existingUser);
        verify(session).removeAttribute("user");
        verify(session).setAttribute("user", existingUser);
        verify(response).sendRedirect("user_info");
    }
}
