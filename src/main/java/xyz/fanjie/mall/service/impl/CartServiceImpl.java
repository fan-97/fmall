package xyz.fanjie.mall.service.impl;

import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import xyz.fanjie.mall.common.Const;
import xyz.fanjie.mall.common.ResponseCode;
import xyz.fanjie.mall.common.ServerResponse;
import xyz.fanjie.mall.dao.CartMapper;
import xyz.fanjie.mall.dao.ProductMapper;
import xyz.fanjie.mall.pojo.Cart;
import xyz.fanjie.mall.pojo.Product;
import xyz.fanjie.mall.service.ICartService;
import xyz.fanjie.mall.util.BigDecimalUtils;
import xyz.fanjie.mall.util.PropertiesUtil;
import xyz.fanjie.mall.vo.CartProductVo;
import xyz.fanjie.mall.vo.CartVo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("iCartService")
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    public ServerResponse<CartVo> addCart(Integer userId, Integer productId, Integer count) {
        if (userId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        if (productId == null && count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        //如果商品在购物车中就在原有的数量上增加count，如果没在购物车中，就创建一个新的购物项
        Cart cart = cartMapper.selectByProductIdUserId(productId, userId);
        if (cart == null) {
            cart = new Cart();
            cart.setChecked(Const.Cart.CHECKED);
            cart.setUserId(userId);
            cart.setQuantity(count);
            cart.setProductId(productId);
            cartMapper.insert(cart);
        } else {
            cart.setQuantity(cart.getQuantity() + count);
            cart.setUserId(userId);
            cart.setProductId(productId);
            cartMapper.updateByProductIdUserId(cart);
        }

        //更新购物车数量
        return this.getList(userId);
    }

    public ServerResponse<CartVo> deletes(Integer userId, String productIds) {
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productList)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        int effectNum = cartMapper.deleteByProductIdUserId(userId, productList);
        if (effectNum <= 0) {
            return ServerResponse.createByErrorMessage("删除失败");
        }
        return this.getList(userId);
    }

    public ServerResponse<CartVo> updateProduct(Integer userId, Integer productId, Integer count) {
        if (userId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        if (productId == null && count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart = new Cart();
        cart.setProductId(productId);
        cart.setUserId(userId);
        cart.setQuantity(count);
        int effectNum = cartMapper.updateByProductIdUserId(cart);
        if (effectNum <= 0) {
            return ServerResponse.createByErrorMessage("更新失败");
        }
        return this.getList(userId);
    }

    public ServerResponse<CartVo> getList(Integer userId) {
        CartVo cartVo = this.getCartProductLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    /**
     * 选择/反选/全选/全反选 商品
     * @param userId
     * @param productId
     * @param checked
     * @return
     */
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked) {
        if (userId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        if (productId == null && checked == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.updateByUserIdProductIdChecked(userId, productId, checked);
        return this.getList(userId);
    }

    public Map<String,Object> getCartProductCount(Integer userId){
        Map<String ,Object> modelMap = new HashMap<>();
        if (userId == null) {
            modelMap.put("status", ResponseCode.NEED_LOGIN.getCode());
            modelMap.put("msg","出现异常");
            return modelMap;
        }
        int count = cartMapper.selectQuantity(userId);
        modelMap.put("status",ResponseCode.SUCCESS.getCode());
        modelMap.put("data",count);
        return modelMap;
    }

    /**
     * 处理购物项
     * @param userId
     * @return
     */
    private CartVo getCartProductLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        List<CartProductVo> cartProductVoList = new ArrayList<>();

        BigDecimal cartTotalPrice = new BigDecimal("0");

        if (!CollectionUtils.isEmpty(cartList)) {
            for (Cart cartItem : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());
                cartProductVo.setProductChecked(cartItem.getChecked());
                //设置商品详情信息
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null) {
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductStock(product.getStock());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductPrice(product.getPrice());

                    int buyLimitCount = 0;
                    //核对用户选择的商品数量和商品库存
                    if (product.getStock() >= cartItem.getQuantity()) {
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    } else {
                        //库存不足
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);

                        Cart cartNum = new Cart();
                        cartNum.setId(cartItem.getId());
                        cartNum.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartNum);
                    }
//                    //更新商品库存
//                    Product productStock = new Product();
//                    productStock.setId(product.getId());
//                    productStock.setStock(product.getStock()-buyLimitCount);
//                    productMapper.updateByPrimaryKeySelective(productStock);


                    cartProductVo.setQuantity(buyLimitCount);

                    //计算商品总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtils.mul(buyLimitCount, cartProductVo.getProductPrice().doubleValue()));

                }
                if (cartItem.getChecked() == Const.Cart.CHECKED) {
                    cartTotalPrice = BigDecimalUtils.add(cartTotalPrice.doubleValue(), cartProductVo.getProductPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);

            }
        }
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        int effectNum = cartMapper.selectAllChecked(userId);
        if (effectNum > 0) {
            cartVo.setAllChecked(false);
        } else {
            cartVo.setAllChecked(true);
        }
        return cartVo;
    }

}
