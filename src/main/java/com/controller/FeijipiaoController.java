package com.controller;


import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import java.util.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;
import com.service.TokenService;
import com.utils.StringUtil;
import java.lang.reflect.InvocationTargetException;

import com.service.DictionaryService;
import org.apache.commons.lang3.StringUtils;
import com.annotation.IgnoreAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;

import com.entity.FeijipiaoEntity;

import com.service.FeijipiaoService;
import com.entity.view.FeijipiaoView;
import com.utils.PageUtils;
import com.utils.R;

/**
 * 飞机票
 * 后端接口
 * @author
 * @email
 * @date 2021-04-03
*/
@RestController
@Controller
@RequestMapping("/feijipiao")
public class FeijipiaoController {
    private static final Logger logger = LoggerFactory.getLogger(FeijipiaoController.class);

    @Autowired
    private FeijipiaoService feijipiaoService;


    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;


    //级联表service


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(StringUtil.isNotEmpty(role) && "用户".equals(role)){
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        }
        params.put("orderBy","id");
        PageUtils page = feijipiaoService.queryPage(params);

        //字典表数据转换
        List<FeijipiaoView> list =(List<FeijipiaoView>)page.getList();
        for(FeijipiaoView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c);
        }
        return R.ok().put("data", page);
    }

    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        logger.debug("info方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        FeijipiaoEntity feijipiao = feijipiaoService.selectById(id);
        if(feijipiao !=null){
            //entity转view
            FeijipiaoView view = new FeijipiaoView();
            BeanUtils.copyProperties( feijipiao , view );//把实体数据重构到view中

            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }

    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public R save(@RequestBody FeijipiaoEntity feijipiao, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,feijipiao:{}",this.getClass().getName(),feijipiao.toString());
        Wrapper<FeijipiaoEntity> queryWrapper = new EntityWrapper<FeijipiaoEntity>()
            .eq("feijipiao_name", feijipiao.getFeijipiaoName())
            .eq("feiji_types", feijipiao.getFeijiTypes())
            .eq("feijipiao_xinghao_types", feijipiao.getFeijipiaoXinghaoTypes())
            .eq("feijipiao_chufadi", feijipiao.getFeijipiaoChufadi())
            .eq("feijipiao_mudidi", feijipiao.getFeijipiaoMudidi())
            .eq("feijipiao_time", feijipiao.getFeijipiaoTime())
            .eq("feijipiao_new_money", feijipiao.getFeijipiaoNewMoney())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        FeijipiaoEntity feijipiaoEntity = feijipiaoService.selectOne(queryWrapper);
        if(feijipiaoEntity==null){
            feijipiao.setCreateTime(new Date());
        //  String role = String.valueOf(request.getSession().getAttribute("role"));
        //  if("".equals(role)){
        //      feijipiao.set
        //  }
            feijipiaoService.insert(feijipiao);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody FeijipiaoEntity feijipiao, HttpServletRequest request){
        logger.debug("update方法:,,Controller:{},,feijipiao:{}",this.getClass().getName(),feijipiao.toString());
        //根据字段查询是否有相同数据
        Wrapper<FeijipiaoEntity> queryWrapper = new EntityWrapper<FeijipiaoEntity>()
            .notIn("id",feijipiao.getId())
            .andNew()
            .eq("feijipiao_name", feijipiao.getFeijipiaoName())
            .eq("feiji_types", feijipiao.getFeijiTypes())
            .eq("feijipiao_xinghao_types", feijipiao.getFeijipiaoXinghaoTypes())
            .eq("feijipiao_chufadi", feijipiao.getFeijipiaoChufadi())
            .eq("feijipiao_mudidi", feijipiao.getFeijipiaoMudidi())
            .eq("feijipiao_time", feijipiao.getFeijipiaoTime())
            .eq("feijipiao_new_money", feijipiao.getFeijipiaoNewMoney())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        FeijipiaoEntity feijipiaoEntity = feijipiaoService.selectOne(queryWrapper);
        if("".equals(feijipiao.getFeijipiaoPhoto()) || "null".equals(feijipiao.getFeijipiaoPhoto())){
                feijipiao.setFeijipiaoPhoto(null);
        }
        if(feijipiaoEntity==null){
            //  String role = String.valueOf(request.getSession().getAttribute("role"));
            //  if("".equals(role)){
            //      feijipiao.set
            //  }
            feijipiaoService.updateById(feijipiao);//根据id更新
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }


    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        feijipiaoService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }



    /**
    * 前端列表
    */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(StringUtil.isNotEmpty(role) && "用户".equals(role)){
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        }
        // 没有指定排序字段就默认id倒序
        if(StringUtil.isEmpty(String.valueOf(params.get("orderBy")))){
            params.put("orderBy","id");
        }
        PageUtils page = feijipiaoService.queryPage(params);

        //字典表数据转换
        List<FeijipiaoView> list =(List<FeijipiaoView>)page.getList();
        for(FeijipiaoView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c);
        }
        return R.ok().put("data", page);
    }

    /**
    * 前端详情
    */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        logger.debug("detail方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
    FeijipiaoEntity feijipiao = feijipiaoService.selectById(id);
        if(feijipiao !=null){
            //entity转view
    FeijipiaoView view = new FeijipiaoView();
            BeanUtils.copyProperties( feijipiao , view );//把实体数据重构到view中

            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }
    }


    /**
    * 前端保存
    */
    @RequestMapping("/add")
    public R add(@RequestBody FeijipiaoEntity feijipiao, HttpServletRequest request){
        logger.debug("add方法:,,Controller:{},,feijipiao:{}",this.getClass().getName(),feijipiao.toString());
        Wrapper<FeijipiaoEntity> queryWrapper = new EntityWrapper<FeijipiaoEntity>()
            .eq("feijipiao_name", feijipiao.getFeijipiaoName())
            .eq("feiji_types", feijipiao.getFeijiTypes())
            .eq("feijipiao_xinghao_types", feijipiao.getFeijipiaoXinghaoTypes())
            .eq("feijipiao_chufadi", feijipiao.getFeijipiaoChufadi())
            .eq("feijipiao_mudidi", feijipiao.getFeijipiaoMudidi())
            .eq("feijipiao_time", feijipiao.getFeijipiaoTime())
            .eq("feijipiao_new_money", feijipiao.getFeijipiaoNewMoney())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
    FeijipiaoEntity feijipiaoEntity = feijipiaoService.selectOne(queryWrapper);
        if(feijipiaoEntity==null){
            feijipiao.setCreateTime(new Date());
        //  String role = String.valueOf(request.getSession().getAttribute("role"));
        //  if("".equals(role)){
        //      feijipiao.set
        //  }
        feijipiaoService.insert(feijipiao);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }


}

