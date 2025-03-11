package com.ecom.ecommerce.orderline;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderLineService {

    private final OrderLineRepository orderLineRepository;
    private final OrderLineMapper mapper;

    public Integer saveOrderLine(OrderLineRequest orderLineRequest) {
        var orderLIne = mapper.toOrderLine(orderLineRequest);
        return orderLineRepository.save(orderLIne).getId();
    }

    public void saveAllOrderLines(List<OrderLineRequest> orderLineRequests) {
        var orderLines = orderLineRequests
                .stream()
                        .map(mapper::toOrderLine)
                                .toList();
        orderLineRepository.saveAll(orderLines);
    }

    public List<OrderLineResponse> findAllByOrderId(Integer orderId) {
        return orderLineRepository.findAllByOrderId(orderId)
                .stream()
                .map(mapper::toOrderLineResponse)
                .collect(toList());

    }
}
