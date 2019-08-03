package xyz.fanjie.mall.service;

import com.github.pagehelper.PageInfo;
import xyz.fanjie.mall.common.ServerResponse;
import xyz.fanjie.mall.pojo.Product;
import xyz.fanjie.mall.vo.ProductDetailVo;

public interface IProductService {

    public ServerResponse<String> saveProduct(Product product);

    ServerResponse<String> setSaleStatus(Integer productId, Integer status);

    ServerResponse<ProductDetailVo> getDetail(Integer productId);

    ServerResponse<PageInfo> manageGetList(int pageNum, int pageSize);

    ServerResponse<PageInfo> searchByNameAndId(int pageNum, int pageSize,String productNamne,Integer productId);

    ServerResponse<PageInfo> getList(Integer categoryId, String keyword, int pageNum, int pageSize, String orderBy);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
}
