//
//  RNDeviceInfo.h
//  Sosgpsfmcg
//
//  Created by tianxuejun on 14-4-6.
//  Copyright (c) 2014年 Sosgps. All rights reserved.
//

#import <Foundation/Foundation.h>

#define WIFI @"WiFi"
#define WWAN @"WWAN"

@interface RNUUIDInfo : NSObject

/**
 获取设备id，这个id是通过保存到keychain中来实现的

 @return 设备id
 */
+ (NSString *)deviceId;

@end
