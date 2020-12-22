
# react-native-udid-generator

## Getting started

`$ npm install react-native-udid-generator --save`

### Mostly automatic installation

`$ react-native link react-native-udid-generator`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-udid-generator` and add `RNUdidGenerator.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNUdidGenerator.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNUdidGeneratorPackage;` to the imports at the top of the file
  - Add `new RNUdidGeneratorPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-udid-generator'
  	project(':react-native-udid-generator').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-udid-generator/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-udid-generator')
  	```

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNUdidGenerator.sln` in `node_modules/react-native-udid-generator/windows/RNUdidGenerator.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Udid.Generator.RNUdidGenerator;` to the usings at the top of the file
  - Add `new RNUdidGeneratorPackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNUdidGenerator from 'react-native-udid-generator';

// TODO: What to do with the module?
RNUdidGenerator;
```
  