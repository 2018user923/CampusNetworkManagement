package com.example.demo.Config;

import com.example.demo.domain.User;
import com.example.demo.service.HttpService;
import com.example.demo.util.EncryptionKey;
import com.example.demo.util.RedisUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    @Value("${user.time.pattern}")
    private String pattern;

    /*redis缓存*/
    @Resource
    private RedisUtil cache;

    @Resource
    private HttpService httpService;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/blank").setViewName("blank");
        registry.addViewController("/main").setViewName("main");
        registry.addViewController("/tab").setViewName("tab");
        registry.addViewController("/ui").setViewName("ui");
        registry.addViewController("/h48ua9700").setViewName("h48ua9700");
    }

    @Bean("random")
    public Random CreateRandom() {
        return new Random();
    }

    @Bean
    public DateTimeFormatter CreateDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }


    @Bean(name = "template")
    public RedisTemplate<String, Object> template(RedisConnectionFactory factory) {
        // 创建RedisTemplate<String, Object>对象
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(factory);
        // 定义Jackson2JsonRedisSerializer序列化对象
        Jackson2JsonRedisSerializer<Object> jacksonSeial = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会报异常
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSeial.setObjectMapper(om);
        StringRedisSerializer stringSerial = new StringRedisSerializer();
        // redis key 序列化方式使用stringSerial
        template.setKeySerializer(stringSerial);
        // redis value 序列化方式使用jackson
        template.setValueSerializer(jacksonSeial);
        // redis hash key 序列化方式使用stringSerial
        template.setHashKeySerializer(stringSerial);
        // redis hash value 序列化方式使用jackson
        template.setHashValueSerializer(jacksonSeial);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String ipAddress = httpService.getIpAddress(request);
                User user = (User) cache.hget(EncryptionKey.userLoginInfo, ipAddress);
                //缓存中没有 user 对象，说明该 ip 地址不存在任何登录.
                if (user == null) {
                    request.getRequestDispatcher("/index").forward(request, response);
                    return false;
                } else {
                    HttpSession session = request.getSession();
                    if (session.getAttribute("user") == null) {
                        session.setAttribute("user", user);
                    }
                    return true;
                }
            }
        }).addPathPatterns("/**").excludePathPatterns(
                "/index",
                "/h48ua9700",
                "/index/**",
                "/assets/**",
                "/login",
                "/register",
                "/register/**",
                "/login/**",
                "/vueResources/**",
                "/test/**"
        );
    }
}