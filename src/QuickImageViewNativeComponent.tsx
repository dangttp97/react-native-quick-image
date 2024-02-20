import type { ColorValue } from 'react-native';
import type { ViewProps } from 'react-native';
import type { HostComponent } from 'react-native';
import type {
  BubblingEventHandler,
  Float,
  Int32,
  WithDefault,
} from 'react-native/Libraries/Types/CodegenTypes';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';

type Headers = ReadonlyArray<Readonly<{ name: string; value: string }>>;
type Priority = WithDefault<'low' | 'normal' | 'high', 'normal'>;
type CacheControl = WithDefault<'immutable' | 'web' | 'cacheOnly', 'web'>;

type QuickImageSource = Readonly<{
  uri?: string;
  headers?: Headers;
  priority?: Priority;
  cache?: CacheControl;
}>;

type OnLoadEvent = Readonly<{
  width: Float;
  height: Float;
}>;

type OnProgressEvent = Readonly<{
  loaded: Int32;
  total: Int32;
}>;

interface NativeProps extends ViewProps {
  onQuickImageError?: BubblingEventHandler<Readonly<{}>>;
  onQuickImageLoad?: BubblingEventHandler<OnLoadEvent>;
  onQuickImageProgress?: BubblingEventHandler<OnProgressEvent>;
  onQuickImageLoadEnd?: BubblingEventHandler<Readonly<{}>>;
  onQuickImageLoadStart?: BubblingEventHandler<Readonly<{}>>;
  source?: QuickImageSource;
  defaultSource?: string | null;
  resizeMode?: WithDefault<'contain' | 'cover' | 'stretch' | 'center', 'cover'>;
  tintColor?: ColorValue;
}

export default codegenNativeComponent<NativeProps>(
  'QuickImageView'
) as HostComponent<NativeProps>;
