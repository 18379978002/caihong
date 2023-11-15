package com.caipiao.modules.common.util;

public class HeatUtil {

    /**
     * 获取热度
     * @param heat
     * @return
     */
    public static int getHeat(int heat){

        if(heat>=2 && heat<4){
            return 1;
        }

        if(heat==4){
            return 2;
        }

        if(heat>=5 && heat<7){
            return 3;
        }

        if(heat>=7&&heat<10){
            return 4;
        }


        if(heat>=10){
            return 5;
        }

        return 0;
    }
}
