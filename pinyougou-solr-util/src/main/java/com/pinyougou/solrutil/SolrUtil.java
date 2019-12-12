package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 导入商品数据
     */
    public void importItemData() {
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1"); //已审核状态
        List<TbItem> items = itemMapper.selectByExample(example);
        System.out.println("===导入商品===");
        for (TbItem item : items) {
            Map specMap = JSON.parseObject(item.getSpec());   //将spec字段中中的json字符串转为Map对象
            item.setSpecMap(specMap);   //给带注解的字段赋值

            System.out.println(item.getTitle());
        }
        solrTemplate.saveBeans(items);
        solrTemplate.commit();
        System.out.println("===结束导入===");
    }

    public void deleteData(){
        Query query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
        System.out.println("删除成功！");
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) applicationContext.getBean("solrUtil");
        solrUtil.importItemData();
        //solrUtil.deleteData();
    }
}
