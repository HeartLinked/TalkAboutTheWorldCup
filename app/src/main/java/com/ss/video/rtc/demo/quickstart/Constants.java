package com.ss.video.rtc.demo.quickstart;

/**
 * VolcEngineRTC 常量定义
 */
public class Constants {

    //APPID 使用SDK前需要为自己的应用申请一个AppId，详情参见{https://www.volcengine.com/docs/6348/69865}
    //room id : 123
    public static final String APPID = "63120b429ebfba018975d577";

    //TOKEN 加入房间的时候需要使用token完成鉴权，详情参见{https://www.volcengine.com/docs/6348/70121}

    //user id : lfy
    //public static final String TOKEN = "00163120b429ebfba018975d577PAAoYnQCZDYYY+RwIWMDADEyMwMAbGZ5BgAAAORwIWMBAORwIWMCAORwIWMDAORwIWMEAORwIWMFAORwIWMgALjqqCTtdeHfgifW37AwW0KGCd6PG2osrF9SgNUlXHI5";

    //user id : thy
  //  public static final String TOKEN = "00163120b429ebfba018975d577PABqHOEBTzYYY89wIWMDADEyMwMAdGh5BgAAAM9wIWMBAM9wIWMCAM9wIWMDAM9wIWMEAM9wIWMFAM9wIWMgAPpSFox1o9NLo8RRJIR+Z+BkyJC5l3ZoKj0scEbBBqYK";

    //user id : test1
   public static final String TOKEN = "00163120b429ebfba018975d577PgBrHUkDf64aY//oI2MDADEyMwUAdGVzdDEGAAAA/+gjYwEA/+gjYwIA/+gjYwMA/+gjYwQA/+gjYwUA/+gjYyAAWwLwLiXb3yA+ju3K6XzUQYzqeglGQhakafkqjmaodsI=";

    //user id : test2
   // public static final String TOKEN = "00163120b429ebfba018975d577PgDeMlQDdrQaY/buI2MDADEyMwUAdGVzdDIGAAAA9u4jYwEA9u4jYwIA9u4jYwMA9u4jYwQA9u4jYwUA9u4jYyAAl4rR/sgssklyELD9EyPNiENOWHxiWIcdBV8ITO1pWi4=";

    //INPUT_REGEX SDK 对房间名、用户名的限制是：非空且最大长度不超过128位的数字、大小写字母、@ . _ | -
    public static final String INPUT_REGEX = "^[a-zA-Z0-9@._|-]{1,128}$";

    public static final String ROOM_ID_EXTRA = "extra_room_id";

    public static final String USER_ID_EXTRA = "extra_user_id";
}
