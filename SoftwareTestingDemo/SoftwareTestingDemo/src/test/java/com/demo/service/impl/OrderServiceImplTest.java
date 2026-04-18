package com.demo.service.impl;

import com.demo.dao.OrderDao;
import com.demo.dao.VenueDao;
import com.demo.entity.Order;
import com.demo.entity.Venue;
import com.demo.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderDao orderDao;
    @Mock
    private VenueDao venueDao;
    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void shouldFindOrderById() {
        Order order = TestDataFactory.order(1, "tester", 2, 1, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 2, 400);
        when(orderDao.getOne(1)).thenReturn(order);

        assertThat(orderService.findById(1)).isSameAs(order);
    }

    @Test
    void shouldFindOrdersWithinDateRange() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        List<Order> orders = Collections.singletonList(
                TestDataFactory.order(1, "tester", 2, 2, start, start.plusHours(2), 2, 400));
        when(orderDao.findByVenueIDAndStartTimeIsBetween(2, start, end)).thenReturn(orders);

        assertThat(orderService.findDateOrder(2, start, end)).containsExactlyElementsOf(orders);
    }

    @Test
    void shouldFindUserOrders() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Order> page = new PageImpl<>(Collections.singletonList(
                TestDataFactory.order(1, "tester", 2, 2, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 2, 400)));
        when(orderDao.findAllByUserID("tester", pageable)).thenReturn(page);

        assertThat(orderService.findUserOrder("tester", pageable)).isSameAs(page);
    }

    @Test
    void shouldUpdateOrderWithRecalculatedFields() {
        Venue venue = TestDataFactory.venue(8, "Hall 8", 150);
        Order order = TestDataFactory.order(3, "tester", 2, 2, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), 1, 150);
        LocalDateTime startTime = LocalDateTime.of(2026, 4, 20, 10, 0);
        when(venueDao.findByVenueName("Hall 8")).thenReturn(venue);
        when(orderDao.findByOrderID(3)).thenReturn(order);

        LocalDateTime before = LocalDateTime.now();
        orderService.updateOrder(3, "Hall 8", startTime, 4, "newUser");
        LocalDateTime after = LocalDateTime.now();

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderDao).save(captor.capture());
        Order saved = captor.getValue();
        assertThat(saved.getState()).isEqualTo(OrderServiceImpl.STATE_NO_AUDIT);
        assertThat(saved.getVenueID()).isEqualTo(8);
        assertThat(saved.getStartTime()).isEqualTo(startTime);
        assertThat(saved.getHours()).isEqualTo(4);
        assertThat(saved.getUserID()).isEqualTo("newUser");
        assertThat(saved.getTotal()).isEqualTo(600);
        assertThat(saved.getOrderTime()).isBetween(before.minusSeconds(1), after.plusSeconds(1));
    }

    @Test
    void shouldSubmitNewOrder() {
        Venue venue = TestDataFactory.venue(6, "Hall 6", 200);
        when(venueDao.findByVenueName("Hall 6")).thenReturn(venue);
        LocalDateTime startTime = LocalDateTime.of(2026, 4, 25, 9, 0);

        LocalDateTime before = LocalDateTime.now();
        orderService.submit("Hall 6", startTime, 3, "tester");
        LocalDateTime after = LocalDateTime.now();

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderDao).save(captor.capture());
        Order saved = captor.getValue();
        assertThat(saved.getState()).isEqualTo(OrderServiceImpl.STATE_NO_AUDIT);
        assertThat(saved.getVenueID()).isEqualTo(6);
        assertThat(saved.getStartTime()).isEqualTo(startTime);
        assertThat(saved.getHours()).isEqualTo(3);
        assertThat(saved.getUserID()).isEqualTo("tester");
        assertThat(saved.getTotal()).isEqualTo(600);
        assertThat(saved.getOrderTime()).isBetween(before.minusSeconds(1), after.plusSeconds(1));
    }

    @Test
    void shouldDeleteOrderById() {
        orderService.delOrder(4);

        verify(orderDao).deleteById(4);
    }

    @Test
    void shouldConfirmExistingOrder() {
        when(orderDao.findByOrderID(5)).thenReturn(
                TestDataFactory.order(5, "tester", 2, 1, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 2, 400));

        orderService.confirmOrder(5);

        verify(orderDao).updateState(OrderServiceImpl.STATE_WAIT, 5);
    }

    @Test
    void shouldThrowWhenConfirmingMissingOrder() {
        when(orderDao.findByOrderID(5)).thenReturn(null);

        assertThatThrownBy(() -> orderService.confirmOrder(5))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("订单不存在");
    }

    @Test
    void shouldFinishExistingOrder() {
        when(orderDao.findByOrderID(6)).thenReturn(
                TestDataFactory.order(6, "tester", 2, 2, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 2, 400));

        orderService.finishOrder(6);

        verify(orderDao).updateState(OrderServiceImpl.STATE_FINISH, 6);
    }

    @Test
    void shouldThrowWhenFinishingMissingOrder() {
        when(orderDao.findByOrderID(6)).thenReturn(null);

        assertThatThrownBy(() -> orderService.finishOrder(6))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("订单不存在");
    }

    @Test
    void shouldRejectExistingOrder() {
        when(orderDao.findByOrderID(7)).thenReturn(
                TestDataFactory.order(7, "tester", 2, 1, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 2, 400));

        orderService.rejectOrder(7);

        verify(orderDao).updateState(OrderServiceImpl.STATE_REJECT, 7);
    }

    @Test
    void shouldThrowWhenRejectingMissingOrder() {
        when(orderDao.findByOrderID(7)).thenReturn(null);

        assertThatThrownBy(() -> orderService.rejectOrder(7))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("订单不存在");
    }

    @Test
    void shouldFindNoAuditOrders() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(Arrays.asList(
                TestDataFactory.order(1, "tester", 2, 1, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 2, 400)));
        when(orderDao.findAllByState(OrderServiceImpl.STATE_NO_AUDIT, pageable)).thenReturn(page);

        assertThat(orderService.findNoAuditOrder(pageable)).isSameAs(page);
    }

    @Test
    void shouldFindAuditedOrders() {
        List<Order> orders = Arrays.asList(
                TestDataFactory.order(1, "tester", 2, 2, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 2, 400),
                TestDataFactory.order(2, "tester", 2, 3, LocalDateTime.now(), LocalDateTime.now().plusDays(2), 1, 200));
        when(orderDao.findAudit(OrderServiceImpl.STATE_WAIT, OrderServiceImpl.STATE_FINISH)).thenReturn(orders);

        assertThat(orderService.findAuditOrder()).containsExactlyElementsOf(orders);
    }
}
