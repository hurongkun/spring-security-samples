package org.javaboy.csrf1;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

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
     * csrf攻击流程案例：
     * 假设用户打开了招商银行网上银行网站，并且登录。
     * 登录成功后，网上银行会返回 Cookie 给前端，浏览器将 Cookie 保存下来。
     * 用户在没有登出网上银行的情况下，在浏览器里边打开了一个新的选项卡，然后又去访问了一个危险网站。
     * 这个危险网站上有一个超链接，超链接的地址指向了招商银行网上银行。
     * 用户点击了这个超链接，由于这个超链接会自动携带上浏览器中保存的 Cookie，所以用户不知不觉中就访问了网上银行，进而可能给自己造成了损失。
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .csrf()
                .disable();//把csrf这个关闭掉测试csrf效果
    }

    /**
     * 模拟csrf攻击
     * 我创建一个名为 csrf-1 的 Spring Boot 项目
     *假设 /transfer 是一个转账接口（这里是假设，主要是给大家演示 CSRF 攻击，真实的转账接口比这复杂）
     * 最后我们还需要配置一下 Spring Security，因为 Spring Security 中默认是可以自动防御 CSRF 攻击的，所以我们要把这个关闭掉：
     * 配置完成后，我们启动 csrf-1 项目。
     *
     * 接下来，我们再创建一个 csrf-2 项目，这个项目相当于是一个危险网站，为了方便，这里创建时我们只需要引入 web 依赖即可。
     * 项目创建成功后，首先修改项目端口：为8081。然后我们在 resources/static 目录下创建一个 hello.html 这里有一个超链接，超链接的文本是点击查看美女图片，当你点击了超链接之后，会自动请求 http://localhost:8080/transfer 接口，同时隐藏域还携带了两个参数。
     *
     *
     * 配置完成后，就可以启动 csrf-2 项目了。
     *
     * 接下来，用户首先访问 csrf-1 项目中的接口，在访问的时候需要登录，用户就执行了登录操作，访问完整后，用户并没有执行登出操作，然后用户访问 csrf-2 中的页面，看到了超链接，好奇这美女到底长啥样，一点击，结果钱就被人转走了。
     *
     * 此时如果把csrf-1项目的csrf开启。在csrf-2项目再访问图片，此时提示：There was an unexpected error (type=Forbidden, status=403).

     */

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
