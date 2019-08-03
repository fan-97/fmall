package xyz.fanjie.mall.controller.protal;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.fanjie.mall.common.Const;
import xyz.fanjie.mall.common.ResponseCode;
import xyz.fanjie.mall.common.ServerResponse;
import xyz.fanjie.mall.pojo.Shipping;
import xyz.fanjie.mall.pojo.User;
import xyz.fanjie.mall.service.IShippingService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    /**
     * 添加地址
     * @param session
     * @param shipping
     * @return
     */
    @RequestMapping(value = "add.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Integer> addShipping(HttpSession session, Shipping shipping){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.addShipping(user.getId(),shipping);
    }

    /**
     * 删除地址
     * @param session
     * @param shippingId
     * @return
     */
    @RequestMapping(value = "del.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String > delShipping(HttpSession session, Integer shippingId){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.delShipping(user.getId(),shippingId);
    }

    /**
     * 更新地址
     * @param session
     * @param shipping
     * @return
     */
    @RequestMapping(value = "update.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String > updateShipping(HttpSession session, Shipping shipping){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.updateShipping(user.getId(),shipping);
    }

    /**
     * 查询地址详情
     * @param session
     * @param shippingId
     * @return
     */
    @RequestMapping(value = "select.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Shipping> selectShipping(HttpSession session, Integer shippingId){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.selectShipping(user.getId(),shippingId);
    }

    /**
     * 查询地址列表
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "list.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> getList(HttpSession session,
                                                   @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                                   @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.getList(user.getId(),pageNum,pageSize);
    }
}
