#import "KRMMKVModule.h"
#import <OpenKuiklyIOSRender/NSObject+KR.h>
#import <MMKV/MMKV.h>

@implementation KuiklyMMKVModule

@synthesize hr_rootView;

#pragma mark - Auto Initialization

+ (void)load {
    // 在类加载时自动初始化 MMKV，业务方无需手动调用
    // +load 方法在主线程执行，满足 MMKV 的初始化要求
    [MMKV initializeMMKV:nil];
}

#pragma mark - Helper

- (MMKV *)mmkvWithParams:(NSDictionary *)params {
    NSString *mmapID = params[@"mmapID"];
    if (mmapID && mmapID.length > 0) {
        return [MMKV mmkvWithID:mmapID];
    }
    return [MMKV defaultMMKV];
}

#pragma mark - Encode

- (void)encode:(NSDictionary *)args {
    NSDictionary *params = [args[KR_PARAM_KEY] hr_stringToDictionary];
    NSString *key = params[@"key"];
    NSString *type = params[@"type"];
    MMKV *mmkv = [self mmkvWithParams:params];
    
    if ([type isEqualToString:@"string"]) {
        [mmkv setString:params[@"value"] forKey:key];
    } else if ([type isEqualToString:@"int"]) {
        [mmkv setInt32:[params[@"value"] intValue] forKey:key];
    } else if ([type isEqualToString:@"long"]) {
        [mmkv setInt64:[params[@"value"] longLongValue] forKey:key];
    } else if ([type isEqualToString:@"float"]) {
        [mmkv setFloat:[params[@"value"] floatValue] forKey:key];
    } else if ([type isEqualToString:@"double"]) {
        [mmkv setDouble:[params[@"value"] doubleValue] forKey:key];
    } else if ([type isEqualToString:@"bool"]) {
        [mmkv setBool:[params[@"value"] boolValue] forKey:key];
    } else if ([type isEqualToString:@"bytes"]) {
        NSString *base64Str = params[@"value"];
        NSData *data = [[NSData alloc] initWithBase64EncodedString:base64Str options:0];
        if (data) {
            [mmkv setData:data forKey:key];
        }
    }
}

#pragma mark - Decode

- (NSString *)decode:(NSDictionary *)args {
    NSDictionary *params = [args[KR_PARAM_KEY] hr_stringToDictionary];
    NSString *key = params[@"key"];
    NSString *type = params[@"type"];
    MMKV *mmkv = [self mmkvWithParams:params];
    
    if ([type isEqualToString:@"string"]) {
        NSString *defaultValue = params[@"defaultValue"] ?: @"";
        NSString *value = [mmkv getStringForKey:key defaultValue:defaultValue];
        return value ?: defaultValue;
    } else if ([type isEqualToString:@"int"]) {
        int defaultValue = [params[@"defaultValue"] intValue];
        int32_t value = [mmkv getInt32ForKey:key defaultValue:defaultValue];
        return [NSString stringWithFormat:@"%d", value];
    } else if ([type isEqualToString:@"long"]) {
        long long defaultValue = [params[@"defaultValue"] longLongValue];
        int64_t value = [mmkv getInt64ForKey:key defaultValue:defaultValue];
        return [NSString stringWithFormat:@"%lld", value];
    } else if ([type isEqualToString:@"float"]) {
        float defaultValue = [params[@"defaultValue"] floatValue];
        float value = [mmkv getFloatForKey:key defaultValue:defaultValue];
        return [NSString stringWithFormat:@"%f", value];
    } else if ([type isEqualToString:@"double"]) {
        double defaultValue = [params[@"defaultValue"] doubleValue];
        double value = [mmkv getDoubleForKey:key defaultValue:defaultValue];
        return [NSString stringWithFormat:@"%f", value];
    } else if ([type isEqualToString:@"bool"]) {
        BOOL defaultValue = [params[@"defaultValue"] boolValue];
        BOOL value = [mmkv getBoolForKey:key defaultValue:defaultValue];
        return value ? @"true" : @"false";
    } else if ([type isEqualToString:@"bytes"]) {
        NSData *data = [mmkv getDataForKey:key];
        if (data) {
            return [data base64EncodedStringWithOptions:0];
        }
        return @"";
    }
    return @"";
}

#pragma mark - Management

- (NSString *)containsKey:(NSDictionary *)args {
    NSDictionary *params = [args[KR_PARAM_KEY] hr_stringToDictionary];
    NSString *key = params[@"key"];
    MMKV *mmkv = [self mmkvWithParams:params];
    return [mmkv containsKey:key] ? @"true" : @"false";
}

- (void)removeValueForKey:(NSDictionary *)args {
    NSDictionary *params = [args[KR_PARAM_KEY] hr_stringToDictionary];
    NSString *key = params[@"key"];
    MMKV *mmkv = [self mmkvWithParams:params];
    [mmkv removeValueForKey:key];
}

- (void)removeValuesForKeys:(NSDictionary *)args {
    NSDictionary *params = [args[KR_PARAM_KEY] hr_stringToDictionary];
    NSArray *keys = params[@"keys"];
    MMKV *mmkv = [self mmkvWithParams:params];
    if (keys) {
        [mmkv removeValuesForKeys:keys];
    }
}

- (void)allKeys:(NSDictionary *)args {
    NSDictionary *params = [args[KR_PARAM_KEY] hr_stringToDictionary];
    MMKV *mmkv = [self mmkvWithParams:params];
    NSArray *keys = [mmkv allKeys] ?: @[];
    NSString *keysStr = [keys componentsJoinedByString:@","];
    
    void (^callback)(id) = args[KR_CALLBACK_KEY];
    if (callback) {
        callback(@{@"keys": keysStr});
    }
}

- (NSString *)count:(NSDictionary *)args {
    NSDictionary *params = [args[KR_PARAM_KEY] hr_stringToDictionary];
    MMKV *mmkv = [self mmkvWithParams:params];
    return [NSString stringWithFormat:@"%lu", (unsigned long)[mmkv count]];
}

- (NSString *)totalSize:(NSDictionary *)args {
    NSDictionary *params = [args[KR_PARAM_KEY] hr_stringToDictionary];
    MMKV *mmkv = [self mmkvWithParams:params];
    return [NSString stringWithFormat:@"%lu", (unsigned long)[mmkv totalSize]];
}

- (NSString *)actualSize:(NSDictionary *)args {
    NSDictionary *params = [args[KR_PARAM_KEY] hr_stringToDictionary];
    MMKV *mmkv = [self mmkvWithParams:params];
    return [NSString stringWithFormat:@"%lu", (unsigned long)[mmkv actualSize]];
}

- (void)clearAll:(NSDictionary *)args {
    NSDictionary *params = [args[KR_PARAM_KEY] hr_stringToDictionary];
    MMKV *mmkv = [self mmkvWithParams:params];
    [mmkv clearAll];
}

- (void)trim:(NSDictionary *)args {
    NSDictionary *params = [args[KR_PARAM_KEY] hr_stringToDictionary];
    MMKV *mmkv = [self mmkvWithParams:params];
    [mmkv trim];
}

- (void)batchEncode:(NSDictionary *)args {
    NSDictionary *params = [args[KR_PARAM_KEY] hr_stringToDictionary];
    NSArray *entries = params[@"entries"];
    MMKV *mmkv = [self mmkvWithParams:params];
    
    for (NSDictionary *entry in entries) {
        NSString *key = entry[@"key"];
        NSString *type = entry[@"type"] ?: @"string";
        
        if ([type isEqualToString:@"string"]) {
            [mmkv setString:entry[@"value"] forKey:key];
        } else if ([type isEqualToString:@"int"]) {
            [mmkv setInt32:[entry[@"value"] intValue] forKey:key];
        } else if ([type isEqualToString:@"long"]) {
            [mmkv setInt64:[entry[@"value"] longLongValue] forKey:key];
        } else if ([type isEqualToString:@"float"]) {
            [mmkv setFloat:[entry[@"value"] floatValue] forKey:key];
        } else if ([type isEqualToString:@"double"]) {
            [mmkv setDouble:[entry[@"value"] doubleValue] forKey:key];
        } else if ([type isEqualToString:@"bool"]) {
            [mmkv setBool:[entry[@"value"] boolValue] forKey:key];
        }
    }
}

@end
