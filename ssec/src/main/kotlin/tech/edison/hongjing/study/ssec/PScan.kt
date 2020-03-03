package tech.edison.hongjing.study.ssec

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger


interface FileCallback {
    fun fileFound(filepath: String)
}

@Service
class PScan {

    var state = 0

    @Async
    fun start(baseFilePath: String, backupFilePath: String) {
        val allKeptFiles = mutableMapOf<String/* digest */, String/*file path*/>()

        if (state != 0) {
            throw Exception("state is $state")
        }

        val allFilesInBaseDir = scan(baseFilePath)
        val allFilesInBackupDir = scan(backupFilePath)

        val allFilesWithDigestInBaseDir = digest(allFilesInBaseDir)
        val allFilesWithDigestInBackDir = digest(allFilesInBackupDir)

        val duplicatedFilesInBaseDir = findDuplicateFiles(allFilesWithDigestInBaseDir, allKeptFiles)
        val duplicatedFilesInBackDir = findDuplicateFiles(allFilesWithDigestInBackDir, allKeptFiles)

        // delete duplicated files in backup dir
        duplicatedFilesInBackDir.forEach {
            val filepath = it.key
            val digest = it.value

            val kept = allKeptFiles[digest]
            println("Try to delete $filepath since it is duplicated with $kept")

            deleteFile(filepath)
        }

        state = 0
    }

    private fun scan(filepath: String) : ConcurrentLinkedQueue<String> {
        var i = 0
        val allFilesInBaseDir = ConcurrentLinkedQueue<String>()
        scan(filepath, object : FileCallback {
            override fun fileFound(filepath: String) {
                i++
                println("scan: $i: $filepath")
                if (filepath.contains("/._")) {
                    println("Try to delete $filepath since it is invalid")
                    deleteFile(filepath)
                } else {
                    allFilesInBaseDir.add(filepath)
                }
            }
        })

        return allFilesInBaseDir
    }

    private fun scan(filepath: String, fileCallback: FileCallback) {
        val file = File(filepath)
        if (!file.exists()) {
            return
        }

        if (file.isFile) {
            fileCallback.fileFound(filepath)
            return
        }

        if (!file.isDirectory) {
            return
        }

        val files = file.listFiles()
        for (file2 in files) {
            val s = file2.absolutePath
            scan(s, fileCallback)
        }
    }

    private fun digest(files: ConcurrentLinkedQueue<String>) : Map<String, String> {
        val COUNT = 16
        val latch = CountDownLatch(COUNT)
        val result = ConcurrentHashMap<String, String>()

        val total = files.size
        val handledCount = AtomicInteger(0)

        for (i in 1..COUNT) {
            Thread {
                try {
                    digestLoop(files, result, total, handledCount)
                } catch (e: Exception) {
                }
                latch.countDown()
            }.start()
        }

        latch.await()

        if (!files.isEmpty()) {
            throw Exception("Exception1")
        }

        return result
    }

    private fun digestLoop(filepathQueue: ConcurrentLinkedQueue<String>, result: ConcurrentHashMap<String, String>,
                           total: Int, handledCount : AtomicInteger
                           ) {

        val globalFileBuff = ByteArray(1024 * 1024 * 10)

        while (!filepathQueue.isEmpty()) {
            val filepath = filepathQueue.remove()
            filepath ?. let {
                try {
                    val d = digestFile(it, globalFileBuff)
                    val seq = handledCount.incrementAndGet()
                    println("Digest: [$seq / $total] $it $d")
                    result.put(it, d)
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun digestFile(filepath: String, globalFileBuff: ByteArray) : String {
        val file = File(filepath)
        if (!file.isFile || !file.exists()) {
            throw Exception(filepath)
        }

        val m = MessageDigest.getInstance("MD5")
        val inputStream: InputStream = FileInputStream(file)

        val len = inputStream.read(globalFileBuff)
        if (len > 0) {
            m.update(globalFileBuff, 0, len)
            //len = inputStream.read(globalFileBuff)
        }
        inputStream.close()

        val sb = StringBuffer()
        sb.append(file.length())
        sb.append('-')

        val md5 = m.digest()
        for (b in md5) {
            val i :Int = b.toInt() and 0xff
            var hexString = Integer.toHexString(i)
            if (hexString.length < 2) {
                hexString = "0$hexString"
            }
            sb.append(hexString)
        }

        return sb.toString()
    }

    private fun findDuplicateFiles(sourceFiles: Map<String, String>,
                                   keptFiles: MutableMap<String, String>) : Map<String, String> {

        val duplicatedFiles = mutableMapOf<String, String>()

        sourceFiles.forEach {
            val filepath = it.key
            val digest = it.value

            if (keptFiles.containsKey(digest)) {
                println("duplicated file found $filepath with ${keptFiles[digest]}")
                duplicatedFiles[filepath] = digest
            } else {
                keptFiles[digest] = filepath
            }
        }

        return duplicatedFiles
    }

    fun deleteFile(filepath: String) {
        println("delete file $filepath")
        try {
            File(filepath).delete()
        } catch (e: Exception) {

        }
    }
}
