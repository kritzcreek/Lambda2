package lambda

import java.io.File
import java.nio.file.*;

fun runFile(file: File) {
    file.readText().split(";").filter { it.isNotBlank() }.forEach {
            try {
                val expr = Parser(Lexer(it)).parseExpression()
                println("Evaluating: ${expr.pretty()}")
                val result = evalExpr(expr)
                println(result.pretty())
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }
}

fun main() {
    runFile(File("examples.lam2"))
    val watchService = FileSystems.getDefault().newWatchService()
    val path = Paths.get(".")
    val watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)
    while (true) {
        val key = watchService.take() ?: break
        for (event in key.pollEvents()) {
            val changed = event.context() as Path
            if (changed.endsWith("examples.lam2")) {
                runFile(changed.toFile())
            }
        }
        key.reset()
    }
}