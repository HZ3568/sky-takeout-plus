package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    // 在菜品套餐关系表中，根据菜品id查询套餐id
    // select setmeal_id from setmeal_dish where dish_id in (ids)
    List<Long> getSetmealIdsByDishIds(List<Long> ids);

    void insertList(List<SetmealDish> dishList);

    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getSetmealDishBySetmealId(Long id);

    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteBySetmealId(Long id);

    void deleteSetmealDish(List<Long> setmealIds);
}
