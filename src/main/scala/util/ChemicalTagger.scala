package util

import java.io.File

import uk.ac.cam.ch.wwmm.chemicaltagger.POSContainer
import uk.ac.cam.ch.wwmm.chemicaltagger.ChemistryPOSTagger
import uk.ac.cam.ch.wwmm.chemicaltagger.ChemistrySentenceParser
import uk.ac.cam.ch.wwmm.chemicaltagger.Utils
import nu.xom.Document

import scala.io.Source

/**
  * Created by Robert on 2016/10/18.
  */
// mvn exec:java -Dexec.mainClass="util.ChemicalTagger"
object ChemicalTagger {
    def main(args: Array[String]): Unit = {
        val input: String = "../1.txt"
        val dir: File = new File(input.stripSuffix(".txt"))
        if (!dir.exists())
            dir.mkdirs()

        Source.fromFile(input, "utf-8").getLines.zipWithIndex.foreach(line => {
            val content: String = line._1
            val index: Int = line._2

            // Calling ChemistryPOSTagger
            val posContainer: POSContainer = ChemistryPOSTagger.getDefaultInstance.runTaggers(content)

            // Returns a string of TAG TOKEN format (e.g.: DT The NN cat VB sat IN on DT the NN matt)
            // Call ChemistrySentenceParser either by passing the POSContainer or by InputStream
            val chemistrySentenceParser: ChemistrySentenceParser = new ChemistrySentenceParser(posContainer)

            // Create a parseTree of the tagged input
            chemistrySentenceParser.parseTags()

            // Return an XMLDoc
            val doc: Document = chemistrySentenceParser.makeXMLDocument()

            Utils.writeXMLToFile(doc, s"${input.stripSuffix(".txt")}/tagged_1_${index + 1}_chemical_tagger.xml")
        })
    }
}
