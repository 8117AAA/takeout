package edu.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.takeout.common.Result;
import edu.takeout.dto.DishDto;
import edu.takeout.entity.Category;
import edu.takeout.entity.Dish;
import edu.takeout.entity.DishFlavor;
import edu.takeout.service.CategoryService;
import edu.takeout.service.DishFlavorService;
import edu.takeout.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/dish")
@Api(tags = "菜品管理")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("获取菜品分页")
    @GetMapping("/page")
    Result<Page> dishPage(int page, int pageSize, String name) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Dish::getName, name);
        wrapper.orderByDesc(Dish::getUpdateTime);
        Page<Dish> p = new Page<>(page, pageSize);
        Page<DishDto> dtop = new Page<>(page, pageSize);
        dishService.page(p, wrapper);
        BeanUtils.copyProperties(p, dtop, "records");
        List<Dish> records = p.getRecords();
        List<DishDto> collect = records.stream().map((i) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(i, dishDto);
            dishDto.setCategoryName(categoryService.getOne(new LambdaQueryWrapper<Category>().select(Category::getName).eq(Category::getId, i.getCategoryId())).getName());
            return dishDto;
        }).collect(Collectors.toList());
        dtop.setRecords(collect);
        log.info(dtop.getRecords().toString());
        return Result.success(dtop);
    }

    @ApiOperation("添加菜品")
    @PostMapping
    Result<String> addDish(@RequestBody DishDto dishDto) {
        boolean res = dishService.addDishWithFlavor(dishDto);
        if (res)
            return Result.success("add dish successful");
        return Result.error("fail to add dish");
    }

    @ApiOperation("更新菜品")
    @PutMapping
    Result<String> updateDish(@RequestBody DishDto dishDto) {
        boolean res = dishService.updateDishWithFlavor(dishDto);
        if (res)
            return Result.success("update dish successful");
        return Result.error("fail to update dish");
    }

    @ApiOperation("获取单个菜品")
    @GetMapping("{id}")
    Result<DishDto> getDish(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return Result.success(dishDto);
    }

    @ApiOperation("修改菜品状态")
    @PostMapping("/status/{status}")
    Result<String> changeDishStatus(@PathVariable Integer status, @RequestParam List<Long> ids) {
        for (Long id : ids) {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return Result.success("change status successful");
    }

//    @GetMapping("/list")
//    Result<List<Dish>> dishList(Dish dish) {
//        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<Dish>();
//        wrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
//        wrapper.eq(Dish::getStatus,1);
//        wrapper.orderByAsc(Dish::getSort);
//        wrapper.orderByDesc(Dish::getUpdateTime);
//        List<Dish> dishList = dishService.list(wrapper);
//        return Result.success(dishList);
//    }

    @ApiOperation("获取菜品列表")
    @GetMapping("/list")
    Result<List<DishDto>> dishList(Dish dish) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<Dish>();
        wrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        wrapper.eq(Dish::getStatus, 1);
        wrapper.orderByAsc(Dish::getSort);
        wrapper.orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(wrapper);
        List<DishDto> collect = dishList.stream().map(
                (i) -> {
                    DishDto dishDto = new DishDto();
                    BeanUtils.copyProperties(i, dishDto);
                    dishDto.setCategoryName(categoryService.getOne(new LambdaQueryWrapper<Category>().select(Category::getName).eq(Category::getId, i.getCategoryId())).getName());
                    dishDto.setFlavors(dishFlavorService
                            .list(
                                    new LambdaQueryWrapper<DishFlavor>()
                                            .eq(DishFlavor::getDishId, i.getId())
                            )
                    );
                    return dishDto;
                }
        ).collect(Collectors.toList());
        return Result.success(collect);
    }

    @ApiOperation("删除菜品")
    @DeleteMapping
    Result<String> delDish(@RequestParam List<Long> ids) {
        dishService.delDishWithFlavor(ids);
        return Result.success("dish deleted!");
    }
}
