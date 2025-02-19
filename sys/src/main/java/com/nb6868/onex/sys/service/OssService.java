package com.nb6868.onex.sys.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.pojo.FileUuidItem;
import com.nb6868.onex.common.pojo.UuidReq;
import com.nb6868.onex.sys.dao.OssDao;
import com.nb6868.onex.sys.dto.OssDTO;
import com.nb6868.onex.sys.entity.OssEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        if (StrUtil.isBlank(uuid)) {
            return null;
        } else {
            return lambdaQuery().eq(OssEntity::getUuid, uuid).last(Const.LIMIT_ONE).one();
        }
    }

    /**
     * 通过uuid列表获得uuid文件内容列表
     */
    public List<FileUuidItem> getUuidItemListByUuidList(List<UuidReq> uuidReqList, String type) {
        List<FileUuidItem> fileItems = new ArrayList<>();
        List<String> uuids = new ArrayList<>();
        CollUtil.emptyIfNull(uuidReqList).forEach(uuidReq -> {
            if (StrUtil.isNotBlank(uuidReq.getUuid())) {
                uuids.add(uuidReq.getUuid());
            }
        });
        if (CollUtil.isNotEmpty(uuids)) {
            lambdaQuery()
                    .in(OssEntity::getUuid, uuids)
                    .eq(StrUtil.isNotBlank(type), OssEntity::getType, type)
                    .list().forEach(ossEntity -> {
                        // 实体转换
                        FileUuidItem uuidItem = new FileUuidItem();
                        BeanUtil.copyProperties(ossEntity, uuidItem);
                        uuidItem.setName(ossEntity.getFilename());
                        fileItems.add(uuidItem);
                    });

        }
        return fileItems;
    }

}
