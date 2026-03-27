#import <Foundation/Foundation.h>
#import <OpenKuiklyIOSRender/KRBaseModule.h>

NS_ASSUME_NONNULL_BEGIN

/**
 * iOS 原生 MMKV Module
 *
 * 接收 KMP 层 MMKVModule 的调用，委派到 MMKV iOS SDK 执行。
 * 方法名与 KMP 层 METHOD_* 常量对应，Kuikly 框架会自动通过方法名路由。
 */
@interface KuiklyMMKVModule : KRBaseModule

@end

NS_ASSUME_NONNULL_END
