package com.geokg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BaseController {

    @RequestMapping("/geokg")
    public String loadGeoKGPage(){
        return "geokg";
    }

    @RequestMapping("")
    public String loadHomePage(Model m) {
        m.addAttribute("name", "SSM");
        return "index";
    }
}