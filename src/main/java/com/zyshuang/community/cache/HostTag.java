package com.zyshuang.community.cache;

import com.zyshuang.community.dto.HostTagDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HostTag {
    public static List<HostTagDTO> getTags(){

        List<HostTagDTO> hostTagDTOS = new ArrayList<>();

        HostTagDTO hostTagDTO = new HostTagDTO();

        hostTagDTO.setTag(Arrays.asList("java","html","python","spring","Linux","docker","mysql","redis","idea","github"));

        hostTagDTOS.add(hostTagDTO);

        return hostTagDTOS;
    }
}
