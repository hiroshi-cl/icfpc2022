package isl

import isl.antlr4.{ISLLexer, ISLParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}

object Main {
  def main(args: Array[String]): Unit = {
    val lexer = new ISLLexer(CharStreams.fromString(
      """cut[0][1,40]
        |cut[0.1.3][x][60]
        |color[0][0,0,0,0]
        |#gsakkkk""".stripMargin))
        println(lexer.getRuleNames)
    val parser = new ISLParser(new CommonTokenStream(lexer))
//    println(parser.program())
    println(ISLTreeToADTVisitor.visitProgram(parser.program()))
  }
}
