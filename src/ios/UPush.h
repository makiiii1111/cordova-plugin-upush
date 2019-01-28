//
//  UPush
//
//  Created by wangkai on 16-04-14.
//
//

#import <Cordova/CDV.h>
#import <UMCommon/UMCommon.h>
#import <UserNotifications/UserNotifications.h>
#import <UMPush/UMessage.h>

@interface UPush : CDVPlugin
@property (nonatomic, strong) NSString *currentCallbackId;
-(void)startUmengPush:(NSDictionary *)launchOptions;
- (void)getToken:(CDVInvokedUrlCommand *)command;
-(void)setToken:(NSString *)token;
@end
