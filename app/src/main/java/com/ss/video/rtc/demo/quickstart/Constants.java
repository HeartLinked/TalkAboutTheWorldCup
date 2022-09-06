package com.ss.video.rtc.demo.quickstart;

/**
 * VolcEngineRTC 常量定义
 */
public class Constants {

    //APPID 使用SDK前需要为自己的应用申请一个AppId，详情参见{https://www.volcengine.com/docs/6348/69865}
    //room id : 123
    public static final String APPID = "6311bc701261360165249c8e";

    //TOKEN 加入房间的时候需要使用token完成鉴权，详情参见{https://www.volcengine.com/docs/6348/70121}
    //user id : lfy
//    public static final String TOKEN = "0016311bc701261360165249c8ePACfD0QBoRIWYyFNH2MDADEyMwMAbGZ5BgAAACFNH2MBACFNH2MCACFNH2MDACFNH2MEACFNH2MFACFNH2MgALcETpJbEb6pr5IVzkIySbNCH2mf+f7sEhj8LjaVh8WE";

    //user id : thy
    public static final String TOKEN = "0016311bc701261360165249c8ePABAWi0FyhIWY0pNH2MDADEyMwMAdGh5BgAAAEpNH2MBAEpNH2MCAEpNH2MDAEpNH2MEAEpNH2MFAEpNH2MgAFljdwV0IgpB9EFgFl1yIfriGOtpcOiZuUOSuldYxZYj";

    //INPUT_REGEX SDK 对房间名、用户名的限制是：非空且最大长度不超过128位的数字、大小写字母、@ . _ | -
    public static final String INPUT_REGEX = "^[a-zA-Z0-9@._|-]{1,128}$";

    public static final String ROOM_ID_EXTRA = "extra_room_id";

    public static final String USER_ID_EXTRA = "extra_user_id";
}
