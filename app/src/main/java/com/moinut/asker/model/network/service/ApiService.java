package com.moinut.asker.model.network.service;

import com.moinut.asker.config.Api;
import com.moinut.asker.model.bean.ApiWrapper;
import com.moinut.asker.model.bean.PageWrapper;
import com.moinut.asker.model.bean.Question;
import com.moinut.asker.model.bean.User;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

public interface ApiService {

    @FormUrlEncoded
    @POST(Api.API_LOGIN)
    Observable<ApiWrapper<User>> login(@Field("accountId") String accountId,
                                       @Field("password") String password);

    @FormUrlEncoded
    @POST(Api.API_GET_ALL_QUESTIONS)
    Observable<PageWrapper<List<Question>>> getAllQuestions(@Field("page") int page,
                                                            @Field("count") int count);

    @FormUrlEncoded
    @POST(Api.API_ASK_QUESTION)
    Observable<ApiWrapper<String>> askQuestion(@Field("token") String token,
                                               @Field("title") String title,
                                               @Field("content") String content,
                                               @Field("type") String type);

    @FormUrlEncoded
    @POST(Api.API_REGISTER)
    Observable<ApiWrapper<String>> register(@Field("accountId") String accountId,
                                            @Field("password") String password,
                                            @Field("type") String type);
}
