# 一起来侃世界杯

### 技术栈

- Java
- 基于火山引擎的实时音视频SDK

### feature

- 实时共享手机屏幕
- 用户进入房间前可以选择是否默认开启摄像头/麦克风
- 用户之间可以通过摄像头实时通话
- 内置聊天框功能，用户可以文字聊天
- 共享屏幕的用户可以播放本地视频/网络视频

### author

- https://github.com/biu9
- https://github.com/HeartLinked

### 实现细节

#### 用户角色以及权限

- 主共享人
  - 可以进行文字聊天
  - 可以选择本地视频进行播放
  - 可以共享屏幕 & 结束共享屏幕
- 普通用户
  - 可以进行文字聊天
  - 可以请求成为主共享人(在没有主共享人的情况下)
  - 可以进行文字聊天

点击共享屏幕 	-> check是否有主共享人  -> 有主共享人 -> toast提示 

​                  		 -> 无主共享人 -> 发送room message广播自己将成为主共享人 -> publishScreen -> setLocalRenderView

点击开启摄像头 	-> check自己是否是主共享人 -> 是主共享人 -> toast提示

​                       		-> 不是主共享人 -> publishStream

新用户进入房间 	-> 是否有主共享人 -> 有主共享人 -> 新用户收到onUserPublishScreen -> 设置ifHasMainSharer为true -> setLocalRenderView

​                 				-> 无主共享人  
