package com.caipiao.modules.football;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;

public class SmsUtilTest {
        public static void main(String [] args) {
            try{
                // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
                // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
                // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
                Credential cred = new Credential("AKIDMZbRm0c4Ur7oOo9NVWLxSzJQYeYEB5pZ", "rZ5OWYIpAKLAoVBh171jDVbUK1f6LODp");
                // 实例化一个http选项，可选的，没有特殊需求可以跳过
                HttpProfile httpProfile = new HttpProfile();
                httpProfile.setEndpoint("sms.tencentcloudapi.com");
                // 实例化一个client选项，可选的，没有特殊需求可以跳过
                ClientProfile clientProfile = new ClientProfile();
                clientProfile.setHttpProfile(httpProfile);
                // 实例化要请求产品的client对象,clientProfile是可选的
                SmsClient client = new SmsClient(cred, "ap-beijing", clientProfile);
                // 实例化一个请求对象,每个接口都会对应一个request对象
                SendSmsRequest req = new SendSmsRequest();
                req.setPhoneNumberSet(new String[]{"15797736442"});
                req.setTemplateId("1748426");
                req.setSignName("江西熠彩大数据发展");
                req.setTemplateParamSet(new String[]{"21321"});
                req.setSmsSdkAppId("1400807798");
                // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
                SendSmsResponse resp = client.SendSms(req);
                // 输出json格式的字符串回包
                System.out.println(SendSmsResponse.toJsonString(resp));
            } catch (TencentCloudSDKException e) {
                System.out.println(e.toString());
            }
        }
}
