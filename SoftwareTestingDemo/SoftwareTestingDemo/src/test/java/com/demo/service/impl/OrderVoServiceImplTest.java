package com.demo.service.impl;

import com.demo.dao.OrderDao;
import com.demo.dao.VenueDao;
import com.demo.entity.Order;
import com.demo.entity.Venue;
import com.demo.entity.vo.OrderVo;
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
class OrderVoServiceImplTest {
    @Mock
    private OrderDao orderDao;
    @Mock
    private VenueDao venueDao;
    @InjectMocks
    private OrderVoServiceImpl orderVoService;

    @Test
    void shouldConvertSingleOrderToViewObject() {
        Order order = TestDataFactory.order(1, "tester", 2, 2, LocalDateTime.now(),
                LocalDateTime.now().plusDays(1), 2, 400);
        Venue venue = TestDataFactory.venue(2, "Hall 2", 200);
        when(orderDao.findByOrderID(1)).thenReturn(order);
        when(venueDao.findByVenueID(2)).thenReturn(venue);

        OrderVo orderVo = orderVoService.returnOrderVoByOrderID(1);

        assertThat(orderVo.getOrderID()).isEqualTo(1);
        assertThat(orderVo.getUserID()).isEqualTo("tester");
        assertThat(orderVo.getVenueName()).isEqualTo("Hall 2");
        assertThat(orderVo.getTotal()).isEqualTo(400);
    }

    @Test
    void shouldConvertOrderListToViewObjects() {
        Order order1 = TestDataFactory.order(1, "tester", 2, 2, LocalDateTime.now(),
                LocalDateTime.now().plusDays(1), 2, 400);
        Order order2 = TestDataFactory.order(2, "tester", 3, 1, LocalDateTime.now(),
                LocalDateTime.now().plusDays(2), 1, 150);
        when(orderDao.findByOrderID(1)).thenReturn(order1);
        when(orderDao.findByOrderID(2)).thenReturn(order2);
        when(venueDao.findByVenueID(2)).thenReturn(TestDataFactory.venue(2, "Hall 2", 200));
        when(venueDao.findByVenueID(3)).thenReturn(TestDataFactory.venue(3, "Hall 3", 150));

        List<OrderVo> orderVos = orderVoService.returnVo(Arrays.asList(order1, order2));

        assertThat(orderVos).hasSize(2);
        assertThat(orderVos).extracting(OrderVo::getVenueName).containsExactly("Hall 2", "Hall 3");
    }
}
