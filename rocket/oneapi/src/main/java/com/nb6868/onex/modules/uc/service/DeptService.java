package com.nb6868.onex.modules.uc.service;

import com.nb6868.onex.booster.service.CrudService;
import com.nb6868.onex.modules.uc.dto.DeptDTO;
import com.nb6868.onex.modules.uc.dto.DeptTreeDTO;
import com.nb6868.onex.modules.uc.entity.DeptEntity;

import java.util.List;
import java.util.Map;

/**
 * 部门管理
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface DeptService extends CrudService<DeptEntity, DeptDTO> {

	/**
	 * 通过参数获取部门树
	 * @param params 参数
	 * @return 部门树
	 */
	List<DeptTreeDTO> treeList(Map<String, Object> params);

	/**
	 * 通过id获取父链
	 * @param id id
	 * @return 父链(包括自己)
	 */
	List<DeptDTO> getParentChain(Long id);

	/**
	 * 根据部门ID，获取本部门及子部门ID列表
	 * @param id   部门ID
	 * @return 子部门列表
	 */
	List<Long> getSubDeptIdList(Long id);
}
