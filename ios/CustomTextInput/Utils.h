//
//  Utils.h
//  NumberInput
//
//  Created by Hiếu Dương Thanh on 23/12/24.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface Utils : NSObject

+ (UIColor *)colorWithHexString:(NSString *)hexString;

+ (NSString *)formatNumberString:(NSString *)inputText withGroupSeparator:(NSString *)groupSeparator decimalSeparator:(NSString *)decimalSeparator;

@end

NS_ASSUME_NONNULL_END
