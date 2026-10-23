"""
Script to generate high-resolution professional icon and image for KEC Academic Evaluation System.
"""
import math
from PIL import Image, ImageDraw, ImageFont

def create_kec_icon():
    size = 512  # Render at 512x512 for crisp antialiasing, then downsample
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    cx, cy = size // 2, size // 2

    # Draw outer glowing ring
    outer_radius = 240
    # Outer dark gold rim
    draw.ellipse([cx - outer_radius, cy - outer_radius, cx + outer_radius, cy + outer_radius], 
                 fill=(245, 158, 11, 255), outline=(180, 83, 9, 255), width=4)

    # Inner gold border
    inner_rim = 230
    draw.ellipse([cx - inner_rim, cy - inner_rim, cx + inner_rim, cy + inner_rim], 
                 fill=(251, 191, 36, 255))

    # Deep Navy Blue circular background
    navy_radius = 220
    draw.ellipse([cx - navy_radius, cy - navy_radius, cx + navy_radius, cy + navy_radius], 
                 fill=(15, 23, 42, 255)) # Dark navy (#0f172a)

    # Inner subtle accent circle
    accent_radius = 205
    draw.ellipse([cx - accent_radius, cy - accent_radius, cx + accent_radius, cy + accent_radius], 
                 outline=(30, 41, 59, 255), width=3)

    # Draw Graduation Cap (Mortarboard)
    # Diamond top of mortarboard
    # Top vertex, right vertex, bottom vertex, left vertex
    cap_cy = cy - 70
    top_v = (cx, cap_cy - 48)
    right_v = (cx + 120, cap_cy - 10)
    bottom_v = (cx, cap_cy + 28)
    left_v = (cx - 120, cap_cy - 10)

    # Shadow under cap top
    draw.polygon([(cx, cap_cy - 44), (cx + 122, cap_cy - 6), (cx, bottom_v[1] + 6), (cx - 122, cap_cy - 6)], 
                 fill=(10, 15, 30, 200))

    # Cap Skull/Base (under the diamond)
    draw.polygon([
        (cx - 65, cap_cy + 5),
        (cx + 65, cap_cy + 5),
        (cx + 60, cap_cy + 45),
        (cx, cap_cy + 65),
        (cx - 60, cap_cy + 45)
    ], fill=(241, 245, 249, 255), outline=(203, 213, 225, 255), width=2)

    # Gold accent line on skull cap
    draw.line([(cx - 50, cap_cy + 42), (cx, cap_cy + 58), (cx + 50, cap_cy + 42)], 
              fill=(245, 158, 11, 255), width=3)

    # Diamond top surface (Gold with highlight)
    draw.polygon([top_v, right_v, bottom_v, left_v], 
                 fill=(251, 191, 36, 255), outline=(217, 119, 6, 255), width=4)

    # Cap button in center
    draw.ellipse([cx - 8, cap_cy - 18, cx + 8, cap_cy - 2], fill=(180, 83, 9, 255))

    # Tassel hanging to the left
    tassel_points = [
        (cx - 4, cap_cy - 10),
        (cx - 65, cap_cy + 15),
        (cx - 85, cap_cy + 55),
        (cx - 90, cap_cy + 75)
    ]
    draw.line(tassel_points, fill=(245, 158, 11, 255), width=4)
    # Tassel fringe
    draw.polygon([
        (cx - 96, cap_cy + 75),
        (cx - 84, cap_cy + 75),
        (cx - 80, cap_cy + 105),
        (cx - 100, cap_cy + 105)
    ], fill=(217, 119, 6, 255))

    # Text: "KEC"
    # Try to load Arial or generic font
    try:
        kec_font = ImageFont.truetype("arialbd.ttf", 80)
        eval_font = ImageFont.truetype("arialbd.ttf", 30)
        badge_font = ImageFont.truetype("arial.ttf", 22)
    except:
        try:
            kec_font = ImageFont.truetype("C:\\Windows\\Fonts\\arialbd.ttf", 80)
            eval_font = ImageFont.truetype("C:\\Windows\\Fonts\\arialbd.ttf", 30)
            badge_font = ImageFont.truetype("C:\\Windows\\Fonts\\arial.ttf", 22)
        except:
            kec_font = ImageFont.load_default()
            eval_font = ImageFont.load_default()
            badge_font = ImageFont.load_default()

    # Draw "KEC"
    kec_text = "KEC"
    bbox = draw.textbbox((0, 0), kec_text, font=kec_font)
    tw = bbox[2] - bbox[0]
    th = bbox[3] - bbox[1]
    kec_y = cy + 40
    # Text shadow
    draw.text((cx - tw // 2 + 3, kec_y + 3), kec_text, font=kec_font, fill=(0, 0, 0, 180))
    # Text main
    draw.text((cx - tw // 2, kec_y), kec_text, font=kec_font, fill=(255, 255, 255, 255))

    # Draw Banner Ribbon for "R2024 EVAL"
    ribbon_y = cy + 135
    rw = 160
    rh = 40
    # Ribbon shadow
    draw.rounded_rectangle([cx - rw - 2, ribbon_y - rh//2 + 2, cx + rw + 2, ribbon_y + rh//2 + 2], 
                           radius=10, fill=(0, 0, 0, 140))
    # Ribbon body
    draw.rounded_rectangle([cx - rw, ribbon_y - rh//2, cx + rw, ribbon_y + rh//2], 
                           radius=8, fill=(245, 158, 11, 255), outline=(254, 240, 138, 255), width=2)

    eval_text = "R2024 EVALUATION"
    ebbox = draw.textbbox((0, 0), eval_text, font=eval_font)
    etw = ebbox[2] - ebbox[0]
    eth = ebbox[3] - ebbox[1]
    draw.text((cx - etw // 2, ribbon_y - eth // 2 - 2), eval_text, font=eval_font, fill=(15, 23, 42, 255))

    # Autonomous status curve or bottom label
    sub_text = "AUTONOMOUS"
    sbbox = draw.textbbox((0, 0), sub_text, font=badge_font)
    stw = sbbox[2] - sbbox[0]
    draw.text((cx - stw // 2, cy + 175), sub_text, font=badge_font, fill=(148, 163, 184, 255))

    # Save high-res PNG
    png_path = r"C:\MY DRIVE\KECEval\assets\kec_logo.png"
    img.save(png_path, format="PNG")
    print(f"Saved PNG to {png_path}")

    # Generate multi-size ICO
    ico_path = r"C:\MY DRIVE\KECEval\assets\kec_icon.ico"
    # Pillow supports saving .ico with sizes parameter
    sizes = [(256, 256), (128, 128), (64, 64), (48, 48), (32, 32), (16, 16)]
    img.save(ico_path, format="ICO", sizes=sizes)
    print(f"Saved multi-resolution ICO to {ico_path}")

if __name__ == "__main__":
    create_kec_icon()

