package com.nb6868.onexboot.api.modules.uc.service.impl;

import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.api.modules.uc.dao.DeptDao;
import com.nb6868.onexboot.api.modules.uc.dto.DeptDTO;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.ConvertUtils;
import com.nb6868.onexboot.common.util.ParamUtils;
import com.nb6868.onexboot.common.util.TreeUtils;
import com.nb6868.onexboot.api.modules.uc.dto.DeptTreeDTO;
import com.nb6868.onexboot.api.modules.uc.entity.DeptEntity;
import com.nb6868.onexboot.api.modules.uc.service.DeptService;
import com.nb6868.onexboot.api.modules.uc.service.UserService;
import com.nb6868.onexboot.api.modules.uc.user.SecurityUser;
import com.nb6868.onexboot.api.modules.uc.user.UserDetail;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

/**
 * 部门管理实现
 *
 * @author Charles(zhangchaoxu @ gmail.com)
 */
@Service
public class DeptServiceImpl extends CrudServiceImpl<DeptDao, DeptEntity, DeptDTO> implements DeptService {

    @Autowired
    private UserService userService;

    @SuppressWarnings("unchecked")
    @Override
    public QueryWrapper<DeptEntity> getWrapper(String method, Map<String, Object> params) {
        return new QueryWrapper<DeptEntity>()
                .eq("uc_dept.deleted", 0)
                .in(ParamUtils.isNotEmpty(params.get("deptIdList")), "uc_dept.id", (List<Long>) params.get("deptIdList"))
                .like(ParamUtils.isNotEmpty(params.get("name")), "uc_dept.name", params.get("name"))
                .like(ParamUtils.isNotEmpty(params.get("code")), "uc_dept.code", params.get("code"));
    }

    @Override
    public List<DeptTreeDTO> treeList(Map<String, Object> params) {
        List<DeptEntity> entityList = getBaseMapper().selectList(getWrapper("treeList", params));

        List<DeptTreeDTO> dtoList = ConvertUtils.sourceToTarget(entityList, DeptTreeDTO.class);

        return TreeUtils.build(dtoList);
    }

    @Override
    public List<DeptDTO> listDto(Map<String, Object> params) {
        // 普通管理员，只能查询所属部门及子部门的数据
        UserDetail user = SecurityUser.getUser();
        if (user.getType() > UcConst.UserTypeEnum.SYSADMIN.value()) {
            params.put("deptIdList", getSubDeptIdList(user.getDeptId()));
        }

        // 查询部门列表
        List<DeptEntity> entityList = getBaseMapper().selectList(getWrapper("list", params));

        return ConvertUtils.sourceToTarget(entityList, DeptDTO.class);
    }

    @Override
    public List<DeptDTO> getParentChain(@NotNull Long id) {
        List<DeptDTO> chain = new ArrayList<>();
        DeptDTO deptDTO = getDtoById(id);
        int loopCount = 0;
        while (deptDTO != null && loopCount < UcConst.DEPT_HIERARCHY_MAX) {
            chain.add(deptDTO);
            deptDTO = getDtoById(deptDTO.getPid());
            loopCount++;
        }
        // 倒序
        Collections.reverse(chain);
        return chain;
    }

    @Override
    public DeptDTO getDtoById(Serializable id) {
        //超级管理员，部门ID为null
        if (id == null || 0 == (Long) id) {
            return null;
        }

        return super.getDtoById(id);
    }

    @Override
    protected void beforeSaveOrUpdateDto(DeptDTO dto, int type) {
        if (type == 1) {
            // 更新下面所有的父类
            if (dto.getId().equals(dto.getPid())) {
                throw new OnexException(ErrorCode.SUPERIOR_DEPT_ERROR);
            }

            //上级部门不能为下级部门
            List<Long> subDeptList = getSubDeptIdList(dto.getId());
            if (subDeptList.contains(dto.getPid())) {
                throw new OnexException(ErrorCode.SUPERIOR_DEPT_ERROR);
            }
        }
        dto.setPids(getPidList(dto.getPid()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean logicDeleteById(Serializable id) {
        // todo 判断是否有子部门
        /*if (getSubDeptIdList(ids[0]).size() > 1) {
            throw new OnexException(ErrorCode.DEPT_SUB_DELETE_ERROR);
        }

        // 判断部门下面是否有用户
        if (sysUserService.getCountByDeptId(id) {
            throw new OnexException(ErrorCode.DEPT_USER_DELETE_ERROR);
        }

        // 删除
        return super.deleteByIds(Arrays.asList(ids));*/
        // 判断部门下面是否有用户
        // 删除
        return super.logicDeleteById(id);
    }

    @Override
    public List<Long> getSubDeptIdList(Long id) {
        List<Long> deptIdList = getBaseMapper().getSubDeptIdList("%" + id + "%");
        deptIdList.add(id);

        return deptIdList;
    }

    /**
     * 获取所有上级部门ID
     *
     * @param pid 上级ID
     */
    private String getPidList(Long pid) {
        //顶级部门，无上级部门
        if (Const.DEPT_ROOT.equals(pid)) {
            return Const.DEPT_ROOT + "";
        }

        //所有部门的id、pid列表
        List<DeptEntity> deptList = getBaseMapper().getIdAndPidList();

        //list转map
        Map<Long, DeptEntity> map = new HashMap<>(deptList.size());
        for (DeptEntity entity : deptList) {
            map.put(entity.getId(), entity);
        }

        //递归查询所有上级部门ID列表
        List<Long> pidList = new ArrayList<>();
        getPidTree(pid, map, pidList);

        return StringUtils.join(pidList, ",");
    }

    private void getPidTree(Long pid, Map<Long, DeptEntity> map, List<Long> pidList) {
        // 顶级部门，无上级部门
        if (Const.DEPT_ROOT.equals(pid)) {
            return;
        }

        // 上级部门存在
        DeptEntity parent = map.get(pid);
        if (parent != null) {
            getPidTree(parent.getPid(), map, pidList);
        }

        pidList.add(pid);
    }
}
