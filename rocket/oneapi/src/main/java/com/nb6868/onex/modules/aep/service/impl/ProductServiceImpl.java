package com.nb6868.onex.modules.aep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.ctg.ag.sdk.biz.AepProductManagementClient;
import com.ctg.ag.sdk.biz.aep_product_management.QueryProductListRequest;
import com.ctg.ag.sdk.biz.aep_product_management.QueryProductListResponse;
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
import com.nb6868.onex.modules.aep.dao.ProductDao;
import com.nb6868.onex.modules.aep.dto.ProductDTO;
import com.nb6868.onex.modules.aep.entity.ProductEntity;
import com.nb6868.onex.modules.aep.sdk.HttpResult;
import com.nb6868.onex.modules.aep.sdk.PageResult;
import com.nb6868.onex.modules.aep.sdk.ProductResponse;
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
 * AEP-产品
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ProductServiceImpl extends CrudServiceImpl<ProductDao, ProductEntity, ProductDTO> implements ProductService {

    @Autowired
    ParamService paramService;

    @Override
    public QueryWrapper<ProductEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<ProductEntity>(new QueryWrapper<>(), params)
                .eq("productId", "product_id")
                .like("productName", "product_name")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    @Override
    public ProductEntity getByProductId(Integer productId) {
        return query().eq("product_id", productId).last(Const.LIMIT_ONE).one();
    }

    /**
     * 批量保存或者更新
     * @param entityList
     * @param batchSize
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateBatchConstraintProductId(List<ProductEntity> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            Integer productId = entity.getProductId();
            if (StringUtils.checkValNull(productId)) {
                sqlSession.insert(tableInfo.getSqlStatement(SqlMethod.INSERT_ONE.getMethod()), entity);
            } else {
                ProductEntity existedEntity = getByProductId(productId);
                if (Objects.isNull(existedEntity)) {
                    sqlSession.insert(tableInfo.getSqlStatement(SqlMethod.INSERT_ONE.getMethod()), entity);
                } else {
                    entity.setId(existedEntity.getId());
                    MapperMethod.ParamMap<ProductEntity> param = new MapperMethod.ParamMap<>();
                    param.put(Constants.ENTITY, entity);
                    sqlSession.update(tableInfo.getSqlStatement(SqlMethod.UPDATE_BY_ID.getMethod()), param);
                }
            }
        });
    }

    @Override
    public boolean sync(String searchValue) {
        // 获取配置
        JsonNode aepConfig = paramService.getContentJsonNode("CTWING_AEP");
        AssertUtils.isNull(aepConfig, ErrorCode.PARAM_CFG_ERROR);
        // 初始化客户端
        AepProductManagementClient client = AepProductManagementClient.newClient().appKey(aepConfig.get("appKey").asText()).appSecret(aepConfig.get("appSecret").asText()).build();
        return getProductListFromApiAndSave(client, 1);
    }

    private boolean getProductListFromApiAndSave(AepProductManagementClient client, int pageNow) {
        // 请求参数
        QueryProductListRequest request = new QueryProductListRequest();
        request.setParamSearchValue(null);
        request.setParamPageNow(pageNow);
        request.setParamPageSize(40);

        QueryProductListResponse response;
        try {
            response = client.QueryProductList(request);
        } catch (Exception e) {
            throw new OnexException("获取产品接口调用失败");
        }
        HttpResult<PageResult<ProductResponse>> result = JacksonUtils.jsonToPojoByTypeReference(new String(response.getBody()),
                new TypeReference<HttpResult<PageResult<ProductResponse>>>(){},
                new HttpResult<>(ErrorCode.JSON_FORMAT_ERROR, "json解析错误"));
        if (result.isSuccess()) {
            if (ObjectUtils.isNotEmpty(result.getResult().getList())) {
                List<ProductEntity> entityList = ConvertUtils.sourceToTarget(result.getResult().getList(), ProductEntity.class);
                saveOrUpdateBatchConstraintProductId(entityList, 100);
            }
            if (result.getResult().hasNextPage()) {
                return getProductListFromApiAndSave(client, pageNow++);
            } else {
                return true;
            }
        } else {
            throw new OnexException("产品接口返回错误:" + result.getMsg());
        }
    }

}
