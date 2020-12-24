//
//  RNUUIDInfo.m
//  Sosgpsfmcg
//
//  Created by tianxuejun on 14-4-6.
//  Copyright (c) 2014年 Sosgps. All rights reserved.
//

#import "RNUUIDInfo.h"
#include <sys/mount.h>
#import <CoreTelephony/CTCarrier.h>
#import <CoreTelephony/CTTelephonyNetworkInfo.h>
#import <sys/sysctl.h>
#import <AVFoundation/AVFoundation.h>
#import <UIKit/UIDevice.h>

#if APPSTORE_VERSION
static const NSString *kKeyChainUDIDAccessGroup = @"WB49V6MPPM.uuidGroup";
#else
static const NSString *kKeyChainUDIDAccessGroup = @"WB49V6MPPM.uuidGroup";
#endif
static NSString *kUserDefaultBackup = @"shareGroupUuid";

@implementation RNUUIDInfo

+ (NSString *)deviceId
{
    [self generateUUIDIfNeeded];
    return [self getUUIDFromKeychain];
}

+ (NSString *)deviceModel {
    NSString *deviceModel = @"";
    size_t size;
    sysctlbyname("hw.machine", NULL, &size, NULL, 0);
    char *machine = malloc(size);
    sysctlbyname("hw.machine", machine, &size, NULL, 0);
    NSString *correspondVersion = [NSString stringWithCString:machine encoding:NSUTF8StringEncoding];
    free(machine);
    
    if ([correspondVersion isEqualToString:@"i386"] || [correspondVersion isEqualToString:@"x86_64"]) {
        deviceModel = @"Simulator";
    } else if ([correspondVersion isEqualToString:@"iPhone1,1"]) {
        deviceModel = @"iPhone 1";
    } else if ([correspondVersion isEqualToString:@"iPhone1,2"]) {
        deviceModel = @"iPhone 3";
    } else if ([correspondVersion isEqualToString:@"iPhone2,1"]) {
        deviceModel = @"iPhone 3S";
    } else if ([correspondVersion isEqualToString:@"iPhone3,1"] || [correspondVersion isEqualToString:@"iPhone3,2"]) {
        deviceModel = @"iPhone 4";
    } else if ([correspondVersion isEqualToString:@"iPhone4,1"]) {
        deviceModel = @"iPhone 4S";
    } else if ([correspondVersion isEqualToString:@"iPhone5,1"] || [correspondVersion isEqualToString:@"iPhone5,2"]) {
        deviceModel = @"iPhone 5";
    } else if ([correspondVersion isEqualToString:@"iPhone5,3"] || [correspondVersion isEqualToString:@"iPhone5,4"]) {
        deviceModel = @"iPhone 5C";
    } else if ([correspondVersion isEqualToString:@"iPhone6,1"] || [correspondVersion isEqualToString:@"iPhone6,2"]) {
        deviceModel = @"iPhone 5S";
    } else if ([correspondVersion isEqualToString:@"iPhone7,1"]) {
        deviceModel = @"iPhone 6 Plus";
    } else if ([correspondVersion isEqualToString:@"iPhone7,2"]) {
        deviceModel = @"iPhone 6";
    } else if ([correspondVersion isEqualToString:@"iPhone8,1"]) {
        deviceModel = @"iPhone 6s";
    } else if ([correspondVersion isEqualToString:@"iPhone8,2"]) {
        deviceModel = @"iPhone 6s Plus";
    } else if ([correspondVersion isEqualToString:@"iPhone8,4"]) {
        deviceModel = @"iPhone SE";
    } else if ([correspondVersion isEqualToString:@"iPad1,1"]) {
        deviceModel = @"iPad 1";
    } else if ([correspondVersion isEqualToString:@"iPad2,1"] || [correspondVersion isEqualToString:@"iPad2,2"] || [correspondVersion isEqualToString:@"iPad2,3"] || [correspondVersion isEqualToString:@"iPad2,4"]) {
        deviceModel = @"iPad 2";
    } else if ([correspondVersion isEqualToString:@"iPad2,5"] || [correspondVersion isEqualToString:@"iPad2,6"] || [correspondVersion isEqualToString:@"iPad2,7"]) {
        deviceModel = @"iPad Mini";
    } else if ([correspondVersion isEqualToString:@"iPad3,1"] || [correspondVersion isEqualToString:@"iPad3,2"] || [correspondVersion isEqualToString:@"iPad3,3"]) {
        deviceModel = @"iPad 3";
    } else if ([correspondVersion isEqualToString:@"iPad3,4"] || [correspondVersion isEqualToString:@"iPad3,5"] || [correspondVersion isEqualToString:@"iPad3,6"]) {
        deviceModel = @"iPad 4";
    } else if ([correspondVersion isEqualToString:@"iPad4,1"] || [correspondVersion isEqualToString:@"iPad4,2"] || [correspondVersion isEqualToString:@"iPad4,3"]) {
        deviceModel = @"iPad Air";
    } else if ([correspondVersion isEqualToString:@"iPad4,4"] || [correspondVersion isEqualToString:@"iPad4,5"] || [correspondVersion isEqualToString:@"iPad4,6"]) {
        deviceModel = @"iPad Mini 2G";
    } else if ([correspondVersion isEqualToString:@"iPod1,1"]) {
        deviceModel = @"iPod Touch 1";
    } else if ([correspondVersion isEqualToString:@"iPod2,1"]) {
        deviceModel = @"iPod Touch 2";
    } else if ([correspondVersion isEqualToString:@"iPod3,1"]) {
        deviceModel = @"iPod Touch 3";
    } else if ([correspondVersion isEqualToString:@"iPod4,1"]) {
        deviceModel = @"iPod Touch 4";
    } else if ([correspondVersion isEqualToString:@"iPod5,1"]) {
        deviceModel = @"iPod Touch 5";
    } else {
        deviceModel = correspondVersion;
    }
    
    return deviceModel;
}

+ (NSString *)appleIFV {
    if(NSClassFromString(@"UIDevice") && [UIDevice instancesRespondToSelector:@selector(identifierForVendor)]) {
        // only available in iOS >= 6.0
        NSString *ifv = [[UIDevice currentDevice].identifierForVendor UUIDString];
        return ifv;
    }
    return nil;
}

+ (void)generateUUIDIfNeeded {
    if (![self getUUIDFromKeychain]) {
        NSString *userDefaultBackupedUUID = [[NSUserDefaults standardUserDefaults] objectForKey:kUserDefaultBackup];
        if (userDefaultBackupedUUID) { //可能是用户升级了provisioning profile，这时keychain数据会丢失。把之前备份到userDefault中的uuid存回来
            [self setUUIDToKeychain:userDefaultBackupedUUID];
        } else {
            NSString *uuid = [self appleIFV];
            if (uuid.length == 0) { //这种情况应该不会出现，这里是防御性编程，万一出现，就用自己的方法生成一个
                NSString *bundleId = [[NSBundle mainBundle] bundleIdentifier];
                long long timeStamp = [RNUUIDInfo timeIntervalSince1970InMilliSecond];
                uuid = [NSString stringWithFormat:@"%@_%lld", bundleId, timeStamp];
            }
            [self setUUIDToKeychain:uuid];
        }
    }
}

+ (double)timeIntervalSince1970InMilliSecond {
    double ret;
    ret = [[NSDate date] timeIntervalSince1970] * 1000;
    return ret;
}

+ (void)setUUIDToKeychain:(NSString*)uuid
{
    NSMutableDictionary *keychainItem = [NSMutableDictionary new];
    keychainItem[(__bridge id)kSecClass] = (__bridge id)kSecClassGenericPassword;
    keychainItem[(__bridge id)kSecAttrAccount] = @"uiid";
    keychainItem[(__bridge id)kSecValueData] = [uuid dataUsingEncoding:NSUTF8StringEncoding];;
    keychainItem[(__bridge id)kSecAttrAccessible] = (__bridge id)kSecAttrAccessibleAlways;
#if TARGET_IPHONE_SIMULATOR
    // Ignore the access group if running on the iPhone simulator.
    //
    // Apps that are built for the simulator aren't signed, so there's no keychain access group
    // for the simulator to check. This means that all apps can see all keychain items when run
    // on the simulator.
    //
    // If a SecItem contains an access group attribute, SecItemAdd and SecItemUpdate on the
    // simulator will return -25243 (errSecNoAccessForItem).
#else
    keychainItem[(__bridge id)kSecAttrAccessGroup] = kKeyChainUDIDAccessGroup;
#endif
    OSStatus status = SecItemAdd((__bridge CFDictionaryRef)keychainItem, NULL);
    if (status == errSecSuccess) {
        [[NSUserDefaults standardUserDefaults] setObject:uuid forKey:kUserDefaultBackup];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
}

+ (NSString *)getUUIDFromKeychain {
    NSMutableDictionary *keychainItem = [NSMutableDictionary new];
    keychainItem[(__bridge id)kSecClass] = (__bridge id)kSecClassGenericPassword;
    keychainItem[(__bridge id)kSecAttrAccount] = @"uiid";
    keychainItem[(__bridge id)kSecReturnData] = (__bridge id)kCFBooleanTrue;
    keychainItem[(__bridge id)kSecMatchLimit] = (__bridge id)kSecMatchLimitOne;
#if TARGET_IPHONE_SIMULATOR
    // Ignore the access group if running on the iPhone simulator.
    //
    // Apps that are built for the simulator aren't signed, so there's no keychain access group
    // for the simulator to check. This means that all apps can see all keychain items when run
    // on the simulator.
    //
    // If a SecItem contains an access group attribute, SecItemAdd and SecItemUpdate on the
    // simulator will return -25243 (errSecNoAccessForItem).
#else
    keychainItem[(__bridge id)kSecAttrAccessGroup] = kKeyChainUDIDAccessGroup;
#endif
    CFTypeRef uuidDataTypeRef = NULL;
    OSStatus status = SecItemCopyMatching((CFDictionaryRef)keychainItem, &uuidDataTypeRef);
    if (status == errSecSuccess) {
        NSData *uuidData = (__bridge NSData *)uuidDataTypeRef;
        NSString *uuid = [NSString stringWithUTF8String:uuidData.bytes];
        if (uuidDataTypeRef) {
            CFRelease(uuidDataTypeRef);
        }
        return uuid;
    } else {
        return nil;
    }
}

@end
