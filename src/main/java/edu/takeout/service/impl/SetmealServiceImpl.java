package edu.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.takeout.common.CustomException;
import edu.takeout.dto.SetmealDto;
import edu.takeout.entity.Setmeal;
import edu.takeout.entity.SetmealDish;
import edu.takeout.mapper.SetmealMapper;
import edu.takeout.service.SetmealDishService;
import edu.takeout.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public boolean addSetmealWithDish(SetmealDto setmealDto) {
        save(setmealDto);
        Long id = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        List<SetmealDish> collect = setmealDishes.stream().map((i) -> {
            i.setSetmealId(id);
            return i;
        }).collect(Collectors.toList());
        boolean res = setmealDishService.saveBatch(collect);
        return res;
    }

    @Override
    @Transactional
    public void delSetmealWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Setmeal::getId,ids);
        wrapper.eq(Setmeal::getStatus,1);
        int c = (int) count(wrapper);
        if (c>0) {
            throw new CustomException("set meal are selling , do not to delete!");
        }
        removeByIds(ids);
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto dto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,dto);
        List<SetmealDish> list = setmealDishService.list(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId, id));
        dto.setSetmealDishes(list);
        return dto;
    }

    @Override
    @Transactional
    public boolean updateSetmealWithDish(SetmealDto setmealDto) {
        updateById(setmealDto);
        Long id = setmealDto.getId();

        setmealDishService.remove(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId, setmealDto.getId()));

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(id);
            return item;
        }).collect(Collectors.toList());
        boolean res = setmealDishService.saveBatch(setmealDishes);
        return res;
    }
}
