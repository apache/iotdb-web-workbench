package org.apache.iotdb.admin.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;

import java.sql.SQLException;
import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 启动后，可以在这里数据库监控的登录页面看到。http://localhost:8080/druid/login.html
 *
 *
 * @author fanli
 */

@Configuration
@ConfigurationProperties(prefix = "spring.datasource")//批量读取application.properties里的内容，必须要有set方法
public class DruidConfig {

    /**
     * 数据库url
     */
    private String url;
    /**
     * 数据库用户名
     */
    private String username;
    /**
     * 数据库登陆密码
     */
    private String password;
    /**
     * 最大的连接数量
     */
    private int maxActive;
    /**
     * 初始化大小
     */
    private int initialSize;
    /**
     * 设置超时的等待时间
     */
    private int maxWait;
    /**
     * 最小的连接数量
     */
    private int minIdle;
    /**
     * 监控统计拦截的filters，如果去掉后监控界面sql将无法统计
     */
    private String filters;


    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    /**
     * 注册 Servlet 组件
     *
     * @return 返回注册的Bean
     */
    @Bean
    public ServletRegistrationBean<StatViewServlet> statViewServlet() {
        ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        /*servletRegistrationBean.addInitParameter("allow", "192.168.1.3"); //白名单IP*/
        servletRegistrationBean.addInitParameter("loginUsername", "cisdigtial");
        servletRegistrationBean.addInitParameter("loginPassword", "cisdigtial");
        return servletRegistrationBean;
    }

    /**
     * 注册 Filter 组件
     *
     * @return Filter 组件
     */
    @Bean
    public FilterRegistrationBean<WebStatFilter> statFilter() {
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<>(new WebStatFilter());
        //验证所有请求
        filterRegistrationBean.addUrlPatterns("/*");
        //对 *.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/* 不进行验证
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

    /**
     * 配置数据源
     *
     * @return 数据源
     */
    @Bean
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
//        dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMaxActive(maxActive);
        dataSource.setInitialSize(initialSize);
        try {
            dataSource.setFilters(filters);
        } catch (SQLException e) {
            //do nothing
        }
        dataSource.setMaxWait(maxWait);
        dataSource.setMinIdle(minIdle);
        return dataSource;
    }
}