package util

import java.io.PrintWriter

import adapter.AdapterOptions
import cc.factorie.app.nlp.segment.{DeterministicNormalizingTokenizer, DeterministicSentenceSegmenter}
import cc.factorie.app.nlp.{Document, DocumentAnnotatorPipeline}
import com.mongodb.MongoClient

import scala.io.Source

/**
  * Created by Robert on 2016/9/16.
  */

//  mvn exec:java -Dexec.mainClass="util.NonVocWordFinder" -Dexec.args="--inputDB predsynth --input-collection parsed_papers --port-num 27017 --port-name localhost"
object NonVocWordFinder {
    def main(args: Array[String]): Unit = {
        val opts = new AdapterOptions
        opts.parse(args)
        assert(opts.inputDB.wasInvoked || opts.inputCollection.wasInvoked)

        // set the port to use
        val mongo = new MongoClient(opts.portName.value, opts.portNum.value.toInt)

        // set the DB and Collection of input and output files
        val inputDB = mongo.getDB(opts.inputDB.value)
        val inputCollection = inputDB.getCollection(opts.inputCollection.value)

        val annotator = DocumentAnnotatorPipeline(DeterministicNormalizingTokenizer, DeterministicSentenceSegmenter)

        // get the set of news wired vocabulary
        val vocabulary: Set[String] = Source.fromFile("../vocabulary.txt", "UTF-8").getLines.toSet
        var tokenInPapers = Set[String]()

        // iterate through all the documents to retrieve all the words
        val cursor = inputCollection.find
        while (cursor.hasNext) {
            val next = cursor.next.toMap
            val doc = new Document(next.get("string").toString)

            annotator.process(doc)

            for (token <- doc.tokens)
                tokenInPapers += token.string
        }

        // output all the words not in vocabulary into non_voc_word.txt
        val writer = new PrintWriter("../non_voc_word.txt", "UTF-8")
        for (word <- tokenInPapers &~ vocabulary)
            writer.write(word + "\n")
        writer.flush()
        writer.close()
        mongo.close()
    }
}
