import * as React from 'react';

import { StatusBar, StyleSheet, View } from 'react-native';
import QuickImage from 'react-native-quick-image';

export default function App() {
  return (
    <View style={styles.container}>
      <StatusBar translucent backgroundColor={'white'} />
      <QuickImage
        source={{
          uri: 'https://upload.wikimedia.org/wikipedia/commons/b/b6/Image_created_with_a_mobile_phone.png',
        }}
        style={styles.image}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: 'white',
  },
  image: {
    width: 200,
    height: 200,
    backgroundColor: 'red',
  },
});
