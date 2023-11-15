package com.caipiao.modules.football;

import com.caipiao.modules.permutation.task.LotteryResultSyncTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LotteryTest {

    @Autowired
    LotteryResultSyncTask lotteryResultSyncTask;

    @Test
    void syncTest(){
        lotteryResultSyncTask.lotteryResultSyncTask();
    }

    @Test
    void syncTest1(){
        lotteryResultSyncTask.lotteryOrderResultSyncTask();
    }
}
