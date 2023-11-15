package com.caipiao.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.caipiao.common.utils.ShiroUtils;
import com.caipiao.config.batisplus.TenantLineInterceptor;
import com.caipiao.config.batisplus.TenantService;
import com.caipiao.config.batisplus.co.TenantContext;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusPropertiesCustomizer mybatisPlusPropertiesCustomizer() {
        return properties -> {
            GlobalConfig globalConfig = properties.getGlobalConfig();
            globalConfig.setBanner(false);
            MybatisConfiguration configuration = new MybatisConfiguration();
            configuration.setDefaultEnumTypeHandler(MybatisEnumTypeHandler.class);
            properties.setConfiguration(configuration);
        };
    }

    /**
     * 新多租户插件配置,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存万一出现问题
     */
    @Bean
    public MybatisPlusInterceptor paginationInterceptor(TenantLineInterceptor tenantLineInterceptor) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 设置租户处理器
        interceptor.addInnerInterceptor(tenantLineInterceptor);
        return interceptor;
    }

    @Bean
    public TenantLineInterceptor tenantLineInterceptor(TenantLineHandler tenantLineHandler){
        return new TenantLineInterceptor(tenantLineHandler);
    }

    @Resource
    private TenantService tenantService;

    @Bean
    public TenantLineHandler tenantLineHandle() {
        return new TenantLineHandler() {

            @Override
            public Expression getTenantId() {
                TenantContext tenantContext = tenantService.queryTenantContext();
                if (null == tenantContext) {
                    throw new RuntimeException("租户上下文为空");
                }
                return new StringValue(String.valueOf(tenantContext.getTenantId()));
            }

            @Override
            public String getTenantIdColumn() {
                // 对应数据库租户ID的列名
                return "tenant_id";
            }

            @Override
            public boolean ignoreTable(String tableName) {
                // 是否需要需要过滤某一张表
                List<String> tableNameList = Arrays.asList( "tb_sm_sysstaff", "user_info","tb_permutation_result",
                                                            "tb_lottery_result","tb_basketball_match",
                                                            "tb_football_match","tb_shop","match_result",
                                                            "tb_prize_level_result");

                if (tableNameList.contains(tableName)) {
                    return true;
                }
                if (tableName.contains("tb_")||(tableName.contains("user_"))){
                    return false;
                }
                return true;
            }
        };
    }
}
