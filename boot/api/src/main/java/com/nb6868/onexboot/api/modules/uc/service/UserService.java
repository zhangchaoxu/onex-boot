package com.nb6868.onexboot.api.modules.uc.service;

import com.nb6868.onexboot.api.modules.uc.dto.RegisterRequest;
import com.nb6868.onexboot.api.modules.uc.dto.UserDTO;
import com.nb6868.onexboot.common.pojo.Kv;
import com.nb6868.onexboot.common.pojo.Result;
import com.nb6868.onexboot.common.service.CrudService;
import com.nb6868.onexboot.api.modules.uc.dto.ChangePasswordByMailCodeRequest;
import com.nb6868.onexboot.api.modules.uc.dto.LoginRequest;
import com.nb6868.onexboot.api.modules.uc.entity.UserEntity;

import java.util.List;

/**
 * 用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface UserService extends CrudService<UserEntity, UserDTO> {

    /**
     * 登录
     */
    Kv login(LoginRequest request);

    /**
     * 退出
     */
    boolean logout(String token);

    /**
     * 通过短信验证码修改密码
     */
    Result<?> changePasswordBySmsCode(ChangePasswordByMailCodeRequest request);

    /**
     * 注册
     */
    Result<?> register(RegisterRequest request);

    /**
     * 通过用户名获取用户
     */
    UserEntity getByUsername(String username);

    /**
     * 通过手机号获取用户
     */
    UserEntity getByMobile(String mobile);

    /**
     * 通过手机号区域和手机号获取用户
     */
    UserEntity getByMobile(String mobileArea, String mobile);

    /**
     * 修改状态
     */
    boolean changeStatus(UserDTO dto);

    /**
     * 修改密码
     *
     * @param id          用户ID
     * @param newPassword 新密码
     */
    boolean updatePassword(Long id, String newPassword);

    /**
     * 根据部门ID，查询用户数
     */
    int getCountByDeptId(Long deptId);

    /**
     * 判断用户名是否存在
     */
    boolean isUsernameExisted(String username, Long id);

    /**
     * 判断手机号是否存在
     */
    boolean isMobileExisted(String mobile, Long id);

    /**
     * 合并帐号,将mergeFrom数据合并到mergeTo
     */
    boolean merge(String mergeTo, List<String> mergeFrom);

}
