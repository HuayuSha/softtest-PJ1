package com.demo.support;

import com.demo.entity.Message;
import com.demo.entity.News;
import com.demo.entity.Order;
import com.demo.entity.User;
import com.demo.entity.Venue;

import java.time.LocalDateTime;

public final class TestDataFactory {
    private TestDataFactory() {
    }

    public static User user(int id, String userID, String password, int isAdmin, String userName) {
        User user = new User();
        user.setId(id);
        user.setUserID(userID);
        user.setPassword(password);
        user.setIsadmin(isAdmin);
        user.setUserName(userName);
        user.setEmail(userID + "@mail.com");
        user.setPhone("13800000000");
        user.setPicture("");
        return user;
    }

    public static Venue venue(int venueID, String venueName, int price) {
        Venue venue = new Venue();
        venue.setVenueID(venueID);
        venue.setVenueName(venueName);
        venue.setPrice(price);
        venue.setAddress("Shanghai");
        venue.setDescription("desc-" + venueName);
        venue.setOpen_time("09:00");
        venue.setClose_time("21:00");
        venue.setPicture("");
        return venue;
    }

    public static News news(int newsID, String title, LocalDateTime time) {
        News news = new News();
        news.setNewsID(newsID);
        news.setTitle(title);
        news.setContent("content-" + title);
        news.setTime(time);
        return news;
    }

    public static Message message(int messageID, String userID, int state, LocalDateTime time) {
        Message message = new Message();
        message.setMessageID(messageID);
        message.setUserID(userID);
        message.setContent("content-" + messageID);
        message.setState(state);
        message.setTime(time);
        return message;
    }

    public static Order order(int orderID, String userID, int venueID, int state, LocalDateTime orderTime,
                              LocalDateTime startTime, int hours, int total) {
        Order order = new Order();
        order.setOrderID(orderID);
        order.setUserID(userID);
        order.setVenueID(venueID);
        order.setState(state);
        order.setOrderTime(orderTime);
        order.setStartTime(startTime);
        order.setHours(hours);
        order.setTotal(total);
        return order;
    }
}
