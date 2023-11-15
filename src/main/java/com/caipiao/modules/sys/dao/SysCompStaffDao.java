package com.caipiao.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import com.caipiao.modules.sys.entity.req.StaffReq;
import com.caipiao.modules.sys.entity.vo.SysCompStaffVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface SysCompStaffDao extends BaseMapper<SysCompStaffEntity> {

    IPage<SysCompStaffVO> queryPage(IPage<SysCompStaffEntity> page1, @Param("req") StaffReq req);

    SysCompStaffVO getByStaffId(@Param("staffId") String staffId);

    String loadStaffIdByName(@Param("staffName") String staffName);

    String getNextId();

    SysCompStaffEntity selectByUserId(String userId);
}
