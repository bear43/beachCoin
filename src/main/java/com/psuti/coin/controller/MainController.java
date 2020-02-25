package com.psuti.coin.controller;

import com.psuti.coin.form.SendForm;
import com.psuti.coin.model.Wallet;
import com.psuti.coin.service.WalletService;
import com.psuti.coin.util.ResultHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {

    @Autowired
    private WalletService walletService;

    @RequestMapping("/")
    public String main(Model model) {
        model.addAttribute("wallet", walletService.getWallet());
        model.asMap().putIfAbsent("message", "Введите кому и сколько перевести");
        return "main";
    }
}
