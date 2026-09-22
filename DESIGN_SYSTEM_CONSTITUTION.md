# HIẾN PHÁP THIẾT KẾ VÀ KIẾN TRÚC ỨNG DỤNG (ARCHITECTURE & DESIGN SYSTEM CONSTITUTION)

Tài liệu này là **tôn chỉ thiết kế giao diện (UI/UX) và kiến trúc phát triển phần mềm** bắt buộc tuân thủ trong toàn bộ dự án `FlashLight`. Mục tiêu là chuẩn hoá toàn bộ hệ thống màu sắc, typography, component states, phân tầng kiến trúc MVVM + Clean Architecture giúp ứng dụng đạt độ ổn định cao nhất, không phát sinh lỗi vòng đời và dễ bảo trì, mở rộng.

---

## 1. TÔN CHỈ THIẾT KẾ CỐT LÕI (CORE DESIGN PRINCIPLES)

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
  * `@color/color_screenlight_seekbar_track`: Nền thanh trượt (`#353534`)
  * `@color/color_screenlight_seekbar_progress`: Màu tiến trình trượt (`#FFD54F`)
  * `@color/color_screenlight_seekbar_thumb_glow`: Quầng sáng con trỏ (`#4DFFD54F`)
  * `@color/color_screenlight_btn_circle`: Nền nút tròn công cụ (`#252933`)
  * `@color/color_screenlight_btn_expand_bg`: Nền nút expand (`#22262C`)
  * `@color/color_screenlight_ring_active`: Viền đánh dấu màu đang chọn (`#FFD54F`)
* **ScreenLight Presets:**
  * `@color/color_screenlight_preset_cyan`: `#4FA4D7`
  * `@color/color_screenlight_preset_white`: `#FFFFFF`
  * `@color/color_screenlight_preset_yellow`: `#FFD54F`
  * `@color/color_screenlight_preset_green`: `#4ADE80`
  * `@color/color_screenlight_preset_purple`: `#A855F7`
  * `@color/color_screenlight_preset_coral`: `#F87171`
* **Bottom Navigation Active Tokens:**
  * `@color/color_bottom_nav_active_bg`: `#1AFFD54F` (10% fill)
  * `@color/color_bottom_nav_active_shadow`: `#26FFD54F` (15% drop shadow)
  * `@color/color_bottom_nav_active_shadow_mid`: `#1AFFD54F` (10% blur step)
  * `@color/color_bottom_nav_active_shadow_soft`: `#0DFFD54F` (5% outer halo)

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

---

## 5. QUY CHUẨN KIẾN TRÚC PHẦN MỀM (MVVM & CLEAN ARCHITECTURE CONSTITUTION)

Nhằm đảm bảo tính bền vững, dễ bảo trì, testable và loại bỏ triệt để các lỗi rò rỉ vòng đời (như lỗi chớp/nháy giao diện khi chuyển màn), toàn bộ dự án bắt buộc phải tuân thủ nghiêm ngặt các quy tắc kiến trúc sau:

### 5.1. Bắt Buộc Chuẩn Hóa MVVM Cho Mọi Tính Năng (Mandatory MVVM)
* **Mỗi màn hình / tính năng chức năng** (`Flashlight`, `ScreenLight`, `LED`, `FlashAlert`, `Language`, `OnBoarding`, v.v.) **bắt buộc phải có ViewModel riêng biệt** (`@HiltViewModel`).
* Nghiêm cấm tạo Activity/Fragment tự quản lý logic nghiệp vụ hoặc tự xử lý lưu trữ dữ liệu mà không thông qua ViewModel.

### 5.2. Nguyên Tắc Tách Lớp Triệt Để (Separation of Concerns)

1. **Tầng View (Activity / Fragment):**
   * **Được phép:**
     * Khởi tạo ViewBinding và thiết lập giao diện (inflate views, set padding, theme).
     * Đăng ký lắng nghe UI State từ ViewModel qua `StateFlow` / `LiveData` (sử dụng `lifecycleScope` và `repeatOnLifecycle(Lifecycle.State.STARTED)`).
     * Chuyển tiếp các thao tác người dùng (Click, kéo SeekBar, nhập Text) thành các hàm gọi vào ViewModel.
   * **Nghiêm cấm:**
     * Tuyệt đối **KHÔNG lưu giữ State nghiệp vụ** (ví dụ: `var currentColor`, `var currentBrightness`, `var isLightOn`,...) trong các biến cục bộ mutable của Activity/Fragment.
     * Tuyệt đối **KHÔNG truy cập trực tiếp tầng Data** (như gọi `SpManager.getInstance()`, Room Database, File, SharedPreferences, API Network) từ View layer. Mọi dữ liệu phải đi qua ViewModel.

2. **Tầng ViewModel:**
   * Kế thừa `BaseViewModel` hoặc `ViewModel`.
   * Khai báo `@HiltViewModel` và nhận dependencies qua `@Inject constructor(...)`.
   * Là **Single Source of Truth** cho trạng thái của màn hình. Mọi trạng thái hiển thị được đóng gói trong một `data class UiState` duy nhất (ví dụ: `ScreenLightUiState`, `LedEditorState`, `MainUiState`) và phát ra ngoài qua `StateFlow<UiState>`.
   * ViewModel giao tiếp với tầng Data (UseCase, Repository, Data Source, `SpManager`, `FlashlightManager`) để lưu trữ/truy xuất dữ liệu.

3. **Tầng Data (Data Layer):**
   * Toàn bộ Data Source (`SpManager`, Database, Network) phải được quản lý và cung cấp thông qua Hilt DI (`@Singleton`, `@Provides`).

### 5.3. Luồng Dữ Liệu Một Chiều (Unidirectional Data Flow - UDF)
Toàn bộ tương tác trong màn hình phải tuân thủ chu trình khép kín:
```
[User Action] ---> [ViewModel Method] ---> [Update StateFlow<UiState>]
      ^                                                  |
      |                                                  v
[View Renders UI] <----------------------- [View Observes StateFlow]
```

### 5.4. Quy Chuẩn Quản Lý Fragment Lifecycle & Navigation
* Để tránh lỗi giật/nhấp nháy khung hình (render flicker/flash) khi chuyển đổi giữa các Fragment:
  * Khi ẩn/hiện Fragment bên trong Container chung, **bắt buộc sử dụng giao dịch đồng bộ**:
    ```kotlin
    supportFragmentManager.commitNow(allowStateLoss = true) {
        setReorderingAllowed(true)
        hide(inactiveFragment)
        show(activeFragment) // hoặc add(...) nếu chưa có
    }
    ```
  * Phải đảm bảo Fragment cũ đã được ẩn triệt để (`hide()`) **trước khi hoặc đồng thời** khi ViewGroup container trở thành `VISIBLE`.
  * Trạng thái hiển thị của Fragment (màu sắc, độ sáng, nội dung đang chỉnh sửa) phải luôn được lưu giữ an toàn trong ViewModel tương ứng, đảm bảo toàn vẹn state khi người dùng quay lại.

---

## 6. QUY CHUẨN STYLE TEMPLATES (UI COMPONENT STYLE TEMPLATES)

Nhằm giữ cho layout XML ngắn gọn, dễ bảo trì, dễ thay đổi đồng loạt và tuân thủ nguyên tắc DRY (Don't Repeat Yourself), tất cả các nhóm view có mẫu hình lặp lại bắt buộc phải đóng gói thành Style Template trong `res/values/styles.xml`.

### 6.1. Nhóm Color Picker & Selection Circles
Áp dụng cho các màn hình có chức năng chọn màu (`ScreenLightFragment`, `LedFragment`,...):

| Style Name | Mục đích | Thuộc tính đóng gói |
| :--- | :--- | :--- |
| `StyleColorItemContainer` | Khung `FrameLayout` chứa vòng chọn & vòng tròn màu | `layout_width="44dp"`, `layout_height="44dp"`, `layout_marginEnd="12dp"`, `clickable="true"`, `focusable="true"` |
| `StyleColorSelectionRing` | Vòng viền vàng (`bg_color_circle_active`) báo hiệu màu được chọn | `layout_width="match_parent"`, `layout_height="match_parent"`, `background="@drawable/bg_color_circle_active"`, `visibility="gone"` |
| `StyleColorCircleInner` | Vòng tròn màu bên trong (`bg_circle_color`) | `layout_width="34dp"`, `layout_height="34dp"`, `layout_gravity="center"`, `background="@drawable/bg_circle_color"` |
| `StyleColorPickerButton` | Icon nút bút vẽ mở Dialog chọn màu tự do | `layout_width="36dp"`, `layout_height="36dp"`, `layout_gravity="center"`, `background="@drawable/bg_color_picker_btn"`, `padding="9dp"`, `scaleType="fitCenter"`, `src="@drawable/ic_draw"`, `contentDescription="@string/color"` |

**Cú pháp khai báo chuẩn trong Layout XML:**
```xml
<!-- Nút mở Color Picker -->
<FrameLayout
    android:id="@+id/containerColorPicker"
    style="@style/StyleColorItemContainer">
    <View
        android:id="@+id/ringColorPicker"
        style="@style/StyleColorSelectionRing"
        tools:visibility="visible" />
    <androidx.appcompat.widget.AppCompatImageView
        android:id="@+id/btnCustomColorPicker"
        style="@style/StyleColorPickerButton" />
</FrameLayout>

<!-- Nút chọn màu Preset -->
<FrameLayout
    android:id="@+id/containerColorCyan"
    style="@style/StyleColorItemContainer">
    <View
        android:id="@+id/ringColorCyan"
        style="@style/StyleColorSelectionRing" />
    <View
        android:id="@+id/circleColorCyan"
        style="@style/StyleColorCircleInner"
        android:backgroundTint="@color/color_screenlight_preset_cyan" />
</FrameLayout>
```

### 6.2. Nhóm Nút Hướng LED (Direction Buttons)
Áp dụng cho các nút chọn hướng chạy chữ trong LED:

| Style Name | Parent | Thuộc tính đóng gói |
| :--- | :--- | :--- |
| `StyleLedSquareButton` | - | `layout_width="52dp"`, `layout_height="52dp"`, `background="@drawable/bg_led_square_btn_selector"`, `padding="20dp"`, `scaleType="fitCenter"`, `tint="@color/color_led_option_icon_selector"` |
| `StyleLedDirectionButton` | `StyleLedSquareButton` | Kế thừa kích thước & background, tự động gán `src="@drawable/ic_arrow"` và `contentDescription="@string/direction"` |

**Cú pháp khai báo chuẩn trong Layout XML:**
```xml
<androidx.appcompat.widget.AppCompatImageView
    android:id="@+id/btnDirectionRight"
    style="@style/StyleLedDirectionButton" />

<androidx.appcompat.widget.AppCompatImageView
    android:id="@+id/btnDirectionLeft"
    style="@style/StyleLedDirectionButton"
    android:layout_marginStart="12dp"
    android:rotation="180" />
```

### 6.3. Nhóm Pill Buttons (Visual Effects)
Áp dụng cho các nút dạng viên thuốc / chip bo tròn (`Glow`, `Blink`, `Neon`, `Fade`):

| Style Name | Parent | Thuộc tính đóng gói |
| :--- | :--- | :--- |
| `StyleLedPillButton` | `Widget.AppCompat.Button.Borderless` | `layout_width="wrap_content"`, `layout_height="@dimen/size40"`, `minWidth="64dp"`, `paddingHorizontal="20dp"`, `background="@drawable/bg_led_pill_chip_selector"`, `textColor="@color/color_led_pill_chip_text_selector"`, `textSize="15sp"`, `fontFamily="@font/plus_jakarta_sans_semi_bold"`, `textAllCaps="false"`, `stateListAnimator="@null"` |

**Cú pháp khai báo chuẩn trong Layout XML:**
```xml
<androidx.appcompat.widget.AppCompatButton
    android:id="@+id/btnEffectGlow"
    style="@style/StyleLedPillButton"
    android:text="@string/effect_glow" />

<androidx.appcompat.widget.AppCompatButton
    android:id="@+id/btnEffectBlink"
    style="@style/StyleLedPillButton"
    android:layout_marginStart="10dp"
    android:text="@string/effect_blink" />
```
