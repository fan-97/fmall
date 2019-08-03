package xyz.fanjie.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.fanjie.mall.common.Const;
import xyz.fanjie.mall.common.ResponseCode;
import xyz.fanjie.mall.common.ServerResponse;
import xyz.fanjie.mall.dao.CategoryMapper;
import xyz.fanjie.mall.dao.ProductMapper;
import xyz.fanjie.mall.pojo.Category;
import xyz.fanjie.mall.pojo.Product;
import xyz.fanjie.mall.service.ICategoryService;
import xyz.fanjie.mall.service.IProductService;
import xyz.fanjie.mall.util.DateTimeUtils;
import xyz.fanjie.mall.util.PropertiesUtil;
import xyz.fanjie.mall.vo.ProductDetailVo;
import xyz.fanjie.mall.vo.ProductListVo;

import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;

    public ServerResponse<String> saveProduct(Product product) {
        if (product == null) {
            return ServerResponse.createBySuccessMessage("更新或新增商品失败");
        }
        //将第一张子图设置为主图
        if (StringUtils.isNotBlank(product.getSubImages())) {
            String[] subImages = product.getSubImages().split(",");
            if (subImages.length > 0) {
                product.setMainImage(subImages[0]);
            }
        }
        if (product.getId() == null) {
            //新增
            int effectNum = productMapper.insert(product);
            if (effectNum > 0) {
                return ServerResponse.createBySuccess("新增产品成功");
            } else {
                return ServerResponse.createByError("新增产品失败");
            }
        } else {
            //更新
            int effectNum = productMapper.updateByPrimaryKey(product);
            if (effectNum > 0) {
                return ServerResponse.createBySuccess("更新产品成功");
            } else {
                return ServerResponse.createByError("更新产品失败");
            }
        }
    }

    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if (productId != null && status != null && Const.ProductStatus.STATUS.contains(status)) {
            Product product = new Product();
            product.setId(productId);
            product.setStatus(status);
            int effectNum = productMapper.updateByPrimaryKeySelective(product);
            if (effectNum > 0) {
                return ServerResponse.createBySuccess("修改产品状态成功");
            }
        }
        return ServerResponse.createByError("修改产品状态失败");
    }

    public ServerResponse<ProductDetailVo> getDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorMessage("商品已下架或者不存在");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("商品已下架或者不存在");
        }
        return ServerResponse.createBySuccess(assembleProductDetailVo(product));
    }

    /**
     * 整合商品详情值对象
     *
     * @param product
     * @return
     */
    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setName(product.getName());
        productDetailVo.setMainImages(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category != null) {
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://image.fanjie.xyz/"));
        productDetailVo.setCreateTime(DateTimeUtils.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtils.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    public ServerResponse<PageInfo> manageGetList(int pageNum, int pageSize) {
        //在sql语句后面添加 Limit （）；
        PageHelper.startPage(pageNum, pageSize);
        //获取list
        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVoList = assembleProductListVo(productList);
        PageInfo resultPage = new PageInfo(productList);
        resultPage.setList(productListVoList);
        return ServerResponse.createBySuccess(resultPage);
    }

    private List<ProductListVo> assembleProductListVo(List<Product> productList) {
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo vo = new ProductListVo();
            vo.setCategoryId(product.getCategoryId());
            vo.setId(product.getId());
            vo.setMainImage(product.getMainImage());
            vo.setName(product.getName());
            vo.setPrice(product.getPrice());
            vo.setStatus(product.getStatus());
            vo.setSubtitle(product.getSubtitle());
            vo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://image.fanjie.xyz/"));
            productListVoList.add(vo);

        }
        return productListVoList;
    }

    public ServerResponse<PageInfo> searchByNameAndId(int pageNum, int pageSize, String productNamne, Integer productId) {
        if (StringUtils.isNotBlank(productNamne)) {
            productNamne = new StringBuilder("%").append(productNamne).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndId(productNamne, productId);
        List<ProductListVo> productListVoList = assembleProductListVo(productList);
        PageHelper.startPage(pageNum, pageSize);

        PageInfo resultPage = new PageInfo(productList);
        resultPage.setList(productListVoList);

        return ServerResponse.createBySuccess(resultPage);

    }

    public ServerResponse<PageInfo> getList(Integer categoryId, String keyword, int pageNum, int pageSize, String orderBy) {
        if (categoryId == null && StringUtils.isBlank(keyword)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryList = new ArrayList<>();
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)) {
                //没有该分类并且没有关键字返回一个空结果集
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVoList = new ArrayList<>();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            //查找分类下的所有子分类
            categoryList = iCategoryService.getDeepCategoryId(categoryId).getData();
        }
        if (StringUtils.isNotBlank(keyword)) {
            keyword = new StringBuilder("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNum, pageSize);
        //排序处理
        if (StringUtils.isNotBlank(orderBy)) {
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                String[] orderArr = orderBy.split("_");
                PageHelper.orderBy(orderArr[0] + " " + orderArr[1]);
            }
        }
        //查询对象集合
        List<Product> productList = productMapper.selectByNameAndCategoryIds(keyword, categoryList);
        List<ProductListVo> productListVoList = assembleProductListVo(productList);
        PageInfo resultPage = new PageInfo(productList);
        resultPage.setList(productListVoList);
        return ServerResponse.createBySuccess(resultPage);
    }

    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorMessage("商品已下架或者不存在");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("商品已下架或者不存在");
        }
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            return ServerResponse.createByErrorMessage("商品已下架或者不存在");
        }
        return ServerResponse.createBySuccess(assembleProductDetailVo(product));
    }
}
