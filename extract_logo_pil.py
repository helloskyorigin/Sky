from PIL import Image, ImageFilter

# Load image
img = Image.open('app/src/main/res/drawable/threatshield_logo.png').convert('RGBA')

# Get pixel data
datas = img.getdata()
newData = []

# Black threshold
for item in datas:
    # Change all black (also shades of black)
    # to transparent
    if item[0] < 20 and item[1] < 20 and item[2] < 20:
        newData.append((255, 255, 255, 0))
    else:
        newData.append(item)

img.putdata(newData)

# Find bounding box of non-transparent pixels
bbox = img.getbbox()
if bbox:
    # Add some padding
    padding = 20
    bbox = (
        max(0, bbox[0] - padding),
        max(0, bbox[1] - padding),
        min(img.width, bbox[2] + padding),
        min(img.height, bbox[3] + padding)
    )
    img = img.crop(bbox)

# Save the transparent image
img.save('app/src/main/res/drawable/ic_official_logo.png', 'PNG')
print("Saved ic_official_logo.png")
