Cloud computing
=============================
О задании
------------
В рамках данного проекта был реализован прототип системы распределения вычислительных мощностей. Система соответствует следующим правилам:

* Система полностью децентрализована (центральный сервер отсутствует);
* Для подключения к системе необходимо знать адрес хоть одного нода;
* Задания в систему поступают в виде скрипта (на данный момент поддерживается только JavaScript как скриптовый язык);
* Для разработчика скрипта предоставляется ограниченный набор API чтобы взаимодействовать со средой выполнения;
 
 Архитектура
------------
Центральной точкой системы является лента с задачами. Каждое задание (скрипт), которое поступает в систему, сохраняется в ленту (очередь задач). Лента в свою очередь размещается в DHT и доступна всем нодам системы по определённому ключу. 
Задание представлено классом Task и имеет следующие свойства:

* taskId: уникальный идентификатор задания;
* input: входные параметры, с которыми задание должно быть выполнено;
* script: скрипт, который будет запущен при выполнении задания;
* parentId: идентификатор родительского задания (установлен когда задание было создано из другого задания);
* dependsOn: список идентификаторов заданий на которые зависит данное задание. Задание не будет выполнено пока не выполнены все задачи из этого списка;
* taskStatus: текущий статус задачи (NOT_SCHEDULED, NOT_STARTED, IN_PROGRESS, FINISHED, ERROR).

Алгоритм работы системы можно упрощённо представить следующими шагами:

1. В системе регистрируются один или несколько исполнителей (далее будем называть их worker);
2. Клиент присоединяется к системе (для этого необходимо знать адрес хоть одного worker'а) и отправляется в систему данные а затем и задание;
3. Данные сохраняются в DHT по заданному клиентом ключу;
4. Задание добавляется в DHT и ему присваивается уникальный идентификатор;
5. Клиент ставит задание на выполнение (делает schedule по уникальному идентификатору задания);
6. Статус задания меняется с NOT_SCHEDULED на NOT_STARTED;
7. Доступные workers выполняют опрос ленты задач (active polling) c заданной периодичностью (периодичность полинга специально высчитывается) и пытаются найти задачу у которой нет незавершенных зависимостей и статус которой NOT_STARTED;
8. При этом в децентрализованной системе возникает сильная конкуренция между исполнителями, которую сложно разрешить в одной точке. Для этого используется механизм блокировки (locking) c двойной перепроверкой;
9. Получив доступ в задаче worker меняет её статус на IN_PROGRESS и начинается выполнение;
10. Успешно выполнив задачу worker меняет её статус на FINISHED и записывает результат в DHT по ключу taskId;
11. Клиент может получить результат выполнения задачи передав в систему taskId.

**Пару слов о конкуренции**. Несомненно, с конкуренцией приходится считаться, и в данном прототипе реализована система блокировки. Когда worker пытается получить ленту задач для модификаций ему необходимо сначала заблокировать ленту. По определённому ключу worker устанавливает lockId и спокойно работает с задачами. Если же заблокировать не полуачется, а по ключу уже есть чужой lockId, то исполнитель должен ждать пока lockId не будет снят. Нередко происходит ситуация когда 2 исполнителя одновременно устанавливают lock и оба уверены что они могу работать со списком задач. Для решения такой проблемы каждый исполнитель после установки lockа должен убедиться что в системе сейчас находится его lock, если же так вышло что lock не его (кто-то его перебил) то исполнитель ждёт когда lock будет снят.
Конечно должно быть более элегантное решение проблемы конкуренции в децентрализованной системе, потому как такая реализация "в лоб" может стать узким местом. Но учитывая то что система рассчитана на работу с большими объёмами данных то время затраченное на получение lock должно быть несущественным по отношению ко времени выполнения самого задания.

**Пару слов о DHT**. Для решения поставленной задачи была выбрана TomP2P реализация DHT. При ручном тестировании никаких проблем с DHT не было выявлено хотя честно сказать я не пробовал разворачивать ноды которые находятся в разных сетях и закрыты фаерволами.

**Пару слов о скрипте**. Скрипты можно писать на JavaScript. Запускаются скрипты с помощью "nashorn" Java8 script engine. К сожалению "nashorn" не поддерживает ES6 поэтому скрипты должны быть ES5 style. Система позволяет использовать в скриптах дополнительные API.

**CloudBinding (cloud)** - позволяет из скрипта получать данные по ключу из DHT и записывать данные по ключу. Так же этот API позволяется создавать новые задачи прямо внутри скрипта. Создание новых задач из скрипта как раз даёт возможность писать сложные сценарии такие как параллельная сортировка.

**LibraryBinging (library)** - конструкция которая облегчает написание сложных скриптов. Если в скрипте создаются новые задачи и в рамках новой задачи мы хотим переиспользовать метод, который был объявлен в главном скрипте - то это становится проблемой. Так как исполнитель увидит только тот скрипт который был обьявлен в задаче которую он выполняет. Исполнитель не увидит скрипт родительской задачи поэтому переиспользование методов будет невозможным. Для того чтобы решить эту проблему был реализован механизм export/import.
LibraryBinding позволяет экспортировать некоторые функции (сохранить функции по ключу в DHT) в главной задаче. А в подзадачах сделать import.

**LoggerBinding (log)** - позволяется внутри скрипта использовать slf4j. Очень удобно на этапе разработки и отладки.

Как запустить
------------
**Requirements:**

* Java8
* Maven

**Компиляция/Сборка**. Для сборки задания используется maven. В командной строке необходимо выполнить **mvn clean install -DskipTests=true**. skipTests флаг устанавливается чтобы пропустить тесты. Тесты в приложении есть и покрытие хорошее (больше 80 процентов если исключить пакет с exceptions)
 
**Запуск worker**. После сборки в папке target появился executable jar **cloud.jar**. Запустим первого исполнителя выполнив **java -jar cloud.jar --worker 4000** в папке target. Запустим ещё одного исполнителя и познакомим его с первым **java -jar cloud.jar localhost:4000 --worker 4001**. Стоит отметить что localhost:4000 это не центральный сервер, а просто один из нодов системы (localhost:4000 и localhost:4001 равнозначны). Признаком того что исполнитель запустился успешно будет строка **No pending task was found. Worker is currently idle**.
  
**Залить входящие данные**. В качестве входящих данных можно передать файл со строками разделёнными запятой (csv). Для примера возмём файл input.txt, который находится в корне проекта (в этом файле отрывок из книги Game of Thrones, надеюсь содержание там приличное :). Чтоб файл попал в систему скопируем его в target(это конечно не обязательно, просто чтоб не писать путь к файлу в командной строке) и выполним **java -jar cloud.jar localhost:4000 --input uawebchallenge --file input.txt**. При заливании данных в качестве ключа мы используем **uawebchallenge** - в дальнейшем мы будем использовать этот ключ в скрипте.
 
 **Создать задачу**. Для примера работы системы был создан скрипт для сортировки массива строк. Скрипт находится в корне проекта (script.js). Создадим задачу используя команду **java -jar cloud.jar localhost:4000 --task script.js** (если script.js не находится в target то нужно указывать полный путь). **При создании задания система отдаёт клиенту ключ. этот ключ важно запомнить так как он будет нужен дальше**. В нашем случае система вернула 148e101b-7bd7-4e5c-ae72-3c2b5a5226c3
 
 **Запланировать выполнение задачи**. Теперь когда входящие данные заданы и задача находится в ленте - можно попросить систему её выполнить. Для этого выполняем **java -jar cloud.jar localhost:4000 --schedule 148e101b-7bd7-4e5c-ae72-3c2b5a5226c3** (вместо 148e101b-7bd7-4e5c-ae72-3c2b5a5226c3 нужно использовать ваш ключ задачи) После этого шага исполнители оживут и начнут выполнять задание сортировки. Когда вы увидите в одном из исполнителей строку "Sorting is completed" - это будет означать что сортировка завершилась и можно забрать результаты.
 
 **Получить рузультаты**. Получить результаты выполнения можно используя комманду **java -jar cloud.jar localhost:4000 --output 148e101b-7bd7-4e5c-ae72-3c2b5a5226c3 --file output.txt**. В --output необходимо передать ключ задачи.
 
Скрипт параллельной сортировки
------------
В качестве алгоритма сортировки был выбран чёт-нечёт. Этот алгоритм был представлен судьями как пример, но мне он сначала показался неоптимальным с точки зрения производительность (я хотел делать merge sort). Но у алгоритма чёт-нечёт есть серьёзное преимущество - если данные уже разбиты на блоки и адреса блоков известны в DHT то для сортировки каждой ноде необходимо только 2 блока. Таким образом ноде не нужен доступ ко всему массиву и это позволяет сортировать большие объёмы данных. Главное побить их по блокам. В предложенном скрипте задаётся один массив данных и внутри скрипта он бьётся на блоки. Это сделано для простоты и для того чтобы показать саму идею. При необходимости скрипт легко можно доработать.
В скрипте есть комментарии к каждой части, поэтому больше информации можно получить посмотрев скрипт.

Заключение
------------
Задание выполнялось и тестировалось под Windows я искренне верю в Java мультиплатформеность и надеюсь что у вас тоже всё заработает :). Спасибо за интересное задание.