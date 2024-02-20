import 'react-native';

export type Cache = 'immutable' | 'web' | 'cacheOnly';
export type Priority = 'low' | 'normal' | 'high';
export type ResizeMode = 'contain' | 'cover' | 'stretch' | 'center';
export type Source = {
  uri?: string;
  headers?: { [kye: string]: string };
  priority?: Priority;
  cache?: Cache;
};

export interface QuickImageViewInterface {
  clearDiskCache: () => Promise<void>;
  clearMemoryCache: () => Promise<void>;
  preload: (sources: Source[]) => void;
}

declare module 'react-native' {
  interface NativeModulesStatic {
    QuickImageView: QuickImageViewInterface;
  }
}

declare global {
  namespace globalThis {
    var nativeFabricUIManager: boolean;
  }
}
