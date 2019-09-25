# 2019.9.6
BuildConfig这个类是如何生成的？

### BuildConfig的用处
程序编译成功后，会在每一个Module下的`build/generated/source/buildConfig`目录下的对应环境包里生成一个BuildConfig文件，文件内容如下：
```Java
public final class BuildConfig {
  public static final boolean DEBUG = false;
  public static final String APPLICATION_ID = "com.hsc.core";
  public static final String BUILD_TYPE = "release";
  public static final String FLAVOR = "test";
  public static final int VERSION_CODE = 332;
  public static final String VERSION_NAME = "3.32";
  // Fields from product flavor: test
  public static final String HTTP_ANALYZE_URL = "https://k.hsc.com";
  public static final String HTTP_BASE_URL = "https://k.hsc.com";
}
```
从内容可以看出，BuildConfig是根据Module下的build.gradle生成的；其中最常用的是`BuildConfig.DEBUG`来判断是否处于debug模式，来控制日志的输出。这个值会根据开发者的Build类型自动设定，不需要手动设置

除此之外，还可以自定义添加BuildConfig里的常量，比如设置开发环境，测试环境和正式环境下的网络请求基地址和不同环境下的常量值

Module的`build.gradle`文件常见配置如下
```groovy
apply plugin: 'com.android.application'
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-android-extensions'

android {
  //多环境配置
  flavorDimensions 'environment'

  compileSdkVersion versions.compileSdk
  buildToolsVersion versions.buildTools

  //通用性变量配置,不区分编译类型和编译环境
  defaultConfig {
		applicationId "com.hsicen.interview"
		minSdkVersion versions.minSdk
		targetSdkVersion versions.targetSdk
		versionCode versions.versionCode
		versionName versions.versionName
	}

	//签名配置
	signingConfigs {
		configRelease {
			def keystoreProps = new Properties()
			keystoreProps.load(project.rootProject                                        
					.file('keystore.properties').newDataInputStream())

			keyAlias keystoreProps.getProperty("keystore.alias")
			keyPassword keystoreProps.getProperty("keystore.password")
			storeFile file(keystoreProps.getProperty("keystore.path"))
			storePassword keystoreProps.getProperty("keystore.aliasPassword")
		}
  }

	//编译类型配置
	buildTypes {
		debug {
			minifyEnabled false
			zipAlignEnabled false
			shrinkResources false
			proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
			signingConfig signingConfigs.configRelease
		}

		release {
			minifyEnabled true
			zipAlignEnabled true
			shrinkResources true
			proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
			signingConfig signingConfigs.configRelease

			ndk {
				abiFilters 'armeabi-v7a'
			}
		}
	}

	//多环境位置
	productFlavors {
		//开发环境
		dev {
			def flavorName = 'dev'

			//host配置
			buildConfigField "String", "BASE_URL", "\"${environment[flavorName].host}\""
			buildConfigField "String", "ANALYSE_URL", "\"${environment[flavorName].analyse}\""

			//key配置
			manifestPlaceholders = [
				APPLICATION_ID: appKey[flavorName].applicationId,
				AMAP_KEY      : appKey[flavorName].amap,
				UMENG_KEY     : appKey[flavorName].umeng,
				WEIXIN_KEY    : appKey[flavorName].weixin,
				IM_KEY        : appKey[flavorName].im,
				PUSH_KEY      : appKey[flavorName].push
			]
		}

		//测试环境
		tst {
			def flavorName = 'tst'

			//host配置
			buildConfigField "String", "BASE_URL", "\"${environment[flavorName].host}\""
			buildConfigField "String", "ANALYSE_URL", "\"${environment[flavorName].analyse}\""

			//key配置
			manifestPlaceholders = [
				APPLICATION_ID: appKey[flavorName].applicationId,
				AMAP_KEY      : appKey[flavorName].amap,
				UMENG_KEY     : appKey[flavorName].umeng,
				WEIXIN_KEY    : appKey[flavorName].weixin,
				IM_KEY        : appKey[flavorName].im,
				PUSH_KEY      : appKey[flavorName].push
			]
		}

		//预发布环境
		pre {
			def flavorName = 'pre'

			//host配置
			buildConfigField "String", "BASE_URL", "\"${environment[flavorName].host}\""
			buildConfigField "String", "ANALYSE_URL", "\"${environment[flavorName].analyse}\""

			//key配置
			manifestPlaceholders = [
				APPLICATION_ID: appKey[flavorName].applicationId,
				AMAP_KEY      : appKey[flavorName].amap,
				UMENG_KEY     : appKey[flavorName].umeng,
				WEIXIN_KEY    : appKey[flavorName].weixin,
				IM_KEY        : appKey[flavorName].im,
				PUSH_KEY      : appKey[flavorName].push
			]
		}

		//生成环境
		prd {
			def flavorName = 'prd'

			//host配置
			buildConfigField "String", "BASE_URL", "\"${environment[flavorName].host}\""
			buildConfigField "String", "ANALYSE_URL", "\"${environment[flavorName].analyse}\""

			//key配置
			manifestPlaceholders = [
				APPLICATION_ID: appKey[flavorName].applicationId,
				AMAP_KEY      : appKey[flavorName].amap,
				UMENG_KEY     : appKey[flavorName].umeng,
				WEIXIN_KEY    : appKey[flavorName].weixin,
				IM_KEY        : appKey[flavorName].im,
				PUSH_KEY      : appKey[flavorName].push
			]
		}
	}
}

//第三方库依赖
dependencies {
  implementation fileTree(dir: 'libs', include: ['*.jar'])
  implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$versions.kotlinVersion"
  implementation 'androidx.appcompat:appcompat:1.1.0'
  implementation 'androidx.core:core-ktx:1.1.0'
  implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
  testImplementation 'junit:junit:4.12'
  androidTestImplementation 'androidx.test:runner:1.2.0'
  androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'
  
  implementation 'com.android.tools.build:gradle:3.5.0'
}
```

配置文件`config.gradle`如下
```groovy
ext {

    //多环境服务器地址配置
    environment = [
            dev: [
                    host   : "https://dev.hsc.com",
                    analyse: "https://dev.hsc.com"
            ],
            tst: [
                    host   : "https://test.hsc.com",
                    analyse: "https://test.hsc.com"
            ],
            pre: [
                    host   : "https://pre.hsc.com",
                    analyse: "https://pre.hsc.com"
            ],
            prd: [
                    host   : "https://product.hsc.com",
                    analyse: "https://product.hsc.com"
            ]
    ]

    //多环境Key配置
    appKey = [
            dev: [
                    applicationId: "com.hsc.app.dev",
                    umeng        : "38966ce8874f2212",
                    weixin       : "wx38966ce8874f2212",
                    amap         : "38966ce8874f2212",
                    im           : "38966ce8874f2212",
                    push         : "38966ce8874f2212"
            ],
            tst: [
                    applicationId: "com.hsc.app.test",
                    umeng        : "38966ce8874f2212",
                    weixin       : "wx38966ce8874f2212",
                    amap         : "38966ce8874f2212",
                    im           : "38966ce8874f2212",
                    push         : "38966ce8874f2212"
            ],
            pre: [
                    applicationId: "com.hsc.app.pre",
                    umeng        : "38966ce8874f2212",
                    weixin       : "wx38966ce8874f2212",
                    amap         : "38966ce8874f2212",
                    im           : "38966ce8874f2212",
                    push         : "38966ce8874f2212"
            ],
            prd: [
                    applicationId: "com.hsc.app",
                    umeng        : "38966ce8874f2212",
                    weixin       : "wx38966ce8874f2212",
                    amap         : "38966ce8874f2212",
                    im           : "38966ce8874f2212",
                    push         : "38966ce8874f2212"
            ]
    ]

    //版本号定义
    versions = [
            'compileSdk'   : 28,
            'buildTools'   : '28.0.3',

            'minSdk'       : 21,
            'targetSdk'    : 28,
            'versonCode'   : 100,
            'versionName'  : '1.0.0',

            'kotlinVersion': '1.3.50'
    ]
}

```
项目`build.gradle`文件配置如下
```groovy
buildscript { scriptHandler ->
    apply from: 'repositor.gradle', to: scriptHandler
    apply from: 'config.gradle'

    dependencies {
        classpath 'com.android.tools.build:gradle:3.5.0'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$versions.kotlinVersion"
    }
}

//项目仓库配置
allprojects {
    repositories {
        google()
        jcenter()
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```
`repositor.gradle`配置文件如下
```groovy
/*** Gradle仓库配置*/
repositories {
    maven { url "https://jitpack.io" }
    google()
    jcenter()
}
```