apply plugin: 'com.android.application'
apply plugin: 'com.google.gms.google-services'

android {
    compileSdkVersion 28
    defaultConfig {
        applicationId "com.hmm.q_time"
        minSdkVersion 21
        targetSdkVersion 28
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    implementation fileTree(include: ['*.jar'], dir: 'libs')
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    implementation 'com.android.support:support-v4:28.0.0'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
    implementation 'com.android.support:design:28.0.0'
    implementation 'com.android.support:recyclerview-v7:28.0.0'
    implementation 'com.android.volley:volley:1.1.0'

    //Glide dependency
    implementation 'com.github.bumptech.glide:glide:4.9.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.9.0'

    //Circle image view
    implementation 'de.hdodenhof:circleimageview:3.0.0'

    implementation 'com.google.firebase:firebase-auth:16.1.0'
    implementation 'com.google.firebase:firebase-core:16.0.7'
    // this line must be included to integrate with Firebase
    implementation 'com.google.firebase:firebase-messaging:17.3.4'
    implementation 'com.google.android.gms:play-services-location:16.0.0'
    implementation 'com.google.android.gms:play-services-places:16.0.0'
    implementation "com.google.firebase:firebase-measurement-connector-impl:17.0.5"

    implementation 'com.scaledrone:scaledrone-java:0.6.0'
}