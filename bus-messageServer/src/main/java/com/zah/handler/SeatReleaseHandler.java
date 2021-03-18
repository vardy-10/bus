package com.zah.handler;

import com.rabbitmq.client.Channel;
import com.zah.dao.CommonDao;
import com.zah.entity.MqMessage;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RabbitListener(queues = "seat.release.queue")
public class SeatReleaseHandler {

    @Autowired
    private CommonDao commonDao;

    @RabbitHandler
    public void handler(Message message, Channel channel, MqMessage mqMessage) throws IOException {
        MessageHeaders headers = message.getHeaders();
        String msgId = (String) headers.get("spring_returned_message_correlation");
        Long tag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        System.out.println(mqMessage);
        Map<String, String> param = new HashMap<String, String>();

        String sql = "select * from  sbus_order where order_id=#{param.order_id}";
        param.put("order_id", mqMessage.getOrderId());
        List<Map<String, Object>> query = commonDao.query(sql, param);
        if (query == null || query.size() <= 0) {
            //不存在订单
        } else {
            Map<String, Object> orderInfo = query.get(0);
            String order_state =orderInfo.get("order_state")+"";
            if (order_state != "1") {
                //释放座位
                param = new HashMap<String, String>();
                param.put("shifts_number",mqMessage.getShiftsNumber());
                param.put("shifts_date",mqMessage.getShiftsDate());
                sql = "update sbus_shifts_real set student_num = (student_num -1) where shifts_number=#{param.shifts_number} AND shifts_date=#{param.shifts_date}";
                int result = commonDao.execute(sql, param);
                if (result < 0) {
                    throw  new RuntimeException("释放座位失败!");
                }
                param = new HashMap<String, String>();
                param.put("order_id",mqMessage.getOrderId());
                 sql = "update sbus_order set order_state=4 where  order_id=#{param.order_id}";
                result= commonDao.execute(sql,param);
                if (result < 0) {
                    throw  new RuntimeException("修改订单状态失败!");
                }
                System.out.println("订单:"+mqMessage.getOrderId()+"超时未支付,释放座位成功!");
                //消息签收
                channel.basicAck(tag, false);
            }else {
                System.out.println("订单:"+mqMessage.getOrderId()+"订单已支付!");
                channel.basicAck(tag, false);
            }

        }

        //消息签收

      /*  if (redisTemplate.opsForHash().entries(REDIS_MAIL_LOG).containsKey(msgId)) {
            //redis中存在key 表示已经消费过
            channel.basicAck(tag, false);
            return;
        }*/

   /*     try {
            redisTemplate.opsForHash().put(REDIS_MAIL_LOG, msgId, "zah");
            //消息签收
            channel.basicAck(tag, false);
            //修改消息状态
            logger.info(msgId + ":邮箱发送成功!");
        } catch (Exception e) {
            //消费失败重新送到队列
            channel.basicNack(tag, false, true);
            e.printStackTrace();
            logger.error(msgId + ":邮箱发送失败！" + e.getMessage());
        }*/
    }

}
