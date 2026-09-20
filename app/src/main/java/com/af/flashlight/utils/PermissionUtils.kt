package com.af.flashlight.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.app.role.RoleManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import kotlin.collections.all
import kotlin.collections.first
import kotlin.collections.map
import kotlin.collections.toTypedArray
import kotlin.jvm.java
import kotlin.let
import kotlin.onFailure
import kotlin.runCatching
import kotlin.text.contains
import kotlin.text.lowercase

private const val TAG = "PermissionUtils.kt"

enum class Permission(
    val value: String,
) {
    READ_EXTERNAL_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE),
    WRITE_EXTERNAL_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE),

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    POST_NOTIFICATIONS(Manifest.permission.POST_NOTIFICATIONS),
    CAMERA(Manifest.permission.CAMERA),
    RECORD_AUDIO(Manifest.permission.RECORD_AUDIO),
    SEND_SMS(Manifest.permission.SEND_SMS),
    RECEIVE_SMS(Manifest.permission.RECEIVE_SMS),
    READ_SMS(Manifest.permission.READ_SMS),
    ACCESS_NOTIFICATION_POLICY(Manifest.permission.ACCESS_NOTIFICATION_POLICY),
    REQUEST_IGNORE_BATTERY_OPTIMIZATIONS(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS),

    @RequiresApi(Build.VERSION_CODES.O)
    ANSWER_PHONE_CALLS(Manifest.permission.ANSWER_PHONE_CALLS),
    READ_PHONE_STATE(Manifest.permission.READ_PHONE_STATE),
    READ_CALL_LOG(Manifest.permission.READ_CALL_LOG),
    SYSTEM_ALERT_WINDOW(Manifest.permission.SYSTEM_ALERT_WINDOW),
    DEFAULT_DIALER("android.app.role.DIALER"),
}

val listStoragePermission =
    listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

fun Context.runWithPermissionChecker(
    permission: Permission,
    isRequestPermission: Boolean = false,
    onPermissionDeny: ((PermissionDeniedResponse?) -> Unit)? = null,
    onPermissionGranted: (PermissionGrantedResponse?) -> Unit,
) {
    runCatching {
        Dexter
            .withContext(this)
            .withPermission(permission.value)
            .withListener(
                object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        onPermissionGranted(response)
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        permissionToken: PermissionToken?,
                    ) {
                        permissionToken?.continuePermissionRequest()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        response?.requestedPermission
                        onPermissionDeny?.let { it(response) }
                        if (isRequestPermission) {
                            response?.let {
                                requestPermission(
                                    this@runWithPermissionChecker,
                                    it,
                                )
                            }
                        }
                    }
                },
            ).onSameThread()
            .check()
    }.onFailure { error ->
        Logger.e("runWithPermissionChecker :$error")
        onPermissionDeny?.invoke(null)
    }
}

fun FragmentActivity.runWithPermissionChecker(
    permission: Permission,
    isRequestPermission: Boolean = false,
    onPermissionDeny: ((PermissionDeniedResponse?) -> Unit)? = null,
    onPermissionGranted: (PermissionGrantedResponse?) -> Unit,
) {
    runCatching {
        Dexter
            .withContext(this)
            .withPermission(permission.value)
            .withListener(
                object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        lifecycleScope.launchWhenResumed { onPermissionGranted(response) }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        permissionToken: PermissionToken?,
                    ) {
                        permissionToken?.continuePermissionRequest()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        response?.requestedPermission
                        lifecycleScope.launchWhenResumed {
                            onPermissionDeny?.let { it(response) }
                            if (isRequestPermission) {
                                response?.let {
                                    requestPermission(
                                        this@runWithPermissionChecker,
                                        it,
                                    )
                                }
                            }
                        }
                    }
                },
            ).onSameThread()
            .check()
    }.onFailure { error ->
        Logger.e("runWithPermissionChecker $error")
        onPermissionDeny?.invoke(null)
    }
}

fun Context.runWithPermissionChecker(
    permissions: Collection<Permission>,
    isRequestPermission: Boolean = false,
    onPermissionDeny: ((List<PermissionDeniedResponse>?) -> Unit)? = null,
    onPermissionGranted: (List<PermissionGrantedResponse>?) -> Unit,
) {
    runCatching {
        Dexter
            .withContext(this)
            .withPermissions(permissions.map { it.value })
            .withListener(
                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report?.areAllPermissionsGranted() == true) {
                            onPermissionGranted(report.grantedPermissionResponses)
                        } else {
                            onPermissionDeny?.invoke(report?.deniedPermissionResponses)
                            if (isRequestPermission) {
                                report?.let {
                                    requestPermissions(
                                        this@runWithPermissionChecker,
                                        it.deniedPermissionResponses,
                                    )
                                }
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?,
                    ) {
                        p1?.continuePermissionRequest()
                    }
                },
            ).onSameThread()
            .check()
    }.onFailure { error ->
        Logger.e("runWithPermissionChecker $error")
        onPermissionDeny?.invoke(null)
    }
}

fun FragmentActivity.runWithPermissionsChecker(
    permissions: Collection<Permission>,
    isRequestPermission: Boolean = false,
    onPermissionDeny: ((List<PermissionDeniedResponse>?) -> Unit)? = null,
    onPermissionGranted: (List<PermissionGrantedResponse>?) -> Unit,
) {
    runCatching {
        Dexter
            .withContext(this)
            .withPermissions(permissions.map { it.value })
            .withListener(
                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report?.areAllPermissionsGranted() == true) {
                            lifecycleScope.launchWhenResumed { onPermissionGranted(report.grantedPermissionResponses) }
                        } else {
                            lifecycleScope.launchWhenResumed {
                                onPermissionDeny?.invoke(report?.deniedPermissionResponses)
                                if (isRequestPermission) {
                                    report?.let {
                                        requestPermissions(
                                            this@runWithPermissionsChecker,
                                            it.deniedPermissionResponses,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?,
                    ) {
                        p1?.continuePermissionRequest()
                    }
                },
            ).onSameThread()
            .check()
    }.onFailure { error ->
        Logger.e("runWithPermissionsChecker $error")
        onPermissionDeny?.invoke(null)
    }
}

fun Fragment.runWithPermissionChecker(
    permission: Permission,
    isRequestPermission: Boolean = false,
    onPermissionDeny: ((PermissionDeniedResponse?) -> Unit)? = null,
    onPermissionGranted: (PermissionGrantedResponse?) -> Unit,
) {
    activity?.runWithPermissionChecker(
        permission,
        isRequestPermission,
        onPermissionDeny,
        onPermissionGranted,
    )
}

fun Fragment.runWithPermissionsChecker(
    permissions: Collection<Permission>,
    isRequestPermission: Boolean = false,
    onPermissionDeny: ((List<PermissionDeniedResponse>?) -> Unit)? = null,
    onPermissionGranted: (List<PermissionGrantedResponse>?) -> Unit,
) {
    requireActivity().runWithPermissionsChecker(
        permissions,
        isRequestPermission,
        onPermissionDeny,
        onPermissionGranted,
    )
}

fun FragmentActivity.isNotificationServiceEnabled(): Boolean {
    val enabledNotificationListeners =
        Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners",
        )
    return enabledNotificationListeners?.contains(packageName) == true
}

fun FragmentActivity.requestNotificationAccess() {
    startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
}

fun FragmentActivity.checkAndRequestOverlayPermission() {
    if (Settings.canDrawOverlays(this).not()) {
        startActivity(
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"),
            ),
        )
    }
}

fun Context.canDrawOverlays(): Boolean = Settings.canDrawOverlays(this)

fun Context.requestDrawOverlaysPermission() {
    startActivity(
        Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName"),
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}

fun FragmentActivity.checkAndRequestNotificationPolicyPermission() {
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (!notificationManager.isNotificationPolicyAccessGranted) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        startActivity(intent)
        return
    }
}

fun Context?.moveToAppDetailSetting(): Boolean {
    this ?: return false
    try {
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.fromParts("package", packageName, null))
                .addCategory(Intent.CATEGORY_DEFAULT)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        startActivity(intent)
        return true
    } catch (e: ActivityNotFoundException) {
        Logger.e(e.message)
    }
    return false
}

@RequiresApi(Build.VERSION_CODES.R)
fun FragmentActivity.checkAndRequestManageStoragePermissionGranted() {
    if (Environment.isExternalStorageManager()) return
    requestManageStoragePermission(
        onAccept = {},
        onDeny = {
            checkAndRequestManageStoragePermissionGranted()
        },
    )
}

@RequiresApi(Build.VERSION_CODES.R)
fun FragmentActivity.isManageStoragePermissionGranted(): Boolean = Environment.isExternalStorageManager()

@RequiresApi(Build.VERSION_CODES.R)
fun FragmentActivity.requestManageStoragePermission(
    onAccept: () -> Unit,
    onDeny: () -> Unit,
) {
    val manageStoragePermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { _ ->
            if (Environment.isExternalStorageManager()) {
                onAccept.invoke()
            } else {
                onDeny.invoke()
            }
        }
    try {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.data = Uri.parse("package:${applicationContext.packageName}")
        manageStoragePermissionLauncher.launch(intent)
    } catch (e: Exception) {
        // Nếu không thành công, mở trang chung
        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
        manageStoragePermissionLauncher.launch(intent)
    }
}

private fun requestPermissions(
    context: Context,
    permissions: (List<PermissionDeniedResponse>),
) {
    if (context is Activity) {
        ActivityCompat.requestPermissions(
            context,
            permissions.map { it.permissionName }.toTypedArray(),
            permissions.first().hashCode(),
        )
    }
}

private fun requestPermission(
    context: Context,
    permission: PermissionDeniedResponse,
) {
    if (context is Activity) {
        ActivityCompat.requestPermissions(
            context,
            arrayOf(permission.permissionName),
            permission.hashCode(),
        )
    }
}

fun Context.isPermissionGranted(permission: Permission): Boolean {
    if (permission.value == Manifest.permission.SYSTEM_ALERT_WINDOW) {
        return Settings.canDrawOverlays(this)
    }

    if (permission == Permission.DEFAULT_DIALER) {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            roleManager.isRoleHeld(RoleManager.ROLE_DIALER)
        } else {
            false
        }
    }

    // Xử lý đặc biệt cho quyền POST_NOTIFICATIONS
    if (permission.value == Manifest.permission.POST_NOTIFICATIONS) {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                permission.value,
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Dưới Android 13, quyền thông báo được cấp tự động khi cài đặt ứng dụng.
            // Tuy nhiên, người dùng có thể tắt thông báo thủ công trong cài đặt.
            // Chúng ta kiểm tra xem thông báo có đang được bật cho ứng dụng không.
            NotificationManagerCompat.from(this).areNotificationsEnabled()
        }
    }

    // Đối với các quyền khác (runtime hoặc normal permissions)
    return ContextCompat.checkSelfPermission(
        this,
        permission.value,
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.isPermissionsGranted(permissions: List<Permission>): Boolean =
    permissions.all {
        isPermissionGranted(it)
    }

@SuppressLint("BatteryLife")
fun Context.requestIgnoreBatteryOptimizations() {
    val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
    val packageName = packageName
    if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }
}

fun Context.openAutostartSettings() {
    val intent = Intent()
    val manufacturer = Build.MANUFACTURER.lowercase()

    when {
        "xiaomi".contains(manufacturer) -> {
            intent.component =
                ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity",
                )
        }

        "oppo".contains(manufacturer) -> {
            intent.component =
                ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity",
                )
        }

        "vivo".contains(manufacturer) -> {
            intent.component =
                ComponentName(
                    "com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity",
                )
        }

        "huawei".contains(manufacturer) -> {
            intent.component =
                ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity",
                )
        }

        "samsung".contains(manufacturer) -> {
            intent.component =
                ComponentName(
                    "com.samsung.android.lool",
                    "com.samsung.android.sm.ui.battery.BatteryActivity",
                )
        }

        else -> {
            // For other manufacturers or if the manufacturer is not listed above, open the application details settings
            val uri = Uri.fromParts("package", packageName, null)
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = uri
        }
    }

    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // Fallback: Open app details if the specific intent does not work
        val uri = Uri.fromParts("package", packageName, null)
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = uri
        startActivity(intent)
    }
}
