package edu.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.takeout.common.BaseContext;
import edu.takeout.common.CustomException;
import edu.takeout.dto.OrdersDto;
import edu.takeout.entity.*;
import edu.takeout.mapper.OrderMapper;
import edu.takeout.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    @Transactional
    public void submit(Orders order) {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartService.list(wrapper);
        if (list == null || list.size() == 0)
            throw new CustomException("shopping cart is empty");
        User user = userService.getById(BaseContext.getCurrentId());
        AddressBook addressBook = addressBookService.getById(order.getAddressBookId());
        if (addressBook == null)
            throw new CustomException("user address are wrong");
        long orderId = IdWorker.getId();
        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = list.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        order.setId(orderId);
        order.setOrderTime(LocalDateTime.now());
        order.setCheckoutTime(LocalDateTime.now());
        order.setStatus(2);
        order.setAmount(new BigDecimal(amount.get()));//总金额
        order.setUserId(BaseContext.getCurrentId());
        order.setNumber(String.valueOf(orderId));
        order.setUserName(user.getName());
        order.setConsignee(addressBook.getConsignee());
        order.setPhone(addressBook.getPhone());
        order.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        save(order);
        orderDetailService.saveBatch(orderDetails);
        shoppingCartService.remove(wrapper);
    }

    @Override
    public Page<OrdersDto> getPageWithOrderDetail(int page, int pageSize) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        wrapper.orderByDesc(Orders::getOrderTime);
        Page<Orders> p = new Page<>(page, pageSize);
        Page<OrdersDto> dtoPage = new Page<>(page, pageSize);
        page(p, wrapper);
        BeanUtils.copyProperties(p, dtoPage, "records");
        List<OrdersDto> collect = p.getRecords().stream().map((i) -> {
            OrdersDto dto = new OrdersDto();
            BeanUtils.copyProperties(i, dto);
            LambdaQueryWrapper<OrderDetail> w = new LambdaQueryWrapper<>();
            w.eq(OrderDetail::getOrderId, i.getId());
            List<OrderDetail> list = orderDetailService.list(w);
            dto.setOrderDetails(list);
            return dto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(collect);
        return dtoPage;
    }

    public Page<OrdersDto> getAllPageWithOrderDetail(int page, int pageSize) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Orders::getOrderTime);
        Page<Orders> p = new Page<>(page, pageSize);
        Page<OrdersDto> dtoPage = new Page<>(page, pageSize);
        page(p, wrapper);
        BeanUtils.copyProperties(p, dtoPage, "records");
        List<OrdersDto> collect = p.getRecords().stream().map((i) -> {
            OrdersDto dto = new OrdersDto();
            BeanUtils.copyProperties(i, dto);
            LambdaQueryWrapper<OrderDetail> w = new LambdaQueryWrapper<>();
            w.eq(OrderDetail::getOrderId, i.getId());
            List<OrderDetail> list = orderDetailService.list(w);
            dto.setOrderDetails(list);
            return dto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(collect);
        return dtoPage;
    }

}
