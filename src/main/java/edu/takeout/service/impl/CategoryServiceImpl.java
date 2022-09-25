package edu.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.takeout.common.CustomException;
import edu.takeout.entity.Category;
import edu.takeout.entity.Dish;
import edu.takeout.entity.Setmeal;
import edu.takeout.mapper.CategoryMapper;
import edu.takeout.service.CategoryService;
import edu.takeout.service.DishService;
import edu.takeout.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void removeCategory(Long id) {
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.eq(Dish::getCategoryId, id);
        long dishCount = dishService.count(dishWrapper);
        if (dishCount > 0) {
            throw new CustomException("have dish to bind this category");
        }
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.eq(Setmeal::getCategoryId, id);
        long setmealCount = setmealService.count(setmealWrapper);
        if (setmealCount > 0) {
            throw new CustomException("have set meal to bind this category");
        }
        this.removeById(id);
    }
}
