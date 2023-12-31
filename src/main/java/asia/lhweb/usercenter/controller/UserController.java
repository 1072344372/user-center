package asia.lhweb.usercenter.controller;

import asia.lhweb.usercenter.common.BaseResponse;
import asia.lhweb.usercenter.common.ErrorCode;
import asia.lhweb.usercenter.common.Result;
import asia.lhweb.usercenter.common.ResultUtils;
import asia.lhweb.usercenter.exception.BusinessException;
import asia.lhweb.usercenter.model.domain.User;
import asia.lhweb.usercenter.model.request.UserLoginRequest;
import asia.lhweb.usercenter.model.request.UserRegisterRequest;
import asia.lhweb.usercenter.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static asia.lhweb.usercenter.contant.UserConstant.*;

/**
 * 用户控制器
 *
 * @author 罗汉
 * @date 2023/11/13
 */
@Slf4j
@RestController// 返回类型都是json restful风格的api
@RequestMapping("/user")
@Api(tags = "用户相关接口")
public class UserController {
    @Resource
    private UserService userService;
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public BaseResponse<Long> UserRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String plantCode = userRegisterRequest.getPlantCode();
        //是否为空
        if (StringUtils.isAllBlank(userAccount, userPassword, checkPassword,plantCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        long res = userService.userRegister(userAccount, userPassword, checkPassword, plantCode);
        return ResultUtils.success(res );
    }
    @ApiOperation("用户退出登录")
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request==null) return null;
        int res = userService.userLogout(request);
        return ResultUtils.success(res,USER_LOGOUT_SUCCESS);
    }
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userRegisterRequest, HttpServletRequest request) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        if (StringUtils.isAllBlank(userAccount, userPassword)){
            return null;
        }

        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user,USER_LOGIN_SUCCESS);
    }

    /**
     * 获取当前用户
     *
     * @param request 请求
     * @return {@link BaseResponse}<{@link User}>
     */
    @ApiOperation("获取当前用户信息")
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        User currentUser= (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        //如果没有登录过
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //如果在session中有这个登录凭证 就再从数据库中获取
        Long userId=currentUser.getId();
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser,"获取当前用户信息成功");
    }

    /**
     * 搜索用户
     *
     * @param userName 用户名
     * @param request  请求
     * @return {@link BaseResponse}<{@link List}<{@link User}>>
     */
    @ApiOperation("搜索全部用户")
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String userName,HttpServletRequest request){
        //鉴权 管理员可查询
        if (!isAdmin(request)) {
          throw new BusinessException(ErrorCode.NO_AUTH,"不是管理员");
        }
        //todo 校验用户是否合法
        //脱敏后返回
        List<User> users = userService.searchUsers(userName);
        return ResultUtils.success(users,"search成功");
    }

    /**
     * 删除用户
     *
     * @param id      id
     * @param request 请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @ApiOperation("删除用户")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUsers(@RequestBody long id, HttpServletRequest request){
        if (!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"不是管理员");
        }
        if (id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"id不能小于0");
        }
        boolean res = userService.deleteUsers(id);
        return ResultUtils.success(res,"删除成功");
    }

    @ApiOperation("根据标签搜索用户")
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagsNameList){
        //效验是否为空
        if (CollectionUtils.isEmpty(tagsNameList)){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求标签为空");
        }
        //脱敏后返回
        List<User> users = userService.searchUsersByTags(tagsNameList);
        return ResultUtils.success(users,"搜索用户列表成功");
    }
    /**
     * 是否管理员
     *
     *
     * @param request 请求
     * @return boolean
     */
    private boolean isAdmin(HttpServletRequest request) {
        //鉴权 管理员可删除
        User user= (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }


}
