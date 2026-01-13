package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.entity.Dish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.EmployeeMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import com.sky.vo.DishItemVO;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    @Transactional
    public void add(SetmealDTO setmealDTO) {

        log.info("添加{}", setmealDTO);
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);

        Long setmealId = setmeal.getId();

        List<SetmealDish> dishList = setmealDTO.getSetmealDishes();
        if (dishList != null && dishList.size() > 0) {
            for (SetmealDish dish : dishList) {
                dish.setSetmealId(setmealId);
            }
            setmealDishMapper.insertList(dishList);
        }
    }

    @Override
    public SetmealVO getById(Long id) {

        SetmealVO setmealVO = new SetmealVO();
        Setmeal setmeal = setmealMapper.getSetmealById(id);
        BeanUtils.copyProperties(setmeal, setmealVO);

        List<SetmealDish> dishList = setmealDishMapper.getSetmealDishBySetmealId(id);
        setmealVO.setSetmealDishes(dishList);

        return setmealVO;
    }

    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.page(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Transactional
    public void update(SetmealDTO setmealDTO) {
        // 更新setmeal表
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.updateSetmeal(setmeal);

        // 更新setmeal_dish表
        setmealDishMapper.deleteBySetmealId(setmeal.getId());

        List<SetmealDish> dishList = setmealDTO.getSetmealDishes();
        if (dishList != null && dishList.size() > 0) {
            setmealDishMapper.insertList(dishList);
        }
    }

    @Transactional
    public void delete(List<Long> ids) {
        // 判断套餐是否在起售中
        List<Setmeal> onSaleSetmeal = setmealMapper.getOnSaleSetmeal(ids);
        if (onSaleSetmeal != null && onSaleSetmeal.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }

        setmealMapper.deleteSetmeal(ids);

        setmealDishMapper.deleteSetmealDish(ids);
    }

    @Override
    public void startOrStop(Long id, Integer status) {

        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .build();

        // 套餐中包含停售的菜品时，不能起售
        if (Objects.equals(status, StatusConstant.ENABLE)) {
            List<Dish> offSaleDishes = dishMapper.getOffSaleDishes(id);
            if (offSaleDishes != null && offSaleDishes.size() > 0) {
                throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            } else {
                setmealMapper.start(setmeal);
            }
        } else {
            setmealMapper.stop(setmeal);
        }
    }

    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    public List<DishItemVO> getDishItemById(Long id){
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
