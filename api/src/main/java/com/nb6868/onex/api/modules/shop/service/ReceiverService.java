package com.nb6868.onex.api.modules.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.uc.entity.UserEntity;
import com.nb6868.onex.api.modules.uc.service.UserService;
import com.nb6868.onex.api.modules.uc.user.SecurityUser;
import com.nb6868.onex.api.modules.uc.user.UserDetail;
import com.nb6868.onex.api.modules.shop.dao.ReceiverDao;
import com.nb6868.onex.api.modules.shop.dto.AppReceiverRequest;
import com.nb6868.onex.api.modules.shop.dto.ReceiverDTO;
import com.nb6868.onex.api.modules.shop.entity.ReceiverEntity;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;

/**
 * 收件地址
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ReceiverService extends DtoService<ReceiverDao, ReceiverEntity, ReceiverDTO> {

    @Autowired
    UserService userService;

    @Override
    public QueryWrapper<ReceiverEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<ReceiverEntity>(new QueryWrapper<>(), params)
                .eq("defaultItem", "default_item")
                .eq("userId", "user_id")
                .eq("mobile", "mobile")
                .like("address", "address")
                .like("consignee", "consignee")
                .eq("tenantId", "tenant_id")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    @Override
    protected void beforeSaveOrUpdateDto(ReceiverDTO dto, int type) {
        AssertUtils.isEmpty(userService.getById(dto.getUserId()), ErrorCode.ACCOUNT_NOT_EXIST);
    }

    @Override
    protected void afterSaveOrUpdateDto(boolean ret, ReceiverDTO dto, ReceiverEntity existedEntity, int type) {
        if (dto.getDefaultItem() == 1) {
            // 为默认地址,将其它地址设置为非默认
            update().eq("user_id", dto.getUserId()).set("default_item", 0).ne("id", dto.getId()).update(new ReceiverEntity());
        } else {
            // 为非默认,查一下用户有没有默认地址
            if (!query().eq("user_id", dto.getUserId()).eq("default_item", 1).exists()) {
                // 没有默认地址
                // 将该会员最新一条地址设置为非默认
                update().set("default_item", 1)
                        .eq("user_id", dto.getUserId())
                        .orderByDesc("create_time")
                        .last(Const.LIMIT_ONE)
                        .update(new ReceiverEntity());
            }
        }
    }

    @Override
    public ReceiverDTO getDtoById(Serializable id) {
        ReceiverDTO dto = super.getDtoById(id);
        AssertUtils.isEmpty(dto, ErrorCode.RECORD_NOT_EXISTED);

        UserEntity user = userService.getById(dto.getUserId());
        AssertUtils.isEmpty(user, ErrorCode.ACCOUNT_NOT_EXIST);
        dto.setUserName(user.getUsername());
        return dto;
    }

    @Override
    public boolean logicDeleteById(Serializable id) {
        // 获取地址
        ReceiverEntity entity = getById(id);
        AssertUtils.isEmpty(entity, ErrorCode.DB_RECORD_NOT_EXISTED);
        if (entity.getDefaultItem() == Const.BooleanEnum.TRUE.value()) {
            // 删除了一个默认项,需要将另外一个设置为默认项
            // 将该会员最新一条地址设置为非默认
            update().set("default_item", 1)
                    .eq("user_id", entity.getUserId())
                    .ne("id", id)
                    .orderByDesc("create_time")
                    .last(Const.LIMIT_ONE)
                    .update(new ReceiverEntity());
        }
        return super.logicDeleteById(id);
    }

    /**
     * 将地址设置为默认项
     *
     * @param id 地址id
     * @return 结果
     */
    public boolean setDefaultItem(Long id) {
        // 获取地址
        ReceiverEntity entity = getById(id);
        AssertUtils.isEmpty(entity, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 将该会员其它地址设置为非默认
        update().eq("user_id", entity.getUserId()).set("default_item", 0).ne("id", id).update(new ReceiverEntity());
        // 将该地址设置为默认项
        update().eq("id", id).set("default_item", 1).update(new ReceiverEntity());
        return true;
    }

    /**
     * 保存收货地址信息
     *
     * @return 结果
     */
    public boolean saveAppReceiver(AppReceiverRequest request) {
        UserDetail user = SecurityUser.getUser();

        ReceiverDTO receiverDTO = new ReceiverDTO();
        receiverDTO.setUserId(user.getId());
        receiverDTO.setUserName(user.getUsername());
        receiverDTO.setRegionName(request.getProvince() + "," + request.getCity() + "," + request.getCounty());
        receiverDTO.setTag("用户自定义");
        receiverDTO.setAddress(request.getAddressDetail());
        receiverDTO.setLat(0d);
        receiverDTO.setLng(0d);
        receiverDTO.setConsignee(request.getName());
        receiverDTO.setZipCode(request.getAreaCode());
        receiverDTO.setMobile(request.getTel());
        receiverDTO.setDefaultItem(request.getDefaultItem());
        receiverDTO.setRegionCode("0");
        saveDto(receiverDTO);

        return true;
    }

    /**
     * 更新收货地址信息
     *
     * @return 结果
     */
    public boolean updateAppReceiver(AppReceiverRequest request) {
        ReceiverDTO receiverDTO = getDtoById(request.getId());
        receiverDTO.setRegionName(request.getProvince() + "," + request.getCity() + "," + request.getCounty());
        receiverDTO.setTag("用户自定义");
        receiverDTO.setAddress(request.getAddressDetail());
        receiverDTO.setLat(0d);
        receiverDTO.setLng(0d);
        receiverDTO.setConsignee(request.getName());
        receiverDTO.setZipCode(request.getAreaCode());
        receiverDTO.setMobile(request.getTel());
        receiverDTO.setDefaultItem(request.getDefaultItem());
        receiverDTO.setRegionCode("0");
        updateDto(receiverDTO);

        return true;
    }

}
