package edu.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import edu.takeout.common.BaseContext;
import edu.takeout.common.Result;
import edu.takeout.entity.AddressBook;
import edu.takeout.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
@Api(tags = "用户地址管理")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @ApiOperation("添加用户地址")
    @PostMapping
    Result<AddressBook> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return Result.success(addressBook);
    }

    @ApiOperation("用户默认地址设置")
    @PutMapping("/default")
    Result<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault, 0);
        addressBookService.update(wrapper);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return Result.success(addressBook);
    }

    @ApiOperation("获取用户默认地址")
    @GetMapping("/default")
    Result<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.eq(AddressBook::getIsDefault, 1);
        AddressBook addressBook = addressBookService.getOne(wrapper);
        if (addressBook != null)
            return Result.success(addressBook);
        return Result.error("fail to get address");
    }

    @ApiOperation("获取用户地址列表")
    @GetMapping("/list")
    Result<List<AddressBook>> getList(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(addressBook.getUserId() != null, AddressBook::getUserId,addressBook.getUserId() );
        wrapper.orderByDesc(AddressBook::getUpdateTime);
        return Result.success(addressBookService.list(wrapper));
    }
}
