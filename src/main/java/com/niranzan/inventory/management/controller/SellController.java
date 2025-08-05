package com.niranzan.inventory.management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sell")
@RequiredArgsConstructor
public abstract class SellController extends BaseController {
    @GetMapping
    public String openSell(Model model) {
        return "sell/sell.html";
    }
}
