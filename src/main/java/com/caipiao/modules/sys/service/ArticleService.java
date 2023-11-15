package com.caipiao.modules.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.modules.sys.constant.ArticleTypeEnum;
import com.caipiao.modules.sys.entity.Article;

import java.util.List;
import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2021/11/29 下午2:25
 */
public interface ArticleService extends IService<Article> {
    /**
     * 分页查询用户数据
     * @param params 查询参数
     * @return PageUtils 分页结果
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询文章详情，每次查询后增加点击次数
     *
     * @param id
     * @return
     */
    Article findById(Long id);

    /**
     * 添加或编辑文章,同名文章不可重复添加
     *
     * @param article
     */
    @Override
    boolean save(Article article);

    /**
     * 按条件分页查询
     *
     * @param title
     * @param page
     * @return
     */
    IPage<Article> getArticles(String title, int page);

    /**
     * 查看目录，不返回文章详情字段
     *
     * @param articleType
     * @param category
     * @return
     */
    List<Article> selectCategory(ArticleTypeEnum articleType, String category);

    /**
     * 文章查找，不返回文章详情字段
     *
     * @param articleType
     * @param category
     * @param keywords
     * @return
     */
    List<Article> search(ArticleTypeEnum articleType, String category, String keywords);

    void createHtml(Long id);

    void createJobListHtml();
}
