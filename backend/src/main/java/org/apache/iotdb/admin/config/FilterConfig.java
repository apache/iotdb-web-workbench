package org.apache.iotdb.admin.config;

import org.apache.iotdb.admin.filter.TokenFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;


/**
 * @anthor fyx 2021/5/24
 */
@Component
public class FilterConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(new TokenFilter());
        List<String> paths = new ArrayList();
        paths.add("/servers/**");
        interceptorRegistration.addPathPatterns(paths);
    }

}
