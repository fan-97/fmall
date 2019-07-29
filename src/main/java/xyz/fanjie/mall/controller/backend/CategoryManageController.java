package xyz.fanjie.mall.controller.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.fanjie.mall.common.Const;
import xyz.fanjie.mall.common.ResponseCode;
import xyz.fanjie.mall.common.ServerResponse;
import xyz.fanjie.mall.pojo.Category;
import xyz.fanjie.mall.pojo.User;
import xyz.fanjie.mall.service.ICategoryService;
import xyz.fanjie.mall.service.IUserService;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 添加品类
     * @param session
     * @param categoryName
     * @param parentId
     * @return
     */
    @RequestMapping(value = "add_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId",defaultValue = "0") Integer parentId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，请登录");
        }
        if(iUserService.checkAdminRole(user.getId()).isSuccess()){
            //添加商品分类
            return iCategoryService.addCategory(categoryName,parentId);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，没有权限进行操作");
        }
    }

    /**
     * 修改品类名字
     * @param session
     * @param categoryName
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "set_category_name.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session,String categoryName,@RequestParam(value = "categoryId",defaultValue = "0")Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，请登录");
        }
        if(iUserService.checkAdminRole(user.getId()).isSuccess()){
            //修改品类名字
            return iCategoryService.updateCategoryName(categoryName,categoryId);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，没有权限进行操作");
        }
    }

    /**
     * 获取品类子节点(平级)
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Category>> getCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0")Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，请登录");
        }
        if(iUserService.checkAdminRole(user.getId()).isSuccess()){
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("不是管理员，没有权限进行操作");
        }
    }

    /**
     * 获取当前分类id及递归子节点categoryId
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_deep_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Integer>> getDeepCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0")Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，请登录");
        }
        if(iUserService.checkAdminRole(user.getId()).isSuccess()){
            return iCategoryService.getDeepCategoryId(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("不是管理员，没有权限进行操作");
        }
    }

}
