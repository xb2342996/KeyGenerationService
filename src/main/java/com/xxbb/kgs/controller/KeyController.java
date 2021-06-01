package com.xxbb.kgs.controller;

import com.xxbb.kgs.common.R;
import com.xxbb.kgs.repository.KeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class KeyController {

    @Autowired
    private KeyRepository keyRepository;

    @GetMapping("/keys/{count}")
    public R<List<String>> getKeys(@PathVariable("count") Integer count) {
        List<String> keys = keyRepository.getUnusedKeys(count);
        return R.success(keys);
    }

}
