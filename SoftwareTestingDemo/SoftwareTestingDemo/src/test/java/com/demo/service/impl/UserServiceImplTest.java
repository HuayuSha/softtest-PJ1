package com.demo.service.impl;

import com.demo.dao.UserDao;
import com.demo.entity.User;
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

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserDao userDao;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldFindUserByUserId() {
        User user = TestDataFactory.user(1, "tester", "pwd", 0, "Tester");
        when(userDao.findByUserID("tester")).thenReturn(user);

        assertThat(userService.findByUserID("tester")).isSameAs(user);
    }

    @Test
    void shouldFindUserById() {
        User user = TestDataFactory.user(1, "tester", "pwd", 0, "Tester");
        when(userDao.findById(1)).thenReturn(user);

        assertThat(userService.findById(1)).isSameAs(user);
    }

    @Test
    void shouldPageOnlyNormalUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(Arrays.asList(
                TestDataFactory.user(1, "u1", "p1", 0, "U1"),
                TestDataFactory.user(2, "u2", "p2", 0, "U2")));
        when(userDao.findAllByIsadmin(0, pageable)).thenReturn(page);

        assertThat(userService.findByUserID(pageable)).isSameAs(page);
    }

    @Test
    void shouldCheckLoginByCredentials() {
        User user = TestDataFactory.user(1, "tester", "pwd", 0, "Tester");
        when(userDao.findByUserIDAndPassword("tester", "pwd")).thenReturn(user);

        assertThat(userService.checkLogin("tester", "pwd")).isSameAs(user);
    }

    @Test
    void shouldReturnNewUserCountAfterCreate() {
        User user = TestDataFactory.user(0, "newUser", "pwd", 0, "New User");
        when(userDao.findAll()).thenReturn(Arrays.asList(user, TestDataFactory.user(1, "u2", "p2", 0, "U2")));

        int createdCount = userService.create(user);

        verify(userDao).save(user);
        assertThat(createdCount).isEqualTo(2);
    }

    @Test
    void shouldDeleteUserById() {
        userService.delByID(5);

        verify(userDao).deleteById(5);
    }

    @Test
    void shouldUpdateUser() {
        User user = TestDataFactory.user(2, "tester", "newPwd", 0, "Tester");

        userService.updateUser(user);

        verify(userDao).save(user);
    }

    @Test
    void shouldCountExistingUserId() {
        when(userDao.countByUserID("tester")).thenReturn(1);

        assertThat(userService.countUserID("tester")).isEqualTo(1);
    }
}
