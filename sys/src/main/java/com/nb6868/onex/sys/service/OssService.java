package com.nb6868.onex.sys.service;

import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.sys.dao.OssDao;
import com.nb6868.onex.sys.dto.OssDTO;
import com.nb6868.onex.sys.entity.OssEntity;
import org.springframework.stereotype.Service;

/**
 * 素材库
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class OssService extends DtoService<OssDao, OssEntity, OssDTO> {

    /**
     * 通过uuid获得数据
     */
    public OssEntity getByUuid(String uuid) {
        if (StrUtil.isNotBlank(uuid)) {
            return null;
        } else {
            return lambdaQuery().eq(OssEntity::getUuid, uuid).last(Const.LIMIT_ONE).one();
        }
    }

}
