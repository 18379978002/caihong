package com.caipiao.modules.sys.controller;

import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.R;
import com.caipiao.modules.sys.entity.Article;
import com.caipiao.modules.sys.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2021/12/3 下午4:35
 */
@RestController
@RequestMapping("/cms/article")
@Slf4j
@RequiredArgsConstructor
public class CmsArticleController {
    private final ArticleService articleService;

    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = articleService.queryPage(params);
        return R.ok().put("page", page);
    }

    @GetMapping("{id}")
    public R get(@PathVariable Long id){
        Article article = articleService.getById(id);
        article.setOpenCount(article.getOpenCount() + 1);
        articleService.updateById(article);
        return R.ok().put(article);
    }


}
