package life.jiasheng.community.controller;

import life.jiasheng.community.dto.AccessTokenDTO;
import life.jiasheng.community.dto.GithubUser;
import life.jiasheng.community.mapper.UserMappper;
import life.jiasheng.community.model.User;
import life.jiasheng.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {

    //获取参数code，其中return "index"的意义就是登陆成功以后返回到主页面
    //返回到一个页面去，即/callback


    @Autowired//这个就是把spring容器的实例加进来
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired(required = false)
    private UserMappper userMappper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletResponse response) {
        //accessTokenDTO放进授权码等数据
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);

        //调用GithubProvider类中的getAccessToken方法带上accessTokenDTO中的上一步拿到的数据，再去拿令牌
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);//从github获取用户信息
        //把用户信息写进数据库，相当于写进去session了
        if (githubUser != null) {
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMappper.insert(user);
            response.addCookie(new Cookie("token", token));
            return "redirect:/";
        } else {
            //登陆失败,重新登陆
            return "redirect:/";
        }
    }
}
