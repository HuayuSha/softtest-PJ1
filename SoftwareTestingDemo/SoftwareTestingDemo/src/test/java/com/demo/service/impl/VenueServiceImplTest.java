package com.demo.service.impl;

import com.demo.dao.VenueDao;
import com.demo.entity.Venue;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VenueServiceImplTest {
    @Mock
    private VenueDao venueDao;
    @InjectMocks
    private VenueServiceImpl venueService;

    @Test
    void shouldFindVenueById() {
        Venue venue = TestDataFactory.venue(1, "Badminton Hall", 120);
        when(venueDao.getOne(1)).thenReturn(venue);

        assertThat(venueService.findByVenueID(1)).isSameAs(venue);
    }

    @Test
    void shouldFindVenueByName() {
        Venue venue = TestDataFactory.venue(1, "Badminton Hall", 120);
        when(venueDao.findByVenueName("Badminton Hall")).thenReturn(venue);

        assertThat(venueService.findByVenueName("Badminton Hall")).isSameAs(venue);
    }

    @Test
    void shouldPageAllVenues() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Venue> page = new PageImpl<>(Arrays.asList(
                TestDataFactory.venue(1, "V1", 100),
                TestDataFactory.venue(2, "V2", 200)));
        when(venueDao.findAll(pageable)).thenReturn(page);

        assertThat(venueService.findAll(pageable)).isSameAs(page);
    }

    @Test
    void shouldListAllVenues() {
        List<Venue> venues = Arrays.asList(
                TestDataFactory.venue(1, "V1", 100),
                TestDataFactory.venue(2, "V2", 200));
        when(venueDao.findAll()).thenReturn(venues);

        assertThat(venueService.findAll()).containsExactlyElementsOf(venues);
    }

    @Test
    void shouldReturnIdWhenVenueCreated() {
        Venue venue = TestDataFactory.venue(10, "New Venue", 300);
        when(venueDao.save(venue)).thenReturn(venue);

        assertThat(venueService.create(venue)).isEqualTo(10);
    }

    @Test
    void shouldUpdateVenue() {
        Venue venue = TestDataFactory.venue(10, "New Venue", 350);

        venueService.update(venue);

        verify(venueDao).save(venue);
    }

    @Test
    void shouldDeleteVenueById() {
        venueService.delById(10);

        verify(venueDao).deleteById(10);
    }

    @Test
    void shouldCountVenueName() {
        when(venueDao.countByVenueName("New Venue")).thenReturn(1);

        assertThat(venueService.countVenueName("New Venue")).isEqualTo(1);
    }
}
