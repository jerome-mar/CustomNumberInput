import React, {useEffect, useState} from 'react';
import {
  NativeModules,
  Platform,
  StyleSheet,
  TouchableOpacity,
  Text,
  requireNativeComponent,
} from 'react-native';

const NumberInput = requireNativeComponent('NumberInput');

export default function App() {
  const [value, setValue] = useState<string>('');

  useEffect(() => {
    setValue('150');
  }, []);

  // Gọi phương thức dismissKeyboardFromRN từ React Native
  const hideKeyboard = () => {
    try {
      NativeModules.NumberInput.dismissKeyboardFromRN();
      // NativeModules.NumberInput.dismissKeyboardFromRN();
    } catch (err) {
      console.log('error: ', err);
    }
  };

  return Platform.OS === 'android' ? (
    <TouchableOpacity activeOpacity={1} style={styles.container}>
      <NumberInput
        style={styles.input}
        textColor={'#0DAA41'}
        config={{preventDecimal: false, maxDecimal: 5}}
      />
      <NumberInput
        style={styles.input}
        textColor={'#0DAA41'}
        config={{ preventDecimal: true, maxInteger: 5 }}
      />
    </TouchableOpacity>
  ) : (
    <TouchableOpacity
      activeOpacity={1}
      style={styles.container}
      onPress={hideKeyboard}>
      {/*<TouchableOpacity activeOpacity={1}>*/}
      {/*  <NumberInput*/}
      {/*    style={styles.input}*/}
      {/*    onChange={ev => console.log(ev.nativeEvent.text)}*/}
      {/*    config={{preventDecimal: true, maxInteger: 5}}*/}
      {/*    placeholder="Hiiii"*/}
      {/*    textColor="#FF7545"*/}
      {/*    // text={value}*/}
      {/*    fontSize={12}*/}
      {/*  />*/}
      {/*</TouchableOpacity>*/}
      <TouchableOpacity activeOpacity={1}>
        <NumberInput
          style={styles.input}
          // onChange={ev => setValue(ev.nativeEvent.text)}
          config={{preventDecimal: false, maxDecimal: 3}}
          placeholder="Helloooo Allow Decimals"
          // textColor="#000000"
          // text={value}
        />
      </TouchableOpacity>
      <TouchableOpacity onPress={() => console.log(value)}>
        <Text>Submit</Text>
      </TouchableOpacity>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#f5f5f5',
    gap: 20,
  },
  input: {
    width: 200,
    height: 50,
    // borderWidth: 1,
    // borderColor: '#ccc',
    // borderRadius: 5,
    // paddingHorizontal: 10,
    backgroundColor: 'tomato',
  },
});

// const Input = () => {
//   const [value, setValue] = useState('');
//
//   const onSubmit = () => {
//     console.log(value);
//   };
//
//   const onChange = ev => {
//     console.log(ev.nativeEvent.value);
//   };
//
//   const onChange2 = ev => {
//     console.log(ev.nativeEvent.value);
//   };
//
//   return (
//     <TouchableOpacity
//       activeOpacity={1}
//       style={styles.container}
//       onPress={Keyboard.dismiss}>
//       <NumberInput
//         style={[styles.input, { backgroundColor: 'red' }]}
//         placeholder="Disable decimals"
//         // value={value}
//         // preventDecimals={true}
//         onChange={onChange}
//         // maxInteger={2}
//         config={{preventDecimal: true, maxInteger: 5}}
//         textColor="rgba(248, 248, 248, 0.9)"
//       />
//       <View style={styles.inputContainer}>
//         <NumberInput
//           style={styles.input}
//           placeholder="Allow decimals"
//           // value={value}
//           // preventDecimals={false}
//           onChange={onChange2}
//           config={{preventDecimal: false, maxDecimal: 4}}
//           textColor="rgba(248, 248, 248, 0.9)"
//         />
//       </View>
//       <View style={styles.inputContainer}>
//         <NumberInput
//           style={styles.input}
//           placeholder="Allow decimals"
//           // value={value}
//           // preventDecimals={false}
//           onChange={onChange2}
//           config={{preventDecimal: false, maxDecimal: 2}}
//           fontSize={14}
//           textColor="rgba(248, 248, 248, 0.9)"
//         />
//       </View>
//       <View style={styles.inputContainer}>
//         <NumberInput
//           style={styles.input}
//           placeholder="Allow decimals"
//           // value={value}
//           // preventDecimals={false}
//           onChange={onChange2}
//           config={{preventDecimal: false, maxDecimal: 1}}
//           fontSize={14}
//           textColor="rgba(248, 248, 248, 0.9)"
//         />
//       </View>
//       <TouchableOpacity style={styles.button} onPress={onSubmit}>
//         <Text style={styles.textButton}>Submit</Text>
//       </TouchableOpacity>
//     </TouchableOpacity>
//   );
// };
//
// export default Input;
//
// const styles = StyleSheet.create({
//   container: {
//     flex: 1,
//     alignItems: 'center',
//     backgroundColor: 'black',
//     paddingTop: 100,
//     gap: 20,
//   },
//   inputContainer: {
//     width: 300,
//     height: 100,
//     borderWidth: 1,
//     borderColor: 'rgba(248, 248, 248, 0.2)',
//     borderRadius: 8,
//     paddingHorizontal: 10,
//     backgroundColor: 'rgba(248, 248, 248, 0.1)',
//   },
//   input: {
//     // flex: 0.5,
//     width: 100,
//     height: 50,
//     backgroundColor: 'black',
//     // fontWeight: '400',
//     fontSize: 12,
//     borderColor: 'red',
//     borderWidth: 1,
//   },
//   button: {
//     width: 300,
//     height: 100,
//     backgroundColor: 'rgba(255, 153, 10, 1)',
//     alignItems: 'center',
//     justifyContent: 'center',
//     borderRadius: 8,
//   },
//   textButton: {
//     color: 'rgba(24, 14, 0, 1)',
//   },
// });
