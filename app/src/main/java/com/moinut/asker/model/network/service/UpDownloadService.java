package com.moinut.asker.model.network.service;

import com.moinut.asker.model.bean.UploadWrapper;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;
import rx.Observable;

public interface UpDownloadService {
    /* 测试的地址 */
    @Deprecated
    String TEST_PIC_URL = "http://qlogo1.store.qq.com/qzone/896001088/896001088/100?1446739988";
    @Deprecated
    String TEST_UPLOAD_URL = "http://www.moinut.com/api/test/upload.php";

    @GET
    Observable<ResponseBody> download(@Url String url);

    /* 测试上传 */
    @Deprecated
    @Multipart
    @POST
    Observable<UploadWrapper> uploadTest(@Url String url,
                                         //@Part("stunum") RequestBody stunum,
                                         @Part MultipartBody.Part file);

}
