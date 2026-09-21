# HIẾN PHÁP THIẾT KẾ ỨNG DỤNG (DESIGN SYSTEM CONSTITUTION)

Tài liệu này là **tôn chỉ thiết kế và quy chuẩn phát triển giao diện (UI/UX)** bắt buộc tuân thủ trong toàn bộ dự án `FlashLight`. Mục tiêu là chuẩn hoá toàn bộ hệ thống màu sắc, typography, component states và tách lớp triệt để giúp việc chỉnh sửa, themeing và bảo trì được nhanh chóng, nhất quán.

---

## 1. TÔN CHỈ THIẾT KẾ CỐT LÕI (CORE PRINCIPLES)

1. **Zero Hardcoded Colors (Tuyệt đối không hardcode mã màu):**
   * Nghiêm cấm viết trực tiếp mã hex (ví dụ: `#FFD54F`, `#1A1D24`, `#FFFFFF`) trong các file:
     * Layout XML (`res/layout/*.xml`)
     * Drawable XML (`res/drawable/*.xml`)
     * Kotlin / Java code (`*.kt`, `*.java`)
   * Tất cả giá trị màu **bắt buộc** phải được định nghĩa qua Design Tokens trong `res/values/colors.xml` và truy xuất qua `@color/...` hoặc `ContextCompat.getColor(context, R.color....)`.

2. **Component State Separation (`res/color/` First):**
   * Bất kỳ view/component nào có từ 2 trạng thái trở lên (ví dụ: `selected`, `pressed`, `focused`, `disabled`, `checked`) **bắt buộc phải định nghĩa ColorStateList** dạng `<selector>` đặt trong thư mục `res/color/`.
   * Tránh việc viết logic `if (isSelected) setTextColor(...) else setTextColor(...)` rải rác trong code Kotlin nếu có thể giải quyết bằng `<selector>`.

3. **Single Source of Truth (Một nguồn chân lý duy nhất):**
   * Thay đổi một token màu tại `colors.xml` hoặc một selector tại `res/color/` phải tự động cập nhật toàn bộ giao diện liên quan trên toàn ứng dụng mà không cần sửa code logic.

---

## 2. PHÂN TẦNG DESIGN TOKENS (TOKEN HIERARCHY)

Hệ thống token màu được phân thành 3 cấp bậc rõ ràng:

```
[Level 1: Primitive / Base Palette]  -->  [Level 2: Semantic Tokens]  -->  [Level 3: Component Tokens]
(black, white, yellow, gray,...)          (bg_surface, text_main,...)         (screenlight_card_bg,...)
```

### Level 1: Primitive Palette (Bảng màu cơ sở)
Định nghĩa các gam màu thô của ứng dụng:
* `@color/black` (`#0D0D0D`)
* `@color/white` (`#FFFFFF`)
* `@color/yellow` / `@color/color_main` (`#FFD54F`)
* `@color/gray`, `@color/gray3`, `@color/gray4`
* `@color/transparent` (`#00000000`)

### Level 2: Semantic Tokens (Màu theo ngữ nghĩa)
Dùng chung cho toàn ứng dụng:
* **Backgrounds:** `@color/background_main`
* **Text:** `@color/text_main`, `@color/text_gray`, `@color/text_black`
* **Buttons:** `@color/yellow_btn_gradient_start`, `@color/yellow_btn_gradient_end`

### Level 3: Component Tokens (Màu đặc thù cho từng màn hình / Component)
Đặt tên theo tiền tố tính năng (`color_<tên_tính_năng>_<thành_phần>_<mục_đích>`):
* **ScreenLight Feature:**
  * `@color/color_screenlight_card_bg`: Nền card bo góc (`#1A1D24`)
  * `@color/color_screenlight_card_stroke`: Viền card (`#252933`)
  * `@color/color_screenlight_seekbar_track`: Nền thanh trượt (`#252933`)
  * `@color/color_screenlight_seekbar_progress`: Màu tiến trình trượt (`#FFD54F`)
  * `@color/color_screenlight_seekbar_thumb_glow`: Quầng sáng con trỏ (`#4DFFD54F`)
  * `@color/color_screenlight_btn_circle`: Nền nút tròn công cụ (`#252933`)
  * `@color/color_screenlight_btn_expand_bg`: Nền nút expand mờ (`#4D000000`)
  * `@color/color_screenlight_ring_active`: Viền đánh dấu màu đang chọn (`#FFD54F`)
* **ScreenLight Presets:**
  * `@color/color_screenlight_preset_cyan`: `#4FA4D7`
  * `@color/color_screenlight_preset_white`: `#FFFFFF`
  * `@color/color_screenlight_preset_yellow`: `#FFD54F`
  * `@color/color_screenlight_preset_green`: `#4ADE80`
  * `@color/color_screenlight_preset_purple`: `#A855F7`
  * `@color/color_screenlight_preset_coral`: `#F87171`
* **Color Picker Dialog:**
  * `@color/color_screenlight_dialog_bg`: Nền trắng card dialog (`#FFFFFF`)
  * `@color/color_screenlight_input_bg`: Nền ô nhập HEX/RGB (`#F4F5F7`)
  * `@color/color_screenlight_input_stroke`: Viền ô nhập (`#E5E7EB`)
  * `@color/color_screenlight_input_text`: Màu chữ ô nhập (`#1F2937`)
  * `@color/color_screenlight_label_text`: Màu chữ tiêu đề ô (`#8E8E93`)

---

## 3. QUY ƯỚC COLOR STATE LISTS (`res/color/`)

Khi một View thay đổi màu dựa vào trạng thái:
1. Tạo file XML tại thư mục `res/color/` với quy tắc đặt tên: `color_<component>_<property>_selector.xml`.
2. Ví dụ chuẩn:
```xml
<!-- res/color/color_screenlight_ring_selector.xml -->
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:color="@color/color_screenlight_ring_active" android:state_selected="true" />
    <item android:color="@color/transparent" />
</selector>
```
```xml
<!-- res/color/color_bottom_nav_selector.xml -->
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:color="@color/color_main" android:state_selected="true" />
    <item android:color="@color/color_bottom_nav_unselected" />
</selector>
```
3. Trong layout XML, sử dụng trực tiếp:
```xml
android:textColor="@color/color_bottom_nav_selector"
android:tint="@color/color_bottom_nav_selector"
```

---

## 4. QUY TRÌNH KHI THÊM COMPONENT HOẶC MÀU MỚI

Trước khi viết bất kỳ file Layout hay Kotlin nào:
1. **Xác định Token:** Kiểm tra `res/values/colors.xml` xem đã có màu tương ứng chưa. Nếu chưa, thêm token mới theo chuẩn tiền tố.
2. **Xác định Trạng thái (States):** Nếu View có tương tác chạm / click / chọn -> Tạo file `<selector>` tương ứng trong `res/color/`.
3. **Xây dựng Drawable:** Nếu cần shape, border, gradient -> Tạo file trong `res/drawable/` và chỉ dùng `@color/...`.
4. **Áp dụng vào Code:**
   * Trong XML: `@color/...`
   * Trong Kotlin: `ContextCompat.getColor(context, R.color....)`
