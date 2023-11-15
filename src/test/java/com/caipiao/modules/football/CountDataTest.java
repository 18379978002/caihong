package com.caipiao.modules.football;

import com.caipiao.modules.common.task.CountDataTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class CountDataTest {
    @Autowired
    CountDataTask countDataTask;


    @Test
    void test(){
        countDataTask.countDataTask();
    }

}
