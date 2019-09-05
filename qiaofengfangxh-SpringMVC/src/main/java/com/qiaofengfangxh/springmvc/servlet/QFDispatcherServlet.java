package com.qiaofengfangxh.springmvc.servlet;


import com.qiaofengfangxh.springmvc.annotation.QFController;
import com.qiaofengfangxh.springmvc.annotation.QFService;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实现属于自己的SpringMVC框架
 * @author 乔峰fangxh
 * @date 2019-09-03
 * @version 1.0
 */
public class QFDispatcherServlet extends javax.servlet.http.HttpServlet {

    /**
     * 全局配置文件
     */
    private final static Properties contextConfig = new Properties();


    /**
     * 保存扫描到的所有类名称
     */
    private final static List<String> classNames = new ArrayList<>();

    /**
     * ioc容器
     */
    private final static ConcurrentHashMap<String, Object> ioc = new ConcurrentHashMap<>();


    /**
     * 初始化阶段
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        //第1步：加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        //第2步：扫描相关的类
        doScanner(contextConfig.getProperty("scanPackage"));

        //第3步：初始化扫描到的类，并将它们放入IOC容器中
        doInstance();

        //第4步：完成依赖注入
        doAutowired();

        //第5步：初始化HandlerMapping
        initHandlerMapping();

        System.out.println("qiaofengfangxh SpringMVC init end！");


    }

    private void initHandlerMapping() {
    }

    private void doAutowired() {
    }

    private void doInstance() {
        //初始化，为DI做准备
        if (classNames.isEmpty()) return;

        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                //什么样的类才需要初始化？
                //加了我们定义的注解的类才需要初始化，怎么判断呢？
                //为了简化代码逻辑，主要体会设计思想，只举例@Controller 和 @Service这两个注解
                if (clazz.isAnnotationPresent(QFController.class)) {
                    try {
                        Object instance = clazz.newInstance();
                        //spring默认类名首字母小写的
                        String beanName = toLowerFirstCase(clazz.getSimpleName());
                        //保存到ioc容器中
                        ioc.put(beanName,instance);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else if (clazz.isAnnotationPresent(QFService.class)) {
                    //1.自定义的beanName
                    QFService service = clazz.getAnnotation(QFService.class);
                    String beanName = service.value();
                    //2.默认类名首字母小写
                    if (StringUtils.isBlank(beanName)) {
                        beanName = toLowerFirstCase(clazz.getSimpleName());
                    }
                    Object instance = clazz.newInstance();
                    ioc.put(beanName,instance);
                    //3.根据类型自动赋值,投机取巧的方式
                    for (Class<?> i : clazz.getInterfaces()) {
                        if (ioc.containsKey(i.getName())) {
                            throw new Exception("The <<" + i.getName() + ">> is Exists！");
                        }
                        ioc.put(i.getName(), instance);
                    }
                } else {
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 首字母转为小写
     * @param simpleName
     * <p>
     *     如果类名本身是小写字母就会出问题，但是我要声明的是
     *     这个方法是我自己用的，private的，传值也是自己传，
     *     默认传人的值首字母大写的，
     *     为了简化代码逻辑就不做其他判断了
     * </p>
     * @return
     */
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        //之所以加，是因为大小写字母的ASCII码值相差32
        //而且大写字母的ASCII码要小于小写字母的ASCII码
        //在java中，对char做算法运算实际是对ASCII码做算法运算
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doScanner(String scanPackage) {
        //scanPackage=com.qiaofengfangxh.springmvc.demo 存的是包路径,我们要转换成文件路径
        String filePath = scanPackage.replaceAll("\\.","/");
        URL url = this.getClass().getClassLoader().getResource(filePath);
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage+ "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) continue;
                String className = (scanPackage + "." + file.getName().replaceAll(".class",""));
                classNames.add(className);
            }
        }

    }

    private void doLoadConfig(String contextConfigLocation) {
        //直接从类路径下找到spring主配置文件所在的路径
        //并且读取出来放到Properties对象中
        //相当于把scanPackage=com.qiaofengfangxh.springmvc.demo从文件中保存到内存中
        //classpath*:config/application.properties要处理下拿到config/application.properties
        String name = contextConfigLocation.split(":") [1];
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(name);
        try {
            contextConfig.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //运行阶段
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //第6步：调用，运行阶段
        doDispatch(req, resp);
    }

    /**
     * 这里是运行阶段，处理客户端发送的请求
     * @param req
     * @param resp
     */
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
    }

    @Override
    public void destroy() {
        super.destroy();
    }


}
