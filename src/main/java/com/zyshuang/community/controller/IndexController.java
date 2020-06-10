package com.zyshuang.community.controller;

import com.zyshuang.community.cache.HostTag;
import com.zyshuang.community.dto.PaginationDTO;
import com.zyshuang.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {


    @Autowired
    private QuestionService questionService;

    @RequestMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page",defaultValue = "1") Integer page,
                        @RequestParam(name = "size",defaultValue = "6") Integer size,
                        @RequestParam(name = "search",required = false) String search){
        PaginationDTO pagination = questionService.list(search,page,size);
        model.addAttribute("pagination",pagination);
        model.addAttribute("search",search);
        model.addAttribute("hostTags", HostTag.getTags());
        return "index";
    }

    @RequestMapping("/index/{tag}")
    public String indexTag(Model model,
                        @RequestParam(name = "page",defaultValue = "1") Integer page,
                        @RequestParam(name = "size",defaultValue = "6") Integer size,
                        @PathVariable(name = "tag") String tag){
        PaginationDTO pagination = questionService.listByTag(tag, page, size);
        model.addAttribute("pagination",pagination);
        model.addAttribute("hostTags", HostTag.getTags());
        return "index";
    }
}
