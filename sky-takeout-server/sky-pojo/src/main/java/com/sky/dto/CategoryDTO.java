package com.sky.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * DTO: 前端给后端
 * VO: 后端给前端
 */


@Data
public class CategoryDTO implements Serializable {

    //主键
    private Long id;

    //类型 1 菜品分类 2 套餐分类
    private Integer type;

    //分类名称
    private String name;

    //排序
    private Integer sort;

}
