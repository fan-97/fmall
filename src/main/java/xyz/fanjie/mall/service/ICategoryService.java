package xyz.fanjie.mall.service;

import xyz.fanjie.mall.common.ServerResponse;
import xyz.fanjie.mall.pojo.Category;

import java.util.List;

public interface ICategoryService {
    ServerResponse addCategory(String categoryName, Integer paretnId);

    ServerResponse updateCategoryName(String categoryName, Integer categoryId);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> getDeepCategoryId(Integer categorId);
}
