package com.zah.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.zah.entity.PayVo;
import lombok.Data;
import org.springframework.stereotype.Component;


@Component
@Data
public class AlipayTemplate {

    /*
                119.29.137.92
    */
    // 商户appid
    public static String APPID = "2016101400683693";
    // 私钥 pkcs8格式的
    public static String RSA_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQChGRlIQ+yQmtimfUtXBW1NOe6QbNWT7TwqhV94VBTqfEtmnwxCes6YEiX3J5tB2IY+ZOvYW0Y+PQBubqyNbHHzvCHKXKjzxGv4lfetpCCNFN0Z/ZLA+zZ/3dGw6kZRkRbTmxGDaLFKqMsSi+krrPBDl9P1SyoMsF9/2iuRA4oaWz+gFfV+JVXBxpqGJH9Z5trcNltdeFC17B2QooUUVhwguehwb6QepXaqY3ZAnjV/vKXjgdY2d6Hfqe2FKLV17AimadnefkxO9JBqlIGbjVybTkrSNdvsttbjGo+bW5TH9GoWM9R6Ks6UB/WDqudsu1YlOtHmZAZgv/R8LXmMeIg1AgMBAAECggEBAJXWXmmUo8er/q58A0MhTRRzOa0jTVOVaEy0WKP2sK2Yhhkg/aonx1gyKRXExOdtmQ8XlzVZT/A7lNciIWQ45ZQp+9PYc1yn6TUBZ6kd6xjNuwidjY3pGju7+XlvrY/YPz76EEQFcEFc1eSeNXXmGGMGKKMSEj5THcAxECyE+kKf9nGG4uxgXe8tdU7jjMPlQDQvJPfPStpXxFHOdvCcznvMya9QBpgBZ2sWDlm/OU7QM4FLlpK53LzCdUuYZp/IiKgVuavVXsWeF5+civqenZMYyV8Wy4nFlSjwcMBKAamxy2JZv2AlVjkZuuRXgmDjDYD1h7ftCtiZkgZE+U0pgIECgYEA7BPB45ylcc/ZMa96zmyx5MOUM06fdXET0xfKIgoayJW5EqtpfFoAzd8IR0SIiCY6GYjp2D+VLUaAxmACCTsCHTF/oe6iu449ySIr8oJhvOh2ePlrwTfebnKin8Mx5HjQgm9ZaSZFWkwwwqp+FVQZtpeOxi2eEV5PMDhxZsF3nikCgYEArrF7cd+asSFE/1srdwGGc+54fXfwyK6vNMcmiF8Iv5MnYexcMRu2Yn0Mwn9hCpvxlHYl3DSaMDts+oj/SV4Cqa+eD64TqcVQMtKp3NSw47XoR+CMxuTRjvc04o2lzu9ayF8VRpSRyQV3zBA3H7S4vtVGAXwtExKmWC4z2iNFQy0CgYEArnCi8jcLoR9+U9Jgoit6/5js50SPW05k4bRQbfqdc93+VSdH2NUAhx7olRC0+jWU9M1QTbQd3Mid2veSSZPKqhurPdKBeKlQSlDvvFBPPDFhaV6UGr/XojEerYKkWThUii0RGjtnP6PQ2+bzJ3tvTJaMry+B/z96sBzk3pLSepECgYB2bbG/QBRx0pkg+CCeO/V3cIpUnNQqaGo0wHH571tR7KO9OexCCoR5fkcBMDOMaq26tOHzx+Fjo1W8/tT6WvY5LOpdvOhtpms5GSG4qKQGq1J+L9egGo2Ke0Jp+BcZE6Eax4L3505x1fgh6jhEkVLRZGpiJVm1XWZRz3itgE27PQKBgHO9evNaAk67g4XzA5jy0AVxxZztYNaFezdj0o9DsJdibbFLklTCXlU4tn4wdQNPS620Wpu/KJhnMdjtBk3AG19szTFGjqoWm1gh0BlHCMvaxiDcfx7Yj0aNwWSqKKVwMjsyBDH9E3msieQOEIkSIsDuznpkhVP5cEsQ6t7sY90H";
    // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://zah123.ngrok2.xiaomiqiu.cn/wechat/notify";
    // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
    public static String return_url = "http://zah123.ngrok2.xiaomiqiu.cn/wechat/success";
    // 请求网关地址
    public static String URL = "https://openapi.alipaydev.com/gateway.do";

    // 编码
    public static String CHARSET = "UTF-8";
    // 返回格式
    public static String FORMAT = "json";
    // 支付宝公钥
    public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiyYzRl8OMWLXL+1zQU7tsygOQm8LGHEavXR9zR+d4pTbhaX+QTngyTDmKRrk6y0D/C7Dq0ar3knOc9V3Pb/R3e96EvTVYl9mR5qZSEfwUZmKV68CHvZzifAy1mGMj3yYl9S4p9U4HOGLK2vkK6fIB1ww9aAVS46acYs6lAEEtLTDtqPrl4gXkrJcylyIk6Dwd2ErvL/cuUzhf7/yN2kcg2aBBfYg7ndF29IxtkM6jihyRhQ6AofvPkuS8UV7aCUHKaZ0PsanpFHdW7X9WhymDFP+U3GpjTx0GsStAS6gWCs27IY7d3VSjYfb2xWdeb6wf4r6yFCi6rxAjdj0CDJFMQIDAQAB";
    // 日志记录目录
    public static String log_path = "/log";
    // RSA2
    public static String SIGNTYPE = "RSA2";


    public String pay(PayVo vo) throws AlipayApiException {
        /**********************/
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
        //调用RSA签名方式
        AlipayClient client = new DefaultAlipayClient(URL, APPID, RSA_PRIVATE_KEY, FORMAT, CHARSET, ALIPAY_PUBLIC_KEY, SIGNTYPE);
        AlipayTradeWapPayRequest alipay_request = new AlipayTradeWapPayRequest();
        // 封装请求支付信息
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setOutTradeNo(vo.getOut_trade_no());
        model.setSubject(vo.getSubject());
        model.setTotalAmount(vo.getTotal_amount());
        model.setBody(vo.getBody());
        System.out.println(vo.getTimeExpire());
        model.setTimeExpire(vo.getTimeExpire());
        /*time_expire*/
      /*      model.setTimeoutExpress(timeout_express);
            model.setProductCode(product_code);*/
        alipay_request.setBizModel(model);
        // 设置异步通知地址
        alipay_request.setNotifyUrl(notify_url);
        // 设置同步地址
        alipay_request.setReturnUrl(return_url);
        alipay_request.setBizContent("{\"out_trade_no\":\"" + vo.getOut_trade_no() + "\",\"total_amount\":\"" + vo.getTotal_amount() + "\",\"subject\":\"" + vo.getSubject() + "\",\"body\":\"" + vo.getBody() + "\",\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        String result = client.pageExecute(alipay_request).getBody();
        System.out.println("支付宝的响应" + result);
        return result;
    }
}
