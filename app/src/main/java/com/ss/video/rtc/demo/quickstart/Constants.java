package com.ss.video.rtc.demo.quickstart;

/**
 * VolcEngineRTC 常量定义
 */
public class Constants {
    // 123 123

    //APPID 使用SDK前需要为自己的应用申请一个AppId，详情参见{https://www.volcengine.com/docs/6348/69865}
    public static final String APPID = "63120b429ebfba018975d577";

    //TOKEN 加入房间的时候需要使用token完成鉴权，详情参见{https://www.volcengine.com/docs/6348/70121}
    public static final String TOKEN = "00163120b429ebfba018975d577PADzfn8CXUQUY91+HWMDADEyMwMAMTIzBgAAAN1+HWMBAN1+HWMCAN1+HWMDAN1+HWMEAN1+HWMFAN1+HWMgAB85Jbp+iCbltzzxFnSkIx4jlsUr+o5V34hzF3EEIAjz/nCf1kVsaqYw";


    //INPUT_REGEX SDK 对房间名、用户名的限制是：非空且最大长度不超过128位的数字、大小写字母、@ . _ | -
    public static final String INPUT_REGEX = "^[a-zA-Z0-9@._|-]{1,128}$";

    public static final String ROOM_ID_EXTRA = "extra_room_id";

    public static final String USER_ID_EXTRA = "extra_user_id";
}
