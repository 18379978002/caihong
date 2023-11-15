package com.caipiao.modules.common.util;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.taobao.api.ApiException;

import java.util.Collections;

/**
 * @author xiaoyinandan
 * @date 2022/3/9 下午9:16
 */
public class BugReportUtil {
    public static void sendMsgToDingtalk(Exception e) {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?access_token=ad956e53734ce6ac45746228ee9cea6439752b03328bd896adffade874ab1589");
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent("您的接口报错啦！！！！！" + e.getMessage());
        request.setText(text);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setAtMobiles(Collections.singletonList("18601754726"));
        // isAtAll类型如果不为Boolean，请升级至最新SDK
        at.setIsAtAll(false);
        request.setAt(at);

        try {
            client.execute(request);
        } catch (ApiException ex) {
            ex.printStackTrace();
        }
    }
}
