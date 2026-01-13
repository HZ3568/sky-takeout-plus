package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品管理")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 用于修改时的页面回显
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        DishVO dishVO = dishService.getDishAndFlavorById(id);
        return Result.success(dishVO);
    }

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        PageResult pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 用于在套餐中选择菜品时，根据分类ID查询菜品列表
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品列表")
    public Result<List<DishVO>> getDishListByCategoryId(Long categoryId) {
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);

        List<DishVO> dishList = dishService.getDishListByCategoryId(dish);
        return Result.success(dishList);
    }

    @PostMapping()
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        dishService.saveDishAndFlavor(dishDTO);
        String key = "dish_" + dishDTO.getCategoryId();
        clearCache("key");
        return Result.success();
    }

    @DeleteMapping()
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids) {
        dishService.delete(ids);
        clearCache("dish_*");
        return Result.success();
    }

    @PutMapping()
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        dishService.updateDishAndFlavor(dishDTO);
        clearCache("dish_*");
        return Result.success();
    }

    @PostMapping("status/{status}")
    @ApiOperation("启用/禁用菜品")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        dishService.startOrStop(id, status);
        clearCache("dish_*");
        return Result.success();
    }

    /**
     * 清理缓存数据
     */
    private void clearCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

}
