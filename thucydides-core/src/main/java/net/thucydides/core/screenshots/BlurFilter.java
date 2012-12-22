package net.thucydides.core.screenshots;

import com.jhlabs.image.BoxBlurFilter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: rahuljai
 * Date: 22/12/12
 * Time: 10:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class BlurFilter {

    public byte[] blur(File srcFile) throws Exception {
        BufferedImage srcImage = ImageIO.read(srcFile);
        BufferedImage destImage = deepCopy(srcImage);

        BoxBlurFilter boxBlurFilter = new BoxBlurFilter();
        boxBlurFilter.setRadius(7);
        boxBlurFilter.setIterations(3);
        destImage = boxBlurFilter.filter(srcImage, destImage);

        WritableRaster raster = destImage.getRaster();
        DataBufferByte destImageData   = (DataBufferByte) raster.getDataBuffer();
        return  destImageData.getData();
    }

    private BufferedImage deepCopy(BufferedImage srcImage) {
        ColorModel cm = srcImage.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = srcImage.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

}
