//
//  Utils.m
//  NumberInput
//
//  Created by Hiếu Dương Thanh on 23/12/24.
//

#import "Utils.h"
#import <UIKit/UIKit.h>

@implementation Utils

+ (UIColor *)colorWithHexString:(NSString *)hexString {
  unsigned int hexInt = 0;
  NSScanner *scanner = [NSScanner scannerWithString:hexString];
  [scanner setScanLocation:1];
  [scanner scanHexInt:&hexInt];
  
  CGFloat red = ((hexInt >> 16) & 0xFF) / 255.0;
  CGFloat green = ((hexInt >> 8) & 0xFF) / 255.0;
  CGFloat blue = (hexInt & 0xFF) / 255.0;
  
  return [UIColor colorWithRed:red green:green blue:blue alpha:1.0];
}

+ (NSString *)formatNumberString:(NSString *)inputText
             withGroupSeparator:(NSString *)groupSeparator
                decimalSeparator:(NSString *)decimalSeparator {

  NSString *cleanText = [inputText stringByReplacingOccurrencesOfString:groupSeparator withString:@""];

  BOOL hasDecimalSeparator = [cleanText rangeOfString:decimalSeparator].location != NSNotFound;

  NSArray *components = [cleanText componentsSeparatedByString:decimalSeparator];
  NSString *integerPart = components.firstObject;
  if (integerPart.length == 0) {
    integerPart = @"0";
  }
  NSString *decimalPart = components.count > 1 ? components[1] : @"";

  NSNumberFormatter *numberFormatter = [[NSNumberFormatter alloc] init];
  [numberFormatter setGroupingSeparator:groupSeparator];
  [numberFormatter setGroupingSize:3];
  [numberFormatter setNumberStyle:NSNumberFormatterDecimalStyle];
  
  NSDecimalNumber *decimalNumber = [NSDecimalNumber decimalNumberWithString:integerPart];

  NSString *formattedIntegerPart = [numberFormatter stringFromNumber:decimalNumber];
  
  if (hasDecimalSeparator) {
    return [NSString stringWithFormat:@"%@%@%@", formattedIntegerPart, decimalSeparator, decimalPart];
  } else {
    return formattedIntegerPart;
  }
}

@end
