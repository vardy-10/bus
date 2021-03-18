package com.zah.controller;

import cn.gjing.http.HttpClient;
import cn.gjing.http.HttpMethod;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.google.zxing.common.BitMatrix;
import com.zah.config.AlipayTemplate;
import com.zah.constant.OrderConstant;
import com.zah.dao.CommonDao;
import com.zah.entity.BusVehicle;
import com.zah.entity.Order;
import com.zah.entity.PayVo;
import com.zah.service.impl.*;
import com.zah.thread.Start;
import com.zah.util.CCPlayUtils;
import com.zah.util.Function;
import com.zah.util.wxFunction;
import com.zah.util.QrCodeUtils;
import com.zah.entity.WechatConstants;
import com.zah.entity.WechatUserInfo;
import com.zah.service.impl.BuyTicketServiceImpl;
import com.zah.service.impl.WeiXinUserInfoImpl;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author shbai 班次查询
 */
@Controller
@RequestMapping("/wechat")
public class WxController {
    @Autowired
    WxShiftsServiceImp wxShiftsServiceImp;
    @Autowired
    WeiXinUserInfoImpl userService;

    @Autowired
    NoticeServiceImpl noticeServiceImpl;

    @Autowired
    OpinionServiceImpl opinionServiceImpl;

    @Autowired
    RetrieveServiceImpl retrieveServiceImpl;

    @Autowired
    OrderServiceImpl orderServiceImpl;

    @Autowired
    DepartRealServiceImpl departRealServiceImpl;

    @Autowired
    BusVehicleServiceImpl busVehicleServiceImpl;

    @Autowired
    BuyTicketServiceImpl buyTicketServiceImpl;

    @Autowired
    CommonDao commonDao;

    /**
     * 订单失效时间（分钟）
     */
    public static int orderDisabledTime = 15;

    // 支付，查询等相关地址
    public static final String PAY_URL = "https://api.cc-pay.cn/api/app/pay";
    public static final String APP_ID = "e848a2914b2845b9";
    public static final String MCH_ID = "100f9e4e298e45d4844ef778fb5f0c67";

    @RequestMapping("ShiftsSearch")
    @ResponseBody
    public String ShiftsSearch(Model model, String up_origin_name, String up_terminal_name, String shifts_date) {
        try {
            List<Map<String, Object>> shiftsSearch = wxShiftsServiceImp.ShiftsSearch(up_origin_name, up_terminal_name,
                    shifts_date);
            return JSON.toJSONString(shiftsSearch);
        } catch (Exception e) {
            Start.projectLog.writeError(e);
            return "";
        }
    }

    // 订票须知
    @RequestMapping("buyTicketAttention")
    public String buyTicketAttention(HttpSession session) {
        session.setAttribute("name", "zah5");
        return "wx/buyTicketAttention";
    }

    // 班次查询
    @RequestMapping("ShiftTheQuery")
    public String ShiftTheQuery(Model model) {
        try {
            List<Map<String, Object>> searchOriginName = wxShiftsServiceImp.SearchOriginName();
            List<Map<String, Object>> searchTerminalName = wxShiftsServiceImp.SearchTerminalName();
            if (searchOriginName != null && searchTerminalName != null && searchOriginName.size() > 0
                    && searchTerminalName.size() > 0) {
                model.addAttribute("searchOriginName", searchOriginName);
                model.addAttribute("searchTerminalName", searchTerminalName);
            }
            SimpleDateFormat Date = new SimpleDateFormat("yyyy-MM-dd");
            String nowDate = Date.format(new Date());
            model.addAttribute("nowDate", nowDate);
            return "wx/ShiftTheQuery";
        } catch (Exception e) {
            Start.projectLog.writeError(e);
            model.addAttribute("message", "网络错误，请稍后再试！");
            return "admin/error";
        }

    }

    @Autowired
    StringRedisTemplate redisTemplate;

    // 购买车票
    @RequestMapping("ticketInfomation")
    public String ticketInfomation(HttpServletResponse response, String shifts_date, String shifts_number, HttpServletRequest request,
                                   Model model) {

        try {
            int type = 2;
            response.setContentType("text/html;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            // 查询班次
            List<Map<String, Object>> list = wxShiftsServiceImp.queryShiftInfo(shifts_number, shifts_date);
            if (list.size() == 0) {
                try {
                    response.getWriter().write("<script>window.history.back(-1); alert('当前班次不存在！')</script>");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            Map<String, Object> map = list.get(0);
            if (null == map.get("seat_num")) {
                response.getWriter().write("<script>window.history.back(-1); alert('系统错误！')</script>");
                return null;
            }
            long nowtime = System.currentTimeMillis() / 1000;
            long depart_time = Function.getInstance().timeStrToSeconds(
                    map.get("shifts_date").toString() + " " + map.get("depart_time").toString(), "yyyy-MM-dd HH:mm");
            if (depart_time <= 0) {
                Start.projectLog.writeInfo("*不应该出现的错误");
                response.getWriter().write("<script>window.history.back(-1); alert('系统错误！')</script>");
                return null;
            }

            // 判断是否到购票时间
            if (type == 1) {// 老师
                int ticket_teacher_hour = Integer
                        .parseInt(ConfigController.configMap.get("ticket_teacher_hour").toString());
                if (nowtime < depart_time - ticket_teacher_hour * 60 * 60) {
                    response.getWriter().write("<script>window.history.back(-1); alert('未到购票时间！')</script>");
                    return null;
                }

            } else {// 学生
                int ticket_student_hour = Integer
                        .parseInt(ConfigController.configMap.get("ticket_student_hour").toString());
                if (nowtime < depart_time - ticket_student_hour * 60 * 60) {
                    response.getWriter().write("<script>window.history.back(-1); alert('未到购票时间！')</script>");
                    return null;
                }
            }
            // 已超过发车时间
            if (nowtime >= depart_time) {
                response.getWriter().write("<script>window.history.back(-1); alert('已超过发车时间！')</script>");
                return null;
            }

            // 得到余票数
            int ticket_num = 0;
            if (type == 1) {// 老师
                ticket_num = Integer.parseInt(map.get("seat_num").toString())
                        - Integer.parseInt(map.get("teacher_num").toString())
                        - Integer.parseInt(map.get("student_num").toString());

            } else {// 学生
                int seatRetainNum = Integer.parseInt(ConfigController.configMap.get("seat_retain_num").toString());
                int ticketAllMinute = Integer.parseInt(ConfigController.configMap.get("ticket_all_minute").toString());// 预留教师位售票时间
                ticket_num = Integer.parseInt(map.get("seat_num").toString())
                        - Integer.parseInt(map.get("teacher_num").toString())
                        - Integer.parseInt(map.get("student_num").toString());
                if (nowtime < depart_time - ticketAllMinute * 60) {
                    ticket_num -= seatRetainNum;
                }
            }
            if (ticket_num < 0) {
                ticket_num = 0;
            }

            // 得到票价
            double price = type == 1
                    ? Double.parseDouble(ConfigController.configMap.get("ticket_teacher_price").toString())
                    : Double.parseDouble(ConfigController.configMap.get("ticket_student_price").toString());

            model.addAttribute("shifts_map", map);
            model.addAttribute("type", type);
            model.addAttribute("week", Function.getInstance().getWeek(shifts_date));
            model.addAttribute("ticket_num", ticket_num);
            model.addAttribute("price", price);
            model.addAttribute("shifts_number", shifts_number);
            HttpSession session = request.getSession();
            if (session.getAttribute("currentUser") == null) {
                response.getWriter().write("<script>window.history.back(-1); alert('请先授权登录！')</script>");
                return null;
            }
            WechatUserInfo currentUser = (WechatUserInfo) session.getAttribute("currentUser");
            System.out.println(currentUser);
            String token = UUID.randomUUID().toString().replace("-", "");
            redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + currentUser.getOpenId(), token);
            model.addAttribute("token", token);
            return "wx/ticketInfomation";
        } catch (Exception e) {
            Start.projectLog.writeError(e);
            try {
                response.getWriter().write("<script>window.history.back(-1); alert('系统错误！')</script>");
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return "wx/ticketInfomation";
    }

    // 我要购票
    @RequestMapping("buyTicket")
    public String buyTicket(Model model) {
        try {
            List<Map<String, Object>> searchOriginName = wxShiftsServiceImp.SearchOriginName();
            List<Map<String, Object>> searchTerminalName = wxShiftsServiceImp.SearchTerminalName();
            if (searchOriginName != null && searchTerminalName != null && searchOriginName.size() > 0
                    && searchTerminalName.size() > 0) {
                model.addAttribute("searchOriginName", searchOriginName);
                model.addAttribute("searchTerminalName", searchTerminalName);
            }
            SimpleDateFormat Date = new SimpleDateFormat("yyyy-MM-dd");
            String nowDate = Date.format(new Date());
            model.addAttribute("nowDate", nowDate);
            return "wx/buyTicket";
        } catch (Exception e) {
            Start.projectLog.writeError(e);
            model.addAttribute("message", "网络错误，请稍后再试！");
            return "admin/error";
        }
    }

    // 我要购票
    @RequestMapping("oauth")
    public String getUserInfo(HttpServletRequest request, HttpSession session, Map<String, Object> map) {
        WechatUserInfo wechatUserInfo = null;
        /*String code = request.getParameter("code");
        try {
            wechatUserInfo = getTheCode(session, code);
            session.setAttribute("currentUser", wechatUserInfo);
            System.out.println(JSON.toJSON(wechatUserInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
        wechatUserInfo = new WechatUserInfo();
        wechatUserInfo.setOpenId("123");
        session.setAttribute("currentUser", wechatUserInfo);
        map.put("wechatUserInfo", wechatUserInfo);

        return "wx/buyTicketAttention";
    }

    /**
     * 获取用户的openId
     *
     * @param session
     * @param code
     * @return 返回封装的微信用户的对象
     * @throws Exception
     */
    private WechatUserInfo getTheCode(HttpSession session, String code) throws Exception {
        Map<String, String> authInfo = new HashMap<>();
        String openId = "";
        System.out.println("code" + code);
        if (code != null) {
            // 调用根据用户的code得到需要的授权信息
            authInfo = userService.getAuthInfo(code);
            // 获取到openId
            openId = authInfo.get("Openid");

        } // 获取基础刷新的接口访问凭证（目前还没明白为什么用authInfo.get("AccessToken");这里面的access_token就不行）
        String OuthAccessToken = authInfo.get("AccessToken");
        // 获取到微信用户的信息
        WechatUserInfo userinfo = userService.getUserInfo(OuthAccessToken, openId);
        System.out.println(userinfo);
        session.setAttribute("currentUser", userinfo);
        return userinfo;
    }

    /**
     * 通知公告
     *
     * @return 所有通知
     */
    @RequestMapping("notification")
    public String Notification() {
        return "wx/inform";
    }

    @RequestMapping("getNotification")
    @ResponseBody
    public String getNotification() {
        return noticeServiceImpl.selectWx();
    }

    /**
     * 投诉建议，联系我们
     *
     * @return status: 1:成功，0:失败
     * @throws IOException
     */
    @RequestMapping("opinion")
    public String Opinion(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        try {
            com.zah.entity.Opinion opinion = new com.zah.entity.Opinion();
            String content = request.getParameter("addinfo");
            if (content == null || "".equals(content) || content.length() > 1000) {
                response.getWriter().print("<script>;window.history.back(-1);alert('内容不能为空或者内容长度不超过1000位！')</script>");
                return null;
            }
            opinion.setOpinion_content(content);
            opinion.setPassenger_id(8);
            opinion.setSubmit_time(System.currentTimeMillis() / 1000);
            int result = opinionServiceImpl.addWx(opinion);
            if (result > 0) {
                response.getWriter().write("<script>window.history.back(-1); alert('提交成功！')</script>");
            } else {
                response.getWriter().write("<script>window.history.back(-1); alert('提交失败！')</script>");
            }
        } catch (Exception e) {
            Start.projectLog.writeError(e);
            try {
                response.getWriter().write("<script>window.history.back(-1); alert('系统异常！')</script>");
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return null;
    }

    @RequestMapping("contactUs")
    public String contactUs(HttpServletResponse response, HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession();
        Object currentUser = session.getAttribute("currentUser");
        System.out.println(currentUser);
        return "wx/contactUs";
    }

    /**
     * 失物招领
     */
    @RequestMapping("retrieve")
    public String Retrieve() {

        return "wx/lostAndFound";
    }

    @RequestMapping("getRetrieve")
    @ResponseBody
    public String getRetrieve() {
        return retrieveServiceImpl.selectWx();
    }

    /**
     * 待出行订单
     *
     * @param page
     * @param
     * @param time
     * @param status:1：未出行订单，2：历史订单，3：退款申请
     * @return
     */
    @RequestMapping("WaitingOrder")
    @ResponseBody
    public String WaitingOrder(int page, long time, int status) {
        System.out.printf("aaa");
        List<Order> list = orderServiceImpl.getList((page - 1) * 10, 10, time, status);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "查询成功");
        result.put("data", list);
        return new JSONObject(result).toString();
    }

    /**
     * 退款申请，需添加用户信息判断
     *
     * @param orderId
     * @return
     */
    @RequestMapping("applicationForDrawback")
    @ResponseBody
    public String applicationForDrawback(String orderId) {
        Map<String, Object> map = new HashMap<>();
        Order order = orderServiceImpl.getOrder(orderId);
        if (null == order) {
            map.put("status", "2");
            map.put("message", "不存在该订单");
            return new JSONObject(map).toJSONString();
        }
        if (order.getOrder_state() != 1) {
            map.put("status", "2");
            map.put("message", "订单状态异常");
            return new JSONObject(map).toJSONString();
        }
        int num = orderServiceImpl.applicationForDrawback(orderId);
        if (num > 0) {
            map.put("status", "1");
            map.put("message", "申请成功");
            return new JSONObject(map).toJSONString();
        } else {
            map.put("status", "2");
            map.put("message", "申请失败");
            return new JSONObject(map).toJSONString();
        }
    }

    /**
     * 微信购票下单接口
     *
     * @param shiftsNumber:班次
     * @param shiftsDate:时间
     * @return
     * @throws Exception
     * @throws NumberFormatException
     * @throws ParseException
     */
    @Autowired
    AlipayTemplate alipayTemplate;

    @ResponseBody
    @RequestMapping(value = "buyTicketForWX", produces = "text/html")
    public String buyTicketForWX(String orderId) throws AlipayApiException {
        Order order = orderServiceImpl.getOrder(orderId);
        if (order == null) {
            return "<script>window.history.back(-1); alert('系统异常！')</script>";
        }
        if (!String.valueOf(order.getOrder_state()).equals("0")) {
            return "<script>window.history.back(-1); alert('订单已超时，请重新下单！')</script>";
        }
        PayVo payVo = new PayVo();

        long create_time = order.getCreate_time();
        create_time= create_time*1000L;
        Date date=new Date(create_time+1000*60L);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeExpire = simpleDateFormat.format(date);
        payVo.setTimeExpire(timeExpire);
        payVo.setTotal_amount(order.getPrice() + "");
        payVo.setOut_trade_no(orderId);
        payVo.setSubject(order.getShifts_date() + "  " + order.getShifts_number() + "车票购买");
        payVo.setBody(order.getShifts_date() + "  " + order.getShifts_number() + "车票购买");
        String pay = alipayTemplate.pay(payVo);
        return pay;
    }

    @RequestMapping("/success")
    public String successPage(HttpServletRequest request, Model model) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        System.out.println(parameterMap);
        String result = null;
        try {
            Map<String, String> params = new HashMap<String, String>();
            Map requestParams = request.getParameterMap();
            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
                params.put(name, valueStr);
            }

            //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
            //商户订单号

            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");

            //支付宝交易号

            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

            //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
            //计算得出通知验证结果
            //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
            boolean verify_result = AlipaySignature.rsaCheckV1(params, AlipayTemplate.ALIPAY_PUBLIC_KEY, AlipayTemplate.CHARSET, "RSA2");

            if (verify_result) {//验证成功
                //////////////////////////////////////////////////////////////////////////////////////////
                //请在这里加上商户的业务逻辑程序代码
                //该页面可做页面美工编辑

                //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
                System.out.println("验证通过");
                //////////////////////////////////////////////////////////////////////////////////////////
            } else {
                //该页面可做页面美工编辑
                System.out.println("验证失败");
            }
        } catch (Exception e) {
            Start.projectLog.writeInfo("支付回调接口出现异常");
            Start.projectLog.writeError(e);
            return "error/500";
        }
        return "wx/paySuccess";
    }

    @PostMapping("/notify")
    @ResponseBody
    public String notify(HttpServletRequest request) {
        try {
            //获取支付宝POST过来反馈信息
            Map<String, String> params = new HashMap<String, String>();
            Map requestParams = request.getParameterMap();
            System.out.println(JSON.toJSONString(requestParams));
            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
                params.put(name, valueStr);
            }
            //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
            //商户订单号

            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //支付宝交易号

            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");

            //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
            //计算得出通知验证结果
            //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
            boolean verify_result = AlipaySignature.rsaCheckV1(params, AlipayTemplate.ALIPAY_PUBLIC_KEY, AlipayTemplate.CHARSET, "RSA2");

            if (verify_result) {//验证成功
                //////////////////////////////////////////////////////////////////////////////////////////
                //请在这里加上商户的业务逻辑程序代码
                //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
                if (trade_status.equals("TRADE_FINISHED")) {
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                    //如果有做过处理，不执行商户的业务程序
                    String access_token = HttpClient.builder("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + WechatConstants.APPID + "&secret=" + WechatConstants.APPSECRET, HttpMethod.GET,String.class).execute().get();
                    JSONObject jsonObject = JSON.parseObject(access_token);
                    if (access_token == null) {

                    } else {
                        Map<String, Object> postData = new HashMap<>();
                        postData.put("touser", "oZ0Ef5wUM-oicmB_aSlDRWuc5_A8");
                        postData.put("template_id", "Dmz_5BU51GqPa1ahMkoRpZU7fARWyRiTYfz6I0LJSoc");
                        postData.put("topcolor", "#173177");
                        postData.put("url", "http://2u3v811432.qicp.vip/wechat/waitingOrder");
                        Map<String, Object> data = new HashMap<>();
                        Map<String, Object> dataInner = new HashMap<>();
                        dataInner.put("value", "周逸龙");
                        dataInner.put("color", "red");
                        data.put("username", dataInner);
                        dataInner= new HashMap<>();
                        dataInner.put("value", "2222");
                        dataInner.put("color", "#173177");
                        data.put("price", dataInner);
                        dataInner=null;
                        postData.put("data", data);
                        String s = JSON.toJSONString(data);
                        System.out.println(s);
                        String ss= JSON.toJSONString(postData);
                        System.out.println(ss);
                        Map map = HttpClient.builder("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" +jsonObject.getString("access_token") , HttpMethod.POST, Map.class).body(postData).execute().get();
                        System.out.println(map.toString());

                    }

                    //注意：
                    //如果签约的是可退款协议，退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                    //如果没有签约可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
                } else if (trade_status.equals("TRADE_SUCCESS")) {
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                    //如果有做过处理，不执行商户的业务程序

                    //注意：
                    //如果签约的是可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。

                    //
                    String access_token = HttpClient.builder("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + WechatConstants.APPID + "&secret=" + WechatConstants.APPSECRET, HttpMethod.GET,String.class).execute().get();
                    JSONObject jsonObject = JSON.parseObject(access_token);
                    if (access_token == null) {

                    } else {
                        Map<String, Object> postData = new HashMap<>();
                        postData.put("touser", "oZ0Ef5wUM-oicmB_aSlDRWuc5_A8");
                        postData.put("template_id", "Dmz_5BU51GqPa1ahMkoRpZU7fARWyRiTYfz6I0LJSoc");
                        postData.put("topcolor", "#173177");
                        postData.put("url", "http://2u3v811432.qicp.vip/wechat/waitingOrder");
                        Map<String, Object> data = new HashMap<>();
                        Map<String, Object> dataInner = new HashMap<>();
                        dataInner.put("value", "周逸龙");
                        dataInner.put("color", "red");
                        data.put("username", dataInner);
                        dataInner= new HashMap<>();
                        dataInner.put("value", "2222");
                        dataInner.put("color", "#173177");
                        data.put("price", dataInner);
                        dataInner=null;
                        postData.put("data", data);
                        String s = JSON.toJSONString(data);
                        System.out.println(s);
                        String ss= JSON.toJSONString(postData);
                        System.out.println(ss);
                        Map map = HttpClient.builder("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" +jsonObject.getString("access_token") , HttpMethod.POST, Map.class).body(postData).execute().get();
                        System.out.println(map.toString());

                    }

                }

                //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——


                //////////////////////////////////////////////////////////////////////////////////////////
            } else {//验证失败

            }
        } catch (Exception e) {
            Start.projectLog.writeInfo("支付回调接口出现异常");
            Start.projectLog.writeError(e);
            return "fail";
        }
        return  "success";
    }
@Autowired
private RabbitTemplate rabbitTemplate;

    @RequestMapping("buyTicketForWX2")
    @ResponseBody
    public Map<String, Object> buyTicketForWX2(HttpServletResponse response, String shiftsNumber, String shiftsDate) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 乘客类型，1：教师, 2：学生
            int passengerType = 2;
            // 发车时间
            long time = 0l;
            long timeStart = System.currentTimeMillis();
            synchronized (MonitorController.SHIFTSLOCK) {
                if (System.currentTimeMillis() - timeStart > 10000) {
                    result.put("status", "2");
                    result.put("message", "操作失效");
                    return result;
                }
                List<Map<String, Object>> list = wxShiftsServiceImp.getShiftsList(shiftsNumber, shiftsDate);
                if (list.size() == 0) {
                    result.put("status", "2");
                    return result;
                }
                Map<String, Object> map = list.get(0);
                int vehicleId = Integer.parseInt(map.get("vehicle_id").toString());
                time = Function.getInstance().timeStrToSeconds(
                        map.get("shifts_date").toString() + " " + map.get("depart_time").toString(),
                        "yyyy-MM-dd HH:mm");
                if (time <= 0) {
                    Start.projectLog.writeInfo("*不应该出现的错误");
                    result.put("status", "2");
                    result.put("message", "系统错误");
                    return result;
                }
                time *= 1000;
                BusVehicle busVehicle = busVehicleServiceImpl.getVehicle(vehicleId);
                if (busVehicle == null) {
                    Start.projectLog.writeInfo("*不应该出现的错误");
                    result.put("status", "2");
                    result.put("message", "系统错误");
                    return result;
                }
                // 已出售座位数
                int sellSeatCount = (((int) map.get("student_num")) + (int) map.get("teacher_num"));
                // 预留教师位数
                int seatRetainNum = Integer.parseInt(ConfigController.configMap.get("seat_retain_num").toString());
                // 预留教师位售票时间
                int ticketAllMinute = Integer.parseInt(ConfigController.configMap.get("ticket_all_minute").toString());
                // 票价
                double price = 0;
                // 售票时间
                int ticketHour = 0;
                if (passengerType == 1) {
                    price = Double.parseDouble(ConfigController.configMap.get("ticket_teacher_price").toString());
                    ticketHour = Integer.parseInt(ConfigController.configMap.get("ticket_teacher_hour").toString());
                } else if (passengerType == 2) {
                    price = Double.parseDouble(ConfigController.configMap.get("ticket_student_price").toString());
                    ticketHour = Integer.parseInt(ConfigController.configMap.get("ticket_student_hour").toString());
                } else {
                    Start.projectLog.writeInfo("*不应该出现的错误");
                    result.put("status", "2");
                    result.put("message", "系统错误");
                    return result;
                }
                long currentTime = System.currentTimeMillis();
                // 判断是否到购票时间
                if ((ticketHour * 60 * 60 * 1000 + currentTime) < time) {
                    result.put("status", "2");
                    result.put("message", "未到购票时间");
                    return result;
                }
                // 判断该班次是否已发车
                if (currentTime > time) {
                    result.put("status", "2");
                    result.put("message", "已超过购票时间");
                    return result;
                }
                // 判断是否还有座位
                if (sellSeatCount >= busVehicle.getSeat_num()) {
                    result.put("status", "2");
                    result.put("message", "当前车次票已售罄");
                    return result;
                }

                if (passengerType == 2) {
                    if ((sellSeatCount + seatRetainNum) >= busVehicle.getSeat_num()
                            && ticketAllMinute * 60 * 1000 + currentTime < time) {
                        result.put("status", "2");
                        result.put("message", "当前车次票已售罄");
                        return result;
                    }
                }
                // 更新座位信息以及创建订单
                Map<String, Object> resultMap = buyTicketServiceImpl.butTicket(passengerType, price * 100, shiftsNumber,
                        shiftsDate, 1, 1, APP_ID, MCH_ID, PAY_URL);

                if ("0".equals(resultMap.get("orderId"))) {
                    result.put("status", "2");
                    result.put("message", "购票失败");
                    return result;
                }
                result.put("status", "1");
                result.put("message", "下单成功");
                result.put("orderId", resultMap.get("orderId"));
                result.put("url", resultMap.get("url"));
                return result;
            }
        } catch (Exception e) {
            Start.projectLog.writeInfo("购票失败:" + e);
            Start.projectLog.writeError(e);
            e.printStackTrace();
            result.put("status", "2");
            result.put("message", "购票失败");
            return result;
        }
    }

    /**
     * 支付回调接口
     *
     * @param request
     * @return
     */
    @RequestMapping("callBackForPay")
    @ResponseBody
    public String CallBackForPay(HttpServletRequest request) {
        System.out.println("进入支付回调接口");
        String orderId = request.getParameter("target_order_id");
        try {
            String result = queryOrder(orderId);
            if (null != result) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                if ("200".equals(jsonObject.get("code"))) {
                    JSONObject data = JSONObject.parseObject(jsonObject.get("data").toString());
                    String orderSn = data.getString("order_id");
                    String status = data.getString("status");
                    if ("SUCCESS".equals(status)) {
                        orderServiceImpl.updateOrderSn(Long.parseLong(orderId), orderSn);
                    }
                }
            }
        } catch (Exception e) {
            Start.projectLog.writeInfo("支付回调接口出现异常");
            Start.projectLog.writeError(e);
        }
        return "success";
    }

    public String queryOrder(String orderId) {
        Map<String, Object> postData = new TreeMap<String, Object>();
        postData.put("app_id", APP_ID);
        postData.put("mch_id", MCH_ID);
        postData.put("format", "JSON");
        postData.put("version", "1");
        postData.put("sign_method", "RSA2");
        postData.put("sign_v", "1");
        postData.put("timestamp", CCPlayUtils.getInstance().getUTC());
        postData.put("sign_nonce", UUID.randomUUID().toString().replaceAll("-", ""));
        postData.put("method", "query");
        postData.put("target_order_id", orderId);
        String sign = "";
        String result = "";
        try {
            sign = CCPlayUtils.getInstance().getSign(postData);
            postData.put("sign", sign);
            result = com.zah.util.wxFunction.getInstance().request(PAY_URL, null, null, null, null, postData, null,
                    "UTF-8", 0, 0);
        } catch (Exception e) {
            Start.projectLog.writeInfo("查询订单接口出现异常");
            Start.projectLog.writeError(e);
        }
        return result;
    }

    @RequestMapping("queryShiftInfo")
    @ResponseBody
    public String queryShiftInfo(String shifts_date, String shifts_number, String depart_time) {
        Map<String, Object> map = wxShiftsServiceImp.queryShiftInfo(shifts_number, shifts_date).get(0);
        double price;
        int num;
        int type = 1;
        int seatRetainNum = Integer.parseInt(ConfigController.configMap.get("seat_retain_num").toString());
        if (type == 1) {
            price = Double.parseDouble(ConfigController.configMap.get("ticket_teacher_price").toString());
            num = Integer.parseInt(map.get("seat_num").toString()) - Integer.parseInt(map.get("teacher_num").toString())
                    - Integer.parseInt(map.get("student_num").toString()) - seatRetainNum;
        } else {
            price = Integer.parseInt(ConfigController.configMap.get("ticket_student_price").toString());
            num = Integer.parseInt(map.get("seat_num").toString()) - Integer.parseInt(map.get("teacher_num").toString())
                    - Integer.parseInt(map.get("student_num").toString());
        }
        map.put("price", price);
        map.put("num", num > 0 ? num : 0);
        map.put("type", type);
        return new JSONObject(map).toJSONString();
    }

    @RequestMapping("ticketPayment")
    public String ticketPayment(HttpServletResponse response, String shifts_date, String shifts_number, String token, Model model, HttpServletRequest request) {
        //验证令牌
        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        HttpSession session = request.getSession();
        if (session.getAttribute("currentUser") == null) {
            try {
                response.getWriter().write("<script>window.history.back(-1); alert('请先授权登录！')</script>");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        WechatUserInfo wechatUserInfo = (WechatUserInfo) session.getAttribute("currentUser");
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + wechatUserInfo.getOpenId()), token);
        if (result == 0L) {
            try {
                response.getWriter().write("<script>window.history.back(-1); alert('请勿重复提交订单！')</script>");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        // 乘客类型，1：教师, 2：学生
        int passengerType = 2;
        // 发车时间
        long time = 0l;
        long timeStart = System.currentTimeMillis();
        List<Map<String, Object>> list2 =null;
        Map<String, Object> resultMap=null;
        synchronized (MonitorController.SHIFTSLOCK) {
			/*	if (System.currentTimeMillis() - timeStart > 10000) {
					result.put("status", "2");
					result.put("message", "操作失效");
					return result;
				}*/
            List<Map<String, Object>> list = wxShiftsServiceImp.getShiftsList(shifts_number, shifts_date);
            if (list.size() == 0) {
                try {
                    response.getWriter().write("<script>window.history.back(-1); alert('系统异常！')</script>");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            Map<String, Object> map = list.get(0);
            int vehicleId = Integer.parseInt(map.get("vehicle_id").toString());
            time = Function.getInstance().timeStrToSeconds(
                    map.get("shifts_date").toString() + " " + map.get("depart_time").toString(),
                    "yyyy-MM-dd HH:mm");
            if (time <= 0) {
                Start.projectLog.writeInfo("*不应该出现的错误");
                try {
                    response.getWriter().write("<script>window.history.back(-1); alert('系统异常！')</script>");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            time *= 1000;
            BusVehicle busVehicle = busVehicleServiceImpl.getVehicle(vehicleId);
            if (busVehicle == null) {
                Start.projectLog.writeInfo("*不应该出现的错误");
                try {
                    response.getWriter().write("<script>window.history.back(-1); alert('系统异常！')</script>");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            // 已出售座位数
            int sellSeatCount = (((int) map.get("student_num")) + (int) map.get("teacher_num"));
            // 预留教师位数
            int seatRetainNum = Integer.parseInt(ConfigController.configMap.get("seat_retain_num").toString());
            // 预留教师位售票时间
            int ticketAllMinute = Integer.parseInt(ConfigController.configMap.get("ticket_all_minute").toString());
            // 票价
            double price = 0;
            // 售票时间
            int ticketHour = 0;
            if (passengerType == 1) {
                price = Double.parseDouble(ConfigController.configMap.get("ticket_teacher_price").toString());
                ticketHour = Integer.parseInt(ConfigController.configMap.get("ticket_teacher_hour").toString());
            } else if (passengerType == 2) {
                price = Double.parseDouble(ConfigController.configMap.get("ticket_student_price").toString());
                ticketHour = Integer.parseInt(ConfigController.configMap.get("ticket_student_hour").toString());
            } else {
                Start.projectLog.writeInfo("*不应该出现的错误");
                try {
                    response.getWriter().write("<script>window.history.back(-1); alert('系统异常！')</script>");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            long currentTime = System.currentTimeMillis();
            // 判断是否到购票时间
            if ((ticketHour * 60 * 60 * 1000 + currentTime) < time) {
                try {
                    response.getWriter().write("<script>window.history.back(-1); alert('未到购票时间！')</script>");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            // 判断该班次是否已发车
            if (currentTime > time) {
                try {
                    response.getWriter().write("<script>window.history.back(-1); alert('已超过购票时间！')</script>");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            // 判断是否还有座位
            if (sellSeatCount >= busVehicle.getSeat_num()) {
                try {
                    response.getWriter().write("<script>window.history.back(-1); alert('当前车次票已售罄！')</script>");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            if (passengerType == 2) {
                if ((sellSeatCount + seatRetainNum) >= busVehicle.getSeat_num()){
                    try {
                        response.getWriter().write("<script>window.history.back(-1); alert('当前车次票已售罄！')</script>");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }
            // 查询班次

           list2=wxShiftsServiceImp.queryShiftInfo(shifts_number, shifts_date);
            if (list.size() == 0) {
                try {
                    response.getWriter().write("<script>window.history.back(-1); alert('当前班次不存在！')</script>");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            // 更新座位信息以及创建订单
            resultMap = buyTicketServiceImpl.butTicket2(passengerType, price * 100, shifts_number,
                    shifts_date, 1, 1, APP_ID, MCH_ID, PAY_URL);

        }
        if(resultMap.get("orderId").equals("1")){
            try {
                response.getWriter().write("<script>window.history.back(-1); alert('系统异常！')</script>");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        int type = 1;
        Map<String, Object> map = list2.get(0);
        // 得到票价
        double price = type == 1 ? Double.parseDouble(ConfigController.configMap.get("ticket_teacher_price").toString())
                : Double.parseDouble(ConfigController.configMap.get("ticket_student_price").toString());
        model.addAttribute("orderId", resultMap.get("orderId"));
        model.addAttribute("shifts_map", map);
        model.addAttribute("type", type);
        model.addAttribute("week", Function.getInstance().getWeek(shifts_date));
        model.addAttribute("price", price);
        model.addAttribute("shifts_number", shifts_number);
        //下单
        return "wx/ticketPayment";
    }

    @RequestMapping("qrCode")
    @ResponseBody
    public void qrCode(HttpServletRequest request, HttpServletResponse response) {
        String content;
        try {
            content = "123141241";
            // 生成二维码
            BitMatrix qRcodeImg = QrCodeUtils.generateQRCodeStream(content, response);
            // 将二维码输出到页面中
            MatrixToImageWriter.writeToStream(qRcodeImg, "png", response.getOutputStream());
        } catch (Exception e) {
            Start.projectLog.writeError(e);
        }
    }

    /*测试*/
    @RequestMapping("waitingOrder")
    public String waitingOrder() {
        return "wx/waitingOrder";
    }

    @RequestMapping("refundOrder")
    public String refundOrder() {
        return "wx/refundOrder";
    }

    @RequestMapping("orderHistory")
    public String orderHistory() {
        return "wx/orderHistory";
    }
}
