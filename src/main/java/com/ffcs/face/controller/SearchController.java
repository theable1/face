package com.ffcs.face.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/search/")
public class SearchController {

    @RequestMapping("visit")
    public ModelAndView visit(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("search");
        return modelAndView;
    }

    @RequestMapping("process")
    public ModelAndView process(String imageB64){
        String url = null;
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("imageUrl",url );
        modelAndView.setViewName("search");
        return modelAndView;
    }


}
