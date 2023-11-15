package com.caipiao.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.ShiroUtils;
import com.caipiao.modules.sys.constant.ArticleTypeEnum;
import com.caipiao.modules.sys.dao.ArticleDao;
import com.caipiao.modules.sys.dao.CmsCategoryDao;
import com.caipiao.modules.sys.entity.Article;
import com.caipiao.modules.sys.entity.dto.ArticleDTO;
import com.caipiao.modules.sys.entity.req.ArticleReq;
import com.caipiao.modules.sys.service.ArticleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2021/11/29 下午2:26
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleDao, Article> implements ArticleService {
    private static final String ID_PLACEHOLDER = "${articleId}";
    private static final String CATEGORY_PLACEHOLDER = "${categoryCode}";
    private static final String SUB_CATEGORY_PLACEHOLDER = "${subCmsCategoryCode}";

    /**
     * 查询文章列表时返回的字段（过滤掉详情字段以加快速度）
     */
    private static final String LIST_FILEDS = "id,summary,image,sub_category,update_time,title,tags,create_time,target_link,open_count,category,company_id,is_banner,create_staff,create_staff_name,avatar,out_link,order_number";
    @Autowired
    ArticleDao articleMapper;
    @Autowired
    CmsCategoryDao categoryMapper;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String title = (String) params.get("title");
        String categoryCode = (String) params.get("categoryId");
        String categoryName = (String) params.get("categoryName");
        String subCategoryCode = (String) params.get("subCategoryId");
        String belongApp = (String) params.get("belongApp");
        String companyId;
        if(ShiroUtils.getUserEntity() == null){
            companyId = (String) params.get("companyId");
        }else{
            companyId = ShiroUtils.getUserEntity().getCompanyId();
        }

        ArticleReq req = ArticleReq.builder()
                .categoryName(categoryName)
                .isBanner((String) params.get("isBanner"))
                .companyId(companyId)
                .categoryCode(categoryCode)
                .subCategoryCode(subCategoryCode)
                .title(title)
                .belongApp(belongApp)
                .build();
        IPage<ArticleDTO> page = new Page<>(Integer.parseInt((String) params.get("page")), Integer.parseInt((String) params.get("limit")));
        IPage<ArticleDTO> vos = articleMapper.queryPage(page, req);
        return new PageUtils(vos);
    }

    /**
     * 查询文章详情，每次查询后增加点击次数
     *
     * @param id
     * @return
     */
    @Override
    public Article findById(Long id) {
        if (id <= 0) {
            return null;
        }
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getCompanyId, ShiroUtils.getUserEntity().getCompanyId())
                .eq(Article::getId, id);
        Article article = articleMapper.selectOne(wrapper);

        return article;
    }


    /**
     * 添加或编辑文章,同名文章不可重复添加
     *
     * @param article
     */

    @Override
    @Transactional
    public boolean save(Article article) {
        article.setUpdateTime(new Date());
        if (article.getId() != null) {
            LambdaUpdateWrapper<Article> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(Article::getCompanyId, ShiroUtils.getUserEntity().getCompanyId())
                    .eq(Article::getId, article.getId());

            articleMapper.update(article, wrapper);
        } else {
            article.setCreateStaff(ShiroUtils.getUserId());
            article.setCreateStaffName(ShiroUtils.getUserEntity().getStaffName());
            article.setAvatar(ShiroUtils.getUserEntity().getAvatar());
            String title = article.getTitle();
            Long count = articleMapper.selectCount(
                    new QueryWrapper<Article>().eq("title", title)
                            .eq("category", article.getCategory())
                            .eq("company_id", ShiroUtils.getUserEntity().getCompanyId())
                            .eq(null != article.getSubCategory(), "sub_category", article.getSubCategory())
            );
            if (count > 0) {
                throw new RRException("同目录下文章[" + title + "]已存在，不可重复添加");
            }
            article.setCompanyId(ShiroUtils.getUserEntity().getCompanyId());
            article.setCreateTime(new Date());
            articleMapper.insert(article);
        }

        return true;
    }

    /**
     * 按条件分页查询
     *
     * @param title
     * @param page
     * @return
     */
    @Override
    public IPage<Article> getArticles(String title, int page) {
        return this.page(new Page<Article>(page, 10),
                new QueryWrapper<Article>().like(!StringUtils.isEmpty("title"), "title", title)
                        .eq("company_id", ShiroUtils.getUserEntity().getCompanyId())
                        .select(LIST_FILEDS)
                        .orderBy(true, false, "update_time"));
    }

    /**
     * 查看目录，不返回文章详情字段
     *
     * @param articleType
     * @param category
     * @return
     */
    @Override
    public List<Article> selectCategory(ArticleTypeEnum articleType, String category) {
        return this.list(new QueryWrapper<Article>()
                .select(LIST_FILEDS)
                .eq("company_id", ShiroUtils.getUserEntity().getCompanyId())
                .eq("type", articleType.getValue())
                .eq("category", category));
    }

    /**
     * 文章查找，不返回文章详情字段
     *
     * @param articleType
     * @param category
     * @param keywords
     * @return
     */
    @Override
    public List<Article> search(ArticleTypeEnum articleType, String category, String keywords) {
        return this.list(new QueryWrapper<Article>()
                .select(LIST_FILEDS)
                .eq("company_id", ShiroUtils.getUserEntity().getCompanyId())
                .eq("type", articleType.getValue())
                .eq(!StringUtils.isEmpty(category), "category", category)
                .and(i -> i.like("summary", keywords).or().like("title", keywords)));
    }

    /**
     * 创建html页面
     *
     * @param id
     * @throws Exception
     */
    public void createHtml(Long id) {


    }


    /**
     * 创建html页面
     *
     * @throws Exception
     */
    public void createJobListHtml() {

    }




}
