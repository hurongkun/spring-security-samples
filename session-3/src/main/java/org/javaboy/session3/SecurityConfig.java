package org.javaboy.session3;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

/**
 * @作者 江南一点雨
 * @微信公众号 江南一点雨
 * @网站 http://www.itboyhub.com
 * @国际站 http://www.javaboy.org
 * @微信 a_java_boy
 * @GitHub https://github.com/lenve
 * @Gitee https://gitee.com/lenve
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated()
                .and()
                .formLogin()
                .permitAll()
                .and()
                .httpBasic()
                .and()
                .sessionManagement()
                .sessionFixation().none()
                .and()
                .csrf().disable();
    }

    /**
     * HttpSession 是一个服务端的概念，服务端生成的 HttpSession 都会有一个对应的 sessionid，这个 sessionid 会通过 cookie 传递给前端
     * ，前端以后发送请求的时候，就带上这个 sessionid 参数，服务端看到这个 sessionid 就会把这个前端请求和服务端的某一个 HttpSession 对应起来
     * ，形成“会话”的感觉
     *
     * 浏览器关闭并不会导致服务端的 HttpSession 失效，想让服务端的 HttpSession 失效，要么手动调用 HttpSession#invalidate 方法；要么等到 session 自动过期；要么重启服务端。
     *
     * 但是为什么有的人会感觉浏览器关闭之后 session 就失效了呢？这是因为浏览器关闭之后，保存在浏览器里边的 sessionid 就丢了（默认情况下），所以当浏览器再次访问服务端的时候，服务端会给浏览器重新分配一个 sessionid ，这个 sessionid 和之前的 HttpSession 对应不上，所以用户就会感觉 session 失效。
     *
     *
     * migrateSession 表示在登录成功之后，创建一个新的会话，然后讲旧的 session 中的信息复制到新的 session 中，默认即此。
     * none 表示不做任何事情，继续使用旧的 session。
     * changeSessionId 表示 session 不变，但是会修改 sessionid，这实际上用到了 Servlet 容器提供的防御会话固定攻击。
     * newSession 表示登录后创建一个新的 session。
     */


}
