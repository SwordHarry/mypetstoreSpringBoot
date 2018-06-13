package org.csu.mypetstorespring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LanguagesController {

    @RequestMapping("/account/login")
    public String loginLang(@RequestParam("lang") String lang,ModelMap model)
    {
        if(null == lang || lang.length() == 0)
        {
            lang = "http://218.77.50.45:8222/showroom/front/first/map_pass";
        }
        model.addAttribute("chinaMapUrl",lang);
        return "account/login";
    }

    @RequestMapping("/account/register")
    public String registerLang(@RequestParam("lang") String lang,ModelMap model)
    {
        if(null == lang || lang.length() == 0)
        {
            lang = "http://218.77.50.45:8222/showroom/front/first/map_pass";
        }
        model.addAttribute("chinaMapUrl",lang);
        return "account/register";
    }
}
