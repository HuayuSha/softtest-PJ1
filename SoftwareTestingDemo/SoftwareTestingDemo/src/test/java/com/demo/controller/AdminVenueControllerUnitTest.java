package com.demo.controller;

import com.demo.controller.admin.AdminVenueController;
import com.demo.service.VenueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminVenueControllerUnitTest {
    @Mock
    private VenueService venueService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @InjectMocks
    private AdminVenueController controller;

    @Test
    void shouldRedirectBackToAddPageWhenVenueCreationFails() throws Exception {
        when(venueService.create(org.mockito.ArgumentMatchers.any())).thenReturn(0);

        controller.addVenue(
                "失败场馆",
                "上海",
                "desc",
                100,
                new MockMultipartFile("picture", "", "application/octet-stream", new byte[0]),
                "09:00",
                "21:00",
                request,
                response
        );

        verify(request).setAttribute("message", "添加失败！");
        verify(response).sendRedirect("venue_add");
    }
}
