package com.service.impl;

import com.utils.StringUtil;
import org.springframework.stereotype.Service;
import java.lang.reflect.Field;
import java.util.*;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import com.utils.PageUtils;
import com.utils.Query;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;

import com.dao.FeijipiaoCollectionDao;
import com.entity.FeijipiaoCollectionEntity;
import com.service.FeijipiaoCollectionService;
import com.entity.view.FeijipiaoCollectionView;

/**
 * 飞机收藏 服务实现类
 * @author 
 * @since 2021-04-03
 */
@Service("feijipiaoCollectionService")
@Transactional
public class FeijipiaoCollectionServiceImpl extends ServiceImpl<FeijipiaoCollectionDao, FeijipiaoCollectionEntity> implements FeijipiaoCollectionService {

    @Override
    public PageUtils queryPage(Map<String,Object> params) {
        if(params != null && (params.get("limit") == null || params.get("page") == null)){
            params.put("page","1");
            params.put("limit","10");
        }
        Page<FeijipiaoCollectionView> page =new Query<FeijipiaoCollectionView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page,params));
        return new PageUtils(page);
    }


}
