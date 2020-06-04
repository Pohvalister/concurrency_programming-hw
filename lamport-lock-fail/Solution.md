## 1. Некорректное исполнение
[![Build Status](https://travis-ci.com/ITMO-MPP-2017/lamport-lock-fail-Pohvalister.svg?token=B2yLGFz6qwxKVjbLm9Ak&branch=master)](https://travis-ci.com/ITMO-MPP-2017/lamport-lock-fail-Pohvalister)


## 2. Исправление алгоритма
Проблема данного алгоритма в том, между выполнением строк 3 и 4 в потоке другие потоки могут успеть запомнить значение, которое выведет их из цикла. Чтобы программа выполнялась верно, нужно, как и в основном алгоритме Лампорта блокировать условие выхода из цикла, пока не произведется вычисление label[id].

Можно убрать переменную other (а в месте с ней 7 строку и условие other == 0), переместив всю логику блокирования на label[id]. Если в коде добавить строку 0 : label[id]=0, а в 9 ой строке label[id] = 0 изменить на label[id] = inf, то условие (8 строка) if (label[k], k) > (my, id): break будет выполнять действия в точности как в алгоритме Лампорта:
label[id]=inf показывает что id пропускает данный поток - (inf,k) > (my, id) == true
label[id]=0 показывает, что id все ещё выбирает - (0, k) > (my, id) == false, т к my >= 1

в итоге код:
threadlocal int id       // 0..N-1 -- идентификатор потока
shared      int label[N] // заполненно нулями по умолчанию

def lock:
  0: label[id] = 0
  1: my = 1
  2: for k in range(N): if k != id:
  3:     my = max(my, label[k] + 1) 
  4: label[id] = my 
  5: for k in range(N): if k != id:
  6:     while true: 
  8:         if  (label[k], k) > (my, id): break@6

def unlock:
  9: label[id] = inf
