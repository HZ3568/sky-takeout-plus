package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐管理")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 用于修改时的页面回显
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据分类id获取套餐")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    @GetMapping("/page")
    @ApiOperation(value = "套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageResult pageResult = setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping()
    @ApiOperation(value = "新增套餐")
    @CacheEvict(value = "setmealCache", key = "#setmealDTO.categoryId")
    public Result add(@RequestBody SetmealDTO setmealDTO) {
        setmealService.add(setmealDTO);
        return Result.success();
    }

    @DeleteMapping()
    @ApiOperation(value = "删除套餐")
    @CacheEvict(value = "setmealCache", allEntries = true)
    public Result delete(@RequestParam List<Long> ids) {
        setmealService.delete(ids);
        return Result.success();
    }

    @PutMapping()
    @ApiOperation(value = "修改套餐")
    @CacheEvict(value = "setmealCache", allEntries = true)
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        setmealService.update(setmealDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation(value = "启用或停用套餐")
    @CacheEvict(value = "setmealCache", allEntries = true)
    public Result startOrStop(@RequestParam Long id, @PathVariable Integer status) {
        setmealService.startOrStop(id, status);
        return Result.success();
    }

}
