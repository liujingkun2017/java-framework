package org.liujk.java.framework.sample.boot.starter.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @GetMapping("/sample/test")
    public String sampleTest() {
        String s = "sampleTest";
        return s;
    }

}
