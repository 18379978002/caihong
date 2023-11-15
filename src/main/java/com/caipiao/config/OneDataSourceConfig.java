package com.caipiao.config;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @author xiaoyinandan
 * @date 2020-09-10
 */
//@Configuration
//@MapperScan(basePackages = {"com.caipiao.modules.*.dao"}, sqlSessionFactoryRef = "oneSqlSessionFactory")
public class OneDataSourceConfig {

    private Db1HikariConfig db1HikariConfig;

    public OneDataSourceConfig(Db1HikariConfig db1HikariConfig){
        this.db1HikariConfig = db1HikariConfig;
    }

    @Primary
    @Bean(name = "oneDataSource")
    @ConfigurationProperties("spring.datasource")
    public DataSource db1() {
        DataSource dataSource = DataSourceBuilder.create().build();
        HikariDataSource hikariDataSource = null;
        if (dataSource instanceof HikariDataSource) {
            // 连接池配置
            hikariDataSource = (HikariDataSource) dataSource;
            hikariDataSource.setPoolName(db1HikariConfig.getPoolName());
            hikariDataSource.setAutoCommit(db1HikariConfig.isAutoCommit());
            hikariDataSource.setConnectionTestQuery(db1HikariConfig.getConnectionTestQuery());
            hikariDataSource.setIdleTimeout(db1HikariConfig.getIdleTimeout());
            hikariDataSource.setConnectionTimeout(db1HikariConfig.getConnectionTimeout());
            hikariDataSource.setMaximumPoolSize(db1HikariConfig.getMaximumPoolSize());
            hikariDataSource.setMaxLifetime(db1HikariConfig.getMaxLifetime());
            hikariDataSource.setMinimumIdle(db1HikariConfig.getMinimumIdle());
        }
        return hikariDataSource == null ? dataSource : hikariDataSource;
    }

    @Primary
    @Bean(name = "oneSqlSessionFactory")
    public SqlSessionFactory mysqlSqlSessionFactory(@Qualifier("oneDataSource") DataSource dataSource) throws Exception {
        // MyBatis-Plus使用MybatisSqlSessionFactoryBean  MyBatis直接使用SqlSessionFactoryBean
        MybatisSqlSessionFactoryBean mybatisSqlSessionFactory = new MybatisSqlSessionFactoryBean();

        // 指明mapper.xml位置(配置文件中指明的xml位置会失效用此方式代替，具体原因未知)
        mybatisSqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*/*Dao.xml"));
        // 指明实体扫描(多个package用逗号或者分号分隔)
        mybatisSqlSessionFactory.setTypeAliasesPackage("com.caipiao.modules.*.entity");
        mybatisSqlSessionFactory.setTypeEnumsPackage("com.caipiao.modules.*.entity.enmu");
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        mybatisConfiguration.setJdbcTypeForNull(JdbcType.NULL);
        //驼峰
        mybatisConfiguration.setMapUnderscoreToCamelCase(true);
        //是否开启缓存
        mybatisConfiguration.setCacheEnabled(true);
        //多数据源下分页模式
        mybatisSqlSessionFactory.setConfiguration(mybatisConfiguration);
        mybatisSqlSessionFactory.setDataSource(dataSource);
        return mybatisSqlSessionFactory.getObject();
    }

    @Primary
    @Bean(name = "oneTransactionManager")
    public DataSourceTransactionManager mysqlTransactionManager(@Qualifier("oneDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

//    @Primary
//    @Bean(name = "oneSqlSessionTemplate")
//    public SqlSessionTemplate mysqlSqlSessionTemplate(@Qualifier("oneSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }


}
