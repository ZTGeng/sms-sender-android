# sms-sender-android

一个使用Kotlin编写的Android客户端app，响应服务端请求，读取最新短信文本并发送到服务端。使用Firebase Cloud Message（FCM）服务。

与此客户端配套的Node.js服务端repo：https://github.com/ZTGeng/sms-sender-android

### 前提：

 - 已有FCM项目或创建一个新的（服务端需使用同一项目）。见：https://console.firebase.google.com/
 - Android 4.4（API 19 Kitkat）及以上，Kotlin 1.3

### 开始：

1. clone此repo
2. 前往Firebase console，进入项目，点击Add app依指引添加此Android app。详见：https://firebase.google.com/docs/cloud-messaging/android/first-message

   1. 需填写此App的Android package name：`com.ztgeng.smssenderkotlin`（可在`AndroidManifest.xml`中查询）
   2. 需下载配置文件`google-services.json`，将其保存到`sms-sender-android/app/`目录下
   3. 该文件名已加入`sms-sender-android/app/.gitignore`以避免暴露于公开repo

3. 编译运行，应打开App界面
   > 根据不同Android版本，在第一次运行app或第一次读取短信时，会申请短信读取权限。卸载重装app后可能需要重新授权
4. 根据指引运行服务端
5. 输入服务器IP地址和端口号（每次启动app时将自动填入上次使用的地址）
6. 点击按钮获取token并发送到服务端（每次重启服务端后需要重新点此按钮更新token，除非修改服务端代码使其能长期保存token）
7. App将启动一个后台运行的Service以接收发送消息，因此关闭app和关闭屏幕不影响其使用
   > 但应注意：部分品牌手机（如小米）会在关闭app一段时间后禁止其使用网络，需在设置中关闭此功能
