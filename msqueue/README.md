# Skip List

## Описание
Проект включает в себя следующие исходные файлы:

* `Queue.java` содержит интерфейс очереди.
* `MSQueue.java` содержит однопоточную реализацию очереди. Данная реализация небезопасна для использования из нескольких потоков одновременно.
* `pom.xml` содержит описание проекта для системы сборки Maven.

## Задание
Необходимо доработать реализую `MSQueue` так, чтобы она стала безопасной для использования из множества потоков одновременно. Используйте алгоритм очереди, предложенный M. Michael и L. Scott.

## Сборка и тестирование
Для тестирования используйте команду `mvn test`. При этом автоматически будут запущены следующие тесты:

* `FunctionalTest.java` проверяет базовую корректность множества.
* `LinearizabilityTest.java` проверяет реализацию множества на корректность в многопоточной среде.

Обратите внимание, что тесты не покрывают все возможные ошибки синхронизации, поэтому прохождение тестов не означает корректность реализации.