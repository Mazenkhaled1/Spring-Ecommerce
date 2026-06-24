package com.codewithmosh.store.payments;

import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.exceptions.CartEmptyException;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.OrderRepository;
import com.codewithmosh.store.services.AuthService;
import com.codewithmosh.store.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor

@Service
public class CheckoutService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AuthService authService;
    private final CartService cartService;
    private final PaymentGateway paymentGateway;


    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request)  {
        var cart = cartRepository.getCartWithItems(request.getCartId()).orElse(null);
        if(cart == null) {
            throw new CartNotFoundException();
        }

        if(cart.isEmpty()){
          throw new CartEmptyException() ;
        }

        var order =  Order.fromCart(cart , authService.getCurrentUser());

        orderRepository.save(order);

      try
      {
         var session =  paymentGateway.createCheckoutSession(order);

          cartService.clearCart(cart.getId());

          return new CheckoutResponse(order.getId() , session.getCheckoutUrl());
      }catch(PaymentException ex){
          System.out.println(ex.getMessage());
          orderRepository.delete(order);
          throw ex;
      }
    }

    public void handleWebhookEvent(WebhookRequest request)
    {
        paymentGateway
                .parseWebhookRequset(request)
                .ifPresent(paymentRequest -> {
                    var order = orderRepository.findById(paymentRequest.getOrderId()).orElseThrow() ;
                    order.setStatus(paymentRequest.getPaymentStatus());
                    orderRepository.save(order);
                });

    }

}
