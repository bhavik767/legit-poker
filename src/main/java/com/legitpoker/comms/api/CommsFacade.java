package com.legitpoker.comms.api;


import com.legitpoker.comms.dto.*;

public interface CommsFacade {
    MessageView send(String tableCode, SendMessageRequest req);
    GetMessagesResponse list(String tableCode, String requestingNick, Long sinceId);
}
