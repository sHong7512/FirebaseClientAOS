# Firebase AOS Client 
FCM & Analytics Example

# Setting FCM
Tools -> firebase -> Cloud Messaging -> Set up Firebase Cloud Messaging

firebase 프로젝트 생성 -> 앱등록 -> 구성파일 다운로드(google-services.json를 app폴더에 추가) -> firebase SDK추가

# Setting Analytics (and DebugView)

- Path (Mac) (On Terminal) 

~ % open -e .bash_profile
add path
```
export PATH=$PATH:/Users/shong/Library/Android/sdk/platform-tools/
```
~ % source ~/.bash_profile

~ % adb version     (adb 동작확인)

~ % adb devices     (adb devices 확인)

~ % cd /Users/[user name]/Library/Android/sdk/platform-tools

~ platform-tools % adb shell setprop debug.firebase.analytics.app com.shong.FirebaseClientAOS

사용을 멈출려면

~ platform-tools % adb shell setprop debug.firebase.analytics.app .none.

- Chrome

확장 프로그램 다운 : https://chrome.google.com/webstore/detail/google-analytics-debugger/jnkmfdileelhofjcijamephohjechhna

- FireBaseConsole DebugView

애널리틱스 -> Debugview

# Setting FIAM

- gradle (:app)

```
implementation 'com.google.firebase:firebase-inappmessaging-display-ktx'
```

참고 : https://firebase.google.com/docs/in-app-messaging?authuser=2&platform=android

# Setting Remote Config

- gradle (:app)

```
implementation 'com.google.firebase:firebase-config-ktx'
```

참고 : https://firebase.google.com/docs/remote-config/get-started?hl=ko&platform=android

# Requirements
- Kotlin 1.5.10
- Gradle 4.2.1
- Android min SDK 28
- Android target SDK 30
- etc

# Installation
- gradle(:project)
```
buildscript {
    …
    dependencies {
        …
        classpath 'com.google.gms:google-services:4.3.8'
    }
}
```


- gradle(:app)
```
apply plugin: 'com.google.gms.google-services'

dependencies {
   	…
    implementation platform('com.google.firebase:firebase-bom:28.2.0')
    implementation 'com.google.firebase:firebase-analytics-ktx'
    implementation 'com.google.firebase:firebase-messaging:22.0.0'
}
```

- Manifest
```
<service
    android:name=".MyFCMService"
    android:enabled="true"
    android:exported="true">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```

- Permission
```
<uses-permission android:name="android.permission.INTERNET" />
```

- MyFCMService
```
class MyFCMService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        ...
    }

    override fun onNewToken(token: String) {
        ...
        super.onNewToken(token)
    }

    private fun sendNotification(title:String ,messageBody: String) {
        ...
    }

    override fun onCreate() {
        super.onCreate()
        ...
    }
}
```


- 발생했던 Error
```
Firebase could not parse the android application modules gradle config
```
jcenter()를 주석처리해주면 해결됨




# description
- onNewToken
```
토큰을 생성하는 메서드이다.
17.0.0 버전 이전에는 무슨 getInstance? refreshToken? 이런 메서드가 있어서
생성하고 토큰 업데이트하고 하는 로직을 작성해야 했던 것 같다.
근데 이제는 이 메서드를 쓰면 알아서 내부에서 관리해주도록 바뀐 모양이다.
```

- onMessageReceived
```
메시지를 수신하는 메서드이다.
메시지에 제목이나 내용이 들어있는지 검사하고
문제가 없다면 sendNotification을 호출한다.
```

- sendNotification
```
알림을 생성하는 메서드이다.
아이콘은 어떻게 할 건지, 알림 소리는 어떻게 할건지 등
이런 세세한 옵션을 설정하고 알림을 생성하면 비로소 푸시 알림이 뜨게 되는 것이다.
```

- 테스트 메시지 수신
```
이제 FCM 연동이 잘 되었는지 확인하기 위해 파이어 베이스 콘솔 홈페이지로 접속한다.
이 중 자신이 생성한 프로젝트를 클릭한다.
SendMessage 메뉴를 찾아서 메시지 전송 버튼을 클릭해준다.
```

# reference
https://firebase.google.com/docs/analytics/get-started?platform=android

https://support.google.com/analytics/answer/7201382?hl=ko

https://todaycode.tistory.com/8

https://kakao-tam.tistory.com/71
