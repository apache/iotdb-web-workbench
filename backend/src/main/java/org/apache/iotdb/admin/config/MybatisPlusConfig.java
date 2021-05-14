package org.apache.iotdb.admin.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 该类是Mybatis-Plus的配置类，mybatis以插件的形式来实现特定功能，目前支持的插件有：
 *
 * 分页插件: PaginationInnerInterceptor
 * 多租户插件: TenantLineInnerInterceptor
 * 动态表名插件: DynamicTableNameInnerInterceptor
 * 乐观锁插件: OptimisticLockerInnerInterceptor
 * sql性能规范插件: IllegalSQLInnerInterceptor
 * 防止全表更新与删除插件: BlockAttackInnerInterceptor
 *
 * @author 辰行
 * @date 2020/10/09
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 分页插件配置，对相关数据库进行拦截并分页
     *
     * @return 分页拦截器
     */
    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 使用不同的数据库，可以更改DbType.MYSQL的类型
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 每页最多100条，可以更改，但不建议太大
        paginationInnerInterceptor.setMaxLimit(100L);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }


}
