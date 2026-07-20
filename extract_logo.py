import cv2
import numpy as np

# Load the image
img = cv2.imread('app/src/main/res/drawable/threatshield_logo.png', cv2.IMREAD_UNCHANGED)

# The background is black. We can create an alpha channel based on brightness or by thresholding the black pixels.
# Convert to grayscale
gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

# Threshold to find non-black pixels
_, mask = cv2.threshold(gray, 10, 255, cv2.THRESH_BINARY)

# Find contours to get the bounding box of the shield
contours, _ = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

if contours:
    # Get the largest contour
    c = max(contours, key=cv2.contourArea)
    x, y, w, h = cv2.boundingRect(c)
    
    # Add some padding
    padding = 20
    x = max(0, x - padding)
    y = max(0, y - padding)
    w = min(img.shape[1] - x, w + 2 * padding)
    h = min(img.shape[0] - y, h + 2 * padding)
    
    # Crop the image
    img = img[y:y+h, x:x+w]
    mask = mask[y:y+h, x:x+w]

# Add alpha channel
b, g, r = cv2.split(img)
rgba = [b, g, r, mask]
dst = cv2.merge(rgba, 4)

# Apply a slight blur to the alpha channel for anti-aliasing
mask_blurred = cv2.GaussianBlur(mask, (3, 3), 0)
rgba = [b, g, r, mask_blurred]
dst = cv2.merge(rgba, 4)

# Save as transparent PNG
cv2.imwrite('app/src/main/res/drawable/ic_official_logo.png', dst)
print("Saved ic_official_logo.png")
