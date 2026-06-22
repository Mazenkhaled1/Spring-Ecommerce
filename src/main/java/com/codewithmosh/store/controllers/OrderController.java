    package com.codewithmosh.store.controllers;

    import com.codewithmosh.store.dtos.ErrorDto;
    import com.codewithmosh.store.dtos.OrderDto;
    import com.codewithmosh.store.exceptions.OrderNotFoundException;
    import com.codewithmosh.store.services.OrderService;
    import lombok.AllArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import org.springframework.security.access.AccessDeniedException;
    import java.util.List;

    @AllArgsConstructor
    @RestController
    @RequestMapping("/orders")
    public class OrderController {

     private final OrderService orderService;

        @GetMapping

        public List<OrderDto> getOrders() {

          return orderService.getAllOrders();
        }

        @GetMapping("/{orderId}")
        public OrderDto getOrder(@PathVariable Long orderId)
        {
            return orderService.getOrder(orderId) ;
        }

        @ExceptionHandler(OrderNotFoundException.class)
        public ResponseEntity<Void> handleOrderNotFoundException()
        {
            return  ResponseEntity.notFound().build() ;
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorDto> handelAccessDeniedException(Exception ex)
        {
            return  ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorDto(ex.getMessage()));
        }


    }
