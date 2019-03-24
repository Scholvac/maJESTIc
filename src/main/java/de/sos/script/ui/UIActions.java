package de.sos.script.ui;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.google.common.io.Resources;

public abstract class UIActions {

	public static final int ICON_SIZE = 32;
	
	public static ImageIcon getIcon(String string) {
		return getIcon(string, ICON_SIZE);
	}
	public static ImageIcon getIcon(String resourceName, int size) {
		URL url = resolveResource(resourceName);
		BufferedImage before = getImage(url);
		
		int w = before.getWidth();
		int h = before.getHeight();
		BufferedImage after = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		float fW = (float)size / (float)w;
		float fH = (float)size / (float)h;
		at.scale(fW, fH);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		after = scaleOp.filter(before, after);
		
		
		return new ImageIcon(after);
	}
		
	public static BufferedImage getImage(URL url) {
		try {
			BufferedImage bimg = ImageIO.read(url);
			return bimg;
		} catch (IOException e) {
			return null;
		}
	}
	public static URL resolveResource(String resourceName) {
		File f = new File(resourceName);
        if (f.exists())
            try {
                return f.toPath().toUri().toURL();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        URL url = UIActions.class.getClassLoader().getResource(resourceName);
        if (url != null)
            return url;
        try {
            url = Resources.getResource(resourceName);
        } catch (Exception e) {
            // nothing to do here, we are allowed to return null
        }
        if (url != null)
            return url;
        // TODO: try some others
		return null;
	}
}
