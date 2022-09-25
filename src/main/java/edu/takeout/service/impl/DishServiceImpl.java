package edu.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.takeout.common.CustomException;
import edu.takeout.dto.DishDto;
import edu.takeout.entity.Dish;
import edu.takeout.entity.DishFlavor;
import edu.takeout.mapper.DishMapper;
import edu.takeout.service.DishFlavorService;
import edu.takeout.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional
    public boolean addDishWithFlavor(DishDto dishDto) {
        save(dishDto);
        Long id = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
        boolean res = dishFlavorService.saveBatch(flavors);
        return res;
    }

    @Override
    @Transactional
    public boolean updateDishWithFlavor(DishDto dishDto) {
        updateById(dishDto);
        Long id = dishDto.getId();

        dishFlavorService.remove(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, dishDto.getId()));

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
        boolean res = dishFlavorService.saveBatch(flavors);
        return res;
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        DishDto dishDto = new DishDto();
        Dish dish = getById(id);
        BeanUtils.copyProperties(dish, dishDto);
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, id);
        dishDto.setFlavors(dishFlavorService.list(wrapper));
        return dishDto;
    }

    @Override
    @Transactional
    public void delDishWithFlavor(List<Long> ids) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Dish::getId,ids);
        wrapper.eq(Dish::getStatus,1);
        int c = (int) count(wrapper);
        if (c>0) {
            throw new CustomException("dish are selling , do not to delete!");
        }
        removeByIds(ids);
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
    }
}
