package edu.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.takeout.dto.DishDto;
import edu.takeout.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    boolean addDishWithFlavor(DishDto dishDto);

    boolean updateDishWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);

    void delDishWithFlavor(List<Long> ids);
}
