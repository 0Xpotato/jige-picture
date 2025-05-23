package com.jige.jigepicturebackend.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jige.jigepicturebackend.manager.auth.StpKit;
import com.jige.jigepicturebackend.model.dto.user.VipCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import cn.hutool.core.util.ObjectUtil;
import com.jige.jigepicturebackend.constant.UserConstant;
import com.jige.jigepicturebackend.exception.BusinessException;
import com.jige.jigepicturebackend.exception.ErrorCode;
import com.jige.jigepicturebackend.exception.ThrowUtils;
import com.jige.jigepicturebackend.mapper.UserMapper;
import com.jige.jigepicturebackend.model.dto.user.UserQueryRequest;
import com.jige.jigepicturebackend.model.entity.User;
import com.jige.jigepicturebackend.model.enums.UserRoleEnum;
import com.jige.jigepicturebackend.model.vo.LoginUserVO;
import com.jige.jigepicturebackend.model.vo.UserVO;
import com.jige.jigepicturebackend.service.UserService;

import static com.jige.jigepicturebackend.constant.UserConstant.USER_LOGIN_STATE;


@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {



    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1.校验参数
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
//        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount,userPassword,checkPassword),ErrorCode.PARAMS_ERROR,"参数为空");
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
//        ThrowUtils.throwIf(userAccount.length()<4,ErrorCode.PARAMS_ERROR,"用户账号过短");
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
//        ThrowUtils.throwIf(userPassword.length()<8||checkPassword.length()<8,ErrorCode.PARAMS_ERROR,"用户密码过短");
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
//        ThrowUtils.throwIf(!userPassword.equals(checkPassword),ErrorCode.PARAMS_ERROR,"两次输入的密码不一致");

        //2.检查用户账号是否和数据库中已有的重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }

        //3.密码一定要加密
        String encryptPassword = getEncryptPassword(userPassword);
        //4.插入数据到数据库中
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("默认名称");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }
        return user.getId();
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword), ErrorCode.PARAMS_ERROR, "参数为空");
        ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "用户账号错误");
        ThrowUtils.throwIf(userPassword.length() < 8, ErrorCode.PARAMS_ERROR, "用户密码错误");
        //2.对用户传递的密码进行加密
        String encryptPassword = getEncryptPassword(userPassword);
        //3.查询数据库中的用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        //不存在，抛异常
        if (user == null) {
            log.info("user login failed,userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或者密码错误");
        }
        // 4.保存用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        // 5. 记录用户登录态到 Sa-token，便于空间鉴权时使用，注意保证该用户信息与 SpringSession 中的信息过期时间一致
        StpKit.SPACE.login(user.getId());
        StpKit.SPACE.getSession().set(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);

    }

    /**
     * 获取当前登录用户（在后端间调用）
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        //判断是否已经登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //region 从数据库中查询（追求性能的话可以注释，直接返回上述结果）
        Long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //endregion
        return currentUser;
    }

    @Override
    public String getEncryptPassword(String userPassword) {
        //加盐，混淆密码
        final String SALT = "jige";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    /**
     * 获取脱敏后的登录用户信息
     *
     * @param user
     * @return
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    /**
     * 获取脱敏后的用户信息
     *
     * @param user
     * @return
     */
    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 获取脱敏后的用户信息列表
     *
     * @param userList
     * @return
     */
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        return userList.stream()
                .map(this::getUserVO)
                .collect(Collectors.toList());
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        //判断是否已经登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        //已登录，移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    /**
     * 获取查询条件
     * @param userQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotNull(id),"id",id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole),"userRole",userRole);
        queryWrapper.like(StrUtil.isNotBlank(userName),"userName",userName);
        queryWrapper.like(StrUtil.isNotBlank(userAccount),"userAccount",userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userProfile),"userProfile",userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField),sortOrder.equals("ascend"),sortOrder);
        return queryWrapper;
    }

    @Override
    public boolean isAdmin(User user) {
        //没登录
        if (user==null){
            return false;
        }
        //用户不是管理员角色
        if (!user.getUserRole().equals(UserConstant.ADMIN_ROLE)){
            return false;
        }
        return true;
    }

    /**
     * 用户兑换会员vip功能
     *
     * @param vipExchangeRequest
     * @param request
     * @return
     */

    // region ------- 以下代码为用户兑换会员功能 --------

    // 新增依赖注入
    @Autowired
    private ResourceLoader resourceLoader;

    // 文件读写锁（确保并发安全）
    private final ReentrantLock fileLock = new ReentrantLock();

    // VIP 角色常量（根据你的需求自定义）
    private static final String VIP_ROLE = "vip";

    /**
     * 兑换会员
     *
     * @param user
     * @param vipCode
     * @return
     */
    @Override
    public boolean exchangeVip(User user, String vipCode) {
        // 1. 参数校验
        if (user == null || StrUtil.isBlank(vipCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 读取并校验兑换码
        VipCode targetCode = validateAndMarkVipCode(vipCode);
        // 3. 更新用户信息
        updateUserVipInfo(user, targetCode.getCode());
        return true;
    }

    /**
     * 校验兑换码并标记为已使用
     */
    private VipCode validateAndMarkVipCode(String vipCode) {
        fileLock.lock(); // 加锁保证文件操作原子性
        try {
            // 读取 JSON 文件
            JSONArray jsonArray = readVipCodeFile();

            // 查找匹配的未使用兑换码
            List<VipCode> codes = JSONUtil.toList(jsonArray, VipCode.class);
            VipCode target = codes.stream()
                    .filter(code -> code.getCode().equals(vipCode) && !code.isHasUsed())
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.PARAMS_ERROR, "无效的兑换码"));

            // 标记为已使用
            target.setHasUsed(true);

            // 写回文件
            writeVipCodeFile(JSONUtil.parseArray(codes));
            return target;
        } finally {
            fileLock.unlock();
        }
    }

    /**
     * 读取兑换码文件
     */
    private JSONArray readVipCodeFile() {
        try {
            // 定义文件路径常量
            final String CLASSPATH_VIP_CODE_PATH = "classpath:biz/vipCode.json";
            final String EXTERNAL_VIP_CODE_PATH = "/www/wwwroot/config/biz/vipCode.json";

            // 优先尝试从外部配置文件读取
            File externalFile = new File(EXTERNAL_VIP_CODE_PATH);
            if (externalFile.exists()) {
                return JSONUtil.readJSONArray(externalFile, StandardCharsets.UTF_8);
            }

            // 如果外部文件不存在，从 classpath 读取初始文件
            Resource resource = resourceLoader.getResource(CLASSPATH_VIP_CODE_PATH);
            if (!resource.exists()) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "兑换码文件不存在，请检查配置");
            }

            try (InputStream inputStream = resource.getInputStream()) {
                // 使用正确的文件读取方式
                String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                // 将初始文件复制到外部路径（首次运行自动创建）
                FileUtil.writeString(content, externalFile, StandardCharsets.UTF_8);
                return JSONUtil.parseArray(content);
            }
        } catch (IOException e) {
            log.error("读取兑换码文件失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统繁忙，请稍后重试");
        }
    }

    /**
     * 写入兑换码文件
     */
    private void writeVipCodeFile(JSONArray jsonArray) {
        try {
            String externalPath = "/www/wwwroot/config/biz/vipCode.json"; // 与读取路径一致
            FileUtil.writeString(jsonArray.toStringPretty(), externalPath, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("更新兑换码文件失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统繁忙");
        }
    }

    /**
     * 更新用户会员信息
     */
    private void updateUserVipInfo(User user, String usedVipCode) {
        // 计算过期时间（当前时间 + 1 年）
        Date expireTime = DateUtil.offsetMonth(new Date(), 12); // 计算当前时间加 1 年后的时间

        // 构建更新对象
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setVipExpireTime(expireTime); // 设置过期时间
        updateUser.setVipCode(usedVipCode);     // 记录使用的兑换码
        updateUser.setUserRole(VIP_ROLE);       // 修改用户角色

        // 执行更新
        boolean updated = this.updateById(updateUser);
        if (!updated) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "开通会员失败，操作数据库失败");
        }
    }

    // endregion ------- 以下代码为用户兑换会员功能 --------
}





