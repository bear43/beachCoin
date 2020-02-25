package com.psuti.coin.controller;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class AdviceController {

    private Logger logger = LogManager.getLogger(AdviceController.class);

    @ExceptionHandler(Exception.class)
    public String exception(Exception ex, RedirectAttributes attributes) {
        logger.log(Level.ERROR, ex);
        attributes.addFlashAttribute("message", ex.getMessage());
        return "redirect:/";
    }
}
