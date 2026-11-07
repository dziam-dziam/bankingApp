# JAC-7 — podstawowe testy

## Uruchamianie

```bash
# 24 testy jednostkowe wszystkich metod serwisów, bez Dockera
./mvnw -Dtest=UserServiceImplTest,CardServiceImplTest test

# 24 testy serwisów oraz 5 dotychczasowych testów kontekstu/audytu na H2
./mvnw test

# Całość, łącznie z 5 testami integracyjnymi Testcontainers
./mvnw verify
```

## Zakres

- `UserServiceImplTest`: 12 prostych testów. Każda z 9 publicznych metod ma scenariusz poprawnego działania; dodatkowo: brak użytkownika, puste DTO i aktywacja już aktywnego użytkownika.
- `CardServiceImplTest`: 12 prostych testów. Każda z 9 publicznych metod ma scenariusz poprawnego działania; dodatkowo: brak karty, limit pięciu kart i aktywacja już aktywnej karty.
- `BankingFlowIT`: 5 podstawowych testów integracyjnych:
  1. Tworzenie, odczyt, aktualizacja, aktywacja/dezaktywacja i usunięcie użytkownika.
  2. CRUD karty, zmiana aktywności oraz pełny numer w bazie i zamaskowany w odpowiedziach.
  3. Odczyt użytkownika z kartami przez Redis i unieważnienie cache po dezaktywacji karty.
  4. HTTP 400 dla niepoprawnego żądania bez zapisu do bazy.
  5. HTTP 404 dla brakującego użytkownika.

Testy jednostkowe używają Mockito. Testy integracyjne używają pełnego kontekstu Springa i MockMvc, prawdziwych serwisów i repozytoriów, migracji Liquibase oraz PostgreSQL 18 i Redis 8 w Testcontainers. Nie wyłączają filtrów Spring Security — helper żądań dodaje testowe uwierzytelnienie i token CSRF.

Pozostawiono tylko podstawowe przypadki, bez macierzy parametryzowanych, testów współbieżności, rollbacku i oddzielnych scenariuszy bezpieczeństwa.

## Weryfikacja

- Polecenie `mvn test`: **29 testów zaliczonych, 0 błędów, 0 pominięć** — 24 testy serwisów i istniejące 5 testów H2/kontekstu.
- Testy integracyjne kompilują się razem z testami jednostkowymi.
- W poprzedniej próbie Docker Desktop nie uruchamiał silnika z powodu błędu tymczasowego socketu `dockerInference`. Wykonanie testów integracyjnych wymaga działającego `docker info`; nie są automatycznie pomijane przy braku Dockera.

Surefire uruchamia `*Test` w fazie `test`, a Failsafe uruchamia `*IT` w fazach `integration-test` i `verify`. Losowe porty i dane połączeń dostarcza Testcontainers; lokalny PostgreSQL i Redis nie są używane.
