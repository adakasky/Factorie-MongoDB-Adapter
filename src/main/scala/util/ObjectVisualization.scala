package util

import java.io.PrintWriter

import adapter.AdapterOptions
import com.mongodb.{DB, DBCollection, DBCursor, MongoClient}
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

import scala.io.Source

/**
  * Created by Robert on 2016/9/22.
  */
//  mvn exec:java -Dexec.mainClass="util.ObjectVisualization" -Dexec.args="--inputDB predsynth --input-collection annotated_paragraphs --port-num 27017 --port-name localhost"
//  mvn exec:java -Dexec.mainClass="util.ObjectVisualization" -Dexec.args="--inputDB predsynth --input-collection annotated_papers --port-num 27017 --port-name localhost"
object ObjectVisualization {
    def main(args: Array[String]): Unit = {
        val opts = new AdapterOptions
        opts.parse(args)
        assert(opts.inputDB.wasInvoked || opts.inputCollection.wasInvoked)

        // set the port to use
        val mongo: MongoClient = new MongoClient(opts.portName.value, opts.portNum.value.toInt)

        // set the DB and Collection of input and output files
        val inputDB: DB = mongo.getDB(opts.inputDB.value)
        val inputCollection: DBCollection = inputDB.getCollection(opts.inputCollection.value)

        val writer: PrintWriter = new PrintWriter(s"../${inputCollection.getName}.json", "UTF-8")
        val cursor: DBCursor = inputCollection.find
        val parser: JSONParser = new JSONParser
        while (cursor.hasNext) {
            val next: JSONObject = parser.parse(cursor.next.toString).asInstanceOf[JSONObject]
            next.writeJSONString(writer)
            writer.write("\n")
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