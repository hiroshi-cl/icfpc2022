package isl

import isl.antlr4.{ISLLexer, ISLParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}

import javax.imageio.ImageIO

object Main {
  val input0 =
    """cut[0][1,40]
      |cut[0.1.3][x][60]
      |color[0][0,0,0,0]
      |#gsakkkk""".stripMargin

  val input1 =
    """cut [0] [x] [220]
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
    println("====")
    val result = ISLInterpreter.execute(
      initialState = State(
        cost = 0,
        globalCounter = 1,
        blocks = Map(BlockId(Seq(0)) -> Block.SimpleBlock(Point(0, 0), Point(400, 400), Color(0, 0, 0, 0))),
        area = Point(400, 400)
      ),
      moves = adt.moves
    )
    println(result)
    val imageScore = ImageScore.calc(result.blocks.values.toSeq, ImageIO.read(new java.io.File("../problems/1.png")))
    println(Seq(result.cost, imageScore, result.cost + imageScore))
  }
}
