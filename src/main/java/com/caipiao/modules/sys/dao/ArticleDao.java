package com.caipiao.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caipiao.modules.sys.entity.Article;
import com.caipiao.modules.sys.entity.dto.ArticleDTO;
import com.caipiao.modules.sys.entity.req.ArticleReq;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author xiaoyinandan
 * @date 2021/11/29 下午2:19
 */
@Repository
public interface ArticleDao extends BaseMapper<Article> {
    Page<ArticleDTO> queryPage(IPage<ArticleDTO> page, @Param("req") ArticleReq req);

    ArticleDTO selectByPrimaryKey(@Param("id") Long id);

    Article selectPrevious(@Param("id")Long id,
                           @Param("category")Long category);

    Article selectNext(@Param("id")Long id,
                       @Param("category")Long category);
}
