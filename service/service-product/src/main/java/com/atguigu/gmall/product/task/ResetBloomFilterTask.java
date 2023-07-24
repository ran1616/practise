package com.atguigu.gmall.product.task;

import com.atguigu.gmall.product.service.BloomFilterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ResetBloomFilterTask {

    @Autowired
    private BloomFilterService bloomFilterService ;

    @Scheduled(cron = "0 0 23 * * ?")
    public void resetBloomFilterTask() {
        log.info("ResetBloomFilterTask...resetBloomFilterTask...执行了");
        bloomFilterService.resetBloomFilter();
    }

}
