package org.javaboy.formlogin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @作者 江南一点雨
 * @公众号 江南一点雨
 * @微信号 a_java_boy
 * @GitHub https://github.com/lenve
 * @博客 http://wangsong.blog.csdn.net
 * @网站 http://www.javaboy.org
 */
@RestController
public class HelloController {

    /**
     * /hello 是任何人都可以访问的接口
     * /admin/hello 是具有 admin 身份的人才能访问的接口
     * /user/hello 是具有 user 身份的人才能访问的接口
     * 所有 user 能够访问的资源，admin 都能够访问
     */
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/admin/hello")
    public String admin() {
        return "admin";
    }

    @GetMapping("/user/hello")
    public String user() {
        return "user";
    }
}
