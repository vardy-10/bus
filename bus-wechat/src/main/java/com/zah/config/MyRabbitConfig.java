package com.zah.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyRabbitConfig {

    @Bean
    public MessageConverter messageConverter(){
        return  new Jackson2JsonMessageConverter();
    }

    @Bean
    public Exchange seatEventExchange(){
        return new TopicExchange("seat-event-exchange",true,false);
    }
    @Bean
    public Queue seatReleaseQueue(){
        return  new Queue("seat.release.queue",true,false,false);
    }
    @Bean
    public Queue seatDelayQueue(){
        Map<String,Object> arguments=new HashMap<>();
        arguments.put("x-dead-letter-exchange","seat-event-exchange");
        arguments.put("x-dead-letter-routing-key","seat.release");
        arguments.put("x-message-ttl",1000*60);
        return  new Queue("seat.delay.queue",true,false,false,arguments);
    }

    @Bean
    public Binding seatReleaseBinding(){
        return  new Binding("seat.release.queue",Binding.DestinationType.QUEUE,"seat-event-exchange","seat.release.#",null);
    }

    @Bean
    public Binding seatLockBinding(){
        return  new Binding("seat.delay.queue",Binding.DestinationType.QUEUE,"seat-event-exchange","seat.lock",null);
    }
    @Autowired
    CachingConnectionFactory cachingConnectionFactory;
    @Bean
    RabbitTemplate rabbitTemplate(){
        RabbitTemplate rabbitTemplate=new RabbitTemplate(cachingConnectionFactory);
        //消息发送到中间件回调，成功投递
        //消息投递到中间件broker
        /**
         * b ack
         *s  cause
         */
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                //获得消息Id
                String msgId = correlationData.getId();
                if(b){
                    System.out.println("p->b消息投递成功！");
                    //TODO
                    /*mailSendLogMapper.updateMailSendLogStatus(msgId,MailConstants.SUCCESS);*/
                }else {
                    System.out.println(msgId+"p->b消息投递失败！原因:"+s);
                }
            }
        });
        //交换机消息发送到队列回调
        /**
         *  message msg
         *  i reqcode
         *  s reqText
         *  s1 exchange
         *  s2 routingkey
         */
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
                System.out.println("e->q消息发送失败");
            }
        });
        return  rabbitTemplate;
    }
}
