package com.caipiao.modules.football;

import com.caipiao.modules.basketball.task.BasketballSyncTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Description: jz-cloud
 * Created by yj198 on 2021/4/17 22:12
 */
@SpringBootTest
@Slf4j
public class BasketballSyncTest {

    @Autowired
    BasketballSyncTask basketballSyncTask;

    @Test
    void test(){
        basketballSyncTask.syncResultDetailList();
    }



}
