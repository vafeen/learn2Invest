# Learn2Invest
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/f07c5098-a7da-4cb9-8f0d-de59f0486e6c)

Learn2Invest это биржевой симмулятор для тренировки инвестиционных навыков, с использованием API [CoinCap](https://docs.coincap.io/). 

* [Участники проекта](#участники-проекта)
* [Основной функционал](#основной-функционал)
* [Технологии](#технологии)
* [Технические детали реализации](#технические-детали-реализации)
* * [Структура проекта](#структура-проекта)
* [Флоу](#флоу)
* [Экраны](#экраны)
* * [Splash screen](#splash-screen-1)
  * [Регистрация](#регистрация-1)
  * [PIN](#pin-1)
  * [Портфель](#портфель-1)
  * [Обзор рынка](#обзор-рынка-1)
  * [Обзор актива](#обзор-актива-1)
  * [История](#история-1)
  * [Настройки](#настройки-1)

## Участники проекта 
* Воробьев Владимир Васильевич (Arengol (Github)/Vladimir (Имя в локальных настройках git))
* Vafeen
* Надежда (cherevkovanadya)

## Основной функционал 
* Создание аккаунта
* Авторизация PIN-кодом и биометрией
* Отображение курсов валют
* Фильтры по цене, рыночной капитализации, проценту роста за 24 часа
* Поиск вылют по названию с историей поиска и подсказками
* Виртуальный инвестиционный счет
* Виртуальная покупка/продажа криптовалют
* Обзор инвестиционного портфеля 
* Настройки приложения
* Локализация (ru/en)
* Обновление данных в реальном времени

## Технологии 
* Retrofit
* Room
* Coroutines
* Biometric
* JetPack Navigation
* Constraintlayout
* Hilt
* Mpandroidchart
* Coil
* Архитектура MVVM
  
  Coil был выбран потому, что через Glide неудобно загружать SVG. Mpandroidchart была выбрана потому, что данная библиотека обладает большой гибкостью и является одной из самых популярных. 

## Технические детали реализации 
* API: https://docs.coincap.io/
* Иконки: https://cryptofonts.com/img/icons/{{symbol}}.svg
* Макеты экранов и карта переходов: https://www.figma.com/design/GvomF07D4aJtrFuc3uj4W9/Learn2Inves
* ТЗ: 

Иконки для коинов загружаются с отдельного API, используя данные из поля "symbol" в JSONах основного API. 

### Структура проекта
В папке ***ui*** хранятся контроллеры, view model, и адаптеры recycler view, для всех acitvity и fragment. У каждого acitvity и fragment своя папка. В папке ***noui*** хранятся репозитории для работы с сетью и базой данных, а так же реализация криптографии, авторизации, и инъекции зависимостей. В папке ***utils*** хранятся константы и вспомогательные методы. Папка ***app*** хранит application класс. 

## Классы экранов 
### Splash screen
MainActivity, MainActivityViewModel

### Регистрация 
SignUpActivity, SignUpActivityViewModel

### PIN
SignInActivity, SignINActivityActions, SignInActivityViewModel

### Портфель 
PortfolioFragment, PortfolioFragmentViewModel, PortfolioAdapter, AssetConstants

### Обзор рынка
MarketReviewFragment, MarketReviewFragmentViewModel, MarketReviewAdapter

### Обзор актива 
AssetReviewActivity

#### Фрагмент информации 
AssetOverviewFragment, AssetOverViewFragmentViewModel

#### Фрагмент истории 
SubHistoryFragment, SubHistoryFragmentViewModel, SubHistoryAdapter

### История 
HistoryFragment, HistoryFragmentViewModel, HistoryAdapter

### Настройки 
ProfileFragmentViewModel, ProfileFragment

## Флоу

## Экраны 
### Splash screen 
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/1c703ee5-a52c-4f1e-8fa5-176525d22c26) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/8286d67d-1099-411a-a880-2ef89818dc3f)

### Регистрация
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/382a3e6c-21ed-4848-a594-6184bee5910a) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/0b9f64af-1292-4a46-b102-97eebd91cfa7)
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/0dbb0e48-48eb-40d1-b2fb-7e721a38e154)
 
### PIN 
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/7beb25cd-c7e3-44dc-bb41-bef1ee9c1158) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/51e21825-28d5-4c84-b619-e845b39a1c0a)
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/37c4e029-e40a-4f0b-964e-7c76d29142af) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/f4a885c4-b4e7-43f2-9d1b-b8f97af9deb1)
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/0318aefc-865a-4f24-a73c-0c217af5e050)

### Портфель 
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/3c1d2a71-f5bb-4afb-b1f3-b39d7336aed3) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/e034f1ff-e798-4bd6-916d-305acc5a9982)
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/619aabbf-7405-46d4-8414-0ad47e3baea2) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/ceb2d292-0785-4a8c-8a62-2e705826f90d)
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/d214be75-ea8a-4758-91fb-ad6f42aa3ae5) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/2f89fe56-0d16-4e69-85de-86c0c12e1e16)


### Обзор рынка 
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/dd335e78-a446-4cc4-b4f3-84c3084bf21c) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/e7cedad7-a257-4b04-b58d-76770a976c9e)
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/077aea76-8a4b-4aed-ac94-30088a4e613b) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/fd7a8437-cf43-4010-b2f0-0d7217ccc436)
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/4516375e-1b2f-4cb4-83ef-24a4f62d0449) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/f8aa83a1-44ed-42f4-acc2-6c8b7b219bf6)

### Обзор актива 
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/af6fc14c-67c8-4295-aa09-540499671618) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/fb1e0f8a-0d99-4792-a829-d118f150ae35)
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/4eb58fba-775f-4e9e-85c4-c9bc3fb95238) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/b34e801e-08d5-4a14-a7d5-775307e774d2)

### История 
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/4b41a24a-3a97-4566-b665-3f425a6c111b) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/32252402-e5a5-4e88-aeb9-5013a4b624b3)

### Настройки 
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/d4d2f408-46e3-4ecf-9a99-410222f90c73) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/350754fe-09be-464a-ba4b-63286b5df191)
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/c7522358-25b7-4051-a854-70779a106521)
















