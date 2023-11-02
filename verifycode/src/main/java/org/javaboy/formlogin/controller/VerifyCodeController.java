package org.javaboy.formlogin.controller;

import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @作者 江南一点雨
 * @微信公众号 江南一点雨
 * @网站 http://www.itboyhub.com
 * @国际站 http://www.javaboy.org
 * @微信 a_java_boy
 * @GitHub https://github.com/lenve
 * @Gitee https://gitee.com/lenve
 */
@RestController
public class VerifyCodeController {
    @Autowired
    Producer producer;


    //接下来提供一个返回验证码图片的接口：
    //这里我们生成验证码图片，并将生成的验证码字符存入 HttpSession 中。注意这里我用到了 try-with-resources ，可以自动关闭流，有的小伙伴可能不太清楚，可以自己搜索看下。

    //测试：用postman先获取验证码，再调用login接口，传入用户名，验证码和密码。试试验证码输入错误时和输入正确时能否正常登录。
    @GetMapping("/vc.jpg")
    public void getVerifyCode(HttpServletResponse resp, HttpSession session) throws IOException {
        resp.setContentType("image/jpeg");
        String text = producer.createText();
        session.setAttribute("verify_code", text);
        BufferedImage image = producer.createImage(text);
        try(ServletOutputStream out = resp.getOutputStream()) {
            ImageIO.write(image, "jpg", out);
        }
    }
}
