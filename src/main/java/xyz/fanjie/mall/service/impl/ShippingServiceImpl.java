package xyz.fanjie.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.fanjie.mall.common.ResponseCode;
import xyz.fanjie.mall.common.ServerResponse;
import xyz.fanjie.mall.dao.ShippingMapper;
import xyz.fanjie.mall.pojo.Shipping;
import xyz.fanjie.mall.service.IShippingService;

import java.util.List;

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {
    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse<Integer> addShipping(Integer userId, Shipping shipping) {
        if (userId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if (shipping == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        int shippingId = shippingMapper.insert(shipping);
        if (shippingId < 0) {
            return ServerResponse.createByErrorMessage("新建地址失败");
        }
        return ServerResponse.createBySuccess("新建地址成功", shippingId);
    }

    public ServerResponse<String> delShipping(Integer userId, Integer shippingId) {
        if (userId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if (shippingId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        int effectNum = shippingMapper.deleteByPrimaryKey(shippingId);
        if (effectNum <= 0) {
            return ServerResponse.createByErrorMessage("删除地址失败");
        }
        return ServerResponse.createBySuccessMessage("删除地址成功");
    }

    @Override
    public ServerResponse<String> updateShipping(Integer userId, Shipping shipping) {
        if (userId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if (shipping == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        int effectNum = shippingMapper.updateByPrimaryKeySelective(shipping);
        if (effectNum <= 0) {
            return ServerResponse.createByErrorMessage("更新地址失败");
        }
        return ServerResponse.createBySuccessMessage("更新地址成功");
    }

    @Override
    public ServerResponse<Shipping> selectShipping(Integer userId, Integer shippingId) {
        if (userId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if (shippingId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
        if (shipping == null) {
            return ServerResponse.createByErrorMessage("请登录后再查询");
        }
        return ServerResponse.createBySuccess(shipping);
    }

    public ServerResponse<PageInfo> getList(Integer userId, int pageNum, int pageSize) {
        if (userId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        PageHelper.startPage(pageNum,pageSize);
        PageHelper.orderBy("update_time desc");
        //查询地址列表
        List<Shipping> shippingList = shippingMapper.selectListByUserId(userId);
        if (shippingList==null){
            return ServerResponse.createByErrorMessage("获取地址列表失败");
        }
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
