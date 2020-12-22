import {NativeModules} from 'react-native';

const {RNUdidGenerator} = NativeModules;

function testToast() {
    RNUdidGenerator.testToast();
}

const udidGenerator = {
    testToast,
};

export default udidGenerator;
