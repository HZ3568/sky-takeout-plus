package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userCategoryController")
@RequestMapping("/user/category")
@Api(tags = "C端-分类接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 用户端获取菜品分类和套餐分类的导航栏
     */
    @GetMapping("/list")
    @ApiOperation("根据type查询分类列表")
    public Result<List<Category>> getDishOrSetmealListByType(Integer type) {
        List<Category> list = categoryService.getDishOrSetmealListByType(type);
        return Result.success(list);
    }
}
