-- Очистка таблиц в правильном порядке (важно!)
DELETE FROM tickets;
DELETE FROM seats;
DELETE FROM sessions;
DELETE FROM movies;
DELETE FROM cinema_halls;

-- Заполнение данными

-- 1. cinema_halls (первая - нет зависимостей)
-- Максимум 9 рядов и 10 мест в ряду
-- Значения будут обновлены после генерации мест
INSERT INTO cinema_halls (id, name, description, total_seats, capacity, hall_type, rows_count, seats_per_row) VALUES
(1, 'Киномакс Планета', 'Современный кинокомплекс с 10 залами', 0, 0, 'STANDARD', 9, 7),
(2, 'Октябрь', 'Кинотеатр в историческом здании', 0, 0, 'STANDARD', 9, 8),
(3, 'Синема Парк', 'Ультрасовременный кинотеатр с IMAX', 0, 0, 'IMAX', 9, 9),
(4, 'Пионер', 'Уютный кинотеатр в центре города', 0, 0, 'STANDARD', 9, 10),
(5, 'Семейный', 'Семейный кинотеатр с детской комнатой', 0, 0, 'STANDARD', 9, 6);

-- 2. movies (нет зависимостей) - 10 фильмов
INSERT INTO movies (id, title, description, duration_minutes, genre, director, actors, age_rating, poster_url, release_date) VALUES
(1, 'Легенда №17', 'История легендарного хоккеиста и его путь к спортивной славе', 136, 'Спорт', 'Артем Аксененко', 'Александр Петров, Виктор Добронравов', '16+', '1.png', '2024-01-01'),
(2, 'Волшебник Изумрудного города', 'Современная адаптация классической сказки о волшебнике Изумрудного города', 120, 'Фэнтези', 'Клим Шипенко', 'Александр Палу, Марина Петренко', '12+', '2.png', '2024-04-25'),
(3, 'Шерлок в России', 'Детективная история о расследовании серии загадочных убийств в Петербурге', 128, 'Детектив', 'Егор Баранов', 'Данила Козловский, Светлана Ходченкова', '16+', '3.png', '2024-02-15'),
(4, 'Тихое место: День первый', 'Постапокалиптический мир, где слепые монстры охотятся на людей по звуку', 100, 'Хоррор', 'Джон Красински', 'Джон Дэвид Вашингтон, Мадс Миккельсен', '16+', '4.png', '2024-06-28'),
(5, 'Дэдпул и Росомаха', 'Дедпул присоединяется к Росомахе в новом приключении по вселенной Marvel', 127, 'Боевик', 'Шон Леви', 'Райан Рейнольдс, Хью Джекман', '12+', '5.png', '2024-07-26'),
(6, 'Интерстеллар 2', 'Продолжение космической эпопеи о путешествиях через червоточины', 158, 'Фантастика', 'Кристофер Нолан', 'Мэттью Макконахи, Энн Хэтэуэй', '12+', '6.png', '2024-09-12'),
(7, 'Три богатыря и Наследница престола', 'Новые приключения знаменитых русских богатырей', 95, 'Мультфильм', 'Константин Феоктистов', 'Озвучка: Сергей Бурунов, Диомид Виноградов', '6+', '7.png', '2024-10-18'),
(8, 'Форсаж 11', 'Заключительная часть саги о уличных гонках и семье', 141, 'Боевик', 'Луис Летерье', 'Вин Дизель, Джейсон Стейтем', '16+', '8.png', '2024-11-01'),
(9, 'Зеленая миля 2', 'Духовное продолжение истории о чудесах в тюрьме', 134, 'Драма', 'Фрэнк Дарабонт', 'Том Хэнкс, Майкл Кларк Дункан', '16+', '9.png', '2024-08-15');

-- Сеансы на 7 дней вперед от даты 2026-02-28
INSERT INTO sessions (start_time, end_time, price, format, hall_id, movie_id) VALUES
-- День 1: 2026-02-28 - Базовая цена 250 ₽
('2026-02-28 19:00:00', '2026-02-28 21:16:00', 250.00, '2D', 1, 1),
('2026-02-28 20:30:00', '2026-02-28 22:30:00', 250.00, '3D', 1, 2),
('2026-02-28 21:00:00', '2026-02-28 23:08:00', 250.00, '3D', 1, 3),
('2026-02-28 21:00:00', '2026-02-28 22:40:00', 250.00, '2D', 2, 4),
('2026-02-28 22:00:00', '2026-03-01 00:07:00', 250.00, '3D', 2, 5),

-- День 2: 2026-03-01 - Базовая цена 250 ₽
('2026-03-01 17:00:00', '2026-03-01 19:16:00', 250.00, '2D', 3, 6),
('2026-03-01 19:00:00', '2026-03-01 21:00:00', 250.00, 'IMAX', 3, 7),
('2026-03-01 21:00:00', '2026-03-01 23:08:00', 250.00, 'IMAX', 3, 8),
('2026-03-01 21:00:00', '2026-03-01 22:40:00', 250.00, '2D', 4, 9),
('2026-03-01 22:00:00', '2026-03-02 00:07:00', 250.00, '3D', 4, 9),

-- День 3: 2026-03-02 - Базовая цена 250 ₽
('2026-03-02 17:00:00', '2026-03-02 19:16:00', 250.00, '2D', 5, 1),
('2026-03-02 18:00:00', '2026-03-02 20:00:00', 250.00, '3D', 5, 2),
('2026-03-02 21:00:00', '2026-03-02 23:08:00', 250.00, 'IMAX', 3, 3),
('2026-03-02 20:00:00', '2026-03-02 21:40:00', 250.00, '2D', 2, 4),
('2026-03-02 22:00:00', '2026-03-03 00:07:00', 250.00, '3D', 2, 5),

-- День 4: 2026-03-03 - Базовая цена 250 ₽
('2026-03-03 19:00:00', '2026-03-03 21:16:00', 250.00, '2D', 1, 6),
('2026-03-03 20:30:00', '2026-03-03 22:30:00', 250.00, '3D', 1, 7),
('2026-03-03 21:00:00', '2026-03-03 23:00:00', 250.00, '3D', 1, 8),
('2026-03-03 21:00:00', '2026-03-03 22:40:00', 250.00, '2D', 2, 9),
('2026-03-03 22:00:00', '2026-03-04 00:07:00', 250.00, '3D', 2, 9),

-- День 5: 2026-03-04 - Базовая цена 250 ₽
('2026-03-04 17:00:00', '2026-03-04 19:16:00', 250.00, '2D', 3, 1),
('2026-03-04 19:00:00', '2026-03-04 21:00:00', 250.00, 'IMAX', 3, 2),
('2026-03-04 21:00:00', '2026-03-04 23:08:00', 250.00, 'IMAX', 3, 3),
('2026-03-04 21:00:00', '2026-03-04 22:40:00', 250.00, '2D', 4, 4),
('2026-03-04 22:00:00', '2026-03-05 00:07:00', 250.00, '3D', 4, 5),

-- День 6: 2026-03-05 - Базовая цена 250 ₽
('2026-03-05 17:00:00', '2026-03-05 19:16:00', 250.00, '2D', 5, 6),
('2026-03-05 18:00:00', '2026-03-05 20:00:00', 250.00, '3D', 5, 7),
('2026-03-05 21:00:00', '2026-03-05 23:08:00', 250.00, 'IMAX', 3, 8),
('2026-03-05 20:00:00', '2026-03-05 21:40:00', 250.00, '2D', 2, 9),
('2026-03-05 22:00:00', '2026-03-06 00:07:00', 250.00, '3D', 2, 9),

-- День 7: 2026-03-06 - Базовая цена 250 ₽
('2026-03-06 19:00:00', '2026-03-06 21:16:00', 250.00, '2D', 1, 1),
('2026-03-06 20:30:00', '2026-03-06 22:30:00', 250.00, '3D', 1, 2),
('2026-03-06 21:00:00', '2026-03-06 23:08:00', 250.00, '3D', 1, 3),
('2026-03-06 21:00:00', '2026-03-06 22:40:00', 250.00, '2D', 2, 4),
('2026-03-06 22:00:00', '2026-03-07 00:07:00', 250.00, '3D', 2, 5);

-- 4. seats - Генерация мест для каждого зала с увеличением количества мест в ряду от первого к последнему

-- Создаем временную таблицу для хранения конфигурации рядов каждого зала
CREATE TEMPORARY TABLE IF NOT EXISTS hall_row_config (
    hall_id INT,
    row_num INT,
    seats_in_row INT
);

-- Зал 1: от 3 мест в 1 ряду до 11 мест в 9 ряду (увеличение на 1 каждый ряд)
INSERT INTO hall_row_config (hall_id, row_num, seats_in_row) VALUES
(1, 1, 3), (1, 2, 4), (1, 3, 5), (1, 4, 6), (1, 5, 7),
(1, 6, 8), (1, 7, 9), (1, 8, 10), (1, 9, 11);

-- Зал 2: от 4 мест в 1 ряду до 12 мест в 9 ряду (увеличение на 1 каждый ряд)
INSERT INTO hall_row_config (hall_id, row_num, seats_in_row) VALUES
(2, 1, 4), (2, 2, 5), (2, 3, 6), (2, 4, 7), (2, 5, 8),
(2, 6, 9), (2, 7, 10), (2, 8, 11), (2, 9, 12);

-- Зал 3 (IMAX): от 5 мест в 1 ряду до 13 мест в 9 ряду (увеличение на 1 каждый ряд)
INSERT INTO hall_row_config (hall_id, row_num, seats_in_row) VALUES
(3, 1, 5), (3, 2, 6), (3, 3, 7), (3, 4, 8), (3, 5, 9),
(3, 6, 10), (3, 7, 11), (3, 8, 12), (3, 9, 13);

-- Зал 4: от 6 мест в 1 ряду до 14 мест в 9 ряду (увеличение на 1 каждый ряд)
INSERT INTO hall_row_config (hall_id, row_num, seats_in_row) VALUES
(4, 1, 6), (4, 2, 7), (4, 3, 8), (4, 4, 9), (4, 5, 10),
(4, 6, 11), (4, 7, 12), (4, 8, 13), (4, 9, 14);

-- Зал 5 (Семейный): от 2 мест в 1 ряду до 10 мест в 9 ряду (увеличение на 1 каждый ряд)
INSERT INTO hall_row_config (hall_id, row_num, seats_in_row) VALUES
(5, 1, 2), (5, 2, 3), (5, 3, 4), (5, 4, 5), (5, 5, 6),
(5, 6, 7), (5, 7, 8), (5, 8, 9), (5, 9, 10);

-- Создаем временную таблицу для хранения информации о залах для обновления
CREATE TEMPORARY TABLE IF NOT EXISTS hall_stats (
    hall_id INT,
    total_seats INT,
    avg_seats_per_row DECIMAL(10,2)
);

-- Вычисляем статистику для каждого зала
INSERT INTO hall_stats (hall_id, total_seats, avg_seats_per_row)
SELECT
    hrc.hall_id,
    SUM(hrc.seats_in_row) as total_seats,
    AVG(hrc.seats_in_row) as avg_seats_per_row
FROM hall_row_config hrc
GROUP BY hrc.hall_id;

-- Обновляем общее количество мест в залах на основе новой конфигурации
UPDATE cinema_halls ch
JOIN hall_stats hs ON ch.id = hs.hall_id
SET
    ch.total_seats = hs.total_seats,
    ch.capacity = hs.total_seats,
    ch.seats_per_row = ROUND(hs.avg_seats_per_row)
WHERE ch.id IN (1, 2, 3, 4, 5);

-- Генерируем места для каждого ряда каждого зала
CREATE TEMPORARY TABLE IF NOT EXISTS seat_generator AS
SELECT
    hrc.hall_id,
    hrc.row_num,
    hrc.seats_in_row,
    seq.n as seat_num
FROM hall_row_config hrc
CROSS JOIN (
    SELECT 1 as n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION
    SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15
) seq
WHERE seq.n <= hrc.seats_in_row;

-- Вставляем места с разными типами для каждого зала
INSERT INTO seats (hall_id, row_num, seat_num, seat_type, is_available)
SELECT
    sg.hall_id,
    sg.row_num,
    sg.seat_num,
    CASE
        -- VIP места (первые 3 места в первых 2 рядах)
        WHEN sg.row_num <= 2 AND sg.seat_num <= 3 THEN 'VIP'

        -- Парные места (последние 2 места в последних 2 рядах)
        WHEN sg.row_num >= 8 AND sg.seat_num >= sg.seats_in_row - 1 THEN 'COUPLE'

        -- Места для инвалидов (первые и последние места в последнем ряду)
        WHEN sg.row_num = 9 AND (sg.seat_num = 1 OR sg.seat_num = sg.seats_in_row) THEN 'DISABLED'

        -- Премиум места (средние ряды 4-6, средние 60% мест)
        WHEN sg.row_num BETWEEN 4 AND 6
             AND sg.seat_num BETWEEN
                 CEILING(sg.seats_in_row * 0.2)  -- 20% от начала ряда
                 AND
                 FLOOR(sg.seats_in_row * 0.8)    -- 80% от начала ряда
        THEN 'PREMIUM'

        -- Остальные - стандартные
        ELSE 'STANDARD'
    END as seat_type,
    TRUE as is_available
FROM seat_generator sg
ORDER BY sg.hall_id, sg.row_num, sg.seat_num;

-- Очистка временных таблиц
DROP TEMPORARY TABLE IF EXISTS hall_row_config;
DROP TEMPORARY TABLE IF EXISTS seat_generator;
DROP TEMPORARY TABLE IF EXISTS hall_stats;

-- Проверка распределения типов мест по залам
SELECT
    s.hall_id,
    ch.name as hall_name,
    s.seat_type,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER (PARTITION BY s.hall_id), 2) as percentage
FROM seats s
JOIN cinema_halls ch ON s.hall_id = ch.id
GROUP BY s.hall_id, ch.name, s.seat_type
ORDER BY s.hall_id,
    CASE s.seat_type
        WHEN 'STANDARD' THEN 1
        WHEN 'PREMIUM' THEN 2
        WHEN 'VIP' THEN 3
        WHEN 'COUPLE' THEN 4
        WHEN 'DISABLED' THEN 5
        ELSE 6
    END;

-- Проверка количества мест в каждом ряду (должно увеличиваться на 1)
SELECT
    s.hall_id,
    ch.name,
    s.row_num,
    COUNT(*) as seats_in_row,
    LAG(COUNT(*)) OVER (PARTITION BY s.hall_id ORDER BY s.row_num) as prev_row_seats,
    COUNT(*) - LAG(COUNT(*)) OVER (PARTITION BY s.hall_id ORDER BY s.row_num) as increase
FROM seats s
JOIN cinema_halls ch ON s.hall_id = ch.id
GROUP BY s.hall_id, ch.name, s.row_num
ORDER BY s.hall_id, s.row_num;

-- Проверка максимального количества мест в ряду
SELECT
    MAX(seats_in_row) as max_seats_in_row
FROM (
    SELECT
        s.hall_id,
        s.row_num,
        COUNT(*) as seats_in_row
    FROM seats s
    GROUP BY s.hall_id, s.row_num
) row_counts;

-- Проверка данных
SELECT 'cinema_halls' as table_name, COUNT(*) as count FROM cinema_halls
UNION ALL
SELECT 'movies', COUNT(*) FROM movies
UNION ALL
SELECT 'sessions', COUNT(*) FROM sessions
UNION ALL
SELECT 'seats', COUNT(*) FROM seats
UNION ALL
SELECT 'tickets (occupied)', COUNT(*) FROM tickets WHERE status = 'PURCHASED';

-- Проверка итоговых параметров залов
SELECT
    id,
    name,
    total_seats,
    capacity,
    rows_count,
    seats_per_row
FROM cinema_halls
ORDER BY id;