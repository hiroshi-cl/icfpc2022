package isl

import isl.antlr4.{ISLLexer, ISLParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}

object Main {
  val input0 =
    """cut[0][1,40]
      |cut[0.1.3][x][60]
      |color[0][0,0,0,0]
      |#gsakkkk""".stripMargin

  val input1 = """cut [0] [x] [220]
                 |cut [0.0] [76, 150]
                 |
                 |cut [0.0.1] [y] [102]
                 |cut [0.0.1.0] [x] [118]
                 |
                 |cut [0.0.3] [x] [35]
                 |color [0.0.3.1] [120, 72, 229, 159]""".stripMargin

  def main(args: Array[String]): Unit = {
    val lexer = new ISLLexer(CharStreams.fromString(input1))
    val parser = new ISLParser(new CommonTokenStream(lexer))
    //    println(parser.program())
    val adt = ISLTreeToADTVisitor.visitProgram(parser.program())
    println(adt)
    println(ISLPrinter.print(adt))
  }
}
