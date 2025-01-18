# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn javax.imageio.spi.ImageInputStreamSpi
-dontwarn javax.imageio.spi.ImageOutputStreamSpi
-dontwarn javax.imageio.spi.ImageReaderSpi
-dontwarn javax.imageio.spi.ImageWriterSpi

-optimizations !code/simplification/arithmetic

# Keep rules for javax.imageio.spi
-dontwarn java.awt.Color
-dontwarn java.awt.Dimension
-dontwarn java.awt.Image
-dontwarn java.awt.Point
-dontwarn java.awt.Rectangle
-dontwarn java.awt.color.ColorSpace
-dontwarn java.awt.color.ICC_ColorSpace
-dontwarn java.awt.color.ICC_Profile
-dontwarn java.awt.image.BandedSampleModel
-dontwarn java.awt.image.BufferedImage
-dontwarn java.awt.image.ColorModel
-dontwarn java.awt.image.ComponentColorModel
-dontwarn java.awt.image.ComponentSampleModel
-dontwarn java.awt.image.DataBuffer
-dontwarn java.awt.image.DataBufferByte
-dontwarn java.awt.image.DataBufferDouble
-dontwarn java.awt.image.DataBufferFloat
-dontwarn java.awt.image.DataBufferInt
-dontwarn java.awt.image.DataBufferShort
-dontwarn java.awt.image.DataBufferUShort
-dontwarn java.awt.image.DirectColorModel
-dontwarn java.awt.image.IndexColorModel
-dontwarn java.awt.image.MultiPixelPackedSampleModel
-dontwarn java.awt.image.PixelInterleavedSampleModel
-dontwarn java.awt.image.Raster
-dontwarn java.awt.image.RenderedImage
-dontwarn java.awt.image.SampleModel
-dontwarn java.awt.image.SinglePixelPackedSampleModel
-dontwarn java.awt.image.WritableRaster
-dontwarn javax.imageio.IIOException
-dontwarn javax.imageio.IIOImage
-dontwarn javax.imageio.ImageIO
-dontwarn javax.imageio.ImageReadParam
-dontwarn javax.imageio.ImageReader
-dontwarn javax.imageio.ImageTypeSpecifier
-dontwarn javax.imageio.ImageWriteParam
-dontwarn javax.imageio.ImageWriter
-dontwarn javax.imageio.event.IIOReadProgressListener
-dontwarn javax.imageio.event.IIOReadUpdateListener
-dontwarn javax.imageio.event.IIOReadWarningListener
-dontwarn javax.imageio.event.IIOWriteProgressListener
-dontwarn javax.imageio.event.IIOWriteWarningListener
-dontwarn javax.imageio.metadata.IIOInvalidTreeException
-dontwarn javax.imageio.metadata.IIOMetadata
-dontwarn javax.imageio.metadata.IIOMetadataNode
-dontwarn javax.imageio.plugins.jpeg.JPEGImageWriteParam
-dontwarn javax.imageio.spi.IIORegistry
-dontwarn javax.imageio.spi.ImageReaderWriterSpi
-dontwarn javax.imageio.spi.ServiceRegistry$Filter
-dontwarn javax.imageio.spi.ServiceRegistry
-dontwarn javax.imageio.stream.FileCacheImageInputStream
-dontwarn javax.imageio.stream.FileCacheImageOutputStream
-dontwarn javax.imageio.stream.IIOByteBuffer
-dontwarn javax.imageio.stream.ImageInputStream
-dontwarn javax.imageio.stream.ImageInputStreamImpl
-dontwarn javax.imageio.stream.ImageOutputStream
-dontwarn javax.imageio.stream.ImageOutputStreamImpl
-dontwarn javax.imageio.stream.MemoryCacheImageInputStream
-dontwarn javax.imageio.stream.MemoryCacheImageOutputStream

