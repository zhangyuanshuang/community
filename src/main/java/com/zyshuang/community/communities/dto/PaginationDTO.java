package com.zyshuang.community.communities.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO {

    List<QuestionDTO> questionDTOS;

    //回到首页
    private boolean showPrevious;

    //上一页
    private boolean showFristPage;

    //下一页
    private boolean showNextPage;

    //回到尾页
    private boolean showEndPage;

    //当前页
    private Integer page;

    //可见总页数
    private List<Integer> pages = new ArrayList<>();

    //总页数
    private Integer totalPage;

    public void setPagination(Integer totalPage, Integer page) {
        this.totalPage = totalPage;
        this.page = page;
        pages.add(page);
        for (int i = 1; i <= 3; i++) {
            if (page - i > 0) {
                pages.add(0, page - i);
            }

            if (page + i <= totalPage) {
                pages.add(page + i);
            }
        }

        //是否显示上一页
        if (page == 1){
            showPrevious = false;
        }else {
            showPrevious =true;
        }

        //是否显示下一页
        if (page == totalPage){
            showNextPage = false;
        }else {
            showNextPage = true;
        }

        //不包含第一页的时候显示第一页
        if (pages.contains(1)){
            showFristPage = false;
        }else {
            showFristPage = true;
        }

        //不包含最后一页的时候显示最后一页
        if (pages.contains(totalPage)){
            showEndPage = false;
        }else {
            showEndPage = true;
        }
    }
}
