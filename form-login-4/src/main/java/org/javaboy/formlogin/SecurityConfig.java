package org.javaboy.formlogin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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

    //所谓的授权，就是用户如果要访问某一个资源，我们要去检查用户是否具备这样的权限，如果具备就允许访问，如果不具备，则不允许访问。
    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    //疑惑：例子又没引入redis，也没引入数据库，那么SpringSecurity框架是通过什么来判断登录成功和注销的呢？   答案：通过Cookie
    /**
     * 角色继承
     * 在前面松哥提到过一点，所有 user 能够访问的资源，admin 都能够访问，很明显我们目前的代码还不具备这样的功能。
     * 要实现所有 user 能够访问的资源，admin 都能够访问，这涉及到另外一个知识点，叫做角色继承。
     *
     * 注意，在配置时，需要给角色手动加上 ROLE_ 前缀。上面的配置表示 ROLE_admin 自动具备 ROLE_user 的权限
     */
    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_admin > ROLE_user");
        return hierarchy;
    }

    /*@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("javaUser")
                .password("123").roles("user")
                .and()
                .withUser("javaAdmin")
                .password("123")
                .roles("admin");
    }*/


    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        //由于 Spring Security 支持多种数据源，例如内存、数据库、LDAP 等，这些不同来源的数据被共同封装成了一个 UserDetailService 接口，任何实现了该接口的对象都可以作为认证数据源。
        //因此我们还可以通过重写 WebSecurityConfigurerAdapter 中的 userDetailsService 方法来提供一个 UserDetailService 实例进而配置多个用户：
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("javaAdmin").password("123").roles("admin").build());
        manager.createUser(User.withUsername("javaUser").password("123").roles("user").build());
        return manager;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
    }

    //接下来我们来配置权限的拦截规则，在 Spring Security 的 configure(HttpSecurity http) 方法中，代码如下：
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("admin")//admin角色才能访问
                .antMatchers("/user/**").hasRole("user")//user角色才能访问
                .anyRequest().authenticated()//anyRequest一定要放在最后，如果你强制将 anyRequest 配置在 antMatchers 前面 此时项目在启动的时候，就会报错，会提示不能在 anyRequest 之后添加 antMatchers
                .and()
                .formLogin()
                .loginProcessingUrl("/doLogin")
                .loginPage("/login.html")
                .usernameParameter("name")
                .passwordParameter("passwd")
                .successHandler((req, resp, authentication) -> {
                    Object principal = authentication.getPrincipal();
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write(new ObjectMapper().writeValueAsString(principal));
                    out.flush();
                    out.close();
                })
                .failureHandler((req, resp, e) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write(e.getMessage());
                    out.flush();
                    out.close();
                })
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler((req, resp, authentication) -> {
                    System.out.println();
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write("注销成功");
                    out.flush();
                    out.close();
                })
                .permitAll()
                .and()
                .csrf().disable().exceptionHandling()
                .authenticationEntryPoint((req, resp, authException) -> {
                            resp.setContentType("application/json;charset=utf-8");
                            PrintWriter out = resp.getWriter();
                            out.write("尚未登录，请先登录");
                            out.flush();
                            out.close();
                        }
                );
    }
}
