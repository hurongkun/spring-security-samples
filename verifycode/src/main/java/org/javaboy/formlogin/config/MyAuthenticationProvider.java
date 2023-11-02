package org.javaboy.formlogin.config;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @作者 江南一点雨
 * @微信公众号 江南一点雨
 * @网站 http://www.itboyhub.com
 * @国际站 http://www.javaboy.org
 * @微信 a_java_boy
 * @GitHub https://github.com/lenve
 * @Gitee https://gitee.com/lenve
 */

/**
 * Authentication 作为一个接口，它定义了用户，或者说 Principal 的一些基本行为，它有很多实现类：
 *
 * 在这些实现类中，我们最常用的就是 UsernamePasswordAuthenticationToken 了，而每一个 Authentication 都有适合它的 AuthenticationProvider 去处理校验。例如处理 UsernamePasswordAuthenticationToken 的 AuthenticationProvider 是 DaoAuthenticationProvider。
 *
 * 所以大家在 AuthenticationProvider 中看到一个 supports 方法，就是用来判断 AuthenticationProvider 是否支持当前 Authentication。
 * 在一次完整的认证中，可能包含多个 AuthenticationProvider，而这多个 AuthenticationProvider 则由 ProviderManager 进行统一管理
 *
 * 由于 AbstractUserDetailsAuthenticationProvider 已经把 authenticate 和 supports 方法实现了，所以在 DaoAuthenticationProvider 中，我们主要关注 additionalAuthenticationChecks 方法即可：
 *
 *
 * 自定义认证思路
 * 之前我们通过自定义过滤器，将自定义的过滤器加入到 Spring Security 过滤器链中，进而实现了添加登录验证码功能
 * ，但是我们也说这种方式是有弊端的，就是破坏了原有的过滤器链，请求每次都要走一遍验证码过滤器，这样不合理。
 *
 *
 * 登录请求是调用 AbstractUserDetailsAuthenticationProvider#authenticate 方法进行认证的，在该方法中，又会调用到
 * DaoAuthenticationProvider#additionalAuthenticationChecks 方法做进一步的校验，去校验用户登录密码。
 * 我们可以自定义一个 AuthenticationProvider 代替 DaoAuthenticationProvider，并重写它里边的 additionalAuthenticationChecks 方法
 * ，在重写的过程中，加入验证码的校验逻辑即可。
 *
 * 这样既不破坏原有的过滤器链，又实现了自定义认证功能。常见的手机号码动态登录，也可以使用这种方式来认证。
 */
public class MyAuthenticationProvider extends DaoAuthenticationProvider {

    //接下来我们来自定义一个 MyAuthenticationProvider 继承自 DaoAuthenticationProvider，并重写 additionalAuthenticationChecks 方法：

    /**
     *
     * 首先获取当前请求，注意这种获取方式，在基于 Spring 的 web 项目中，我们可以随时随地获取到当前请求，获取方式就是我上面给出的代码。
     * 从当前请求中拿到 code 参数，也就是用户传来的验证码。
     * 从 session 中获取生成的验证码字符串。
     * 两者进行比较，如果验证码输入错误，则直接抛出异常。
     * 最后通过 super 调用父类方法，也就是 DaoAuthenticationProvider 的 additionalAuthenticationChecks 方法，该方法中主要做密码的校验。
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String code = req.getParameter("code");
        String verify_code = (String) req.getSession().getAttribute("verify_code");
        if (code == null || verify_code == null || !code.equals(verify_code)) {
            throw new AuthenticationServiceException("验证码错误");
        }
        super.additionalAuthenticationChecks(userDetails, authentication);
    }
}
