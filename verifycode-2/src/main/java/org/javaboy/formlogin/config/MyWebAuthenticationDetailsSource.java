package org.javaboy.formlogin.config;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.stereotype.Component;

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
@Component
public class MyWebAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest,MyWebAuthenticationDetails> {

    //将 MyWebAuthenticationDetailsSource 注入到 SecurityConfig 中，并在 formLogin 中配置 authenticationDetailsSource 即可成功使用我们自定义的 WebAuthenticationDetails。
    //
    //这样自定义完成后，WebAuthenticationDetails 中原有的功能依然保留，也就是我们还可以利用老办法继续获取用户 IP 以及 sessionId
    @Override
    public MyWebAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new MyWebAuthenticationDetails(context);
    }
}
