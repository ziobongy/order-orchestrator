package it.adesso.orchestration.order.mappers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.adesso.orchestration.order.DTO.OrderDTO;
import it.adesso.orchestration.order.entities.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.Map;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapping {

    @Mapping(target = "order", source = "orderDTO", qualifiedByName = "toMap")
    Order toEntity(OrderDTO orderDTO);

    @Named("toMap")
    default Map<String, Object> toMap(OrderDTO orderDTO) {
        ObjectMapper om = new ObjectMapper();
        return om.convertValue(orderDTO, new TypeReference<Map<String, Object>>(){});
    }
}
