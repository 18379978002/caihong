package com.caipiao.modules.oss.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.modules.oss.dao.VideoCategoryDao;
import com.caipiao.modules.oss.entity.VideoCategory;
import com.caipiao.modules.oss.service.VideoCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VideoCategoryServiceImpl extends ServiceImpl<VideoCategoryDao, VideoCategory> implements VideoCategoryService {
}
