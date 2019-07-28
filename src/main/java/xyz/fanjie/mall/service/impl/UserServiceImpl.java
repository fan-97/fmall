package xyz.fanjie.mall.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.fanjie.mall.common.Const;
import xyz.fanjie.mall.common.ServerResponse;
import xyz.fanjie.mall.common.TokenCache;
import xyz.fanjie.mall.dao.UserMapper;
import xyz.fanjie.mall.pojo.User;
import xyz.fanjie.mall.service.IUserService;
import xyz.fanjie.mall.util.MD5Util;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        //1.检查用户名是否存在
        int resultNum = userMapper.checkUsername(username);
        if (resultNum == 0) {
            return ServerResponse.createByErrorMessage("username is not found");
        }
        //2.登录
        //todo MD5密码登录
        String md5psw = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5psw);
        if (user == null) {
            return ServerResponse.createByErrorMessage("username or password error");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("login success", user);
    }

    public ServerResponse<String> register(User user) {
        //校验用户名是否存在
        ServerResponse<String> checkValid = checkValid(user.getUsername(), Const.USER_NAME);
        if (!checkValid.isSuccess()) {
            return checkValid;
        }
        //校验邮箱
        checkValid = checkValid(user.getEmail(), Const.EMAIL);
        if (!checkValid.isSuccess()) {
            return checkValid;
        }
        //设置用户为普通用户
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //将密码用MD5加密并将用户存入数据库
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if (resultCount <= 0) {
            return ServerResponse.createByErrorMessage("fail to regiter");
        }
        return ServerResponse.createBySuccess("注册成功");
    }

    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isBlank(type)) {
            return ServerResponse.createByErrorMessage("参数错误");
        }

        if (StringUtils.equals(type, Const.USER_NAME)) {
            //校验用户名
            int resultCount = userMapper.checkUsername(str);
            if (resultCount > 0) {
                return ServerResponse.createByErrorMessage("用户名已经存在 ");
            }
        }

        if (StringUtils.equals(type, Const.EMAIL)) {
            //校验邮箱
            int resultCount = userMapper.checkEmail(str);
            if (resultCount > 0) {
                return ServerResponse.createBySuccessMessage("邮箱已经存在");
            }
        }
        return ServerResponse.createBySuccess("校验成功");

    }

    public ServerResponse<User> getUserInfo(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        //设置密码为空
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    public ServerResponse<String> forgetGetQuetsion(String username) {
        //校验用户名
        ServerResponse checkValid = checkValid(username, Const.USER_NAME);
        if (checkValid.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //通过用户名获取找回密码问题
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isBlank(question)) {
            return ServerResponse.createByErrorMessage("用户未设置密保问题");
        }
        return ServerResponse.createBySuccess(question);
    }

    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        //校验用户名
        ServerResponse checkValid = checkValid(username, Const.USER_NAME);
        if (checkValid.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //校验密保问题是否存在
        if (StringUtils.isBlank(question)) {
            return ServerResponse.createByErrorMessage("该用户未设置密保问题");
        }
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount <= 0) {
            return ServerResponse.createByErrorMessage("密保问题答案错误");
        }
        //将验证放入本地缓存
        String forgetToken = UUID.randomUUID().toString();
        TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
        return ServerResponse.createBySuccess(forgetToken);
    }

    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        //检查token是否存在
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMessage("参数错误,token需要传递");
        }
        //检查用户是否存在
        ServerResponse<String> checkValid = checkValid(username, Const.USER_NAME);
        if (checkValid.isSuccess()) {
            return ServerResponse.createByErrorMessage("该用户不存在");
        }
        //与本地缓存的token进行比较
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token无效或过期");
        }
        if(StringUtils.equals(token,forgetToken)){
            //更新用户密码-->DAO
            String md5Psw = MD5Util.MD5EncodeUtf8(passwordNew);
            int resultCount = userMapper.updatePasswordByUsername(username, md5Psw);
            if (resultCount > 0) {
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token 错误，请重新获取重置密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        //检测旧密码是否正确
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if (resultCount <= 0) {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        resultCount = userMapper.updateByPrimaryKeySelective(user);
        if(resultCount<=0){
            return ServerResponse.createByErrorMessage("重置密码失败");
        }
        return ServerResponse.createBySuccess("重置密码成功");
    }

    public ServerResponse<String> update_information(User user){
        if(user==null){
            return  ServerResponse.createByErrorMessage("参数错误");
        }

        //校验更新后的email 是否可用
       int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount>0){
            return ServerResponse.createByErrorMessage("邮箱不可用");
        }

        //不更新username
        User userNew = new User();
        userNew.setId(user.getId());
        userNew.setEmail(user.getEmail());
        userNew.setPhone(user.getPhone());
        userNew.setQuestion(user.getQuestion());
        userNew.setAnswer(user.getAnswer());

        resultCount = userMapper.updateByPrimaryKeySelective(userNew);

        if(resultCount<=0){
            return ServerResponse.createByErrorMessage("更新信息失败");
        }
        return ServerResponse.createBySuccess("更新信息成功");
    }

    public ServerResponse<User> get_information(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user==null){
            return ServerResponse.createByErrorMessage("找不到该用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }
}
