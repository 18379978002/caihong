package com.caipiao.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName(value = "tb_pm_dept")
public class DeptEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.INPUT)
	private String id;

	private String parentId;

	private String name;

	private String state;

	private String companyId;

	@TableField(exist = false)
	private List<DeptEntity> children = new ArrayList<>();


}
