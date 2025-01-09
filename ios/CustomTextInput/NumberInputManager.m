//
//  NumberInputManager.m
//  NumberInput
//
//  Created by Hiếu Dương Thanh on 23/12/24.
//

#import "NumberInputManager.h"
#import <React/RCTBridge.h>
#import <React/RCTEventDispatcher.h>
#import "Utils.h"

@interface NumberInput : UITextField<UITextFieldDelegate>
@property (nonatomic, strong) UITextField *textField;
@property (nonatomic, copy) RCTBubblingEventBlock onFocus;
@property (nonatomic, copy) RCTBubblingEventBlock onBlur;
@property (nonatomic, copy) RCTBubblingEventBlock onChange;
@property (nonatomic, strong) NSString *decimalSeparator;
@property (nonatomic, strong) NSString *groupSeparator;
@property (nonatomic, assign) BOOL preventDecimal;
@property (nonatomic, assign) NSInteger maxDecimal;
@property (nonatomic, assign) NSInteger maxInteger;
@property (nonatomic, assign) NSString* colorText;
@end

@implementation NumberInput
- (instancetype)init {
  self = [super init];
  if (self) {
    self.keyboardType = UIKeyboardTypeDecimalPad;
    self.delegate = self;
    self.tintColor = [Utils colorWithHexString:@"#FF990A"];

    NSLocale *currentLocale = [NSLocale currentLocale];
    self.decimalSeparator = [currentLocale objectForKey:NSLocaleDecimalSeparator];
    self.groupSeparator = [currentLocale objectForKey:NSLocaleGroupingSeparator];
  }
  return self;
}

- (void)setKeyboardAppearanceMode:(NSString *)mode {
  if ([mode isEqualToString:@"dark"]) {
    self.keyboardAppearance = UIKeyboardAppearanceDark;
  } else {
    self.keyboardAppearance = UIKeyboardAppearanceLight;
  }
}

- (void)setFontSize:(NSNumber *)fontSize {
  CGFloat size = [fontSize floatValue];
  self.font = [UIFont systemFontOfSize:size];
}

- (void)textFieldDidBeginEditing:(UITextField *)textField {
  if (self.onFocus) {
    self.onFocus(@{});
  }
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
  if (self.onBlur) {
    self.onBlur(@{});
  }
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {
  // preventDecimal
  if (self.preventDecimal && [string containsString:self.decimalSeparator]) {
    return NO;
  }
  
  if (string.length == 0 && range.location == 0 && range.length == textField.text.length) {
    textField.text = @"";
    if (self.onChange) {
      self.onChange(@{ @"text": @"" });
    }
    return NO;
  }

  NSString *updatedText = [textField.text stringByReplacingCharactersInRange:range withString:string];
  
  // allow decimalSeparator
  NSUInteger separatorCount = [[updatedText componentsSeparatedByString:self.decimalSeparator] count] - 1;
  if (separatorCount > 1) {
    return NO;
  }
  
  // maxInteger
  NSString *cleanText = [updatedText stringByReplacingOccurrencesOfString:self.groupSeparator withString:@""];
  NSArray<NSString *> *components = [cleanText componentsSeparatedByString:self.decimalSeparator];
  NSString *integerPart = components.firstObject;
  if (self.maxInteger != NSNotFound && integerPart.length > self.maxInteger) {
    return NO;
  }

  // maxDecimal
  if (components.count == 2) {
    NSString *decimalPart = components[1];
    if (decimalPart.length > self.maxDecimal) {
      NSString *newDecimalPart = [decimalPart substringToIndex:self.maxDecimal];
      updatedText = [NSString stringWithFormat:@"%@%@%@", components[0], self.decimalSeparator, newDecimalPart];
    }
  }

  if (updatedText.length == 0) {
    return YES;
  }

  NSString *formattedText = [Utils formatNumberString:updatedText withGroupSeparator:self.groupSeparator decimalSeparator:self.decimalSeparator];

  NSString *currentText = textField.text;
  UITextPosition *cursorPositionStart = textField.selectedTextRange.start;
  NSInteger cursorPosition = [textField offsetFromPosition:textField.beginningOfDocument toPosition:cursorPositionStart];

  textField.text = formattedText;
  
  NSInteger newCursorPosition = cursorPosition + (formattedText.length - currentText.length);

  if (newCursorPosition > 0 && newCursorPosition <= formattedText.length) {
    UITextPosition *newCursorPositionStart = [textField positionFromPosition:textField.beginningOfDocument offset:newCursorPosition];
    textField.selectedTextRange = [textField textRangeFromPosition:newCursorPositionStart toPosition:newCursorPositionStart];
  } else {
    UITextPosition *endPosition = [textField positionFromPosition:textField.beginningOfDocument offset:formattedText.length];
    textField.selectedTextRange = [textField textRangeFromPosition:endPosition toPosition:endPosition];
  }
  if (self.onChange) {
    self.onChange(@{ @"text": textField.text });
  }

  return NO;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
  [textField resignFirstResponder];
  return YES;
}

@end

@implementation NumberInputManager

RCT_EXPORT_MODULE(NumberInput)

+ (BOOL)requiresMainQueueSetup {
  return YES;
}

- (UIView *)view {
  NumberInput *textField = [[NumberInput alloc] init];
  textField.delegate = textField;

  UIToolbar *toolbar = [[UIToolbar alloc] initWithFrame:CGRectMake(0, 0, UIScreen.mainScreen.bounds.size.width, 44)];
  UIBarButtonItem *flexibleSpace = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
  UIBarButtonItem *doneButton = [[UIBarButtonItem alloc] initWithTitle:@"Done"
                                                                style:UIBarButtonItemStyleDone
                                                               target:self
                                                                action:@selector(dismissKeyboardImpl)];
  [toolbar setItems:@[flexibleSpace, doneButton]];
  textField.inputAccessoryView = toolbar;
  
  return textField;
}

- (void)dismissKeyboardImpl {
  [[UIApplication sharedApplication] sendAction:@selector(resignFirstResponder) to:nil from:nil forEvent:nil];
}

- (NSDictionary *)getNumberFormatSettingsImpl {
  NSLocale *currentLocale = [NSLocale currentLocale];

  return @{
    @"decimalSeparator": [currentLocale objectForKey:NSLocaleDecimalSeparator],
    @"groupSeparator": [currentLocale objectForKey:NSLocaleGroupingSeparator],
  };
}

RCT_EXPORT_METHOD(dismissKeyboard) {
  dispatch_async(dispatch_get_main_queue(), ^{
    NSLog(@"Should dismiss keyboard");
    [self dismissKeyboardImpl];
  });
}

RCT_EXPORT_BLOCKING_SYNCHRONOUS_METHOD(getNumberFormatSettings) {
  return [self getNumberFormatSettingsImpl];
}

RCT_EXPORT_VIEW_PROPERTY(text, NSString)
RCT_EXPORT_VIEW_PROPERTY(onFocus, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onBlur, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onChange, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(placeholder, NSString)
RCT_EXPORT_VIEW_PROPERTY(textColor, UIColor)
RCT_EXPORT_VIEW_PROPERTY(fontSize, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(keyboardAppearance, NSString)

RCT_CUSTOM_VIEW_PROPERTY(config, NSDictionary, NumberInput) {
  BOOL preventDecimal = [RCTConvert BOOL:json[@"preventDecimal"]];
  view.preventDecimal = preventDecimal;
  
  // Receive maxDecimal (default: 8)
  NSInteger maxDecimal = json[@"maxDecimal"] ? [RCTConvert NSInteger:json[@"maxDecimal"]] : 8;
  view.maxDecimal = maxDecimal;
  
  // Receive maxInteger (default: nil - NotFound)
  NSNumber *maxInteger = json[@"maxInteger"] ? [RCTConvert NSNumber:json[@"maxInteger"]] : nil;
  view.maxInteger = maxInteger ? maxInteger.integerValue : NSNotFound;
}

@end


