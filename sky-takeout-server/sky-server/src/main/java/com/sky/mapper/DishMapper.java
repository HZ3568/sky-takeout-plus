package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);


    // 向菜品表插入1条数据
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    Page<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    // delete from dish where id in (ids)
    void deleteByIds(List<Long> ids);

    @AutoFill(OperationType.UPDATE)
    void updateDish(Dish dish);

    // TODO: 是否需要左外连接category表, 用来查询菜品分类名称
    List<Dish> getDishListByCategoryId(Long categoryId);

    List<Dish> getDishListByCategoryIdAndStatus(Long categoryId, Integer status);

    @Update("update dish set status = 0, update_time = #{updateTime}, update_user = #{updateUser} where id = #{id}")
    @AutoFill(value=OperationType.UPDATE)
    void stop(Dish dish);


    @Update("update dish set status = 1, update_time = #{updateTime}, update_user = #{updateUser} where id = #{id}")
    @AutoFill(value=OperationType.UPDATE)
    void start(Dish dish);

    @Select("select d.*, s.setmeal_id from dish d left join setmeal_dish s on d.id = s.dish_id where s.setmeal_id = #{id} and d.status = 0")
    List<Dish> getOffSaleDishes(Long id);

    /**
     * 根据条件统计菜品数量
     */
    int countByMap(Map map);

}
