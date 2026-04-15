package kg.benext.api.services;

import kg.benext.api.HttpRequest;
import kg.benext.api.model.request.OrderRequest;
import kg.benext.api.model.response.CreateOrderResponse;
import kg.benext.common.constants.Endpoints;
import io.qameta.allure.Step;
import kg.benext.api.model.response.OrderListResponse;
import kg.benext.api.model.response.OrderResponse;
import kg.benext.api.model.response.OrderWrapper;
import kg.benext.api.model.response.SuccessResponse;

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

    @Step("Get all orders")
    public OrderListResponse getOrders() {
        return super.get(Endpoints.ORDERS)
                .as(OrderListResponse.class);
    }

    @Step("Get order by id {id}")
    public OrderResponse getOrderById(String id) {
        return super.get(String.format(Endpoints.ORDER_BY_ID, id))
                .as(OrderWrapper.class)
                .getOrder();
    }

    @Step("Update order")
    public SuccessResponse updateOrder(OrderRequest request) {
        return super.put(Endpoints.ORDERS, request.toJson())
                .as(SuccessResponse.class);
    }

    @Step("Delete order {id}")
    public void deleteOrder(String id) {
        super.delete(String.format(Endpoints.ORDER_BY_ID, id));
    }
}