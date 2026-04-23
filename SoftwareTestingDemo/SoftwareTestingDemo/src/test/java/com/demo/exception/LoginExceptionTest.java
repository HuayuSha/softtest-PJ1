package com.demo.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginExceptionTest {
    @Test
    void shouldCreateDefaultLoginException() {
        LoginException exception = new LoginException();

        assertThat(exception).hasMessage(null);
        assertThat(exception).hasNoCause();
    }

    @Test
    void shouldCreateLoginExceptionWithMessage() {
        LoginException exception = new LoginException("请先登录");

        assertThat(exception).hasMessage("请先登录");
        assertThat(exception).hasNoCause();
    }

    @Test
    void shouldCreateLoginExceptionWithMessageAndCause() {
        Throwable cause = new IllegalStateException("session missing");

        LoginException exception = new LoginException("请先登录", cause);

        assertThat(exception).hasMessage("请先登录");
        assertThat(exception).hasCause(cause);
    }

    @Test
    void shouldCreateLoginExceptionWithCause() {
        Throwable cause = new IllegalArgumentException("bad user");

        LoginException exception = new LoginException(cause);

        assertThat(exception).hasCause(cause);
    }

    @Test
    void shouldCreateLoginExceptionWithFullConstructor() {
        Throwable cause = new RuntimeException("expired");

        LoginException exception = new LoginException("请重新登录", cause, false, false);

        assertThat(exception).hasMessage("请重新登录");
        assertThat(exception).hasCause(cause);
    }
}
