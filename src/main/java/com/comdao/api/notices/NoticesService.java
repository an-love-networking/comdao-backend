package com.comdao.api.notices;

import com.comdao.api.notices.entities.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticesService {
    private final NoticesRepository noticesRepository;

    public Page<Notice> getNotifications(Long userId, Integer page, Integer size) {
        return noticesRepository.findByUser_IdOrUser_IdIsNullOrderByCreatedAsc(userId, PageRequest.of(page, size));
    }
}
