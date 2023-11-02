package org.javaboy.stricthttpfirewall;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import java.util.Arrays;

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
     * 从 StrictHttpFirewall 的源码中看出来： 你的 HTTP 请求方法必须是 DELETE、GET、HEAD、OPTIONS、PATCH、POST 以及 PUT 中的一个，请求才能发送成功，否则的话，就会抛出 RequestRejectedException 异常。

     */
    @Bean
    HttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        //setUnsafeAllowAnyHttpMethod 方法表示不做 HTTP 请求方法校验，也就是什么方法都可以过。或者也可以通过 setAllowedHttpMethods 方法来重新定义可以通过的方法。
        firewall.setAllowUrlEncodedPeriod(true);
        //这四种路径中，都不能包含如下字符串：  "./", "/../" or "/."
        firewall.setAllowUrlEncodedPercent(true);//希望请求地址中可以出现 %
        firewall.setAllowUrlEncodedDoubleSlash(true);//希望请求地址中可以出现 //
        firewall.setAllowUrlEncodedSlash(true);// 去掉 % 和 反斜杠的限制
        firewall.setAllowUrlEncodedPeriod(true);//如果请求地址中存在 . 编码之后的字符 %2e、%2E，则请求将被拒绝   如需支持，按照如下方式进行配置

        return firewall;
    }

}
