package com.legitpoker.comms.controller;

import com.legitpoker.comms.api.CommsFacade;
import com.legitpoker.comms.dto.GetMessagesResponse;
import com.legitpoker.comms.dto.MessageView;
import com.legitpoker.comms.dto.SendMessageRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tables/{code}/chat")
public class CommsController {

    private final CommsFacade comms;

    public CommsController(CommsFacade comms) {
        this.comms = comms;
    }

    @PostMapping("/messages")
    public ResponseEntity<MessageView> send(@PathVariable String code,
                                            @Valid @RequestBody SendMessageRequest req) {
        return ResponseEntity.ok(comms.send(code, req));
    }

    @GetMapping("/messages")
    public ResponseEntity<GetMessagesResponse> list(@PathVariable String code,
                                                    @RequestParam(required = false) Long sinceId,
                                                    @RequestParam(required = false, name = "nick") String requestingNick) {
        return ResponseEntity.ok(comms.list(code, requestingNick, sinceId));
    }
}
