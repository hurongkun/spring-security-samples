package org.javaboy.formlogin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * @作者 江南一点雨
 * @公众号 江南一点雨
 * @微信号 a_java_boy
 * @GitHub https://github.com/lenve
 * @博客 http://wangsong.blog.csdn.net
 * @网站 http://www.javaboy.org
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("javaboy")
                .password("123").roles("admin");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**","/images/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                //.loginPage("/login.html")
                .permitAll()
                .and()
                .csrf().disable()
                .sessionManagement()
                .maximumSessions(1)//想要用新的登录踢掉旧的登录，我们只需要将最大会话数设置为 1 即可
                .maxSessionsPreventsLogin(true);//如果相同的用户已经登录了，你不想踢掉他，而是想禁止新的登录操作，那也好办  添加 maxSessionsPreventsLogin 配置即可  有缺陷：如果用户直接关闭浏览器。或者登录失败，都会记录为已有用户登录
    }



    /**
     * 测试：
     * 配置完成后，分别用 Chrome 和 Firefox 两个浏览器进行测试（或者使用 Chrome 中的多用户功能）。
     *
     * Chrome 上登录成功后，访问 /hello 接口。
     * Firefox 上登录成功后，访问 /hello 接口。
     *
     * 提示：Maximum sessions of 1 for this principal exceeded
     *
     * 实现原理：
     * AbstractAuthenticationProcessingFilter#doFilter 方法
     *
     * 调用 attemptAuthentication 方法走完认证流程之后，回来之后，接下来就是调用 sessionStrategy.onAuthentication 方法，这个方法就是用来处理 session 的并发问题的

     */
    @Bean
    HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
