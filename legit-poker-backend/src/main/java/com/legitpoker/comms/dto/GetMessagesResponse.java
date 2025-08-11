package com.legitpoker.comms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter @AllArgsConstructor
public class GetMessagesResponse {
    private List<MessageView> messages;
    private Long lastId;
}
