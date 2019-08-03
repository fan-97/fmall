package xyz.fanjie.mall.service;

import com.github.pagehelper.PageInfo;
import xyz.fanjie.mall.common.ServerResponse;
import xyz.fanjie.mall.pojo.Shipping;

public interface IShippingService {

    ServerResponse<Integer> addShipping(Integer userId, Shipping shipping);

    ServerResponse<String> delShipping(Integer userId, Integer shippingId);

    ServerResponse<String> updateShipping(Integer userId, Shipping shipping);

    ServerResponse<Shipping> selectShipping(Integer userId, Integer shippingId);

    ServerResponse<PageInfo> getList(Integer userId, int pageNum, int pageSize);
}
