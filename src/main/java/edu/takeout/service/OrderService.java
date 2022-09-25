package edu.takeout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.takeout.dto.OrdersDto;
import edu.takeout.entity.Orders;

public interface OrderService extends IService<Orders> {
    void submit(Orders order);
    Page<OrdersDto> getPageWithOrderDetail(int page, int pageSize);
    Page<OrdersDto> getAllPageWithOrderDetail(int page,int pageSize);
}
