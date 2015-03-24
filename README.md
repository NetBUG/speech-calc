# speech-calc
Speech calculator

Developed as a project for Natural Language Technologies course by Anatole Gershman, 2015

The typical calculator request is spoken in a form mathematical expressions. It's also very important that word order usually follows the order of evaluation:
“five minus two” → 5-2
“square root of nine” → sqrt(9)
“one thoursand seven hundred and eleven divided by three” → 1711 / 3
However, some changes may occur:
“five plus seven multiplied by three” → (5+7)*3
“square root of five plus three point one” → sqrt(5+3.1)
“subtract twenty five from ninety” → 90 – 25

The following operators have been implemented:
addition ('plus', 'плюс', 'at')
subtraction ('minus', 'минус')
multiplication('multiplied', 'times', 'умножить')
division('divi*', 'over', 'дел*')

The following functions (unary and binary) have been implemented:
addition ('add', 'сложить', 'at')
subtraction ('subtract', 'вычесть')
multiplication('multiply', 'умножить')
division('divi*', 'дел*')
square root ('root', 'корень')
logarithm('logarithm', 'логарифм')
sine('sine', 'sign', 'синус')
cosine('cosine', 'косинус')
tangent('tangent', 'тангенс')