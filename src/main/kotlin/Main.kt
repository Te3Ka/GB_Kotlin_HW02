package org.example

//TIP За основу берём код решения домашнего задания из предыдущего семинара и дорабатываем его.
//
//— Создайте иерархию sealed классов, которые представляют собой команды. В корне иерархии интерфейс Command.
//
//— В каждом классе иерархии должна быть функция isValid(): Boolean,
// которая возвращает true, если команда введена с корректными аргументами.
// Проверку телефона и email нужно перенести в эту функцию.
//
//— Напишите функцию readCommand(): Command, которая читает команду из текстового ввода,
// распознаёт её и возвращает один из классов-наследников Command, соответствующий введённой команде.
//
//— Создайте data класс Person, который представляет собой запись о человеке. Этот класс должен содержать поля:
//name – имя человека
//phone – номер телефона
//email – адрес электронной почты
//
//— Добавьте новую команду show, которая выводит последнее значение, введённой с помощью команды add.
// Для этого значение должно быть сохранено в переменную типа Person.
// Если на момент выполнения команды show не было ничего введено, нужно вывести на экран сообщение “Not initialized”.
//
//— Функция main должна выглядеть следующем образом. Для каждой команды от пользователя:
//Читаем команду с помощью функции readCommand
//Выводим на экран получившийся экземпляр Command
//Если isValid для команды возвращает false, выводим help.
//Если true, обрабатываем команду внутри when.

data class Person(
    val name: String,
    val phone: String,
    val email: String
)

var lastPerson: Person? = null

sealed class Command {
    abstract fun isValid(): Boolean

    data class Help(val message: String) : Command() {
        override fun isValid(): Boolean = true
    }

    data class AddPhoneNumber(val userName: String, val phoneNumber: String) : Command() {
        override fun isValid(): Boolean {
            return phoneNumber.contains('+') && phoneNumber.toCharArray().size == 12
        }
    }

    data class AddEmailAddress(val userName: String, val emailAddress: String) : Command() {
        override fun isValid(): Boolean {
            return emailAddress.contains('@') && emailAddress.contains('.') &&
                    emailAddress.split('@', '.').size == 3
        }
    }

    data object Exit : Command(){
        override fun isValid(): Boolean = true
    }

    data object Show : Command() {
        override fun isValid(): Boolean = true
    }
}

fun readCommand(userInput: String): Command {
    val parts = userInput.split(" ")
    return when {
        userInput == "help" -> Command.Help(
            "В командах add вместо userName необходимо вводить 1 имя.\n" +
                    "Телефон должен быть в формате: +70000000000, без пробелов и других знаков, кроме ' + '.\n" +
                    "Email должен быть формата userName@example.ru, без пробелов и лишних знаков ' @ ' и ' . '"
        )
        userInput == "exit" -> Command.Exit
        userInput == "show" -> Command.Show
        parts.size == 4 && (parts[2] == "phone" || parts[2] == "email") -> {
            if (parts[2] == "phone") {
                if (Command.AddPhoneNumber(parts[1], parts[3]).isValid()) {
                    lastPerson = Person(parts[1], parts[3], "")
                    Command.AddPhoneNumber(parts[1], parts[3])
                } else {
                    Command.Help("Неверная команда." +
                            "Телефон должен быть в формате: +70000000000, без пробелов и других знаков, кроме ' + '.")
                }
            } else {
                if (Command.AddEmailAddress(parts[1], parts[3]).isValid()) {
                    lastPerson = Person(parts[1],  "", parts[3])
                    Command.AddEmailAddress(parts[1], parts[3])
                } else {
                    Command.Help("Неверная команда." +
                            "Email должен быть формата userName@example.ru, без пробелов и лишних знаков ' @ ' и ' . '")
                }
            }
        }
        else -> throw IllegalArgumentException("Неверная команда!")
    }
}

fun main() {
    var userInput: String = ""

    while (userInput != "exit") {
        println("Введите команду:")
        println("-- help - помощь по программе.")
        println("-- show - показывает последнюю исполненную команду add.")
        println("-- add userName phone numberPhone - добавить пользователю номер телефона.")
        println("-- add userName email emailAddress - добавить пользователю адрес электронной почты.")
        println("-- exit - выход из программы.")
        print(">>: ")
        userInput = readlnOrNull().toString();

        try {
            when (val command = readCommand(userInput)) {
                is Command.Show -> {
                    if (lastPerson != null) {
                        println("Последняя команда: $lastPerson")
                    } else {
                        println("Не инициализировано")
                    }
                }
                else -> println("Команда: $command")
            }
        } catch (e: IllegalArgumentException) {
            println(e.message)
        }
    }
}