package com.zyshuang.community.provide;

import com.alibaba.fastjson.JSON;
import com.zyshuang.community.dto.AccessTokenDTO;
import com.zyshuang.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

/**
 * 提供第三方业务
 */
@Component
public class GithubProvide {

    public String getAccessToken(AccessTokenDTO accessToken){
         MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        //访问令牌
        RequestBody body = RequestBody.create(JSON.toJSONString(accessToken), mediaType);
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            //返回得到访问令牌
            return string.split("&")[0].split("=")[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public GithubUser getUser(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token="+accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
