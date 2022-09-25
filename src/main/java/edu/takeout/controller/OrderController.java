package edu.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.takeout.common.Result;
import edu.takeout.entity.Orders;
import edu.takeout.service.OrderDetailService;
import edu.takeout.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/order")
@Api(tags = "订单管理")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @ApiOperation("添加订单")
    @PostMapping("/submit")
    Result<String> submit(@RequestBody Orders order){
        orderService.submit(order);
        return Result.success("order created");
    }

    @ApiOperation("订单分页（前台）")
    @GetMapping("/userPage")
    Result<Page> userPage(int page,int pageSize){
        return Result.success(orderService.getPageWithOrderDetail(page,pageSize));
    }

    @ApiOperation("获取订单分页（后台）")
    @GetMapping("/page")
    Result<Page> page(int page, int pageSize, String number, String beginTime,String endTime){
        Page<Orders> p = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(number!=null,Orders::getNumber,number);
        wrapper.apply(StringUtils.isNotEmpty(beginTime),"UNIX_TIMESTAMP(order_time) >= UNIX_TIMESTAMP('" + beginTime + "')");
        wrapper.apply(StringUtils.isNotEmpty(endTime),"UNIX_TIMESTAMP(order_time) < UNIX_TIMESTAMP('" + endTime + "')");
        return Result.success(orderService.page(p,wrapper));
    }

    @ApiOperation("更新订单")
    @PutMapping
    Result<String> update(@RequestBody Orders order){
        orderService.updateById(order);
        return Result.success("updated!");
    }
}
