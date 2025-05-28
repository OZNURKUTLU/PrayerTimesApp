# Prayer Times App

A modern Android application designed to provide accurate and up-to-date prayer times based on the user's current location or a manually selected city. 

## Features

* **Location-Based Services:** Automatically detects user's location using GPS for real-time prayer time updates.
* **Manual City Selection:** Allows users to manually select a city to view prayer times.
* **Offline Support:** (If applicable, e.g., if you cache data) Prayer times are cached for offline viewing.

## Technologies Used

* **Programming Language:**
    * **Kotlin

* **Architecture & Design Patterns:**
    * **MVVM (Model-View-ViewModel):** Decouples application logic from the UI, enhancing testability, maintainability, and readability.
    * **Repository Pattern:** Provides an abstraction layer over data sources (local database, remote API) for easier data management and a cleaner codebase.

* **Data Management & Persistence:**
    * **Room Persistence Library:** Offers a high level of abstraction over SQLite database, simplifying local data storage and management. Used for efficiently storing `City` and `PrayerTime` data.
    * **LiveData:** An observable data holder used to safely and lifecycle-awarely transmit data changes to the UI.

* **Asynchronous Operations:**
    * **Kotlin Coroutines:** A concurrency framework used to write non-blocking operations (network calls, database transactions) in a more readable and manageable way. Integrated with `viewModelScope` and `lifecycleScope`.

* **Network Communication:**
    * **Retrofit:** A popular HTTP client library for easy interaction with RESTful APIs. Used to fetch prayer times and city data.

* **Dependency Injection:**
    * **Dagger Hilt:** A library that simplifies dependency injection for building scalable, testable, and maintainable Android applications. All dependencies (ViewModel, Repository, API services, database, etc.) are provided via Hilt.

* **Location & Sensor Services:**
    * **Google Play Services Location (FusedLocationProviderClient):** API used to obtain device location. Implemented for automatic location-based prayer time updates.
    * **Android Sensor Framework (Magnetic Field and Accelerometer Sensors):** Utilized to determine the device's orientation and magnetic north. This data forms the basis for calculating the Qibla direction.

* **User Interface (UI):**
    * **RecyclerView:** A flexible view group used for efficiently displaying large data sets in a scrollable list. Utilized for displaying city and prayer time lists.
    * **DiffUtil:** Works with RecyclerView to efficiently calculate list changes and optimize UI updates.
    * **View Binding:** A feature that provides easy and type-safe access to views in XML layout files, reducing `findViewById` usage and NullPointerException risks.


# Namaz Vakitleri Uygulaması

Kullanıcının mevcut konumuna veya manuel olarak seçilen bir şehre göre doğru ve güncel namaz vakitlerini sağlamak amacıyla tasarlanmış bir Android uygulamasıdır. 

## Özellikler

* **Konum Tabanlı Servisler:** Gerçek zamanlı namaz vakti güncellemeleri için kullanıcının konumunu GPS kullanarak otomatik olarak algılar.
* **Manuel Şehir Seçimi:** Kullanıcıların namaz vakitlerini görüntülemek için manuel olarak bir şehir seçmelerine olanak tanır.
* **Çevrimdışı Destek:** (Eğer uygulamanızda varsa, örn: verileri önbelleğe alıyorsanız) Namaz vakitleri çevrimdışı görüntüleme için önbelleğe alınır.

## Kullanılan Teknolojiler

* **Programlama Dili:**
    * **Kotlin

* **Mimari ve Tasarım Desenleri:**
    * **MVVM (Model-View-ViewModel):** Uygulama mantığı, UI'dan ayrıştırılarak test edilebilirliği, sürdürülebilirliği ve okunabilirliği artırılmıştır.
    * **Repository Deseni:** Veri kaynakları (yerel veritabanı, uzak API) arasında soyutlama sağlayarak veri işlemlerini kolaylaştırır ve temiz bir kod tabanı sunar.

* **Veri Yönetimi ve Kalıcılık:**
    * **Room Persistence Library:** SQLite veritabanı üzerinde yüksek soyutlama sağlayarak yerel veri depolamayı ve yönetmeyi kolaylaştırır. `City` ve `PrayerTime` verilerini verimli bir şekilde saklar.
    * **LiveData:** Veri değişikliklerini UI'a güvenli ve yaşam döngüsü farkındalığıyla iletmek için kullanılan gözlemlenebilir veri tutucudur.

* **Ağ Bağlantısı ve API Etkileşimi:**
    * **Retrofit:** RESTful API'lerle kolayca iletişim kurmak için kullanılan popüler bir HTTP istemci kütüphanesidir. Namaz vakitleri ve şehir verilerini çekmek için kullanılmıştır.

* **Bağımlılık Enjeksiyonu:**
    * **Dagger Hilt:** Ölçeklenebilir, test edilebilir ve sürdürülebilir Android uygulamaları oluşturmak için bağımlılık enjeksiyonunu basitleştiren bir kütüphanedir. Tüm bağımlılıklar (ViewModel, Repository, API servisleri, veritabanı vb.) Hilt aracılığıyla sağlanmıştır.

* **Konum ve Sensör Servisleri:**
    * **Google Play Services Location (FusedLocationProviderClient):** Cihazın konumunu almak için kullanılan API'dir. Otomatik konum belirleme özelliği için kullanılmıştır.
    * **Android Sensor Framework (Manyetik Alan ve İvmeölçer Sensörleri):** Cihazın yönelimini ve manyetik kuzeyi belirlemek için kullanılır. Bu veriler, kıble yönünü hesaplamak için temel oluşturur.

* **Kullanıcı Arayüzü (UI):**
    * **RecyclerView:** Büyük veri setlerini verimli bir şekilde liste halinde göstermek için kullanılan esnek bir view grubudur. Şehir ve namaz vakitleri listelerini göstermek için kullanılmıştır.
    * **DiffUtil:** RecyclerView ile birlikte kullanılarak listelerdeki değişiklikleri etkin bir şekilde hesaplar ve UI güncellemelerini optimize eder.
    * **View Binding:** XML layout dosyalarındaki view'lara kolay ve güvenli bir şekilde erişmek için kullanılan bir özelliktir. `findViewById` kullanımını azaltır ve NullPointerException riskini düşürür.


## Ekran Görüntüleri

![image](https://github.com/user-attachments/assets/5dbf17d3-8567-49c5-b4c8-186c7191d08e)
