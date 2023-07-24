package com.atguigu.gmall.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.atguigu.gmall.common.constant.GmallConstant;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@CanalEventListener  // 声明当前这类是canal的一个事件监听器
@Slf4j
public class CanalSkuInfoListener {

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    @ListenPoint(destination = "example", schema = "gmall_product", table = {"sku_info"})
    public void listenSkuInfoChange(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {

        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();// 获取变更之前的数据列
        beforeColumnsList.stream().forEach(column -> {
            String columnName = column.getName();
            String columnValue = column.getValue();
            log.info("columnName: {} , columnValue: {}" , columnName , columnValue);
        });

        System.out.println("-------------------------------------------------------------------------");

        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();  // 获取变更之后的数据列
        afterColumnsList.stream().forEach(column -> {
            String columnName = column.getName();
            if("id".equalsIgnoreCase(columnName)) {
                String columnValue = column.getValue();
                redisTemplate.delete(GmallConstant.REDSI_SKU_DETAIL_PREFIX + columnValue) ;
                log.info("canal client工作了，从Redis中删除了skuId为：{} 数据" , columnValue);
            }
        });

    }

}
