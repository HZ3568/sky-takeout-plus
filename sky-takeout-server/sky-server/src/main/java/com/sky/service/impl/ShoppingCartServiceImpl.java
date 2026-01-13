package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;


    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        // 判断购物车是否存在，存在则数量+1，不存在则新增
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.getShoppingCart(shoppingCart);
        if (shoppingCartList != null && shoppingCartList.size() > 0) {
            shoppingCart = shoppingCartList.get(0);
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            shoppingCartMapper.updateShoppingCart(shoppingCart);
        } else {
            Long dishId = shoppingCartDTO.getDishId();
            if (dishId != null) {
                shoppingCart.setName(dishMapper.getById(dishId).getName());
                shoppingCart.setImage(dishMapper.getById(dishId).getImage());
                shoppingCart.setAmount(dishMapper.getById(dishId).getPrice());
            } else {
                Long setmealId = shoppingCartDTO.getSetmealId();
                shoppingCart.setName(setmealMapper.getSetmealById(setmealId).getName());
                shoppingCart.setImage(setmealMapper.getSetmealById(setmealId).getImage());
                shoppingCart.setAmount(setmealMapper.getSetmealById(setmealId).getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insertShoppingCart(shoppingCart);
        }
    }


    @Override
    public List<ShoppingCart> getShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> list = shoppingCartMapper.getShoppingCart(shoppingCart);
        return list;
    }

    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }

    @Override
    public void deleteShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.getShoppingCart(shoppingCart);
        if (shoppingCartList != null && shoppingCartList.size() > 0) {
            shoppingCart = shoppingCartList.get(0);
            Integer num = shoppingCart.getNumber();
            if (num > 1) {
                shoppingCart.setNumber(num - 1);
                shoppingCartMapper.updateShoppingCart(shoppingCart);
            } else {
                shoppingCartMapper.deleteOne(shoppingCart);
            }
        }
    }
}


