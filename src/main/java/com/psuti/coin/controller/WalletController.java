package com.psuti.coin.controller;

import com.psuti.coin.dto.TxInfoDTO;
import com.psuti.coin.form.SendForm;
import com.psuti.coin.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping("/transfer")
    public String transfer(Float amount, RedirectAttributes attributes) {
        attributes.addFlashAttribute("message", walletService.sendFromSystem(amount));
        return "redirect:/";
    }

    @PostMapping("/send")
    public String send(SendForm form, RedirectAttributes attributes) {
        attributes.addFlashAttribute("message", walletService.sendTo(form));
        return "redirect:/";
    }

    @RequestMapping("/trans/get")
    @ResponseBody
    public TxInfoDTO getTrans() {
        return walletService.getAllTransaction();
    }
}
