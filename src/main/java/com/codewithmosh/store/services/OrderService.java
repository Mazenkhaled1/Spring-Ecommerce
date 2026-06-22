package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.OrderDto;
import com.codewithmosh.store.dtos.OrderItemDto;
import com.codewithmosh.store.dtos.OrderProductDto;
import com.codewithmosh.store.exceptions.OrderNotFoundException;
import com.codewithmosh.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class OrderService {

    private final AuthService authService;
    private final OrderRepository orderRepository;

    public List<OrderDto> getAllOrders() {
        var user = authService.getCurrentUser();

        return orderRepository.findAllByCustomer(user)
                .stream()
                .map(order -> {

                    OrderDto dto = new OrderDto();

                    dto.setId(order.getId());
                    dto.setStatus(order.getStatus().toString());
                    dto.setCreatedAt(order.getCreatedAt());
                    dto.setTotalPrice(order.getTotalPrice());

                    dto.setItems(
                            order.getItems()
                                    .stream()
                                    .map(item -> {
                                        OrderItemDto itemDto = new OrderItemDto();

                                        itemDto.setQuantity(item.getQuantity());
                                        itemDto.setTotalPrice(item.getTotalPrice());

                                        OrderProductDto productDto = new OrderProductDto();
                                        productDto.setId(item.getProduct().getId());
                                        productDto.setName(item.getProduct().getName());

                                        itemDto.setProduct(productDto);

                                        return itemDto;
                                    })
                                    .toList()
                    );

                    return dto;
                })
                .toList();
    }

    public OrderDto getOrder(Long id) {
        var order = orderRepository.getOrderWithItems(id).
                orElseThrow(OrderNotFoundException::new) ;

        var user = authService.getCurrentUser();
        if(! order.isPlacedBy(user)){
            throw new AccessDeniedException("You are not allowed to access this order");
        }

        OrderDto dto = new OrderDto();

        dto.setId(order.getId());
        dto.setStatus(order.getStatus().toString());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setTotalPrice(order.getTotalPrice());

        dto.setItems(
                order.getItems()
                        .stream()
                        .map(item -> {
                            OrderItemDto itemDto = new OrderItemDto();

                            itemDto.setQuantity(item.getQuantity());
                            itemDto.setTotalPrice(item.getTotalPrice());

                            OrderProductDto productDto = new OrderProductDto();
                            productDto.setId(item.getProduct().getId());
                            productDto.setName(item.getProduct().getName());

                            itemDto.setProduct(productDto);

                            return itemDto;
                        })
                        .toList()
        );

        return dto;

    }
}
