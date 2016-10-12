package util

import java.io.PrintWriter

import adapter.AdapterOptions
import com.mongodb.{BasicDBList, MongoClient}

/**
  * Created by Robert on 2016/10/11.
  */
//  mvn exec:java -Dexec.mainClass="util.ObjectVisualization" -Dexec.args="--inputDB predsynth --input-collection chemical_db --port-num 27017 --port-name localhost"
class DownloadChemicalDB {
    def main(args: Array[String]): Unit = {
        val opts = new AdapterOptions
        opts.parse(args)
        assert(opts.inputDB.wasInvoked || opts.inputCollection.wasInvoked)

        // set the port to use
        val mongo = new MongoClient(opts.portName.value, opts.portNum.value.toInt)

        // set the DB and Collection of input and output files
        val inputDB = mongo.getDB(opts.inputDB.value)
        val inputCollection = inputDB.getCollection(opts.inputCollection.value)

        val writer = new PrintWriter("../chemical_db.txt", "UTF-8")
        val cursor = inputCollection.find
        while (cursor.hasNext) {
            val next = cursor.next.toMap
            writer.write(next.get("_id") + "\n")
            val names = next.get("names").asInstanceOf[BasicDBList].toArray
            names.foreach(name => writer.write(name + "\n"))
            writer.write("\n")
        }
        writer.flush()
        writer.close()
        mongo.close()
    }
}
