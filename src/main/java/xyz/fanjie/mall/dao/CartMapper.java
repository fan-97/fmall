package xyz.fanjie.mall.dao;

import org.apache.ibatis.annotations.Param;
import xyz.fanjie.mall.pojo.Cart;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectByProductIdUserId(@Param("productId")Integer productId,@Param("userId")Integer userId);

    int updateByProductIdUserId(Cart record);

    List<Cart> selectByUserId(Integer userId);

    int selectAllChecked(@Param("userId")Integer userId);

    int deleteByProductIdUserId(@Param("userId")Integer userId,@Param("productIds")List<String>productIds);

    int updateByUserIdProductIdChecked(@Param("userId")Integer userId,@Param("productId")Integer productId,@Param("checked")Integer checked);

    int selectQuantity(Integer userId);

    List<Cart> selectCheckedCartByUserId(Integer userId);
}