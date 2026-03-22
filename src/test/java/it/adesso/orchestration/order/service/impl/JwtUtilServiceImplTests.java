package it.adesso.orchestration.order.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilServiceImplTests {

    @InjectMocks
    private JwtUtilServiceImpl jwtUtilService;

    @Test
    void testGetUserIdentifier() {
        String result = jwtUtilService.getUserIdentifier();
        assertNotNull(result);
        assertEquals("QRT", result);
    }

    @Test
    void testGetUserIdentifierNotEmpty() {
        String result = jwtUtilService.getUserIdentifier();
        assertFalse(result.isEmpty());
    }
}
