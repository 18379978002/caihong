

package com.caipiao.modules.oss.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caipiao.modules.oss.entity.MinioEntity;
import org.springframework.stereotype.Repository;

/**
 * 文件上传
 * @author yujun
 */
@Repository
public interface MinioFileDao extends BaseMapper<MinioEntity> {

}
