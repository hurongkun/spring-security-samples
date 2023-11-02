package org.javaboy.withjpa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javaboy.withjpa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
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
    /**
     * 要实现一个用户不可以同时在两台设备上登录，我们有两种思路：
     * 1.后来的登录自动踢掉前面的登录，就像大家在扣扣中看到的效果。
     * 2.如果用户已经登录，则不允许后来者登录。
     *
     *
     */
    @Autowired
    UserService userService;
    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/doLogin")
                .permitAll()
                .and()
                .csrf().disable()
                .sessionManagement()
                .sessionFixation().migrateSession()
                .maximumSessions(1);
        /**
         * 这里都是常规配置，我就不再多说。注意最后面我们将 session 数量设置为 1。
         *
         * 好了，配置完成后，我们启动项目，并行性多端登录测试。
         *
         * 打开多个浏览器，分别进行多端登录测试，我们惊讶的发现，每个浏览器都能登录成功，每次登录成功也不会踢掉已经登录的用户！
         *
         * 要搞清楚这个问题，我们就要先搞明白 Spring Security 是怎么保存用户对象和 session 的。
         *
         * Spring Security 中通过 SessionRegistryImpl 类来实现对会话信息的统一管理，我们来看下这个类的源码（部分）：
         *
         *
         * 1.首先大家看到，一上来声明了一个 principals 对象，这是一个支持并发访问的 map 集合，集合的 key 就是用户的主体（principal），正常来说，用户的 principal 其实就是用户对象，松哥在之前的文章中也和大家讲过 principal 是怎么样存入到 Authentication 中的（参见：松哥手把手带你捋一遍 Spring Security 登录流程），而集合的 value 则是一个 set 集合，这个 set 集合中保存了这个用户对应的 sessionid。
         * 2.如有新的 session 需要添加，就在 registerNewSession 方法中进行添加，具体是调用 principals.compute 方法进行添加，key 就是 principal。
         * 3.如果用户注销登录，sessionid 需要移除，相关操作在 removeSessionInformation 方法中完成，具体也是调用 principals.computeIfPresent 方法，这些关于集合的基本操作我就不再赘述了。
         * 看到这里，大家发现一个问题，ConcurrentMap 集合的 key 是 principal 对象，用对象做 key，一定要重写 equals 方法和 hashCode 方法，否则第一次存完数据，下次就找不到了，这是 JavaSE 方面的知识，我就不用多说了。
         *
         *
         * 测试：先在一个浏览器登录，然后访问/hello 接口。再在另一个浏览器登录。访问 /hello 接口。  此时回去第一个登录的浏览器访问 /hello 接口。发现没法正常访问了。提示：
         * This session has been expired (possibly due to multiple concurrent logins being attempted as the same user).
         */

    }
}
