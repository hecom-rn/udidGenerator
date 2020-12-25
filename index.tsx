import {NativeModules} from 'react-native';

const {RNUdidGenerator} = NativeModules;

function getUdid(parentDir: string) {
    return RNUdidGenerator.getUdid(parentDir) as Promise<string>;
}

const udidGenerator = {
    getUdid,
};

export default udidGenerator;
