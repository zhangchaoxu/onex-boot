package com.nb6868.onex.modules.tms.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nb6868.onex.booster.dao.BaseDao;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.modules.tms.entity.WaybillEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * TMS-运单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface WaybillDao extends BaseDao<WaybillEntity> {

    @Select("SELECT tms_waybill.*, (SELECT count(id) FROM tms_waybill_item WHERE tms_waybill_item.deleted = 0 and tms_waybill_item.waybill_id = tms_waybill.id) AS waybill_item_count " +
            "FROM tms_waybill " +
            "${ew.customSqlSegment}")
    @Override
    <E extends IPage<WaybillEntity>> E selectPage(@Param(Const.PAGE) E page, Wrapper<WaybillEntity> ew);

}
