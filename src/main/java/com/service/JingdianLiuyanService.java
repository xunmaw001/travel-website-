package com.service;

import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.JingdianLiuyanEntity;
import java.util.Map;

/**
 * 景点评价 服务类
 * @author 
 * @since 2021-04-03
 */
public interface JingdianLiuyanService extends IService<JingdianLiuyanEntity> {

    /**
    * @param params 查询参数
    * @return 带分页的查询出来的数据
    */
     PageUtils queryPage(Map<String, Object> params);
}