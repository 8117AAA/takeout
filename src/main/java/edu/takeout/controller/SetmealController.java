package edu.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.takeout.common.Result;
import edu.takeout.dto.SetmealDto;
import edu.takeout.entity.Category;
import edu.takeout.entity.Setmeal;
import edu.takeout.service.CategoryService;
import edu.takeout.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Api(tags = "套餐管理")
public class SetmealController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealService setmealService;

    @ApiOperation("添加套餐")
    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public Result<String> addSetmeal(@RequestBody SetmealDto setmealDto) {
        boolean res = setmealService.addSetmealWithDish(setmealDto);
        if (res) return Result.success("add setmeal successful");
        return Result.error("fail to add setmeal");
    }

    @ApiOperation("套餐分页")
    @GetMapping("/page")
    Result<Page> setmealPage(int page, int pageSize, String name) {
        Page<Setmeal> p = new Page<>();
        Page<SetmealDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Setmeal::getName, name);
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(p, wrapper);
        BeanUtils.copyProperties(p, dtoPage, "records");
        List<Setmeal> records = p.getRecords();
        List<SetmealDto> collect = records.stream().map((i) -> {
            SetmealDto dto = new SetmealDto();
            BeanUtils.copyProperties(i, dto);
            Long id = i.getCategoryId();
            dto.setCategoryName(categoryService.getOne(new LambdaQueryWrapper<Category>().eq(Category::getId, id).select(Category::getName)).getName());
            return dto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(collect);
        return Result.success(dtoPage);
    }

    @ApiOperation("删除套餐")
    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public Result<String> delSetmeal(@RequestParam List<Long> ids) {
        setmealService.delSetmealWithDish(ids);
        return Result.success("set meal deleted");
    }

    @ApiOperation("修改套餐状态")
    @PostMapping("/status/{status}")
    Result<String> changeSetmealStatus(@PathVariable Integer status, @RequestParam List<Long> ids) {
        for (Long id : ids) {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(id);
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        }
        return Result.success("change status successful");
    }

    @ApiOperation("套餐以及所包含菜品")
    @GetMapping("/{id}")
    Result<SetmealDto> getSetmealWithDish(@PathVariable Long id) {
        return Result.success(setmealService.getByIdWithDish(id));
    }

    @ApiOperation("更新套餐")
    @PutMapping
    Result<String> updateSetmeal(@RequestBody SetmealDto setmealDto) {
        setmealService.updateSetmealWithDish(setmealDto);
        return Result.success("update successful");
    }

    @ApiOperation("获取套餐列表")
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public Result<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        wrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(wrapper);
        return Result.success(list);
    }
}