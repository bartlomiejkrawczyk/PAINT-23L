# Programowanie Aplikacji Internetowych
## Dokumentacja projektu Wordle

### Zespół
Aleksandra Sypuła
Bartłomiej Krawczyk
Daniel Kobiałka
Jakub Marcowski
Łukasz Jaremek
Mateusz Brzozowski
Mateusz Krakowski

### Temat
Wordle - gra polegająca na odgadnięciu pięcioliterowego słowa. Użytkownik ma do dyspozycji sześć prób, a po każdej próbie gra informuje go, czy użyte litery znajdują się w słowie-odpowiedzi i w jakim dokładnie miejscu, czy też znajdują się w słowie-odpowiedzi, ale w innym miejscu, albo też nie znajdują się w słowie-odpowiedzi w ogóle.

### Implementacja

#### Baza danych

Baza danych PostgreSQL 15.2 wordledb działa na środowisku dockerowym na localhoscie, port 5432. Po stronie bazy nie skorzystaliśmy z programowania, jedynie użyliśmy typów serial, aby zautomatyzować nadawanie identyfikatorów dla rzędów. Służy ona głównie do przechowywania danych zbieranych na potrzebę aplikacji. Cała struktura jest sformułowana w pliku DATABASE/scripts/DATABASE_CREATE.sql.


```mermaid
erDiagram
    LANGUAGES {
        SERIAL language_id PK
        VARCHAR_3 code
        VARCHAR_50 name
        VARCHAR_255 flag_url
    }
    
    WORDS {
        BIGSERIAL word_id PK
        INTEGER language_id FK
        INTEGER word_length
        INTEGER word_number
        VARCHAR_255 word
    }
    
    USERS {
        SERIAL user_id PK
        VARCHAR_50 login
        VARCHAR_64 password_hash
    }
    
    SESSIONS {
        BIGSERIAL session_id PK
        INTEGER user_id FK
        INTEGER language_id FK
        INTEGER word_length FK
        INTEGER word_number FK
        TIMESTAMP created_date
    }
    
    GUESSES {
        SERIAL guess_id PK
        BIGINT session_id FK
        INTEGER guess_number
        VARCHAR_15 guess
        TIMESTAMP created_date
    }
    
    RESULTS {
        SERIAL result_id PK
        INTEGER user_id FK
        BOOLEAN result
        TIMESTAMP created_date
        INTEGER guess_number
    }
    
    LANGUAGES ||--o{ WORDS : from
    LANGUAGES ||--o{ SESSIONS : in
    WORDS ||--o{ SESSIONS : "to guess"
    SESSIONS ||--o{ USERS : "plays"
    SESSIONS ||--o{ GUESSES : "plays"
    
    RESULTS ||--o{ USERS : "played"
    
```

#### Back End

Część backend-owa składa się z dwóch niezależnych serwisów: `SESSION` i `AUTHORIZATION`.

SESSION odpowiada za zarządzanie rozgrywką. Zalogowani użytkownicy mogą zgłosić się w celu nawiązania sesji (rozgrywki). W ramach gry przesyłają kolejnymi zapytaniami słowa. Serwis weryfikuje czy słowo jest poprawne i zwraca rezultat.

AUTHORIZATION odpowiada za zarządzanie użytkownikami oraz zapewnienie bezpieczeństwa. To ten serwis umożliwia rejestrację oraz logowanie. W ramach projektu skorzystaliśmy z autoryzacji za pomocą JSON Web Token. Generalny zarys stosowanego przez nas modelu bezpieczeństwa:

```mermaid 
sequenceDiagram
    actor u as User
    participant s as Authorization serivce
    participant db as User Database
    participant r as Resource Server

    u->>s: Authentication Request
    
    s->>db: Request User Credentials
    db->>s: User Credentials Returned
    s->>s: Match Hash of passed secret and database secret
    s->>s: Generate JWT

    s->>u: Signed JWT
    
    loop While token is valid
        u->>r: Resource Request
        r->>r: Verify JWT
        r->>u: Resource Returned
    end

```

**Budowanie projektu**

W ramach projektu skorzystaliśmy z narzędzia do automatycznego budowania - `gradle`. Projekt można uruchomić z lini poleceń wywołując:

```
./gradlew clean build bootRun
```

**Swagger**

Aby ułatwić testowanie serwisu dodaliśmy do projektów endpoint ze swaggerem. Pozwala on na łatwe wywoływanie endpointów bez korzystania z zewnętrznych narzędzi. Po uruchomieniu dockera swagger jest dostępny pod adresem odpowiednio http://localhost:7777/swagger oraz http://localhost:7788/swagger 

![](https://hackmd.io/_uploads/HJm42U1Pn.png)


![](https://hackmd.io/_uploads/HyhR3rJvh.png)

#### Front End

<!-- ![image alt](https://blog.axway.com/wp-content/uploads/2019/09/API-Gateway-capabilities-and-features-1.jpg "me" =408x400) -->

Część frontendową aplikacji stanowią 3 pliki znajdujące się w podfolderze `public-html` folderu `FE`, tj. `index.html`, `style.css` oraz `app.js`.

HTML:

- Plik HTML zawiera strukturę strony internetowej. Składa się z różnych elementów, takich jak nagłówek, sekcje itp.,
- W sekcji nagłówka umieszczony jest tytuł aplikacji "Wordle",
- W sekcji głównej znajduje się obszar, w którym będzie wyświetlany obecny stan gry, tj. próby zgadnięć i ich rezultaty oraz pomocnicza klawiatura, obrazująca wykorzystane litery,
- W prawym górnym rogu aplikacji widnieją trzy przyciski: zmiany motywu (domyślny: `ciemny` oraz `jasny`), logowania oraz rejestracji; dwa ostatnie po kliknięciu powodują wyświetlenie okienka typu pop-up (za pomocą przekierowania na odpowiedni adres URL).

CSS:

- Arkusz stylów CSS jest używany do określania wyglądu i układu elementów HTML,
- Dla różnych elementów zastosowano różne style, takie jak kolory, czcionki, marginesy, wyrównanie, tło itp., aby nadać aplikacji atrakcyjny wygląd,
- Została użyta zewnętrzna biblioteka CSS - Material Icons, aby mieć dostęp do gotowych ikonek.

JavaScript:

- Skrypt JavaScript jest używany do dodawania interaktywności do aplikacji,
- Zdefiniowano funkcje obsługujące różne zdarzenia, takie jak np. kliknięcie przycisku "Enter",
- Oprócz wpisywania zgadywanego słowa za pomocą wyżej wspomnianej klawiatury wyświetlanej na ekranie (co jest kluczowym mechanizmem jeśli chodzi o np. korzystanie z aplikacji na urządzeniu mobilnym) dla wygody użytkownika istnieje również opcja wpisywania kolejnych liter za pomocą fizycznej klawiatury komputera,
- Do kontaktu z Back Endem wykorzystywane są asynchroniczne zawołania typu "fetch"; sama "rozmowa" polega na przesyłaniu odpowiednich HTTP Requestów.

### Uruchomienie
1. Postawić obrazy, na których zbudowana jest aplikacja, za pomocą komendy `docker compose up --build`,
2. Poczekać, aż wszystkie kontenery zakończą proces wstawania (zazwyczaj można to rozpoznać po tym, że w kontenerze `session` widnieje `Application availability state ReadinessState changed to ACCEPTING_TRAFFIC` w logach,
3. W przeglądarce można wejść na adres `localhost` i cieszyć się grą (ważne, żeby był to **localhost**, a **nie 127.0.0.1** z powodu CORS (Cross-Origin Resource Sharing), ponieważ tylko adres http://localhost/ zostanie zaakceptowany przez Back End.

### Przykłady

<!-- ![image alt](https://i0.wp.com/gamingretro.co.uk/wp-content/uploads/2022/03/windle.png?resize=752%2C440&ssl=1 "wordle" =564x300) -->

- Ekran gry (tryb ciemny):
![image alt](https://hackmd.io/_uploads/SJC_9C1Dh.png "Ekran gry (tryb ciemny)")
- Ekran gry (tryb jasny):
![image alt](https://hackmd.io/_uploads/BJMYq01vh.png "Ekran gry (tryb jasny)")
- Okno rejestracji:
![image alt](https://hackmd.io/_uploads/Hy8_oCkvn.png "Okno rejestracji")
- Okno logowania:
![image alt](https://hackmd.io/_uploads/HkF_oR1v3.png "Okno logowania")

<!-- ![](https://hackmd.io/_uploads/SJ8owLJP3.png) -->
