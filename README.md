[https://docs.mongodb.com/realm/android/install](https://docs.mongodb.com/realm/android/install)

[https://realm.io/docs/kotlin/latest/](https://realm.io/docs/kotlin/latest/)

⇒ 문서에 정리가 엄청 잘되서 문서보고 그때 그때 필요한거 가져다 써도 됨.

# App Level Gradle

```kotlin
plugins {
	//this is not a realm specific but it's needed to use realm
	id 'kotlin-kapt'
	
	//realm
	id 'realm-android'
}

//realm
realm {
    syncEnabled = true
}
```

```kotlin
plugins {
    id 'com.android.application'
    id 'kotlin-android'

    //this is not a realm specific but it's needed to use realm
    id 'kotlin-kapt'

    //realm
    id 'realm-android'
}

android {
    compileSdkVersion 30
    buildToolsVersion "30.0.2"

    defaultConfig {
        applicationId "com.paigesoftware.realm_kotlin"
        minSdkVersion 16
        targetSdkVersion 30
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = '1.8'
    }

}

//realm
realm {
    syncEnabled = true
}

dependencies {

//    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"

//    implementation ('com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.4'){
//        exclude group:"org.jetbrains.kotlin", module: "kotlin-stdlib-jdk7"
//    }
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"

    implementation 'androidx.core:core-ktx:1.3.2'
    implementation 'androidx.appcompat:appcompat:1.2.0'
    implementation 'com.google.android.material:material:1.2.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.0.4'
    testImplementation 'junit:junit:4.13.1'
    androidTestImplementation 'androidx.test.ext:junit:1.1.2'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.3.0'

}
```

# Project Level Gradle

```kotlin
classpath "io.realm:realm-gradle-plugin:10.2.0"
```

```kotlin
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    ext.kotlin_version = "1.4.21"
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath "com.android.tools.build:gradle:4.1.2"
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        classpath "io.realm:realm-gradle-plugin:10.2.0"

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()

//        maven { url "https://jitpack.io" }
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```

# Configuration

```kotlin
class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)

        val configuration = RealmConfiguration.Builder()
            .name("Notes.db")
            .deleteRealmIfMigrationNeeded()
            .schemaVersion(0)
            .build()

        Realm.setDefaultConfiguration(configuration)

    }

}
```

# Initialization

```kotlin
realm = Realm.getDefaultInstance()
```

# Read

```kotlin
val results: RealmResults<Note> = realm.where<Note>(Note::class.java).findAll()
```

# Create

```kotlin
try {

        // Auto Increment id

        realm.beginTransaction()

        val currentNumber: Number? = realm.where(Note::class.java).max("id")
        val nextId: Int = if(currentNumber == null) {
            1
        } else {
            currentNumber.toInt() + 1
        }

        val note = Note()
        note.title = binding.edittextTitle.text.toString()
        note.description = binding.edittextDescription.text.toString()
        note.id = nextId

        // copy this transiction & commit
        realm.copyToRealmOrUpdate(note)
        realm.commitTransaction()

        Toast.makeText(this, "Notes Added Successfully", Toast.LENGTH_LONG).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()

    } catch (e: Exception) {

        e.printStackTrace()

    }
```

# Read with Listener

```kotlin
//read with listener
val callback: OrderedRealmCollectionChangeListener<RealmResults<Note>> =
    OrderedRealmCollectionChangeListener { results, changeSet ->
        if (changeSet == null) {
            // The first time async returns with an null changeSet.
        } else {
            // Called on every update.
        }
    }

val readResult: RealmResults<Note> = realm.where<Note>(Note::class.java).findAll()
readResult.addChangeListener { results, changeSet ->
    if (changeSet == null) {
        //do something...

    } else {
        //calls on every update
    }
}
readResult.addChangeListener(callback)

if (isFinishing) {
    readResult.removeAllChangeListeners()
    //or
    readResult.removeChangeListener(callback)
}
```

# Async

```kotlin
// async
val result = realm.where<Note>(Note::class.java).findAllAsync()
result.load() // be careful, this will block the current thread until it returns
```

# Update

```kotlin
//update
realm.beginTransaction()
val updateResult: Note? = realm.where<Note>(Note::class.java).lessThan("id", 3).findFirst()
updateResult?.description = "kkk"
realm.commitTransaction()
```

# Delete

```kotlin
//delete
realm.beginTransaction()
val deleteResult: Note? = realm.where(Note::class.java).equalTo("id", 2.toInt()).findFirst()
deleteResult?.deleteFromRealm()
realm.commitTransaction()
```

# Query

```kotlin
//query
val queryResult: RealmResults<Note> = realm.where<Note>(Note::class.java)
    .equalTo("id", 1.toInt())
    .or()
    .equalTo("id", 2.toInt())
    .findAll()
```

# Back Up & Restore

`Realm.writeCopyTo` might be helpful for this case. You can find [doc](https://realm.io/docs/java/latest/api/) here.

```kotlin
//Backup
Realm orgRealm = Realm.getInstance(orgConfig);
orgRealm.writeCopyTo(pathToBackup);
orgRealm.close();
//Restore
Realm.deleteRealm(orgConfig);
Realm backupRealm = Realm.getInstance(backupConfig);
backupRealm.writeCopyTo(pathToRestore);
backupRealm.close();
orgRealm = Realm.getInstance(orgConfig);

```

But in your case, it would be much simpler and faster to just move your Realm file to a place to backup, and move it back when you want to restore it. To get the Realm file path, try:

```
realm.getPath();

```