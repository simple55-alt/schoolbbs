package cdu.zb.service;

import cdu.zb.dto.TopicDto;
import cdu.zb.entity.TopicEntity;
import cdu.zb.response.TopicResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author accountw
 * @since 2020-02-10
 */
public interface TopicService extends IService<TopicEntity> {

    List<TopicResponse> getFirstTopices() throws UnsupportedEncodingException;

    List<TopicResponse> getTopicByPlateid(String plateid,Integer index) throws UnsupportedEncodingException;

    TopicResponse getTopicbyid(String id) throws UnsupportedEncodingException;

    Integer saveTopic(TopicDto topicDto) throws UnsupportedEncodingException;
}
