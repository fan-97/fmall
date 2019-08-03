package xyz.fanjie.mall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import xyz.fanjie.mall.common.Const;
import xyz.fanjie.mall.common.ResponseCode;
import xyz.fanjie.mall.common.ServerResponse;
import xyz.fanjie.mall.pojo.Product;
import xyz.fanjie.mall.pojo.User;
import xyz.fanjie.mall.service.IFileService;
import xyz.fanjie.mall.service.IProductService;
import xyz.fanjie.mall.service.IUserService;
import xyz.fanjie.mall.util.PropertiesUtil;
import xyz.fanjie.mall.vo.ProductDetailVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("manage/product/")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;


    /**
     * 新增OR保存产品
     * @param session
     * @param product
     * @return
     */
    @RequestMapping(value = "save.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> saveProduct(HttpSession session, Product product){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(iUserService.checkAdminRole(user.getId()).isSuccess()){
            //进行商品增加或者更新操作
            return iProductService.saveProduct(product);
        }else {
            return ServerResponse.createByErrorMessage("没有权限操作");
        }
    }

    /**
     * 产品的上架与下架
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping(value = "set_sale_status.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> setSaleStatus(HttpSession session,Integer productId,Integer status){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(iUserService.checkAdminRole(user.getId()).isSuccess()){
            //修改商品状态
            return iProductService.setSaleStatus(productId,status);
        }else {
            return ServerResponse.createByErrorMessage("没有权限操作");
        }
    }

    /**
     * 获取商品的详情
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping(value = "detail.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetail(HttpSession session, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(iUserService.checkAdminRole(user.getId()).isSuccess()){
            //获取商品详情
            return iProductService.getDetail(productId);
        }else {
            return ServerResponse.createByErrorMessage("没有权限操作");
        }
    }

    /**
     * 获取商品列表
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "list.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> getProductList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,@RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(iUserService.checkAdminRole(user.getId()).isSuccess()){
            //获取分页对象
            return iProductService.manageGetList(pageNum,pageSize);
        }else {
            return ServerResponse.createByErrorMessage("没有权限操作");
        }
    }

    /**
     * 搜索商品
     * @param session
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "search.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> searchProduct(HttpSession session, String productName,Integer productId,@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,@RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(iUserService.checkAdminRole(user.getId()).isSuccess()){
            //获取分页对象
            return iProductService.searchByNameAndId(pageNum,pageSize,productName,productId);
        }else {
            return ServerResponse.createByErrorMessage("没有权限操作");
        }
    }

    /**
     * 上传图片
     * @param session
     * @param upload_file
     * @param request
     * @return
     */
    @RequestMapping(value = "upload.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String ,String>> upload(HttpSession session , @RequestParam(value = "upload_file",required = false) MultipartFile upload_file, HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(iUserService.checkAdminRole(user.getId()).isSuccess()){
            //获取创建目录的地址
            String path = request.getSession().getServletContext().getRealPath("upload");
            //上传文件
            String name = iFileService.uploadFile(path, upload_file);
            if(name==null){
                return ServerResponse.createByErrorMessage("上传失败");
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+name;
            Map resultMap = Maps.newHashMap();
            resultMap.put("uri",name);
            resultMap.put("url",url);
            return ServerResponse.createBySuccess(resultMap);
        }else {
            return ServerResponse.createByErrorMessage("没有权限操作");
        }
    }


    @RequestMapping(value = "richtext_img_upload.do",method = RequestMethod.POST)
    @ResponseBody
    public Map<String ,String> richtextImgUpload(HttpSession session ,@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        Map resultMap = Maps.newHashMap();
        if(user==null){
            resultMap.put("success",false);
            resultMap.put("msg","用户未登录");
            return resultMap;
        }
        //使用的simditor富文本要求格式返回
        if(iUserService.checkAdminRole(user.getId()).isSuccess()){
            //获取创建目录的地址
            String path = request.getSession().getServletContext().getRealPath("upload");
            //上传文件
            String name = iFileService.uploadFile(path, file);
            if(name==null){
                resultMap.put("msg","上传失败");
                resultMap.put("success",false);
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+name;
            resultMap.put("file_path",url);
            resultMap.put("msg","上传成功");
            resultMap.put("success",true);
            //设置响应头
            response.setHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }else {
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }
    }


}
