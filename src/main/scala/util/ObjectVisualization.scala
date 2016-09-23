package util

import java.io.PrintWriter

import adapter.AdapterOptions
import com.mongodb.MongoClient

/**
  * Created by Robert on 2016/9/22.
  */
//  mvn exec:java -Dexec.mainClass="util.ObjectVisualization" -Dexec.args="--inputDB predsynth --input-collection annotated_paragraphs --port-num 27017 --port-name localhost"
object ObjectVisualization {
    def main(args: Array[String]): Unit = {
        val opts = new AdapterOptions
        opts.parse(args)
        assert(opts.inputDB.wasInvoked || opts.inputCollection.wasInvoked)

        // set the port to use
        val mongo = new MongoClient(opts.portName.value, opts.portNum.value.toInt)

        // set the DB and Collection of input and output files
        val inputDB = mongo.getDB(opts.inputDB.value)
        val inputCollection = inputDB.getCollection(opts.inputCollection.value)

        val writer = new PrintWriter("../annotated_paragraphs.txt", "UTF-8")
        val cursor = inputCollection.find
        while (cursor.hasNext) {
            val next = cursor.next.toMap
            writer.write(next.get("text") + "\n")
        }
        writer.flush()
        writer.close()
        mongo.close()
    }
}
