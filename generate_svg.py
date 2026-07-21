def shield_path(margin):
    x_c = 512
    y_t = 80 + margin
    x_r = 896 - margin
    y_tr = 220 + margin/2
    y_b = 944 - margin
    x_l = 128 + margin
    return f"M {x_c} {y_t} L {x_r} {y_tr} C {x_r} {550 - margin/4} {750 - margin/2} {800 - margin} {x_c} {y_b} C {274 + margin/2} {800 - margin} {x_l} {550 - margin/4} {x_l} {y_tr} Z"

def shield_half_left(margin):
    x_c = 512
    y_t = 80 + margin
    y_b = 944 - margin
    x_l = 128 + margin
    y_tr = 220 + margin/2
    return f"M {x_c} {y_t} L {x_l} {y_tr} C {x_l} {550 - margin/4} {274 + margin/2} {800 - margin} {x_c} {y_b} Z"

def shield_half_right(margin):
    x_c = 512
    y_t = 80 + margin
    y_b = 944 - margin
    x_r = 896 - margin
    y_tr = 220 + margin/2
    return f"M {x_c} {y_t} L {x_r} {y_tr} C {x_r} {550 - margin/4} {750 - margin/2} {800 - margin} {x_c} {y_b} Z"

print(shield_half_left(0))
