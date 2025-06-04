import { TurboModule, TurboModuleRegistry } from "react-native";

export interface Spec extends TurboModule {
  getPersistentUDID(): Promise<string>;
  requestStoragePermission(): Promise<boolean>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RTNUDIDGenerator');