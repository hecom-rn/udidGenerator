import {NativeModules} from 'react-native';

const {RNUdidGenerator} = NativeModules;

async function getUdid(parentDir: string) {
    return (await RNUdidGenerator.getUdid(parentDir)) as string;
}

const udidGenerator = {
    getUdid,
};

export default udidGenerator;
