package edu.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.takeout.dto.SetmealDto;
import edu.takeout.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    boolean addSetmealWithDish(SetmealDto setmealDto);

    void delSetmealWithDish(List<Long> ids);

    SetmealDto getByIdWithDish(Long id);

    boolean updateSetmealWithDish(SetmealDto setmealDto);
}
