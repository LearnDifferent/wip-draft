package com.github.learndifferent.mtm;

import com.github.learndifferent.mtm.dto.WebForSearchDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.query.WebFilter;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.service.WebsiteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class MtmApplicationTests {

    @Autowired
    private WebsiteService websiteService;

    @Autowired
    private UserService userService;

    @Test
    void webFilterTest() throws ParseException {
        WebFilter webFilter = new WebFilter();
        webFilter.setLoad(6);

        List<String> usernames = new ArrayList<>();
        usernames.add("145613221");
//        usernames.add("261262163126");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<Date> dates = new ArrayList<>();
//        dates.add(sdf.parse("2021-04-06"));
//        dates.add(sdf.parse("2021-03-31"));

//        webFilter.setIfOrderByTime(true);
//        webFilter.setDesc(true);

        webFilter.setUsernames(usernames);
        webFilter.setDates(dates);

        System.out.println(webFilter);

        List<WebsiteDTO> webs = websiteService.findWebsiteByFilter(webFilter);
        webs.forEach(w -> System.out.println(w.getCreateTime()));
    }

    @Test
    void printTest() {
        List<WebForSearchDTO> allWebForSearch = websiteService.getAllWebForSearch();
        allWebForSearch.forEach(System.out::println);
    }


    @Test
    void testProxy() {
        // 创建原始对象
//        UserService userService = new UserServiceImpl();

        // 通过 Cglib 的 Enhancer 实例来实现动态代理
        Enhancer enhancer = new Enhancer();

        // 随便借用一个 ClassLoader
        enhancer.setClassLoader(UserService.class.getClassLoader());
        // 设置父类为原始类
        enhancer.setSuperclass(userService.getClass());
        // 调用原方法，并设置额外功能/增强功能
        enhancer.setCallback(new MethodInterceptor() {
            // 通过 MethodInterceptor 的匿名类来实现额外功能
            @Override
            public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

                System.out.println("------在方法之前运行------");

                // 调用原始方法，并传入原始类的实例对象，以及原始方法的参数，获得原始方法的结果
                Object res = method.invoke(userService, args);

                System.out.println("------在方法之后运行------");

                return res;
            }
        });

        // 通过 Enhancer 来创建动态代理类
        UserService userServiceProxy = (UserService) enhancer.create();

        // 调用动态代理类的方法
        userServiceProxy.getUserByName("sally");
    }

}
