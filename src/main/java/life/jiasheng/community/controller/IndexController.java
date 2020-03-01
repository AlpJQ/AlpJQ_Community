package life.jiasheng.community.controller;

import life.jiasheng.community.mapper.UserMappper;
import life.jiasheng.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


@Controller
public class IndexController {

    @Autowired(required = false)
    private UserMappper userMappper;

    //功能：当用户访问网站的时候，先检查是否登陆过，即验证token是否存在服务器的数据库中
    @GetMapping("/")
    public String index(HttpServletRequest request){
        //request会得到很多的cookie，其中只有key为token的数据才是我们想要的
        //因为我们只有通过token来验证用户的登录过该网站信息
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if("token".equals(cookie.getName())){
                String token = cookie.getValue();
                User user = userMappper.finByToken(token);
                if (user != null) {
                    request.getSession().setAttribute("user", user);
                }
                break;
            }
        }
        return "index";
    }

}
