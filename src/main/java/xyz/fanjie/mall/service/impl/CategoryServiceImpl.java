package xyz.fanjie.mall.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import xyz.fanjie.mall.common.ServerResponse;
import xyz.fanjie.mall.dao.CategoryMapper;
import xyz.fanjie.mall.pojo.Category;
import xyz.fanjie.mall.service.ICategoryService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse addCategory(String categoryName, Integer paretnId) {
        if (paretnId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("参数错误，请重新传递");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(paretnId);
        category.setStatus(true);
        int resultCount = categoryMapper.insertSelective(category);
        if(resultCount>=1){
            return ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    public ServerResponse updateCategoryName(String categoryName,Integer categoryId){
        if (categoryId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("参数错误，请重新传递");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int effectNums = categoryMapper.updateByPrimaryKeySelective(category);
        if(effectNums>=1){
            return ServerResponse.createBySuccessMessage("更新品类名字成功");
        }
        return ServerResponse.createByErrorMessage("更新品类名字失败");
    }

    /**
     * 获取当前节点下的子节点 同一层
     * @param categoryId
     * @return
     */
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        if(categoryId==null){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            logger.error("未找到当前分类下的子分类");
            return ServerResponse.createByErrorMessage("未找到该品类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 递归查询子节点
     * @param categorId
     * @return
     */
    public ServerResponse<List<Integer>> getDeepCategoryId(Integer categorId){
        Set<Category> categorySet = new HashSet<>();
        findChildCategory(categorySet,categorId);

        List<Integer> categoryList = new ArrayList<>(categorySet.size());
        for(Category categoryItem:categorySet){
            categoryList.add(categoryItem.getId());
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
        //查询当前分类是否存在
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category!=null){
            categorySet.add(category);
        }
        //查询当前节点下所有子节点
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for(Category categoryItem:categoryList){
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }
}
