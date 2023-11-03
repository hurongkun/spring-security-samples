package org.javaboy.csrf3;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.LazyCsrfTokenRepository;

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
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login.html")
                .successHandler((req,resp,authentication)->{
                    resp.getWriter().write("success");
                })
                .permitAll()
                .and()
                .csrf().csrfTokenRepository(new LazyCsrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
    }

    /**
     * 前后端分离方案
     *如果是前后端分离项目，Spring Security 也提供了解决方案。
     * 这次不是将 _csrf 放在 Model 中返回前端了，而是放在 Cookie 中返回前端，配置方式如下：
     * @Configuration
     * public class SecurityConfig extends WebSecurityConfigurerAdapter {
     *     @Override
     *     protected void configure(HttpSecurity http) throws Exception {
     *         http.authorizeRequests().anyRequest().authenticated()
     *                 .and()
     *                 .formLogin()
     *                 .and()
     *                 .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
     *     }
     * }
     *
     * 有小伙伴可能会说放在 Cookie 中不是又被黑客网站盗用了吗？其实不会的，大家注意如下两个问题：
     *
     * 黑客网站根本不知道你的 Cookie 里边存的啥，他也不需要知道，因为 CSRF 攻击是浏览器自动携带上 Cookie 中的数据的。
     * 我们将服务端生成的随机数放在 Cookie 中，前端需要从 Cookie 中自己提取出来 _csrf 参数，然后拼接成参数传递给后端，单纯的将 Cookie 中的数据传到服务端是没用的。
     * 理解透了上面两点，你就会发现 _csrf 放在 Cookie 中是没有问题的，但是大家注意，配置的时候我们通过 withHttpOnlyFalse 方法获取了 CookieCsrfTokenRepository 的实例，该方法会设置 Cookie 中的 HttpOnly 属性为 false，也就是允许前端通过 js 操作 Cookie（否则你就没有办法获取到 _csrf）。
     *
     */
}
