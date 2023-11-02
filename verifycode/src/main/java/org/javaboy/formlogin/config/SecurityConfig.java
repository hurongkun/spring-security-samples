package org.javaboy.formlogin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.javaboy.formlogin.model.RespBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Properties;

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
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        ProviderManager manager = new ProviderManager(Arrays.asList(myAuthenticationProvider()));
        return manager;
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("javaboy").password("123").roles("admin").build());
        return manager;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/vc.jpg").permitAll()//设置 /vc.jpg 任何人都能访问。
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .successHandler((req, resp, auth) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write(new ObjectMapper().writeValueAsString(RespBean.ok("success", auth.getPrincipal())));
                    out.flush();
                    out.close();
                })
                .failureHandler((req, resp, e) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write(new ObjectMapper().writeValueAsString(RespBean.error(e.getMessage())));
                    out.flush();
                    out.close();
                })
                .permitAll()
                .and()
                .csrf().disable();
    }

    //首先我们需要验证码，这次我就懒得自己去实现了，我们用网上一个现成的验证码库 kaptcha
    //这段配置很简单，我们就是提供了验证码图片的宽高、字符库以及生成的验证码字符长度。
    /**
     *
     * <dependency>
     *     <groupId>com.github.penggle</groupId>
     *     <artifactId>kaptcha</artifactId>
     *     <version>2.3.2</version>
     * </dependency>
     */
    @Bean
    Producer verifyCode() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "150");
        properties.setProperty("kaptcha.image.height", "50");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

    /**
     *
     * MyAuthenticationProvider 定义好之后，接下来主要是如何让 MyAuthenticationProvider 代替 DaoAuthenticationProvider。
     *
     * 前面我们说，所有的 AuthenticationProvider 都是放在 ProviderManager 中统一管理的，所以接下来我们就要自己提供 ProviderManager
     * ，然后注入自定义的 MyAuthenticationProvider，这一切操作都在 SecurityConfig 中完成：
     *
     * 我们需要提供一个 MyAuthenticationProvider 的实例，创建该实例时，需要提供 UserDetailService 和 PasswordEncoder 实例。
     * 通过重写 authenticationManager 方法来提供一个自己的 AuthenticationManager，实际上就是 ProviderManager，在创建 ProviderManager 时，加入自己的 myAuthenticationProvider。
     * 这里为了简单，我将用户直接存在内存中，提供一个 UserDetailsService 实例即可。如果大家想将用户存在数据库中，可以参考松哥之前的文章：Spring Security+Spring Data Jpa 强强联手，安全管理只有更简单！。
     * 最后就简单配置一下各种回调即可，另外记得设置 /vc.jpg 任何人都能访问。
     */
    @Bean
    MyAuthenticationProvider myAuthenticationProvider() {
        MyAuthenticationProvider myAuthenticationProvider = new MyAuthenticationProvider();
        myAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        myAuthenticationProvider.setUserDetailsService(userDetailsService());
        return myAuthenticationProvider;
    }
}
