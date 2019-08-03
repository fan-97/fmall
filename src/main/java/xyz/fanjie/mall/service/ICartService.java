package xyz.fanjie.mall.service;

import xyz.fanjie.mall.common.ServerResponse;
import xyz.fanjie.mall.vo.CartVo;

import java.util.Map;

public interface ICartService {
    ServerResponse<CartVo> addCart(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> deletes(Integer userId,String productIds);

    ServerResponse<CartVo> updateProduct(Integer userId,Integer productId,Integer count);

    ServerResponse<CartVo> getList(Integer userId);

    ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked);

    Map<String,Object> getCartProductCount(Integer userId);
}
