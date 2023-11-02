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
public class MyAuthenticationProvider extends DaoAuthenticationProvider {
    /**
     *
     *Authentication 这个接口前面和大家聊过多次，今天还要再来聊一聊。
     *
     * Authentication 接口用来保存我们的登录用户信息，实际上，它是对主体（java.security.Principal）做了进一步的封装。
     *
     * 我们来看下 Authentication 的一个定义：
     * public interface Authentication extends Principal, Serializable {
     * 	Collection<? extends GrantedAuthority> getAuthorities();
     * 	Object getCredentials();
     * 	Object getDetails();
     * 	Object getPrincipal();
     * 	boolean isAuthenticated();
     * 	void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException;
     * }
     *
     * 接口的解释如下：
     *
     * getAuthorities 方法用来获取用户的权限。
     * getCredentials 方法用来获取用户凭证，一般来说就是密码。
     * getDetails 方法用来获取用户携带的详细信息，可能是当前请求之类的东西。
     * getPrincipal 方法用来获取当前用户，可能是一个用户名，也可能是一个用户对象。
     * isAuthenticated 当前用户是否认证成功。
     *
     * 这里有一个比较好玩的方法，叫做 getDetails。关于这个方法，源码的解释如下：
     * Stores additional details about the authentication request. These might be an IP address, certificate serial number etc.
     * 从这段解释中，我们可以看出，该方法实际上就是用来存储有关身份认证的其他信息的，例如 IP 地址、证书信息等等。
     * 实际上，在默认情况下，这里存储的就是用户登录的 IP 地址和 sessionId。我们从源码角度来看下。
     *
     *
     * 相信大家已经明白用户登录必经的一个过滤器就是 UsernamePasswordAuthenticationFilter，在该类的 attemptAuthentication 方法中，对请求参数做提取，在 attemptAuthentication 方法中，会调用到一个方法，就是 setDetails。
     *
     * 我们一起来看下 setDetails 方法：
     */

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (!((MyWebAuthenticationDetails) authentication.getDetails()).isPassed()) {
            throw new AuthenticationServiceException("验证码错误");
        }
        super.additionalAuthenticationChecks(userDetails, authentication);
    }
}
