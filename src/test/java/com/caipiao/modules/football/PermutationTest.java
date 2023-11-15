package com.caipiao.modules.football;

import com.caipiao.modules.permutation.task.PermutationResultSyncTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PermutationTest {

    @Autowired
    PermutationResultSyncTask permutationResultSyncTask;

    @Test
    void syncTest(){
        permutationResultSyncTask.permutationResultSyncTask();
    }

    @Test
    void resultTest(){
        permutationResultSyncTask.permutationOrderResultSyncTask();
    }
}
