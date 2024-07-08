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
* Черевкова Надежда Александровна (cherevkovanadya (Github)/Nadezhda (Имя в локальных настройках
  git))

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
* Темная тема

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
* Макеты экранов и карта переходов: https://www.figma.com/design/GvomF07D4aJtrFuc3uj4W9/Learn2Invest
* ТЗ: https://necessary-spot-b65.notion.site/Learn2Invest-16e7a0523381411486c2a22513fcae03

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
https://github.com/vafeen/learn2Invest/assets/67644124/7639e883-1c0b-4ded-ad49-91c798b0c878

## Экраны 
### Splash screen 
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/266d9c22-32ce-429c-8eaf-6d6c84ad46ab) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/d3c5999b-0032-4e28-8f58-77dbf59715d9)

### Регистрация
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/a2231dda-ee04-4f6b-b9c0-6f866735d21b) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/f62095f4-c89a-4d5f-8286-3da9ba101c83)
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/feea49cf-4ab8-40a5-b4f5-e8645181c36c)
 
### PIN 
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/7beb25cd-c7e3-44dc-bb41-bef1ee9c1158) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/51e21825-28d5-4c84-b619-e845b39a1c0a)
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/37c4e029-e40a-4f0b-964e-7c76d29142af) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/f4a885c4-b4e7-43f2-9d1b-b8f97af9deb1)
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/0318aefc-865a-4f24-a73c-0c217af5e050)

### Портфель 
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/13ae0038-727f-4ece-9b41-e0fa1f3b854d) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/5fa76421-f74b-4e6b-819c-a53743ee537d)
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/c079e9bb-c60e-4bc2-8fd9-66a647c91048) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/07ec934c-33f8-4752-a5a9-b9a6355713be)
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/3412c347-13b8-4ad9-9b9d-3d977ca95c98) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/c3a248fa-f8a7-42a9-b05b-2097cfb29a3e)


### Обзор рынка 
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/781684dc-6f39-4139-a35b-ac1419b5e0f9) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/8807be34-1fea-41ba-a461-38eee33803bb)
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/bcc5e46f-86ed-41e7-bc66-cfb93c1f5e5f) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/25331746-957e-4f8c-ac1f-fdf9f0886f8c)
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/32138a33-8e41-4c1a-9428-d3c8522b8b3b) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/d5d089c0-feff-4c6f-bb0d-8fe14368d612)


### Обзор актива 
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/7442f586-138a-4a63-8072-a7331a194ab4) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/88dd3a17-9b8a-4b88-990b-bd2e51e36c7b)
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/21198a3d-7d33-4aaf-b57f-b773375c152f) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/a35bb0c9-c80e-43ce-998c-b5a50c740161)
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/c09205b3-4286-4709-9719-81d40bf274a5)

### История 
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/4b41a24a-3a97-4566-b665-3f425a6c111b) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/a32bb428-4aad-4117-9a23-9bc00da7ecfb)

### Настройки 
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/a7ab6a8a-99cf-459c-9a58-23c38bbc4243) ![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/9614395e-a854-4dcc-932c-703f733c5f73)
![изображение](https://github.com/vafeen/learn2Invest/assets/67644124/7d21243f-c22a-4002-9fac-7cb71008fa67)

















