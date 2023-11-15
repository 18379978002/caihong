package com.caipiao.modules.football;

import com.caipiao.common.component.RedisComponent;
import com.caipiao.modules.common.service.MatchResultService;
import com.caipiao.modules.football.service.FootballMatchService;
import com.caipiao.modules.football.task.FootballSyncTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

/**
 * Description: jz-cloud
 * Created by yj198 on 2021/4/17 22:12
 */
@SpringBootTest
@Slf4j
public class FootballSyncTest {

    @Autowired
    RestTemplate httpRestTemplate;
    @Autowired
    FootballMatchService matchService;
    @Autowired
    RedisComponent redisComponent;
    @Autowired
    MatchResultService matchResultService;
    @Autowired
    FootballSyncTask footballSyncTask;

    @Test
    void test(){
        footballSyncTask.syncCurrentMatchData();
    }

}
