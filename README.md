# cordova-plugin-upush
## 友盟推送cordova插件
=====================

在友盟管理平台注册android和ios应用.

安装插件：cordova plugin add https://github.com/makiiii1111/cordova-plugin-upush

## android部分

1. 在工程的自定义Application类的 onCreate() 方法中注册推送服务,替换@appKey,@Umeng Message Secret为友盟管理平台注册的app对应key
    ```java
    import com.umeng.commonsdk.UMConfigure;
    import com.umeng.message.IUmengRegisterCallback;
    import com.umeng.message.PushAgent;

    @Override
      public void onCreate() {
        //初始化组件化基础库, 统计SDK/推送SDK/分享SDK都必须调用此初始化接口
        UMConfigure.init(this, "@appKey", "Umeng", UMConfigure.DEVICE_TYPE_PHONE,
          "@Umeng Message Secret");
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务 每次调用register都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
          @Override
          public void onSuccess(String deviceToken) {           
            UPush.setToken(deviceToken);
          }
          @Override
          public void onFailure(String s, String s1) {
            
          }
        });
      }
    ```
2. 在工程Activity的 onCreate() 方法中添加代码
    ```java
    PushAgent mPushAgent = PushAgent.getInstance(this);
      mPushAgent.onAppStart();
      UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
        //自定义点击通知的处理方式，这里调用了js的方法打开消息详情页
        @Override
        public void dealWithCustomAction(Context context, UMessage msg) {
          super.launchApp(context,msg);
          loadUrl("javascript:nativeCallJs("+JSON.parseObject(msg.custom)+")");
        }
      };
      mPushAgent.setNotificationClickHandler(notificationClickHandler);
    ```

## ios部分

1. 在TARGETS -> Capabilities中，打开 Push Notifications . 在 Background Modes中的Remote Notifications前打钩
2. 在AppDelegate.m中添加
```objectivec
	#import "UPush.h"
    	#import <UserNotifications/UserNotifications.h>
    	#import <UMPush/UMessage.h>
	- (BOOL)application:(UIApplication*)application didFinishLaunchingWithOptions:		(NSDictionary*)launchOptions
	{
    		
        [UMConfigure initWithAppkey:@"appkey" channel:@"App Store"];
        // Push组件基本功能配置
        [UNUserNotificationCenter currentNotificationCenter].delegate = self;
         UMessageRegisterEntity * entity = [[UMessageRegisterEntity alloc] init];
         //type是对推送的几个参数的选择，可以选择一个或者多个。默认是三个全部打开，即：声音，弹窗，角标等
         entity.types = UMessageAuthorizationOptionBadge|UMessageAuthorizationOptionAlert|UMessageAuthorizationOptionSound;
         //[UNUserNotificationCenter currentNotificationCenter].delegate = self;
         [UMessage registerForRemoteNotificationsWithLaunchOptions:launchOptions Entity:entity completionHandler:^(BOOL granted, NSError 		* _Nullable error) {
            if (granted) {
            // 用户选择了接收Push消息
            }else{
            // 用户拒绝接收Push消息
            }
           }];
	}
	- (void)application:(UIApplication *)application
		didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {

    		// 1.2.7版本开始不需要用户再手动注册devicetoken，SDK会自动注册
    		//[UMessage registerDeviceToken:deviceToken];
    		NSString * token = [[[[deviceToken description]
                          stringByReplacingOccurrencesOfString: @"<" withString: @""]
                         stringByReplacingOccurrencesOfString: @">" withString: @""]
                        stringByReplacingOccurrencesOfString: @" " withString: @""];
    		NSLog(@"deviceToken: %@",token);
    		[[UPush alloc]setToken:token];
	}
```
3. 如果需要处理收到消息和点击消息事件，则要实现以下方法
```objectivec
 //iOS10以下使用这两个方法接收通知，
-(void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler
{
    [UMessage setAutoAlert:NO];
    if([[[UIDevice currentDevice] systemVersion]intValue] < 10){
        [UMessage didReceiveRemoteNotification:userInfo];
        completionHandler(UIBackgroundFetchResultNewData);
    }
}
- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
{
    //关闭友盟自带的弹出框
    [UMessage setAutoAlert:NO];
    [UMessage didReceiveRemoteNotification:userInfo];
}
//iOS10新增：处理前台收到通知的代理方法
-(void)userNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler{
    NSDictionary * userInfo = notification.request.content.userInfo;
    if([notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
        //应用处于前台时的远程推送接受
        //关闭U-Push自带的弹出框
        [UMessage setAutoAlert:NO];
        //必须加这句代码
        [UMessage didReceiveRemoteNotification:userInfo];
        
    }else{
        //应用处于前台时的本地推送接受
}
    //当应用处于前台时提示设置，需要哪个可以设置哪一个
    completionHandler(UNNotificationPresentationOptionSound|UNNotificationPresentationOptionBadge|UNNotificationPresentationOptionAlert);
}

//iOS10新增：处理后台点击通知的代理方法
-(void)userNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void (^)())completionHandler{
    NSDictionary * userInfo = response.notification.request.content.userInfo;
    if([response.notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
        //应用处于后台时的远程推送接受
        //必须加这句代码
        [UMessage didReceiveRemoteNotification:userInfo];     
    }else{
        //应用处于后台时的本地推送接受
    }
}
```
**需要注意的是：如果app已经装了localNotification也就是本地通知的插件，这些回调事件会被覆盖**
