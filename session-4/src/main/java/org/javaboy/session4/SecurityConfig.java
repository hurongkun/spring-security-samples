package org.javaboy.session4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

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

    /**
     * 微服务session共享问题
     * 在微服务架构中，会出现一些单服务中不存在的问题，例如客户端发起一个请求，这个请求到达 Nginx 上之后，被 Nginx 转发到 Tomcat A 上，然后在 Tomcat A 上往 session 中保存了一份数据，下次又来一个请求，这个请求被转发到 Tomcat B 上，此时再去 Session 中获取数据，发现没有之前的数据。
     *
     * 方案
     * 1.对于这一类问题的解决，目前比较主流的方案就是将各个服务之间需要共享的数据，保存到一个公共的地方（主流方案就是 Redis）
     * 2.session 拷贝就是不利用 redis，直接在各个 Tomcat 之间进行 session 数据拷贝，但是这种方式效率有点低，Tomcat A、B、C 中任意一个的 session 发生了变化，都需要拷贝到其他 Tomcat 上，如果集群中的服务器数量特别多的话，这种方式不仅效率低，还会有很严重的延迟。
     * 3.所谓的粘滞会话就是将相同 IP 发送来的请求，通过 Nginx 路由到同一个 Tomcat 上去，这样就不用进行 session 共享与同步了。这是一个办法，但是在一些极端情况下，可能会导致负载失衡（因为大部分情况下，都是很多人用同一个公网 IP）。
     *
     *
     * session共享实现：
     * 首先 创建一个 Spring Boot 工程，引入 Web、Spring Session、Spring Security 以及 Redis:
     *
     * 配置完成后 ，就可以使用 Spring Session 了，其实就是使用普通的 HttpSession ，其他的 Session 同步到 Redis 等操作，框架已经自动帮你完成了：
     *
     */
    @Autowired
    FindByIndexNameSessionRepository sessionRepository;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .permitAll()
                .and()
                .csrf()
                .disable()
                .sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true)
                .sessionRegistry(sessionRegistry());
    }
    @Bean
    SpringSessionBackedSessionRegistry sessionRegistry() {
        return new SpringSessionBackedSessionRegistry(sessionRepository);
    }
}
