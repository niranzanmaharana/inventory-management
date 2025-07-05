package com.niranzan.inventory.management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MiscController {

    @GetMapping("/.well-known/appspecific/com.chrome.devtools.json")
    @ResponseBody
    public String handleChromeDevtoolsProbe() {
        return "{}";
    }
}
