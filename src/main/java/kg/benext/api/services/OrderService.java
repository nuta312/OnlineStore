package kg.benext.api.services;

import kg.benext.api.HttpRequest;
import kg.benext.api.model.request.OrderRequest;
import kg.benext.api.model.response.CreateOrderResponse;
import kg.benext.common.constants.Endpoints;

public class OrderService extends HttpRequest {

    public OrderService(String url) {
        super(url);
    }

    public CreateOrderResponse createOrder(OrderRequest request) {
        super.post(Endpoints.ORDERS, request.toJson());

        String body = getResponse().getBody().asString();
        if (body == null || body.isEmpty()) {
            return new CreateOrderResponse();
        }

        return getResponse().as(CreateOrderResponse.class);
    }
}