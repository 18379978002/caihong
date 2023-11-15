package com.caipiao.config.batisplus;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import javax.annotation.Resource;
import java.sql.SQLException;

public class TenantLineInterceptor extends TenantLineInnerInterceptor {

    @Resource
    private TenantService tenantService;
    public TenantLineInterceptor(TenantLineHandler tenantLineHandler){
        super(tenantLineHandler);
    }


    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        if (!tenantService.isQueryTenant()){
            return;
        }
        super.beforeQuery(executor, ms, parameter, rowBounds, resultHandler, boundSql);
    }


}
