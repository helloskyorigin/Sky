import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ProcessLogo {
    public static void main(String[] args) throws Exception {
        File inputFile = new File("app/src/main/res/drawable/threatshield_logo_v2_1784217942420.jpg");
        if (!inputFile.exists()) {
            System.out.println("Input file not found");
            return;
        }
        
        BufferedImage img = ImageIO.read(inputFile);
        if (img == null) {
            System.out.println("Could not read image");
            return;
        }
        
        int width = img.getWidth();
        int height = img.getHeight();
        
        int minX = width;
        int minY = height;
        int maxX = 0;
        int maxY = 0;
        
        int tolerance = 35;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = img.getRGB(x, y);
                Color color = new Color(rgb, true);
                if (color.getRed() > tolerance || color.getGreen() > tolerance || color.getBlue() > tolerance) {
                    if (x < minX) minX = x;
                    if (y < minY) minY = y;
                    if (x > maxX) maxX = x;
                    if (y > maxY) maxY = y;
                }
            }
        }
        
        System.out.println("Bounding box: " + minX + ", " + minY + " to " + maxX + ", " + maxY);
        
        int cropWidth = maxX - minX + 1;
        int cropHeight = maxY - minY + 1;
        
        BufferedImage croppedImg = new BufferedImage(cropWidth, cropHeight, BufferedImage.TYPE_INT_ARGB);
        
        for (int y = 0; y < cropHeight; y++) {
            for (int x = 0; x < cropWidth; x++) {
                int srcX = x + minX;
                int srcY = y + minY;
                
                int rgb = img.getRGB(srcX, srcY);
                Color color = new Color(rgb, true);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                
                // Better feathering: if very dark, blend alpha
                int maxColor = Math.max(r, Math.max(g, b));
                if (maxColor <= tolerance) {
                    croppedImg.setRGB(x, y, 0x00000000);
                } else if (maxColor < tolerance + 20) {
                    int alpha = (int)(((maxColor - tolerance) / 20.0) * 255);
                    Color newColor = new Color(r, g, b, alpha);
                    croppedImg.setRGB(x, y, newColor.getRGB());
                } else {
                    croppedImg.setRGB(x, y, color.getRGB());
                }
            }
        }
        
        File outputFile = new File("app/src/main/res/drawable/ic_official_logo.png");
        ImageIO.write(croppedImg, "png", outputFile);
        System.out.println("Saved transparent logo");
    }
}
