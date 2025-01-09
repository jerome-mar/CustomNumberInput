import React, {useState} from 'react';
import { StyleSheet, TouchableOpacity } from "react-native";
import CustomTextInput, { dismissKeyboard } from "./src/components/NumberInput";
import { KeyboardProvider } from 'react-native-keyboard-controller'

const App = () => {
  const [text, setText] = useState<string>('');
  const [text2, setText2] = useState<string>('');
  const [active, setActive] = useState<number | null>(null);

  const renderInputWithDecimals = () => (
    <TouchableOpacity
      activeOpacity={1}
      style={[styles.inputContainer, active === 1 ? { borderColor: '#FF990A' } : {}]}
    >
      <CustomTextInput
        style={styles.input}
        placeholder="Allow decimals..."
        text={text}
        config={{preventDecimal: false, maxDecimal: 5}}
        onChange={newText => {
          console.log("change 1: " + newText.nativeEvent.text)
          setText(newText.nativeEvent.text)
        }}
        fontSize={12}
        onFocus={() => setActive(1)}
        onBlur={() => setActive(null)}
      />
    </TouchableOpacity>
  )

  const renderInputWithoutDecimals = () => (
    <TouchableOpacity
      activeOpacity={1}
      style={[styles.inputContainer, active === 2 ? { borderColor: '#FF990A' } : {}]}
    >
      <CustomTextInput
        style={styles.input}
        placeholder="Disable decmials..."
        text={text2}
        textColor={'orange'}
        config={{preventDecimal: true, maxInteger: 15}}
        onChange={newText => {
          console.log("change 2: " + newText.nativeEvent.text)
          setText2(newText.nativeEvent.text)
        }}
        onFocus={() => setActive(2)}
        onBlur={() => setActive(null)}
      />
    </TouchableOpacity>
  )

  return (
      <TouchableOpacity
        activeOpacity={1}
        onPress={dismissKeyboard}
        style={styles.container}
      >
          {renderInputWithDecimals()}
          {renderInputWithoutDecimals()}
      </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    gap: 10,
  },
  inputContainer: {
    width: 200,
    height: 44,
    borderWidth: 1,
    borderColor: '#ccc',
    paddingVertical: 2,
    paddingHorizontal: 8,
    borderRadius: 8,
  },
  input: {
    flex: 1,
    height: 38,
  },
});

export default App;

