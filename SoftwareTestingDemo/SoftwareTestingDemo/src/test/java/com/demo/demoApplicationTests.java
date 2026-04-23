package com.demo;

import com.demo.support.MainMethodAutoCloseListener;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class demoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void shouldInstantiateApplicationClass() {
        assertThat(new demoApplication()).isNotNull();
    }

    @Test
    void shouldRunMainMethod() {
        MainMethodAutoCloseListener.reset();

        demoApplication.main(new String[]{
                "--spring.profiles.active=main-method-test",
                "--spring.main.web-application-type=none",
                "--spring.main.banner-mode=off"
        });

        assertThat(MainMethodAutoCloseListener.wasInvoked()).isTrue();
    }

}
