import java.io.File
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

fun main() {
    val filePath = "путь_к_вашему_файлу.txt" 
    val path = Paths.get(filePath)

    val charset = detectCharset(path) ?: Charset.forName("UTF-8") // 
    
    val content = Files.readAllLines(path, charset).joinToString("\n")
    println(content)
}

fun detectCharset(path: Path): Charset? {
    Files.newInputStream(path).use { inputStream ->
        return when (detectBOM(inputStream)) {
            BOM.UTF_8 -> Charset.forName("UTF-8")
            BOM.UTF_16BE -> Charset.forName("UTF-16BE")
            BOM.UTF_16LE -> Charset.forName("UTF-16LE")
            null -> null 
        }
    }
}


enum class BOM(val bytes: ByteArray) {
    UTF_8(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())),
    UTF_16BE(byteArrayOf(0xFE.toByte(), 0xFF.toByte())),
    UTF_16LE(byteArrayOf(0xFF.toByte(), 0xFE.toByte()))
}


fun detectBOM(inputStream: InputStream): BOM? {
    inputStream.mark(3)
    val bomBytes = ByteArray(3)
    val readBytes = inputStream.read(bomBytes)

    if (readBytes >= 3 && bomBytes.sliceArray(0..2).contentEquals(BOM.UTF_8.bytes)) {
        return BOM.UTF_8
    } else if (readBytes >= 2 && bomBytes.sliceArray(0..1).contentEquals(BOM.UTF_16BE.bytes)) {
        return BOM.UTF_16BE
    } else if (readBytes >= 2 && bomBytes.sliceArray(0..1).contentEquals(BOM.UTF_16LE.bytes)) {
        return BOM.UTF_16LE
    }

    inputStream.reset()
    return null
}
