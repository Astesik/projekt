-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sty 17, 2024 at 08:18 PM
-- Wersja serwera: 10.4.28-MariaDB
-- Wersja PHP: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `pr`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `classes`
--

CREATE TABLE `classes` (
  `classID` int(11) NOT NULL,
  `className` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `classes`
--

INSERT INTO `classes` (`classID`, `className`) VALUES
(1, '1A');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `grades`
--

CREATE TABLE `grades` (
  `gradeID` int(11) NOT NULL,
  `studentID` int(11) DEFAULT NULL,
  `subjectID` int(11) DEFAULT NULL,
  `grade` int(11) DEFAULT NULL CHECK (`grade` between 1 and 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `grades`
--

INSERT INTO `grades` (`gradeID`, `studentID`, `subjectID`, `grade`) VALUES
(5, 2, 2, 1),
(6, 2, 1, 2),
(7, 3, 1, 5),
(8, 2, 1, 4);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `logs`
--

CREATE TABLE `logs` (
  `id` int(11) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `message` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `logs`
--

INSERT INTO `logs` (`id`, `date`, `message`) VALUES
(1, '2024-01-17 18:33:51', 'Zalogowano użytkownika s1 (id: 6, rola: student)'),
(2, '2024-01-17 18:33:51', 'Wysłano zapytanie o oceny studenta o id: 6'),
(3, '2024-01-17 18:34:08', 'Wysłano zapytanie o oceny studenta o id: 6'),
(4, '2024-01-17 18:34:52', 'Zalogowano użytkownika a (id: 4, rola: admin)'),
(5, '2024-01-17 18:44:30', 'Zalogowano użytkownika a (id: 4, rola: admin)'),
(6, '2024-01-17 18:45:27', 'Zalogowano użytkownika a (id: 4, rola: admin)'),
(7, '2024-01-17 18:54:31', 'Zalogowano użytkownika a (id: 4, rola: admin)'),
(8, '2024-01-17 18:56:08', 'Zalogowano użytkownika a (id: 4, rola: admin)'),
(9, '2024-01-17 18:58:02', 'Wysłano zapytanie o oceny studenta o id: 6'),
(10, '2024-01-17 18:58:18', 'Zalogowano użytkownika a (id: 4, rola: admin)'),
(11, '2024-01-17 18:58:35', 'Zalogowano użytkownika a (id: 4, rola: admin)'),
(12, '2024-01-17 18:59:26', 'Zalogowano użytkownika a (id: 4, rola: admin)'),
(13, '2024-01-17 19:00:05', 'Zalogowano użytkownika a (id: 4, rola: admin)'),
(14, '2024-01-17 19:16:45', 'Zalogowano użytkownika a (id: 4, rola: admin)');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `students`
--

CREATE TABLE `students` (
  `studentID` int(11) NOT NULL,
  `userID` int(11) DEFAULT NULL,
  `classID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`studentID`, `userID`, `classID`) VALUES
(2, 3, 1),
(3, 6, NULL);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `subjects`
--

CREATE TABLE `subjects` (
  `subjectID` int(11) NOT NULL,
  `subjectName` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `subjects`
--

INSERT INTO `subjects` (`subjectID`, `subjectName`) VALUES
(1, 'Matematyka'),
(2, 'Polski'),
(3, 'Fizyka');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('student','teacher','admin') NOT NULL,
  `name` varchar(255) NOT NULL,
  `surname` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `role`, `name`, `surname`) VALUES
(2, 't', 't', 'teacher', 't', 't'),
(3, 's', 's', 'student', 's', 's'),
(4, 'a', 'a', 'admin', 'a', 'a'),
(6, 's1', 's1', 'student', 's1', 's1');

--
-- Wyzwalacze `users`
--
DELIMITER $$
CREATE TRIGGER `tr_create_student_after_insert` AFTER INSERT ON `users` FOR EACH ROW BEGIN
    IF NEW.role = 'student' THEN
        INSERT INTO students (userID) VALUES (NEW.id);
    END IF;
END
$$
DELIMITER ;

--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `classes`
--
ALTER TABLE `classes`
  ADD PRIMARY KEY (`classID`);

--
-- Indeksy dla tabeli `grades`
--
ALTER TABLE `grades`
  ADD PRIMARY KEY (`gradeID`),
  ADD KEY `studentID` (`studentID`),
  ADD KEY `subjectID` (`subjectID`);

--
-- Indeksy dla tabeli `logs`
--
ALTER TABLE `logs`
  ADD PRIMARY KEY (`id`);

--
-- Indeksy dla tabeli `students`
--
ALTER TABLE `students`
  ADD PRIMARY KEY (`studentID`),
  ADD UNIQUE KEY `userID` (`userID`),
  ADD KEY `classID` (`classID`);

--
-- Indeksy dla tabeli `subjects`
--
ALTER TABLE `subjects`
  ADD PRIMARY KEY (`subjectID`);

--
-- Indeksy dla tabeli `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `classes`
--
ALTER TABLE `classes`
  MODIFY `classID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `grades`
--
ALTER TABLE `grades`
  MODIFY `gradeID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `logs`
--
ALTER TABLE `logs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `students`
--
ALTER TABLE `students`
  MODIFY `studentID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `subjects`
--
ALTER TABLE `subjects`
  MODIFY `subjectID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `grades`
--
ALTER TABLE `grades`
  ADD CONSTRAINT `grades_ibfk_1` FOREIGN KEY (`studentID`) REFERENCES `students` (`studentID`) ON DELETE CASCADE,
  ADD CONSTRAINT `grades_ibfk_2` FOREIGN KEY (`subjectID`) REFERENCES `subjects` (`SubjectID`) ON DELETE CASCADE;

--
-- Constraints for table `students`
--
ALTER TABLE `students`
  ADD CONSTRAINT `students_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `students_ibfk_2` FOREIGN KEY (`classID`) REFERENCES `classes` (`classID`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
