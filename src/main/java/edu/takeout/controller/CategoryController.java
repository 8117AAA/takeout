package edu.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.takeout.common.Result;
import edu.takeout.entity.Category;
import edu.takeout.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Api(tags = "菜品类别")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("添加分类")
    @PostMapping
    Result<String> addCategory(@RequestBody Category category) {
        boolean status = categoryService.save(category);
        if (status)
            return Result.success("add category successful");
        return Result.error("fail to add category");
    }

    @ApiOperation("获取分类分页")
    @GetMapping("/page")
    Result<Page> CategoryPage(int page, int pageSize) {
        Page<Category> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        categoryService.page(p, wrapper);
        return Result.success(p);
    }

    @ApiOperation("删除分类")
    @DeleteMapping
    Result<String> delCategory(Long ids) {
        categoryService.removeCategory(ids);
        return Result.success("delete category");
    }

    @ApiOperation("更新分类")
    @PutMapping
    Result<String> updateCategory(@RequestBody Category category) {
        boolean res = categoryService.updateById(category);
        if (res)
            return Result.success("update category!");
        return Result.error("fail to update category");
    }

    @ApiOperation("获取分类列表")
    @GetMapping("/list")
    Result<List<Category>> categoryList(Category category) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(category.getType() != null, Category::getType, category.getType());
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(wrapper);
        return Result.success(list);
    }
}
