package com.caipiao.modules.sys.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.caipiao.modules.sys.entity.DeptEntity;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class TreeSelect implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 节点ID */
    private String id;

    /** 节点名称 */
    private String label;

    /** 子节点 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelect> children;

    public TreeSelect()
    {

    }

    public TreeSelect(DeptEntity deptEntity)
    {
        this.id = deptEntity.getId();
        this.label = deptEntity.getName();
        this.children = deptEntity.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }


    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public List<TreeSelect> getChildren()
    {
        return children;
    }

    public void setChildren(List<TreeSelect> children)
    {
        this.children = children;
    }
}
