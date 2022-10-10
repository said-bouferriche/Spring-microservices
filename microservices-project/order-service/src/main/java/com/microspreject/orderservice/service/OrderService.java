package com.microspreject.orderservice.service;

import com.microspreject.orderservice.dto.InventoryResponse;
import com.microspreject.orderservice.dto.OrderLineItemsDto;
import com.microspreject.orderservice.dto.OrderRequest;
import com.microspreject.orderservice.model.Order;
import com.microspreject.orderservice.model.OrderLineItems;
import com.microspreject.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
            .stream()
            .map(this::mapToDto).toList();
        order.setOrderLineItem(orderLineItems);

        List<String> skuCodes = order.getOrderLineItem()
            .stream().map(OrderLineItems::getSkuCode)
            .toList();


        //Call inventory Service to check the existence of the product

        InventoryResponse[] inventoryResponsesArray = webClientBuilder.build().get()
            .uri("http://inventory-service/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
            .retrieve()
            .bodyToMono(InventoryResponse[].class)
            .block();

        boolean allProductsInStock = Arrays.stream(inventoryResponsesArray).allMatch(InventoryResponse::isInStock);

        if (allProductsInStock){
            orderRepository.save(order);
        }
        else {
            throw new IllegalArgumentException("Some Products is not in stock, please try again");
        }




    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        return orderLineItems;
    }
}
