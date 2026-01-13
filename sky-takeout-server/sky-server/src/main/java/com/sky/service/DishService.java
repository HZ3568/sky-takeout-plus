package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import com.sky.entity.Dish;

import java.util.List;

public interface DishService {

    void saveDishAndFlavor(DishDTO dishDTO);

    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    void delete(List<Long> ids);

    DishVO getDishAndFlavorById(Long id);

    void updateDishAndFlavor(DishDTO dishDTO);

    List<DishVO> getDishListByCategoryId(Dish dish);

    void startOrStop(Long id, Integer status);
}
