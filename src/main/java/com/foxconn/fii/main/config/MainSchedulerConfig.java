package com.foxconn.fii.main.config;

import com.foxconn.fii.main.data.entity.UserChannel;
import com.foxconn.fii.main.data.repository.UserChannelRepository;
import com.foxconn.fii.rabbitmq.service.RabbitmqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Properties;

@Slf4j
@Configuration
@EnableScheduling
public class MainSchedulerConfig {

    @Autowired
    private RabbitmqService rabbitmqService;

    @Autowired
    private UserChannelRepository channelRepository;

    @Scheduled(cron = "0 0 0 * * *")
//    @Scheduled(cron = "0 * * * * *")
    public void inactiveChannel() {
        log.info("### inactive channel START");
        List<UserChannel> channelList = channelRepository.findAll();
        for (UserChannel channel : channelList) {
            if (channel.getLatestActiveTime() != null && (System.currentTimeMillis() - channel.getLatestActiveTime().getTime() > 3 * 24 * 60 * 60 * 1000L)) {
//            if (channel.getLatestActiveTime() != null && (System.currentTimeMillis() - channel.getLatestActiveTime().getTime() > 360000L)) {
                Properties messageChannelProperties = rabbitmqService.getQueueProperties(channel.getMessageChannelName());
                Properties notifyChannelProperties = rabbitmqService.getQueueProperties(channel.getNotifyChannelName());

                boolean existMessageChannel = messageChannelProperties != null && ((Integer) messageChannelProperties.getOrDefault("QUEUE_CONSUMER_COUNT", 0) > 0);
                boolean existNotifyChannel = notifyChannelProperties != null && ((Integer) notifyChannelProperties.getOrDefault("QUEUE_CONSUMER_COUNT", 0) > 0);

                if (!existMessageChannel && !existNotifyChannel) {
                    rabbitmqService.deleteQueue(channel.getMessageChannelName());
                    rabbitmqService.deleteQueue(channel.getNotifyChannelName());
                    channelRepository.deleteById(channel.getId());
                }
            }
        }
        log.info("### inactive channel END");
    }

}
