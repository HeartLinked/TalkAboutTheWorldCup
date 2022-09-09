package com.ss.video.rtc.demo.quickstart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ss.bytertc.engine.RTCEngine;
import com.ss.bytertc.engine.RTCRoom;
import com.ss.bytertc.engine.VideoCanvas;
import com.ss.bytertc.engine.RTCRoomConfig;
import com.ss.bytertc.engine.RTCVideo;
import com.ss.bytertc.engine.UserInfo;
import com.ss.bytertc.engine.VideoEncoderConfig;
import com.ss.bytertc.engine.data.AudioRoute;
import com.ss.bytertc.engine.data.CameraId;
import com.ss.bytertc.engine.data.RemoteStreamKey;
import com.ss.bytertc.engine.data.ScreenMediaType;
import com.ss.bytertc.engine.data.StreamIndex;
import com.ss.bytertc.engine.data.VideoFrameInfo;
import com.ss.bytertc.engine.data.VideoSourceType;
import com.ss.bytertc.engine.handler.IRTCEngineEventHandler;
import com.ss.bytertc.engine.handler.IRTCVideoEventHandler;
import com.ss.bytertc.engine.type.ChannelProfile;
import com.ss.bytertc.engine.type.MediaDeviceState;
import com.ss.bytertc.engine.type.MediaStreamType;
import com.ss.bytertc.engine.type.RTCRoomStats;

import com.ss.bytertc.engine.type.StreamRemoveReason;
import com.ss.bytertc.engine.type.VideoDeviceType;
import com.ss.rtc.demo.quickstart.R;

import org.webrtc.RXScreenCaptureService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * VolcEngineRTC 视频通话的主页面
 * 本示例不限制房间内最大用户数；同时最多渲染四个用户的视频数据（自己和三个远端用户视频数据）；
 *
 * 包含如下简单功能：
 * - 创建引擎
 * - 设置视频发布参数
 * - 渲染自己的视频数据
 * - 加入音视频通话房间
 * - 切换前置/后置摄像头
 * - 打开/关闭麦克风
 * - 打开/关闭摄像头
 * - 切换听筒/扬声器
 * - 渲染远端用户的视频数据
 * - 离开房间
 * - 销毁引擎
 *
 * 实现一个基本的音视频通话的流程如下：
 * 1.创建引擎 {@link RTCEngine#create(Context, String, IRTCEngineEventHandler)}
 * 2.设置编码参数 {@link RTCEngine#setVideoEncoderConfig(List)}
 * 3.开启本地视频采集 {@link RTCEngine#startVideoCapture()}
 * 4.设置本地视频渲染视图 {@link RTCEngine#setLocalVideoCanvas(StreamIndex, VideoCanvas)}
 * 5.加入音视频通话房间 {@link RTCEngine#joinRoom(java.lang.String, java.lang.String,
 *   com.ss.bytertc.engine.UserInfo, com.ss.bytertc.engine.RTCRoomConfig)}
 * 6.在收到远端用户视频首帧之后，设置用户的视频渲染视图 {@link RTCEngine#setRemoteVideoCanvas(String, StreamIndex, VideoCanvas)}
 * 7.在用户离开房间之后移出用户的视频渲染视图 {@link RTCRoomActivity#(String)}
 * 8.离开音视频通话房间 {@link RTCEngine#leaveRoom()}
 * 9.销毁引擎 {@link RTCEngine#destroyEngine(RTCEngine)}
 *
 * 有以下常见的注意事项：
 * 1.创建引擎时，需要注册 RTC 事件回调的接口，类型是 IRTCEngineEventHandler 用户需要持有这个引用，例如本示例中
 *   将其作为 Activity 成员变量。因为这个引用在引擎内部是弱引用，可能会被系统回收导致收不到回调事件
 * 2.本示例中，我们在 {@link IRTCEngineEventHandler#onFirstRemoteVideoFrameDecoded} 这个事件中给远端用户
 *   设置远端用户视频渲染视图，这个回调表示的是收到了远端用户的视频第一帧。当然也可以在
 *   {@link IRTCEngineEventHandler#onUserJoined} 回调中设置远端用户视频渲染视图
 * 3.SDK 回调不在主线程，UI 操作需要切换线程
 * 4.用户成功加入房间后，SDK 会通过 {@link IRTCEngineEventHandler#onUserJoined} 回调已经在房间的用户信息
 * 5.SDK 支持同时发布多个参数的视频流，接口是 {@link RTCEngine#setVideoEncoderConfig}
 * 6.加入房间时，需要有有效的 token，加入失败时会通过 {@link IRTCEngineEventHandler#onError(int)} 输出对应的错误码
 * 7.用户可以通过 {@link RTCEngine#joinRoom(java.lang.String, java.lang.String,
 *   com.ss.bytertc.engine.UserInfo, com.ss.bytertc.engine.RTCRoomConfig)} 中的 ChannelProfile
 *   来获得不同场景下的性能优化。本示例是音视频通话场景，因此使用 {@link ChannelProfile#CHANNEL_PROFILE_COMMUNICATION}
 * 8.不需要在每次加入/退出房间时销毁引擎。本示例退出房间时销毁引擎是为了展示完整的使用流程
 *
 * 详细的API文档参见{https://www.volcengine.com/docs/6348/70080}
 */
public class RTCRoomActivity extends AppCompatActivity {
    private ImageView mAudioIv;
    private ImageView mVideoIv;

    private int RENDER_SCREEN = 1;
    private int RENDER_CAMERA = 0;

    private boolean ifHasMainSharer = false;//是否有主共享人
    private boolean isPublishScreen = false;//本地是否在publish screen
    private boolean isPublishStream = false;//本地是否在publish stream

    private String[] remoteUserIds = new String[100];
    private FrameLayout[] remoteUserContainer = new FrameLayout[100];
    private int remoteUserPtr = 0;

    private FrameLayout rootRemoteContainer;
    private String localRoomId;

    private boolean mIsSpeakerPhone = true;
    private boolean mIsMuteAudio = false;
    private boolean mIsMuteVideo = false;
    private CameraId mCameraID = CameraId.CAMERA_ID_FRONT;

    private FrameLayout mSelfContainer;

    private RTCVideo mRTCVideo;
    private RTCRoom mRTCRoom;

    private Timer timer;
    private boolean Start;
    private int cnt;

    public static final int REQUEST_CODE_OF_SCREEN_SHARING = 101;

    private RTCRoomEventHandlerAdapter mIRtcRoomEventHandler = new RTCRoomEventHandlerAdapter() {

        @Override
        public void onUserPublishScreen( String uid, MediaStreamType type) {
            ifHasMainSharer = true;
            Log.d("msg","remote user publish screen");
            //runOnUiThread(() -> setLocalRenderView(uid));
        }

        @Override
        public void onUserUnpublishScreen(String uid, MediaStreamType type,StreamRemoveReason reason) {
            ifHasMainSharer = false;
            //mSelfContainer.removeAllViews();
        }

        @Override
        public void onUserPublishStream( String uid, MediaStreamType type) {
            Log.d("msg","remote user publish stream, uid "+uid);
        }

        @Override
        public void onUserUnpublishStream( String uid, MediaStreamType type,StreamRemoveReason reason) {

        }

        @Override
        public void onRoomStats(RTCRoomStats stats) {
            super.onRoomStats(stats);
            Log.d("msg","current user : "+stats.users);
        }

        @Override
        public void onRoomMessageSendResult(long msgid, int error) {
            super.onRoomMessageSendResult(msgid, error);
            mHandler.sendMessage(Message.obtain(mHandler, 1));
            mVCChatRv.smoothScrollToPosition(mVCChatAdapter.getItemCount()- 1);
            Log.d("lfysendMessageID", String.valueOf(msgid));
            Log.d("lfySendMessageResult", String.valueOf(error));
        }

        @Override
        public void onRoomMessageReceived(String uid, String message) {
            super.onUserMessageReceived(uid, message);
            mHandler.sendMessage(Message.obtain(mHandler, 1));
            mVCChatAdapter.addChatMsg(uid + ": " + message);

            Log.d("lfyUserMessageRecevied", uid + " " + message);
        }

        /**
         * 远端主播角色用户加入房间回调。
         */
        @Override
        public void onUserJoined(UserInfo userInfo, int elapsed) {
            super.onUserJoined(userInfo, elapsed);
            Log.d("IRTCRoomEventHandler", "onUserJoined: " + userInfo.getUid());
        }

        /**
         * 远端用户离开房间回调。
         */
        @Override
        public void onUserLeave(String uid, int reason) {
            super.onUserLeave(uid, reason);
            Log.d("IRTCRoomEventHandler", "onUserLeave: " + uid);
            //runOnUiThread(() -> removeRemoteView(uid));
        }

        @Override
        public void onRoomStateChanged(String roomId, String uid, int state, String extraInfo){
            super.onRoomStateChanged(roomId, uid, state, extraInfo);
            Log.d("lfyRoomStateUid", uid);
            Log.d("lfyRoomState", String.valueOf(state));
        }

    };

    private IRTCVideoEventHandler mIRtcVideoEventHandler = new IRTCVideoEventHandler() {

        @Override
        public void onVideoDeviceStateChanged(String deviceId, VideoDeviceType deviceType, int deviceState, int deviceError) {
            if (deviceType == VideoDeviceType.VIDEO_DEVICE_TYPE_SCREEN_CAPTURE_DEVICE) {
                Log.d("msg","VIDEO_DEVICE_TYPE_SCREEN_CAPTURE_DEVICE");
                if (deviceState == MediaDeviceState.MEDIA_DEVICE_STATE_STARTED) {
                    //mRTCRoom.publishStream(MediaStreamType.RTC_MEDIA_STREAM_TYPE_BOTH);
                    Log.d("msg","MEDIA_DEVICE_STATE_STARTED");
                    mRTCRoom.publishScreen(MediaStreamType.RTC_MEDIA_STREAM_TYPE_BOTH);
                    mRTCVideo.setVideoSourceType(StreamIndex.STREAM_INDEX_SCREEN, VideoSourceType.VIDEO_SOURCE_TYPE_INTERNAL);
                } else if (deviceState == MediaDeviceState.MEDIA_DEVICE_STATE_STOPPED
                        || deviceState == MediaDeviceState.MEDIA_DEVICE_STATE_RUNTIMEERROR) {
                    //mRTCRoom.unpublishStream(MediaStreamType.RTC_MEDIA_STREAM_TYPE_BOTH);
                    mRTCRoom.publishScreen(MediaStreamType.RTC_MEDIA_STREAM_TYPE_BOTH);
                }
            }
        }

        /**
         * SDK收到第一帧远端视频解码数据后，用户收到此回调。
         */
        @Override
        public void onFirstRemoteVideoFrameDecoded(RemoteStreamKey remoteStreamKey, VideoFrameInfo frameInfo) {
            super.onFirstRemoteVideoFrameDecoded(remoteStreamKey, frameInfo);
            if(remoteStreamKey.getStreamIndex() == StreamIndex.STREAM_INDEX_SCREEN) {
                runOnUiThread(() -> setLocalRenderView(remoteStreamKey.getUserId(),remoteStreamKey.getRoomId()));
                Log.d("msg","onFirstRemoteVideoFrameDecoded stream type = STREAM_INDEX_SCREEN");
            } else {
                boolean ifFindFlag = false;
                for(int index=0;index<remoteUserPtr;index++) {
                    if(remoteUserIds[index].equals(remoteStreamKey.getUserId())) {
                        Log.d("msg","render index : "+index);
                        FrameLayout container = remoteUserContainer[index];
                        runOnUiThread(() -> setRemoteRenderView(remoteStreamKey.getUserId(),remoteStreamKey.getRoomId(),container));
                        ifFindFlag = true;
                        break;
                    }
                }
                if(ifFindFlag == false) {
                    Log.d("msg","render index : "+remoteUserPtr);
                    remoteUserIds[remoteUserPtr] = remoteStreamKey.getUserId();
                    FrameLayout container = remoteUserContainer[remoteUserPtr];
                    runOnUiThread(() -> setRemoteRenderView(remoteStreamKey.getUserId(),remoteStreamKey.getRoomId(),container));
                    remoteUserPtr++;
                }
                // runOnUiThread(() -> setRemoteRenderView(remoteStreamKey.getUserId(),remoteStreamKey.getRoomId(),rootRemoteContainer));
                Log.d("msg","onFirstRemoteVideoFrameDecoded stream type != STREAM_INDEX_SCREEN");
            }
        }

        /**
         * 警告回调，详细可以看 {https://www.volcengine.com/docs/6348/70082#warncode}
         */
        @Override
        public void onWarning(int warn) {
            super.onWarning(warn);
            Log.d("IRTCVideoEventHandler", "onWarning: " + warn);
        }

        /**
         * 错误回调，详细可以看 {https://www.volcengine.com/docs/6348/70082#errorcode}
         */
        @Override
        public void onError(int err) {
            super.onError(err);
            Log.d("IRTCVideoEventHandler", "onError: " + err);
            showAlertDialog(String.format(Locale.US, "error: %d", err));
        }

    };

    private RecyclerView mVCChatRv;

    private List<String> itemList = new ArrayList<>();

    private VCChatAdapter mVCChatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        Intent intent = getIntent();
        String roomId = intent.getStringExtra(Constants.ROOM_ID_EXTRA);
        String userId = intent.getStringExtra(Constants.USER_ID_EXTRA);
        localRoomId = intent.getStringExtra(Constants.ROOM_ID_EXTRA);

        mSelfContainer = findViewById(R.id.self_video_container);
        remoteUserContainer[0] = findViewById(R.id.remote_container_root);
        remoteUserContainer[1] = findViewById(R.id.remote_container_root_1);
        remoteUserContainer[2] = findViewById(R.id.remote_container_root_2);

        initList();
        initUI(roomId, userId);
        setMenu(userId);

        Boolean sxt = intent.getBooleanExtra("sxt", false);
        Boolean mkf = intent.getBooleanExtra("mkf", false);

        initEngineAndJoinRoom(roomId, userId, sxt, mkf);
        initGetMessage(userId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_OF_SCREEN_SHARING && resultCode == Activity.RESULT_OK) {
            Log.i("ShareScreen","startScreenShare function");
            startScreenShare(data);
        } else if (requestCode == 2) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                VideoView renderView = new VideoView(this);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                mSelfContainer.removeAllViews();
                mSelfContainer.addView(renderView, params);
                renderView.setVideoURI(uri);
                renderView.start();
                //lab7_video.setMediaController(new MediaController(lab7_video.getContext()));
            }
        } else if(resultCode == 1) {
            String uri = data.getStringExtra("Uri");
            VideoView renderView = new VideoView(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mSelfContainer.removeAllViews();
            mSelfContainer.addView(renderView, params);
            renderView.setVideoPath(uri);
            renderView.start();
            Toast.makeText(RTCRoomActivity.this,"视频打开成功！",Toast.LENGTH_SHORT).show();
        } else {
                super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void startScreenShare(Intent data) {
        startRXScreenCaptureService(data);
        //编码参数
        VideoEncoderConfig config = new VideoEncoderConfig();
        config.width = 720;
        config.height = 1280;
        config.frameRate = 15;
        config.maxBitrate = 1600;
        mRTCVideo.setScreenVideoEncoderConfig(config);
        // 开启屏幕视频数据采集
        mRTCVideo.startScreenCapture(ScreenMediaType.SCREEN_MEDIA_TYPE_VIDEO_AND_AUDIO, data);
        //mRTCRoom.publishStream(MediaStreamType.RTC_MEDIA_STREAM_TYPE_BOTH);
        mRTCRoom.publishScreen(MediaStreamType.RTC_MEDIA_STREAM_TYPE_BOTH);
    }

    private void startRXScreenCaptureService(@NonNull Intent data) {
        Context context = getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent intent = new Intent();
            RTCRoomActivity mHostActivity = new RTCRoomActivity();
            intent.putExtra(RXScreenCaptureService.KEY_LARGE_ICON, R.drawable.launcher_quick_start);
            intent.putExtra(RXScreenCaptureService.KEY_SMALL_ICON, R.drawable.launcher_quick_start);
            intent.putExtra(RXScreenCaptureService.KEY_LAUNCH_ACTIVITY, mHostActivity.getClass().getCanonicalName());
            intent.putExtra(RXScreenCaptureService.KEY_CONTENT_TEXT, "正在录制/投射您的屏幕");
            intent.putExtra(RXScreenCaptureService.KEY_RESULT_DATA, data);
            context.startForegroundService(RXScreenCaptureService.getServiceIntent(context, RXScreenCaptureService.COMMAND_LAUNCH, intent));
        }
    }

    // 向系统发起屏幕共享的权限请求
    public void requestForScreenSharing() {
        Log.i("ShareScreen","start ShareScreen");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Log.e("ShareScreen","当前系统版本过低，无法支持屏幕共享");
            return;
        }
        MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (projectionManager != null) {
            startActivityForResult(projectionManager.createScreenCaptureIntent(), REQUEST_CODE_OF_SCREEN_SHARING);
        } else {
            Log.e("ShareScreen","当前系统版本过低，无法支持屏幕共享");
        }
    }

    void setInvisible() {
        AlphaAnimation disappearAnimation = new AlphaAnimation(1, 0);
        disappearAnimation.setDuration(500);
        mVCChatRv.startAnimation(disappearAnimation);
        disappearAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                mVCChatRv.setVisibility(View.INVISIBLE);
            }
        });
    }

    final Handler mHandler = new Handler(){

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1) {
                mVCChatRv.setVisibility(View.VISIBLE);
                cnt = 10;
                if(Start == true) {
                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(cnt <= 0) {
                                if(mVCChatRv.getVisibility() == View.VISIBLE) setInvisible();
                            } else cnt--;
                            Log.d("lfycnt", String.valueOf(cnt));
                        }
                    }, 0, 1000);
                } else {
                    Start = true;
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(cnt <= 0) {
                                if(mVCChatRv.getVisibility() == View.VISIBLE) setInvisible();
                            } else cnt--;
                            Log.d("lfycnt", String.valueOf(cnt));
                        }
                    }, 0, 1000);
                }
            }
        }
    };

    private void initGetMessage(String userId){

        mVCChatAdapter = new VCChatAdapter();
        mVCChatRv = (RecyclerView) findViewById(R.id.voice_chat_demo_main_chat_rv);
        mVCChatRv.setLayoutManager(new LinearLayoutManager(
                RTCRoomActivity.this, RecyclerView.VERTICAL, false));
        mVCChatRv.setAdapter(mVCChatAdapter);
        cnt = 10;

        View view = findViewById(R.id.review);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.sendMessage(Message.obtain(mHandler, 1));
            }
        });

        TextView textViewButton = findViewById(R.id.voice_chat_demo_main_input_send);
        EditText textView = findViewById(R.id.voice_chat_demo_main_input_et);

        textViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String MESSAGE = textView.getText().toString();
                mRTCRoom.sendRoomMessage(MESSAGE);
                mVCChatAdapter.addChatMsg(userId + ": " + MESSAGE);
                textView.setText("");
                if(mVCChatAdapter.getItemCount() > 6) mVCChatRv.smoothScrollToPosition(mVCChatAdapter.getItemCount()-1);
            }
        });
    }

    private void initUI(String roomId, String userId) {

        mSelfContainer = findViewById(R.id.self_video_container);
        //rootRemoteContainer = findViewById(R.id.remote_container_root);

        mAudioIv = findViewById(R.id.switch_local_audio);      // 左下角
        mVideoIv = findViewById(R.id.switch_local_video);       // 右下角
        findViewById(R.id.leave).setOnClickListener((v) -> onBackPressed());
        mAudioIv.setOnClickListener((v) -> updateLocalAudioStatus(userId));
        mVideoIv.setOnClickListener((v) -> updateLocalVideoStatus(userId));
        TextView roomIDTV = findViewById(R.id.room_id_text);
        TextView userIDTV = findViewById(R.id.self_video_user_id_tv);
        roomIDTV.setText(String.format("RoomID:%s", roomId));
        userIDTV.setText(String.format("UserID:%s", userId));
    }

    private void initEngineAndJoinRoom(String roomId, String userId, boolean sxt, boolean mkf) {
        // 创建引擎
        mRTCVideo = RTCVideo.createRTCVideo(getApplicationContext(), Constants.APPID, mIRtcVideoEventHandler, null, null);
        // 设置视频发布参数
        VideoEncoderConfig videoEncoderConfig = new VideoEncoderConfig(360, 640, 15, 800);
        mRTCVideo.setVideoEncoderConfig(videoEncoderConfig);
        // 开启本地视频采集
        if(sxt) mRTCVideo.startVideoCapture();
        // 开启本地音频采集
        if(mkf) mRTCVideo.startAudioCapture();

        // 加入房间
        mRTCRoom = mRTCVideo.createRTCRoom(roomId);
        mRTCRoom.setRTCRoomEventHandler(mIRtcRoomEventHandler);
        RTCRoomConfig roomConfig = new RTCRoomConfig(ChannelProfile.CHANNEL_PROFILE_COMMUNICATION,
                false, true, true);
        int joinRoomRes = mRTCRoom.joinRoom(Constants.TOKEN,
                UserInfo.create(userId, ""), roomConfig);
        Log.i("TAG", "initEngineAndJoinRoom: " + joinRoomRes);

    }

    private void setLocalRenderView(String uid,String roomId) {
        VideoCanvas videoCanvas = new VideoCanvas();
        //TextureView renderView = new TextureView(this);
        TextureView renderView = new TextureView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mSelfContainer.removeAllViews();
        mSelfContainer.addView(renderView, params);
        videoCanvas.renderView = renderView;
        videoCanvas.uid = uid;
        videoCanvas.roomId = roomId;
        videoCanvas.renderMode = VideoCanvas.RENDER_MODE_HIDDEN;
        // 设置本地视频渲染视图
        videoCanvas.isScreen = true;
        int res = mRTCVideo.setRemoteVideoCanvas(uid,StreamIndex.STREAM_INDEX_SCREEN, videoCanvas);
        Log.d("msg","setLocalVideoCanvas res : "+res);
    }

    private void setRemoteRenderView(String uid,String roomId,FrameLayout container) {
        Log.d("msg","setRemoteRenderView uid:"+uid+"roomId:"+roomId);
        TextureView renderView = new TextureView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        container.removeAllViews();
        container.addView(renderView, params);

        VideoCanvas videoCanvas = new VideoCanvas();
        videoCanvas.renderView = renderView;
        videoCanvas.roomId = roomId;
        videoCanvas.uid = uid;
        videoCanvas.isScreen = false;
        videoCanvas.renderMode = VideoCanvas.RENDER_MODE_HIDDEN;
        // 设置远端用户视频渲染视图
        mRTCVideo.setRemoteVideoCanvas(uid, StreamIndex.STREAM_INDEX_MAIN, videoCanvas);
    }

    private void onSwitchCameraClick() {
        // 切换前置/后置摄像头（默认使用前置摄像头）
        if (mCameraID.equals(CameraId.CAMERA_ID_FRONT)) {
            Toast.makeText(RTCRoomActivity.this,"已切换至后置摄像头！",Toast.LENGTH_SHORT).show();
            mCameraID = CameraId.CAMERA_ID_BACK;
        } else {
            Toast.makeText(RTCRoomActivity.this,"已切换至前置摄像头！",Toast.LENGTH_SHORT).show();
            mCameraID = CameraId.CAMERA_ID_FRONT;
        }
        mRTCVideo.switchCamera(mCameraID);
    }

    private void updateSpeakerStatus( ) {
        if(mIsSpeakerPhone) {
            Toast.makeText(RTCRoomActivity.this,"已切换至扬声器！",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(RTCRoomActivity.this,"已切换至手机听筒！",Toast.LENGTH_SHORT).show();
        }
        mIsSpeakerPhone = !mIsSpeakerPhone;
        // 设置使用哪种方式播放音频数据
        mRTCVideo.setAudioRoute(mIsSpeakerPhone ? AudioRoute.AUDIO_ROUTE_SPEAKERPHONE
                : AudioRoute.AUDIO_ROUTE_EARPIECE);
    }

    private void updateLocalAudioStatus(String userId) {
        mIsMuteAudio = !mIsMuteAudio;
        // 开启/关闭本地音频发送
        if (mIsMuteAudio) {
            Toast.makeText(RTCRoomActivity.this,"麦克风关闭成功！",Toast.LENGTH_SHORT).show();
            mRTCRoom.unpublishStream(MediaStreamType.RTC_MEDIA_STREAM_TYPE_AUDIO);
            mRTCVideo.stopAudioCapture();
        } else {
            Toast.makeText(RTCRoomActivity.this,"麦克风开启成功！",Toast.LENGTH_SHORT).show();
            mRTCRoom.publishStream(MediaStreamType.RTC_MEDIA_STREAM_TYPE_AUDIO);
            mRTCVideo.stopAudioCapture();
        }
        mAudioIv.setImageResource(mIsMuteAudio ? R.drawable.mute_audio : R.drawable.normal_audio);
    }

    private void updateLocalVideoStatus(String userId) {
        mIsMuteVideo = !mIsMuteVideo;
        if (mIsMuteVideo) {
            // 关闭视频采集
            mRTCRoom.unpublishStream(MediaStreamType.RTC_MEDIA_STREAM_TYPE_VIDEO);
            mRTCVideo.stopVideoCapture();
            Toast.makeText(RTCRoomActivity.this,"摄像头关闭成功！",Toast.LENGTH_SHORT).show();
        } else {
            // 开启视频采集
            Toast.makeText(RTCRoomActivity.this,"摄像头开启成功！",Toast.LENGTH_SHORT).show();
            mRTCVideo.startVideoCapture();
            mRTCRoom.publishStream(MediaStreamType.RTC_MEDIA_STREAM_TYPE_VIDEO);
        }
        mVideoIv.setImageResource(mIsMuteVideo ? R.drawable.mute_video : R.drawable.normal_video);
    }

    private void showAlertDialog(String message) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(message);
            builder.setPositiveButton("知道了", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });
    }

    @Override
    public void finish() {
        super.finish();
        // 离开房间
        if (mRTCRoom != null) {
            mRTCRoom.leaveRoom();
            mRTCRoom.destroy();
        }
        mRTCRoom = null;
        // 销毁引擎
        RTCVideo.destroyRTCVideo();
        mIRtcVideoEventHandler = null;
        mIRtcRoomEventHandler = null;
        mRTCVideo = null;
    }

    private void initList() {
        for(int i = 1; i <= 10; i++) {
            itemList.add(String.valueOf(i));
        }
    }

    public void OutSendMessage(){
        mHandler.sendMessage(Message.obtain(mHandler, 1));
    }

    @SuppressLint("RestrictedApi")
    public void setMenu(String userId) {
        ImageView amenu = (ImageView) findViewById(R.id.amenu);

        amenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pm = new PopupMenu(RTCRoomActivity.this, amenu);
                pm.getMenuInflater().inflate(R.menu.menu,pm.getMenu());

                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.shareScreen:{
                                if(ifHasMainSharer == false) {
                                    //无主共享人，当前用户成为主共享人
                                    requestForScreenSharing();
                                    isPublishScreen = true;
                                    mRTCRoom.publishScreen(MediaStreamType.RTC_MEDIA_STREAM_TYPE_BOTH);
                                } else {
                                    Toast.makeText(RTCRoomActivity.this,"存在正在运行的投屏进程!",Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                            case R.id.shareScreen2:{
                                if( isPublishScreen == true) {
                                    mRTCRoom.unpublishScreen(MediaStreamType.RTC_MEDIA_STREAM_TYPE_BOTH);
                                    mRTCVideo.stopScreenCapture();//停止屏幕capture
                                    isPublishScreen = false;//本地不再开启屏幕共享
                                    ifHasMainSharer = false;//无主共享人
                                } else {
                                    Toast.makeText(RTCRoomActivity.this,"请先共享屏幕",Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                            case R.id.openVideo:{
                                if(isPublishScreen) {
                                    Intent intent = new Intent(RTCRoomActivity.this, MainActivity2.class);
                                    startActivityForResult(intent, 1);
                                }  else {
                                    Toast.makeText(RTCRoomActivity.this,"请先共享屏幕",Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                            case R.id.listener1:{
                                updateSpeakerStatus();
                                break;
                            }
                            case R.id.swithcamera:{
                                onSwitchCameraClick();
                                break;
                            }
                        }
                        return false;
                    }
                });
                pm.show();
            }
        });
    }

}