import React from 'react';
import { NativeModules, requireNativeComponent } from 'react-native';

interface NumberInputProps {
  style?: object
  placeholder?: string
  keyboardAppearance?: 'dark' | 'light'
  textColor?: string
  fontSize?: number
  text: string
  onChange: (event: any) => void
  onFocus?: () => void
  onBlur?: () => void
  config?: NumberInputConfig
}

interface NumberInputConfig {
  preventDecimal?: boolean
  maxInteger?: number
  maxDecimal?: number
}

export interface LocaleSeparators {
  decimalSeparator: string;
  groupSeparator: string;
}

interface NumberInputModule {
  getNumberFormatSettings: () => LocaleSeparators
  dismissKeyboard: () => void
}

const DEFAULT_CONFIG: NumberInputConfig = {
  preventDecimal: false,
  maxDecimal: 8,
}

const NumberInputMoule: NumberInputModule = NativeModules.NumberInput;

export const getNumberFormatSettings = (): LocaleSeparators => {
  try {
    return NumberInputMoule.getNumberFormatSettings()
  } catch (err) {
    return {
      decimalSeparator: '.',
      groupSeparator: ','
    }
  }
}

export const dismissKeyboard = () => {
  try {
    NumberInputMoule.dismissKeyboard()
  } catch (err) {
    console.log('Dismiss keyboard from native not working!')
  }
}

const NumberInputComponent = requireNativeComponent<NumberInputProps>('NumberInput');

const NumberInput: React.FC<NumberInputProps> = (props) => (
  <NumberInputComponent
    keyboardAppearance='dark'
    fontSize={14}
    {...props}
    config={{ ...DEFAULT_CONFIG, ...props.config }}
  />
)

export default NumberInput
