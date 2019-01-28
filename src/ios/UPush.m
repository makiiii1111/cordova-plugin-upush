//
//  UMPlugin
//
//  Created by wangkai on 16-04-14.
//
//

#import "UPush.h"
#import <UMCommon/UMCommon.h>
#import <UserNotifications/UserNotifications.h>
#import <UMPush/UMessage.h>
@implementation UPush
NSString *deviceToken;
-(void)startUmengPush:(NSDictionary *)launchOptions
{
    NSString *appkey;
    appkey = [[self.commandDelegate settings] objectForKey:@"pushkey"];
    if(appkey.length ==0){
        appkey = @"key";
    }
    [UMConfigure initWithAppkey:appkey channel:@"App Store"];
    
    // Push's basic setting
    UMessageRegisterEntity * entity = [[UMessageRegisterEntity alloc] init];
    //type是对推送的几个参数的选择，可以选择一个或者多个。默认是三个全部打开，即：声音，弹窗，角标
    entity.types = UMessageAuthorizationOptionBadge|UMessageAuthorizationOptionAlert;
    [UNUserNotificationCenter currentNotificationCenter].delegate=self;
    [UMessage registerForRemoteNotificationsWithLaunchOptions:launchOptions Entity:entity completionHandler:^(BOOL granted, NSError * _Nullable error) {
        if (granted) {
        }else
        {
        }
    }];
    
}

-(void)setToken:(NSString *)token{
    deviceToken = token;
}

-(void)getToken:(CDVInvokedUrlCommand *)command{
    self.currentCallbackId = command.callbackId;
    CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:deviceToken];
    [self.commandDelegate sendPluginResult:commandResult callbackId:self.currentCallbackId];
}
@end
