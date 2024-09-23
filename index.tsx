import {NativeModules, Platform} from 'react-native';

const {RNUdidGenerator} = NativeModules;

function getUdid(parentDir: string) {
    // fixme: 提供鸿蒙实现
    if (Platform.OS === 'harmony') {
        return Promise.resolve('123456');
    }
    return RNUdidGenerator.getUdid(parentDir) as Promise<string>;
}

const udidGenerator = {
    getUdid,
};

export default udidGenerator;
