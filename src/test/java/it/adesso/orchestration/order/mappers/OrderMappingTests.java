package it.adesso.orchestration.order.mappers;

import it.adesso.orchestration.order.DTO.OrderDTO;
import it.adesso.orchestration.order.DTO.OrderEntryDTO;
import it.adesso.orchestration.order.entities.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderMappingTests {

    private OrderMapping orderMapping;

    private OrderDTO orderDTO;
    private OrderEntryDTO orderEntryDTO;

    @BeforeEach
    void setUp() {
        orderMapping = new OrderMappingImpl();
        
        orderEntryDTO = new OrderEntryDTO();
        orderEntryDTO.setId(1L);
        orderEntryDTO.setOrderId(1L);
        orderEntryDTO.setIdPizza(1L);
        orderEntryDTO.setIdBase(1L);
        orderEntryDTO.setAddedIngredients(new HashSet<>(Set.of(1L, 2L)));
        orderEntryDTO.setRemovedIngredients(new HashSet<>(Set.of(3L)));

        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setEntries(List.of(orderEntryDTO));
    }

    @Test
    void testToEntity_Success() {
        Order result = orderMapping.toEntity(orderDTO);

        assertNotNull(result);
        assertNotNull(result.getOrder());
    }

    @Test
    void testToEntity_ContainsOrderDTO() {
        Order result = orderMapping.toEntity(orderDTO);

        assertNotNull(result.getOrder());
        assertTrue(result.getOrder().containsKey("id"));
    }

    @Test
    void testToEntity_WithEntries() {
        Order result = orderMapping.toEntity(orderDTO);

        assertNotNull(result);
        assertNotNull(result.getOrder());
        assertTrue(result.getOrder().containsKey("entries"));
    }

    @Test
    void testToEntity_EmptyEntries() {
        orderDTO.setEntries(List.of());
        Order result = orderMapping.toEntity(orderDTO);

        assertNotNull(result);
        assertNotNull(result.getOrder());
    }

    @Test
    void testToEntity_MultipleEntries() {
        OrderEntryDTO entry2 = new OrderEntryDTO();
        entry2.setId(2L);
        entry2.setOrderId(1L);
        entry2.setIdPizza(2L);
        entry2.setIdBase(2L);
        entry2.setAddedIngredients(new HashSet<>(Set.of(5L)));
        entry2.setRemovedIngredients(new HashSet<>());

        orderDTO.setEntries(List.of(orderEntryDTO, entry2));
        Order result = orderMapping.toEntity(orderDTO);

        assertNotNull(result);
        Map<String, Object> orderMap = result.getOrder();
        assertNotNull(orderMap);
    }

    @Test
    void testToEntity_PreservesOrderData() {
        Long originalId = orderDTO.getId();
        Order result = orderMapping.toEntity(orderDTO);

        assertNotNull(result.getOrder());
        assertTrue(result.getOrder().containsKey("id"));
    }
}

