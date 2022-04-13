# SmsForward
Forward your phone's SMS to webhook

# usage
Setting your webhook url:
```
adb shell am startservice -n com.zhangke.smsforward/com.zhangke.smsforward.DaemonService --es "url" "your-url"
```
