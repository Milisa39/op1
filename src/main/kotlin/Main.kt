import java.io.File
import java.io.InputStreamReader
import org.mozilla.universalchardet.UniversalDetector

fun detectEncoding(file: File): String {
    val buffer = ByteArray(4096)
    val detector = UniversalDetector(null)

    file.inputStream().use { input ->
        var nread: Int
        while (input.read(buffer).also { nread = it } > 0 && !detector.isDone) {
            detector.handleData(buffer, 0, nread)
        }
        detector.dataEnd()
    }

    return detector.detectedCharset ?: "UTF-8"
}

fun readFileWithDetectedEncoding(file: File): String {
    val charsetName = detectEncoding(file)
    println("Обнаружена кодировка: $charsetName")

    return file.inputStream().bufferedReader(charset(charsetName)).use { it.readText() }
}

fun main() {
    val file = File("путь_к_вашему_файлу.txt")
    if (!file.exists()) {
        println("Файл не найден!")
        return
    }

    try {
        val text = readFileWithDetectedEncoding(file)
        println("Содержимое файла:\n$text")
    } catch (e: Exception) {
        println("Ошибка при чтении файла: ${e.message}")
    }
}
