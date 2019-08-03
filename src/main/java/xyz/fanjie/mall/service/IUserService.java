package xyz.fanjie.mall.service;

import com.github.pagehelper.PageInfo;
import xyz.fanjie.mall.common.ServerResponse;
import xyz.fanjie.mall.pojo.User;

public interface IUserService {


    ServerResponse<User> login(String userName,String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str,String type);

    ServerResponse<User> getUserInfo(Integer userId);

    ServerResponse<String> forgetGetQuetsion(String username);

    ServerResponse<String> forgetCheckAnswer(String username,String question,String answer);

    ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken);

    ServerResponse<PageInfo> getUserList(int pageNum, int pageSize);

    ServerResponse<String> resetPassword(String passwordOld ,String passwordNew,User user);

    ServerResponse<String> update_information(User user);

    ServerResponse<User> get_information(Integer userId);

    ServerResponse checkAdminRole(Integer userId);
}
