package com.niranzan.inventory.management.controller;


import com.niranzan.inventory.management.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @GetMapping("/stock-list")
    public String getStocks(Model model) {
        model.addAttribute("stocks", stockService.findAll());
        return "stock/stock-list.html";
    }
}
