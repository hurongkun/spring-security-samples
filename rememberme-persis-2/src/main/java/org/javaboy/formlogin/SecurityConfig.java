package org.javaboy.formlogin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.RememberMeConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

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

    @Override
    @Bean
    protected UserDetailsService userDetailsService() {

        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("javaboy").password("{bcrypt}$2a$10$Sb1gAUH4wwazfNiqflKZve4Ubh.spJcxgHG8Cp29DeGya5zsHENqi").roles("admin").build());
        manager.createUser(User.withUsername("sang").password("{noop}123").roles("admin").build());
        manager.createUser(User.withUsername("江南一点雨").password("{MD5}{Wucj/L8wMTMzFi3oBKWsETNeXbMFaHZW9vCK9mahMHc=}4d43db282b36d7f0421498fdc693f2a2").roles("user").build());
        return manager;
    }

    @Autowired
    DataSource dataSource;
    @Bean
    JdbcTokenRepositoryImpl jdbcTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Bean
    PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices() {
        return new PersistentTokenBasedRememberMeServices("javaboy123", userDetailsService(), jdbcTokenRepository());
    }

    //持久化令牌的方式其实已经安全很多了，但是依然存在用户身份被盗用的问题，这个问题实际上很难完美解决，我们能做的，只能是当发生用户身份被盗用这样的事情时，将损失降低到最小,因此，我们来看下另一种方案，就是二次校验。

    /**
     * 为了让用户使用方便，我们开通了自动登录功能，但是自动登录功能又带来了安全风险，一个规避的办法就是如果用户使用了自动登录功能，我们可以只让他做一些常规的不敏感操作，
     * 例如数据浏览、查看，但是不允许他做任何修改、删除操作，如果用户点击了修改、删除按钮，我们可以跳转回登录页面，让用户重新输入密码确认身份，然后再允许他执行敏感操作。
     *
     * 这个功能在 Shiro 中有一个比较方便的过滤器可以配置，Spring Security 当然也一样，例如我现在提供三个访问接口：
     * 第一个 /hello 接口，只要认证后就可以访问，无论是通过用户名密码认证还是通过自动登录认证，只要认证了，就可以访问。
     * 第二个 /admin 接口，必须要用户名密码认证之后才能访问，如果用户是通过自动登录认证的，则必须重新输入用户名密码才能访问该接口。
     * 第三个 /rememberme 接口，必须是通过自动登录认证后才能访问，如果用户是通过用户名/密码认证的，则无法访问该接口。

     */

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/rememberme").rememberMe()///rememberme 接口是需要 rememberMe 才能访问。
                .antMatchers("/admin").fullyAuthenticated()// admin 是需要 fullyAuthenticated，fullyAuthenticated 不同于 authenticated，fullyAuthenticated 不包含自动登录的形式，而 authenticated 包含自动登录的形式。
                .anyRequest().authenticated()//最后剩余的接口（/hello）都是 authenticated 就能访问。
                .and()
                .formLogin()
                .permitAll()
                .and()
                .rememberMe()
                .key("javaboy")
//                .tokenRepository(jdbcTokenRepository())
//                .and()
                .withObjectPostProcessor(new ObjectPostProcessor<RememberMeConfigurer>() {
                    @Override
                    public <O extends RememberMeConfigurer> O postProcess(O object) {
//                        object.setPublishAuthorizationSuccess(true);
//                        object.setAlwaysCreateSession(false);
                        object.rememberMeServices(persistentTokenBasedRememberMeServices());
                        return object;
                    }
                })
                .and()
                .csrf().disable();
    }
}
