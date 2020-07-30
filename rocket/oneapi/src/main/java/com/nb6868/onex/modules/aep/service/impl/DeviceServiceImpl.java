package com.nb6868.onex.modules.aep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.ctg.ag.sdk.biz.AepDeviceCommandClient;
import com.ctg.ag.sdk.biz.AepDeviceManagementClient;
import com.ctg.ag.sdk.biz.aep_device_command.CreateCommandRequest;
import com.ctg.ag.sdk.biz.aep_device_command.CreateCommandResponse;
import com.ctg.ag.sdk.biz.aep_device_management.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.exception.OnexException;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.ConvertUtils;
import com.nb6868.onex.booster.util.JacksonUtils;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.modules.aep.dao.DeviceDao;
import com.nb6868.onex.modules.aep.dto.DeviceDTO;
import com.nb6868.onex.modules.aep.entity.DeviceEntity;
import com.nb6868.onex.modules.aep.entity.EnterpriseEntity;
import com.nb6868.onex.modules.aep.entity.ProductEntity;
import com.nb6868.onex.modules.aep.sdk.DeviceResponse;
import com.nb6868.onex.modules.aep.sdk.HttpResult;
import com.nb6868.onex.modules.aep.sdk.PageResult;
import com.nb6868.onex.modules.aep.service.DeviceService;
import com.nb6868.onex.modules.aep.service.EnterpriseService;
import com.nb6868.onex.modules.aep.service.ProductService;
import com.nb6868.onex.modules.sys.service.ParamService;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * AEP-设备
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class DeviceServiceImpl extends CrudServiceImpl<DeviceDao, DeviceEntity, DeviceDTO> implements DeviceService {

    @Autowired
    ParamService paramService;
    @Autowired
    ProductService productService;
    @Autowired
    EnterpriseService enterpriseService;

    @Override
    public QueryWrapper<DeviceEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<DeviceEntity>(new QueryWrapper<>(), params)
                .eq("deviceId", "device_id")
                .like("deviceName", "device_name")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    @Override
    public DeviceEntity getByDeviceId(String deviceId) {
        return query().eq("device_id", deviceId).last(Const.LIMIT_ONE).one();
    }

    @Override
    public boolean changeNetStatus(String deviceId, int status) {
        return update()
                .eq("device_id", deviceId)
                .ne("net_status", status)
                .set("net_status", status)
                .update(new DeviceEntity());
    }

    @Override
    public boolean changeAepStatus(String deviceId, String aepStatus) {
        return update()
                .eq("device_id", deviceId)
                .ne("aep_status", aepStatus)
                .set("aep_status", aepStatus)
                .update(new DeviceEntity());
    }

    /**
     * 批量保存或者更新
     *
     * @param entityList
     * @param batchSize
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateBatchConstraintDeviceId(List<DeviceEntity> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            String deviceId = entity.getDeviceId();
            if (StringUtils.checkValNull(deviceId) || Objects.isNull(getByDeviceId(deviceId))) {
                sqlSession.insert(tableInfo.getSqlStatement(SqlMethod.INSERT_ONE.getMethod()), entity);
            } else {
                DeviceEntity existedEntity = getByDeviceId(deviceId);
                if (Objects.isNull(existedEntity)) {
                    sqlSession.insert(tableInfo.getSqlStatement(SqlMethod.INSERT_ONE.getMethod()), entity);
                } else {
                    entity.setId(existedEntity.getId());
                    MapperMethod.ParamMap<DeviceEntity> param = new MapperMethod.ParamMap<>();
                    param.put(Constants.ENTITY, entity);
                    sqlSession.update(tableInfo.getSqlStatement(SqlMethod.UPDATE_BY_ID.getMethod()), param);
                }
            }
        });
    }

    @Override
    public boolean syncByProduct(ProductEntity product, String searchValue) {
        // 获取配置
        JsonNode aepConfig = paramService.getContentJsonNode("CTWING_AEP");
        AssertUtils.isNull(aepConfig, ErrorCode.PARAM_CFG_ERROR);
        // 初始化客户端
        AepDeviceManagementClient client = AepDeviceManagementClient.newClient().appKey(aepConfig.get("appKey").asText()).appSecret(aepConfig.get("appSecret").asText()).build();
        return getDeviceListFromApiAndSave(client, 0, product);
    }

    @Override
    public boolean sync(String searchValue) {
        List<ProductEntity> productList = productService.list();
        for (ProductEntity product : productList) {
            syncByProduct(product, searchValue);
        }
        return true;
    }

    private boolean getDeviceListFromApiAndSave(AepDeviceManagementClient client, int pageNow, ProductEntity product) {
        // 请求参数
        QueryDeviceListRequest request = new QueryDeviceListRequest();
        request.setParamSearchValue(null);
        request.setParamProductId(product.getProductId());
        request.setParamMasterKey(product.getApiKey());
        request.setParamPageNow(pageNow);
        request.setParamPageSize(10);

        QueryDeviceListResponse response;
        try {
            response = client.QueryDeviceList(request);
        } catch (Exception e) {
            throw new OnexException("获取设备接口调用失败");
        }
        String body = new String(response.getBody());
        System.out.println(body);
        HttpResult<PageResult<DeviceResponse>> result = JacksonUtils.jsonToPojoByTypeReference(body,
                new TypeReference<HttpResult<PageResult<DeviceResponse>>>() {
                },
                new HttpResult<>(ErrorCode.JSON_FORMAT_ERROR, "json解析错误"));
        if (result.isSuccess()) {
            if (ObjectUtils.isNotEmpty(result.getResult().getList())) {
                List<DeviceEntity> entityList = ConvertUtils.sourceToTarget(result.getResult().getList(), DeviceEntity.class);
                saveOrUpdateBatchConstraintDeviceId(entityList, 100);
            }
            // 接口返回的page和pageNo有问题，都是0
            if (pageNow * 10 < result.getResult().getTotal()) {
                return getDeviceListFromApiAndSave(client, pageNow + 1, product);
            } else {
                return true;
            }
        } else {
            throw new OnexException("设备接口返回错误:" + result.getMsg());
        }
    }

    @Override
    public boolean deleteFromCtwingByDeviceIds(Integer productId, String masterKey, String deviceIds) {
        // 获取配置
        JsonNode aepConfig = paramService.getContentJsonNode("CTWING_AEP");
        AssertUtils.isNull(aepConfig, ErrorCode.PARAM_CFG_ERROR);
        // 初始化客户端
        AepDeviceManagementClient client = AepDeviceManagementClient.newClient().appKey(aepConfig.get("appKey").asText()).appSecret(aepConfig.get("appSecret").asText()).build();

        DeleteDeviceRequest request = new DeleteDeviceRequest();
        request.setParamProductId(productId);
        request.setParamMasterKey(masterKey);
        request.setParamDeviceIds(deviceIds);
        DeleteDeviceResponse response;
        try {
            response = client.DeleteDevice(request);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        String body = new String(response.getBody());
        System.out.println(body);
        HttpResult result = JacksonUtils.jsonToPojoByTypeReference(body, new TypeReference<HttpResult>() {}, new HttpResult<>(ErrorCode.JSON_FORMAT_ERROR, "json解析错误"));
        return result.isSuccess();
    }

    /**
     * 插入数据
     *
     * @param dto 数据
     * @return 插入结果
     */
    @Override
    public boolean saveDto(DeviceDTO dto) {
        ProductEntity product = productService.getByProductId(dto.getProductId());
        AssertUtils.isNull(product, "所属产品不存在");

        EnterpriseEntity enterprise = enterpriseService.getById(dto.getEnterpriseId());
        AssertUtils.isNull(enterprise, "所属企业不存在");
        dto.setEnterpriseName(enterprise.getName());

        // 获取配置
        JsonNode aepConfig = paramService.getContentJsonNode("CTWING_AEP");
        AssertUtils.isNull(aepConfig, ErrorCode.PARAM_CFG_ERROR);
        // 初始化客户端
        AepDeviceManagementClient client = AepDeviceManagementClient.newClient().appKey(aepConfig.get("appKey").asText()).appSecret(aepConfig.get("appSecret").asText()).build();

        CreateDeviceRequest request = new CreateDeviceRequest();
        request.setParamMasterKey(product.getApiKey());
        /**
         * 描述：
         * deviceName: 设备名称，必填
         * deviceSn: 设备编号，MQTT,T_Link,TCP,HTTP,JT/T808，南向云协议必填
         * imei: imei号，LWM2M,NB网关协议必填
         * operator: 操作者，必填
         * other: LWM2M协议必填参数,其他协议不填：{
         *       autoObserver:0.自动订阅 1.取消自动订阅，必填;
         *       imsi:总长度不超过15位，使用0~9的数字，String类型,选填;
         *       pskValue:由大小写字母加0-9数字组成的16位字符串,选填
         * }
         * productId: 产品ID，必填
         */
        request.setBody(("{\"productId\":\"" + dto.getProductId() + "\"" +
                "," +
                "\"deviceName\":\"" + dto.getDeviceName() + "\"" +
                "," +
                "\"imei\":\"" + dto.getImei() + "\"" +
                "," +
                "\"operator\":\"" + "api" + "\"" +
                "," +
                "\"other\":{\"autoObserver\":" + dto.getAutoObserver() + ",\"imsi\":\"" + dto.getImsi() + "\"}" +
                "}").getBytes());
        CreateDeviceResponse response;
        try {
            response = client.CreateDevice(request);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        String body = new String(response.getBody());
        HttpResult<DeviceEntity> result = JacksonUtils.jsonToPojoByTypeReference(body, new TypeReference<HttpResult<DeviceEntity>>() {}, new HttpResult<>(ErrorCode.JSON_FORMAT_ERROR, "json解析错误"));
        if (result.isSuccess()) {
            DeviceEntity entity = result.getResult();
            entity.setDeviceType(dto.getDeviceType());
            entity.setDevicePid(dto.getDevicePid());
            save(entity);
        } else {
            throw new OnexException(ErrorCode.ERROR_REQUEST, result.getCode() + ":" + result.getMsg());
        }
        return true;
    }

    /**
     * 插入数据
     *
     * @param dto 数据
     * @return 插入结果
     */
    @Override
    public boolean updateDto(DeviceDTO dto) {
        // 已存在数据
        DeviceEntity existedEntity = getById(dto.getId());
        AssertUtils.isNull(existedEntity,  ErrorCode.DB_RECORD_NOT_EXISTED);

        ProductEntity product = productService.getByProductId(dto.getProductId());
        AssertUtils.isNull(product, "所属产品不存在");

        EnterpriseEntity enterprise = enterpriseService.getById(dto.getEnterpriseId());
        AssertUtils.isNull(enterprise, "所属企业不存在");
        dto.setEnterpriseName(enterprise.getName());


        if (!dto.getDeviceName().equalsIgnoreCase(existedEntity.getDeviceName())
                || !dto.getAutoObserver().equals(existedEntity.getAutoObserver())
                || !dto.getImsi().equalsIgnoreCase(existedEntity.getImsi())) {
            // 只有设备名称、imsi、自动订阅可以修改平台
            // 获取配置
            JsonNode aepConfig = paramService.getContentJsonNode("CTWING_AEP");
            AssertUtils.isNull(aepConfig, ErrorCode.PARAM_CFG_ERROR);
            // 初始化客户端
            AepDeviceManagementClient client = AepDeviceManagementClient.newClient().appKey(aepConfig.get("appKey").asText()).appSecret(aepConfig.get("appSecret").asText()).build();

            UpdateDeviceRequest request = new UpdateDeviceRequest();
            request.setParamMasterKey(product.getApiKey());
            request.setParamDeviceId(dto.getDeviceId());
            request.setBody(("{\"productId\":\"" + dto.getProductId() + "\"" +
                    "," +
                    "\"deviceName\":\"" + dto.getDeviceName() + "\"" +
                    "," +
                    "\"operator\":\"" + "api" + "\"" +
                    "," +
                    "\"other\":{\"autoObserver\":" + dto.getAutoObserver() + ",\"imsi\":\"" + (StringUtils.isBlank(dto.getImsi()) ? "" : dto.getImsi()) + "\"}" +
                    "}").getBytes());
            UpdateDeviceResponse response;
            try {
                response = client.UpdateDevice(request);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            String body = new String(response.getBody());
            HttpResult result = JacksonUtils.jsonToPojoByTypeReference(body, new TypeReference<HttpResult>() {}, new HttpResult<>(ErrorCode.JSON_FORMAT_ERROR, "json解析错误"));
            if (!result.isSuccess()) {
                return false;
            }
        }
        DeviceEntity entity = ConvertUtils.sourceToTarget(dto, DeviceEntity.class);
        updateById(entity);
        return true;
    }

    @Override
    public boolean sendCommand(String deviceId, String command) {
        DeviceEntity entity = getByDeviceId(deviceId);
        AssertUtils.isNull(entity, "设备不存在");

        ProductEntity product = productService.getByProductId(entity.getProductId());
        AssertUtils.isNull(product, "所属产品不存在");

        // 获取配置
        JsonNode aepConfig = paramService.getContentJsonNode("CTWING_AEP");
        AssertUtils.isNull(aepConfig, ErrorCode.PARAM_CFG_ERROR);
        // 初始化客户端
        AepDeviceCommandClient client = AepDeviceCommandClient.newClient().appKey(aepConfig.get("appKey").asText()).appSecret(aepConfig.get("appSecret").asText()).build();

        CreateCommandRequest request = new CreateCommandRequest();
        request.setParamMasterKey(product.getApiKey());
        request.setBody(("{\"productId\":" + product.getProductId() + "" +
                "," +
                "\"deviceId\":\"" + entity.getDeviceId() + "\"" +
                "," +
                "\"operator\":\"" + "api" + "\"" +
                "," +
                "\"level\":2" +
                "," +
                "\"ttl\":7200" +
                "," +
                "\"content\":{\"payload\":\"" + command + "\",\"dataType\":2,\"isReturn\":1}" +
                "}").getBytes());
        CreateCommandResponse response;
        try {
            response = client.CreateCommand(request);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        String body = new String(response.getBody());
        HttpResult result = JacksonUtils.jsonToPojoByTypeReference(body, new TypeReference<HttpResult>() {}, new HttpResult<>(ErrorCode.JSON_FORMAT_ERROR, "json解析错误"));
        return result.isSuccess();
    }
}
