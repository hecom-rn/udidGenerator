import {NativeModules, Platform} from 'react-native';
import { RTNUDIDGeneratorModule } from './harmony';


const {RNUdidGenerator} = NativeModules;

function getUdid(parentDir: string) {
    // fixme: 提供鸿蒙实现
    if (Platform.OS === 'harmony') {
        return RTNUDIDGeneratorModule.getPersistentUDID() as Promise<string>;
    }
    return RNUdidGenerator.getUdid(parentDir) as Promise<string>;
}

const udidGenerator = {
    getUdid,
};

export default udidGenerator;
