package adapter

import com.mongodb.MongoClient

import scala.io._
import cc.factorie.app.nlp.lemma.WordNetLemmatizer
import cc.factorie.app.nlp.{DocumentAnnotationPipeline, Document}
import cc.factorie.app.nlp.pos.OntonotesForwardPosTagger
import cc.factorie.app.nlp.segment.{DeterministicSentenceSegmenter, DeterministicNormalizingTokenizer}

object Adapter{
    def main(args: Array[String]) {
        val mongoClient = new MongoClient("localhost", 27972)
        val mongoDB = mongoClient.getDB("mongocubbie-test")
        print(mongoDB.getName + "\n")


        val file = Source.fromFile("src/main/resources/input.json")
        val doc = new Document(file.mkString)
        val annotators = Seq(DeterministicNormalizingTokenizer, DeterministicSentenceSegmenter, OntonotesForwardPosTagger, WordNetLemmatizer)
        val pipeline = new DocumentAnnotationPipeline(annotators)

        pipeline.process(doc)
        println(s"sentences: ${doc.sentenceCount} tokens: ${doc.tokenCount}")
        doc.sentences.foreach{s =>
            s.tokens.foreach{t =>
                println(s"${t.positionInSentence}\t${t.string}\t${t.posTag.categoryValue}\t${t.lemma.lemma}")
            }
        }
    }
}

object InputFromFile extends App{

}
