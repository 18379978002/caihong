package com.caipiao.modules.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.R;
import com.caipiao.common.utils.ShiroUtils;
import com.caipiao.common.utils.StringUtils;
import com.caipiao.config.TaskExcutor;
import com.caipiao.modules.sys.entity.Article;
import com.caipiao.modules.sys.service.ArticleService;
import com.caipiao.modules.sys.service.RichTextService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2021/11/29 下午2:33
 */
@RestController
@RequestMapping("/manage/article")
@Api(tags = "文章管理")
public class ArticleManageController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private RichTextService richTextService;
    /**
     * 查看文章详情
     *
     * @param articleId
     * @return
     */
    @GetMapping("/detail")
    @ApiOperation("详情")
    public R getArticle(Long articleId) {
        Article article = articleService.findById(articleId);
        return R.ok().put(article);
    }

    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = articleService.queryPage(params);
        return R.ok().put("page", page);
    }

    @GetMapping("/generateHtml")
    public R generateHtml() {
        TaskExcutor.submit(()->articleService.createJobListHtml());
        return R.ok();
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {

        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getCompanyId, ShiroUtils.getUserEntity().getCompanyId())
                .eq(Article::getId, id);

        Article article = articleService.getOne(wrapper);
        return R.ok().put("article", article);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody Article article) throws Exception {

        if(StringUtils.isNotBlank(article.getContent())){
            String resume = richTextService.transferRichText(article.getContent());
            article.setContent(resume);
        }

        articleService.save(article);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getCompanyId, ShiroUtils.getUserEntity().getCompanyId())
                .in(Article::getId, Arrays.asList(ids));
        articleService.remove(wrapper);
        return R.ok();
    }

}

