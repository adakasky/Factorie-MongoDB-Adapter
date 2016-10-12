package util

import java.io.PrintWriter

import adapter.AdapterOptions
import com.mongodb.{BasicDBList, MongoClient}

import scala.io.Source

/**
  * Created by Robert on 2016/9/22.
  */
//  mvn exec:java -Dexec.mainClass="util.ObjectVisualization" -Dexec.args="--inputDB predsynth --input-collection annotated_papers --port-num 27017 --port-name localhost"
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

        val writer = new PrintWriter("../annotated_papers", "UTF-8")
        val cursor = inputCollection.find
        while (cursor.hasNext) {
            val next = cursor.next.toMap
            writer.write(next + "\n")
        }
        writer.flush()
        writer.close()
        mongo.close()
    }
}

object NewLineReplacing{
    def main(args: Array[String]): Unit = {
        val writer = new PrintWriter("../chemical_db_new.txt", "UTF-8")
        Source.fromFile("../chemical_db.txt").getLines.foreach(l => writer.write(l + "\n"))
        writer.flush()
        writer.close()
    }
}