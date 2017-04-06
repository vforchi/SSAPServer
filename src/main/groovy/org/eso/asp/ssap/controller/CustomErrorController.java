package org.eso.asp.ssap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {

    private static final String PATH = "/error";
    
    @Autowired
    private ErrorAttributes errorAttributes;

    @RequestMapping(value = PATH)
    String error() {
        return "Resource not found";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

}