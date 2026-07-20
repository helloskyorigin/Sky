import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() {
    val inputFile = File("app/src/main/res/drawable/threatshield_logo.png")
    if (!inputFile.exists()) {
        println("Input file not found")
        return
    }
    
    val img: BufferedImage? = ImageIO.read(inputFile)
    if (img == null) {
        println("Could not read image")
        return
    }
    
    val width = img.width
    val height = img.height
    val outImg = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    
    // Find bounding box of non-black pixels
    var minX = width
    var minY = height
    var maxX = 0
    var maxY = 0
    
    val tolerance = 25
    
    for (y in 0 until height) {
        for (x in 0 until width) {
            val rgb = img.getRGB(x, y)
            val color = Color(rgb, true)
            val r = color.red
            val g = color.green
            val b = color.blue
            
            if (r > tolerance || g > tolerance || b > tolerance) {
                if (x < minX) minX = x
                if (y < minY) minY = y
                if (x > maxX) maxX = x
                if (y > maxY) maxY = y
            }
        }
    }
    
    println("Bounding box: $minX, $minY to $maxX, $maxY")
    
    val cropWidth = maxX - minX + 1
    val cropHeight = maxY - minY + 1
    
    if (cropWidth <= 0 || cropHeight <= 0) {
        println("Image is completely black")
        return
    }
    
    val croppedImg = BufferedImage(cropWidth, cropHeight, BufferedImage.TYPE_INT_ARGB)
    
    for (y in 0 until cropHeight) {
        for (x in 0 until cropWidth) {
            val srcX = x + minX
            val srcY = y + minY
            
            val rgb = img.getRGB(srcX, srcY)
            val color = Color(rgb, true)
            val r = color.red
            val g = color.green
            val b = color.blue
            
            // Simple transparent masking
            if (r <= tolerance && g <= tolerance && b <= tolerance) {
                croppedImg.setRGB(x, y, 0x00000000)
            } else {
                croppedImg.setRGB(x, y, color.rgb)
            }
        }
    }
    
    val outputFile = File("app/src/main/res/drawable/logo_transparent.png")
    ImageIO.write(croppedImg, "png", outputFile)
    println("Saved transparent logo")
}
