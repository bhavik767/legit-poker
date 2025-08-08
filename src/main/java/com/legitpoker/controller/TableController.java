package com.legitpoker.controller;

import com.legitpoker.dto.CreateTableRequest;
import com.legitpoker.dto.CreateTableResponse;
import com.legitpoker.service.PokerTableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tables")
public class TableController {
    private final PokerTableService pokerTableService;

    public TableController(PokerTableService pokerTableService) {
        this.pokerTableService = pokerTableService;
    }

    @PostMapping
    public ResponseEntity<CreateTableResponse> createTable(@RequestBody CreateTableRequest req) {
        CreateTableResponse response = pokerTableService.createTable(req);
        return ResponseEntity.ok(response);
    }
}
