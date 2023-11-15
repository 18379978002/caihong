package com.caipiao.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.modules.sys.dao.DeptDao;
import com.caipiao.modules.sys.entity.DeptEntity;
import com.caipiao.modules.sys.entity.StaffDeptEntity;
import com.caipiao.modules.sys.entity.vo.TreeSelect;
import com.caipiao.modules.sys.service.IStaffDeptService;
import com.caipiao.modules.sys.service.ISysDeptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SysDeptServiceImpl extends
        ServiceImpl<DeptDao, DeptEntity> implements ISysDeptService {

    private final IStaffDeptService staffDeptService;

    /**
     * 构建前端所需要下拉树结构
     *
     * @param deptEntities 部门列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildDeptTreeSelect(List<DeptEntity> deptEntities) {
        List<DeptEntity> deptEntityTrees = buildDeptTree(deptEntities);
        return deptEntityTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 构建前端所需要树结构
     *
     * @param deptEntities 部门列表
     * @return 树结构列表
     */
    @Override
    public List<DeptEntity> buildDeptTree(List<DeptEntity> deptEntities) {
        List<DeptEntity> returnList = new ArrayList<>();
        List<String> tempList = new ArrayList<>();
        for (DeptEntity deptEntity : deptEntities) {
            tempList.add(deptEntity.getId());
        }
        for (Iterator<DeptEntity> iterator = deptEntities.iterator(); iterator.hasNext(); ) {
            DeptEntity deptEntity = iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(deptEntity.getParentId())) {
                recursionFn(deptEntities, deptEntity);
                returnList.add(deptEntity);
            }
        }
        if (returnList.isEmpty()) {
            returnList = deptEntities;
        }
        return returnList;
    }

    @Override
    public List<DeptEntity> queryUserDept(String dingUserId) {

        QueryWrapper<StaffDeptEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", dingUserId);

        List<StaffDeptEntity> list = staffDeptService.list(queryWrapper);

        if(!CollectionUtils.isEmpty(list)){
            return list.stream().map(staffDeptEntity -> this.getById(staffDeptEntity.getDeptId())).collect(Collectors.toList());
        }else {
            return
                    null;
        }



    }

    /**
     * 递归列表
     */
    private void recursionFn(List<DeptEntity> list, DeptEntity t) {
        // 得到子节点列表
        List<DeptEntity> childList = getChildList(list, t);
        t.setChildren(childList);
        for (DeptEntity tChild : childList) {
            if (hasChild(list, tChild)) {
                // 判断是否有子节点
                Iterator<DeptEntity> it = childList.iterator();
                while (it.hasNext()) {
                    DeptEntity n = it.next();
                    recursionFn(list, n);
                }
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<DeptEntity> getChildList(List<DeptEntity> list, DeptEntity t) {
        List<DeptEntity> tlist = new ArrayList<>();
        Iterator<DeptEntity> it = list.iterator();
        while (it.hasNext()) {
            DeptEntity n = it.next();
            if (com.caipiao.common.utils.StringUtils.isNotNull(n.getParentId()) && n.getParentId().equals(t.getId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<DeptEntity> list, DeptEntity t) {
        return getChildList(list, t).size() > 0 ? true : false;
    }
}
