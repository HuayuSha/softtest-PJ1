package com.demo.controller;

import com.demo.dao.MessageDao;
import com.demo.dao.NewsDao;
import com.demo.dao.OrderDao;
import com.demo.dao.UserDao;
import com.demo.dao.VenueDao;
import com.demo.entity.Message;
import com.demo.entity.News;
import com.demo.entity.Order;
import com.demo.entity.User;
import com.demo.entity.Venue;
import com.demo.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractControllerIntegrationTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected UserDao userDao;
    @Autowired
    protected VenueDao venueDao;
    @Autowired
    protected NewsDao newsDao;
    @Autowired
    protected MessageDao messageDao;
    @Autowired
    protected OrderDao orderDao;

    protected User normalUser;
    protected User anotherUser;
    protected User adminUser;
    protected Venue venueA;
    protected Venue venueB;
    protected News olderNews;
    protected News newerNews;
    protected Message pendingMessage;
    protected Message approvedMessage;
    protected Message anotherApprovedMessage;
    protected Order pendingOrder;
    protected Order approvedOrder;
    protected Order finishedOrder;

    @BeforeEach
    void initData() {
        orderDao.deleteAll();
        messageDao.deleteAll();
        newsDao.deleteAll();
        venueDao.deleteAll();
        userDao.deleteAll();

        adminUser = userDao.save(TestDataFactory.user(0, "admin", "admin", 1, "管理员"));
        normalUser = userDao.save(TestDataFactory.user(0, "student1", "pwd1", 0, "学生一"));
        anotherUser = userDao.save(TestDataFactory.user(0, "student2", "pwd2", 0, "学生二"));

        venueA = venueDao.save(TestDataFactory.venue(0, "羽毛球馆", 100));
        venueB = venueDao.save(TestDataFactory.venue(0, "篮球馆", 200));

        olderNews = newsDao.save(TestDataFactory.news(0, "旧通知", LocalDateTime.of(2026, 4, 17, 10, 0)));
        newerNews = newsDao.save(TestDataFactory.news(0, "新通知", LocalDateTime.of(2026, 4, 18, 10, 0)));

        pendingMessage = messageDao.save(TestDataFactory.message(0, normalUser.getUserID(), 1,
                LocalDateTime.of(2026, 4, 18, 8, 0)));
        approvedMessage = messageDao.save(TestDataFactory.message(0, normalUser.getUserID(), 2,
                LocalDateTime.of(2026, 4, 18, 9, 0)));
        anotherApprovedMessage = messageDao.save(TestDataFactory.message(0, anotherUser.getUserID(), 2,
                LocalDateTime.of(2026, 4, 18, 10, 0)));

        pendingOrder = orderDao.save(TestDataFactory.order(0, normalUser.getUserID(), venueA.getVenueID(), 1,
                LocalDateTime.of(2026, 4, 18, 9, 0),
                LocalDateTime.of(2026, 4, 20, 10, 0), 2, 200));
        approvedOrder = orderDao.save(TestDataFactory.order(0, normalUser.getUserID(), venueB.getVenueID(), 2,
                LocalDateTime.of(2026, 4, 18, 10, 0),
                LocalDateTime.of(2026, 4, 21, 11, 0), 3, 600));
        finishedOrder = orderDao.save(TestDataFactory.order(0, anotherUser.getUserID(), venueA.getVenueID(), 3,
                LocalDateTime.of(2026, 4, 18, 11, 0),
                LocalDateTime.of(2026, 4, 22, 12, 0), 1, 100));
    }

    protected MockHttpSession userSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", normalUser);
        return session;
    }

    protected MockHttpSession adminSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("admin", adminUser);
        return session;
    }

    protected MockMultipartFile emptyImage(String fieldName) {
        return new MockMultipartFile(fieldName, "", "application/octet-stream", new byte[0]);
    }
}
