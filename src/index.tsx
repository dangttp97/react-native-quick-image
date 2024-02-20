import React, { forwardRef, memo } from 'react';
import {
  Platform,
  type AccessibilityProps,
  type ColorValue,
  type ImageRequireSource,
  type LayoutChangeEvent,
  type ShadowStyleIOS,
  type StyleProp,
  type ViewProps,
  Image,
  StyleSheet,
} from 'react-native';
import type { ImageResolvedAssetSource } from 'react-native';
import type { TransformsStyle } from 'react-native';
import type { FlexStyle } from 'react-native';
import { View } from 'react-native';
import QuickImageView from './QuickImageViewNativeComponent';
import type { ResizeMode, Source } from './declaration';
import { NativeModules } from 'react-native';

const resizeMode = {
  contain: 'contain',
  cover: 'cover',
  stretch: 'stretch',
  center: 'center',
} as const;

const priority = {
  low: 'low',
  normal: 'normal',
  high: 'high',
} as const;

const cacheControl = {
  immutable: 'immutable',
  web: 'web',
  cacheOnly: 'cacheOnly',
} as const;

export interface OnLoadEvent {
  nativeEvent: {
    width: number;
    height: number;
  };
}

export interface OnProgressEvent {
  nativeEvent: {
    loaded: number;
    total: number;
  };
}

export interface ImageStyle extends FlexStyle, TransformsStyle, ShadowStyleIOS {
  backfaceVisibility?: 'visible' | 'hidden';
  borderBottomLeftRadius?: number;
  borderBottomRightRadius?: number;
  backgroundColor?: string;
  borderColor?: string;
  borderWidth?: number;
  borderRadius?: number;
  borderTopLeftRadius?: number;
  borderTopRightRadius?: number;
  overlayColor?: string;
  opacity?: number;
}
export interface QuickImageProps extends AccessibilityProps, ViewProps {
  source?: Source | ImageRequireSource;
  defaultSource?: ImageRequireSource;
  resizeMode?: ResizeMode;
  fallback?: boolean;

  onLoadStart?(): void;

  onProgress?(event: OnProgressEvent): void;

  onLoad?(event: OnLoadEvent): void;

  onError?(): void;

  onLoadEnd?(): void;

  /**
   * onLayout function
   *
   * Invoked on mount and layout changes with
   *
   * {nativeEvent: { layout: {x, y, width, height}}}.
   */
  onLayout?: (event: LayoutChangeEvent) => void;

  /**
   *
   * Style
   */
  style?: StyleProp<ImageStyle>;

  /**
   * TintColor
   *
   * If supplied, changes the color of all the non-transparent pixels to the given color.
   */

  tintColor?: ColorValue;

  /**
   * A unique identifier for this element to be used in UI Automation testing scripts.
   */
  testID?: string;

  /**
   * Render children within the image.
   */
  children?: React.ReactNode;
}

const resolveDefaultSource = (
  defaultSource?: ImageRequireSource
): string | number | null => {
  if (!defaultSource) {
    return null;
  }
  if (Platform.OS === 'android') {
    // Android receives a URI string, and resolves into a Drawable using RN's methods.
    const resolved = Image.resolveAssetSource(
      defaultSource as ImageRequireSource
    );

    if (resolved) {
      return resolved.uri;
    }

    return null;
  }
  // iOS or other number mapped assets
  // In iOS the number is passed, and bridged automatically into a UIImage
  return defaultSource;
};

const QuickImageBase = ({
  ...props
}: QuickImageProps & { forwardedRef: React.Ref<any> }) => {
  const {
    fallback,
    source,
    defaultSource,
    tintColor,
    onLoadStart,
    onProgress,
    onLoad,
    onError,
    onLoadEnd,
    style,
    children,
    // eslint-disable-next-line @typescript-eslint/no-shadow
    resizeMode = 'cover',
    forwardedRef,
  } = props;

  if (fallback) {
    const cleanedSource = { ...(source as any) };
    delete cleanedSource.cache;

    const resolvedSource = Image.resolveAssetSource(cleanedSource);

    return (
      <View style={[styles.imageContainer, style]} ref={forwardedRef}>
        <Image
          {...props}
          style={[StyleSheet.absoluteFill, { tintColor }]}
          source={resolvedSource}
          defaultSource={defaultSource}
          onLoadStart={onLoadStart}
          onProgress={onProgress}
          onLoad={onLoad as any}
          onError={onError}
          onLoadEnd={onLoadEnd}
          resizeMode={resizeMode}
        />
        {children}
      </View>
    );
  }

  const FABRIC_ENABLED = !!global?.nativeFabricUIManager;

  const resolvedSource = Image.resolveAssetSource(
    source as any
  ) as ImageResolvedAssetSource & { headers: any };

  if (
    resolvedSource?.headers &&
    (FABRIC_ENABLED || Platform.OS === 'android')
  ) {
    const headersArray: { name: string; value: string }[] = [];
    Object.keys(resolvedSource.headers).forEach((key) => {
      headersArray.push({ name: key, value: resolvedSource.headers[key] });
    });
    resolvedSource.headers = headersArray;
  }

  const resolvedDefaultSource = resolveDefaultSource(defaultSource);
  const resolveDefaultSourceAsString =
    resolvedDefaultSource !== null ? String(resolveDefaultSource) : null;

  return (
    <View style={[styles.imageContainer, style]} ref={forwardedRef}>
      <QuickImageView
        {...props}
        tintColor={tintColor}
        style={StyleSheet.absoluteFill}
        source={resolvedSource}
        defaultSource={resolveDefaultSourceAsString}
        onQuickImageLoadStart={onLoadStart}
        onQuickImageError={onError}
        onQuickImageLoad={onLoad}
        onQuickImageLoadEnd={onLoadEnd}
        onQuickImageProgress={onProgress}
        resizeMode={resizeMode}
      />
      {children}
    </View>
  );
};

const QuickImageMemo = memo(QuickImageBase);
const QuickImageComp = forwardRef(
  (props: QuickImageProps, ref: React.Ref<any>) => (
    <QuickImageMemo forwardedRef={ref} {...props} />
  )
);
QuickImageComp.displayName = 'QuickImage';

export interface QuickImageStaticProps {
  resizeMode?: typeof resizeMode;
  priority?: typeof priority;
  cacheControl?: typeof cacheControl;
  preload: (sources: Source[]) => void;
  clearMemoryCache: () => Promise<void>;
  clearDiskCache: () => Promise<void>;
}

const QuickImage: React.ComponentType<QuickImageProps> & QuickImageStaticProps =
  QuickImageComp as any;

QuickImage.resizeMode = resizeMode;
QuickImage.cacheControl = cacheControl;
QuickImage.priority = priority;
QuickImage.preload = (sources: Source[]) =>
  NativeModules.QuickImageView.preload(sources);
QuickImage.clearMemoryCache = () =>
  NativeModules.QuickImageView.clearMemoryCache();
QuickImage.clearDiskCache = () => NativeModules.QuickImageView.clearDiskCache();

const styles = StyleSheet.create({
  imageContainer: {
    overflow: 'hidden',
  },
});

export default QuickImage;
