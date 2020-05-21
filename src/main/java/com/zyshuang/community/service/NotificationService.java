package com.zyshuang.community.service;

import com.zyshuang.community.dto.NotificationDTO;
import com.zyshuang.community.dto.PaginationDTO;
import com.zyshuang.community.entities.Notification;
import com.zyshuang.community.entities.NotificationExample;
import com.zyshuang.community.entities.User;
import com.zyshuang.community.enums.NotificationStatusEnum;
import com.zyshuang.community.enums.NotificationTypeEnum;
import com.zyshuang.community.exception.CustomerErrorCode;
import com.zyshuang.community.exception.CustomerException;
import com.zyshuang.community.mapper.NotificationMapper;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;

    public PaginationDTO listByUserId(Long userId, Integer page, Integer size) {

        PaginationDTO paginationDTO = new PaginationDTO();

        Integer totalPage;

        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(userId);
        Integer totalCount = (int) notificationMapper.countByExample(notificationExample);

        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }

        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }

        //当前用户通知分页
        paginationDTO.setPagination(totalPage,page);

        //查询数据索引值
        int offset = size*(page-1);

        //查询通知并分页
        NotificationExample example = new NotificationExample();
        example.createCriteria()
                .andReceiverEqualTo(userId);
        //通知降序
        example.setOrderByClause("gmt_create desc");

        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));

        if (notifications.size() == 0){
            return paginationDTO;
        }

        //放置查询出来的数据
        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification,notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }

        paginationDTO.setData(notificationDTOS);
        return paginationDTO;
    }

    /**
     * 未读
     * @param userId
     * @return
     */
    public Long unreadCount(Long userId) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(userId)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus()); //只查询未读状态的回复
        notificationExample.setOrderByClause("gmt_create desc");
        return notificationMapper.countByExample(notificationExample);
    }

    /**
     * 已读
     * @param id
     * @param user
     * @return
     */
    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        //判断此通知是否存在
        if (notification == null){
            throw new CustomerException(CustomerErrorCode.NOTIFICATION_NOT_FOUND);
        }
        //判断接收通知的是否是当前登录用户
        if (notification.getReceiver() != user.getId()){
            throw new CustomerException(CustomerErrorCode.READ_NOTIFICATION_FAIL);
        }
        //点击后变为已读状态 更新数据库
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);

        //重新绑定数据
        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;
    }
}
