package isl

import isl.antlr4.{ISLBaseVisitor, ISLParser}

import scala.annotation.tailrec

object ISLTreeToADTVisitor {

  def visitProgram(ctx: ISLParser.ProgramContext): Program = {
    val builder = Seq.newBuilder[Move]
    var current = ctx
    while (current != null) {
      visitProgram_line(current.program_line()) match {
        case ProgramLine.MoveProgramLine(m) => builder += m
        case _ =>
      }
      current = current.program()
    }
    Program(builder.result())
  }

  private def visitProgram_line(ctx: ISLParser.Program_lineContext): ProgramLine =
    (ctx.move(), ctx.COMMENT()) match {
      case (c, null) => ProgramLine.MoveProgramLine(visitMove(c))
      case (null, c) => ProgramLine.None
    }

  private def visitMove(ctx: ISLParser.MoveContext): Move =
    (ctx.lcut_move(), ctx.pcut_move(), ctx.color_move(), ctx.swap_move(), ctx.merge_move()) match {
      case (c, null, null, null, null) => visitLcut_move(c)
      case (null, c, null, null, null) => visitPcut_move(c)
      case (null, null, c, null, null) => visitColor_move(c)
      case (null, null, null, c, null) => visitSwap_move(c)
      case (null, null, null, null, c) => visitMerge_move(c)
    }

  private def visitPcut_move(ctx: ISLParser.Pcut_moveContext): Move =
    Move.PcutMove(visitBlock(ctx.block()), visitPoint(ctx.point()))

  private def visitLcut_move(ctx: ISLParser.Lcut_moveContext): Move =
    Move.LcutMove(visitBlock(ctx.block()), visitOrientation(ctx.orientation()), ctx.line_number().NUMBER().getText.toInt)

  private def visitColor_move(ctx: ISLParser.Color_moveContext): Move =
    Move.ColorMove(visitBlock(ctx.block()), visitColor(ctx.color()))

  private def visitSwap_move(ctx: ISLParser.Swap_moveContext): Move =
    Move.SwapMove(visitBlock(ctx.block(0)), visitBlock(ctx.block(1)))

  private def visitMerge_move(ctx: ISLParser.Merge_moveContext): Move =
    Move.MergeMove(visitBlock(ctx.block(0)), visitBlock(ctx.block(1)))

  private def visitOrientation(ctx: ISLParser.OrientationContext): OrientationType =
    visitOrientation_type(ctx.orientation_type())

  private def visitOrientation_type(ctx: ISLParser.Orientation_typeContext): OrientationType =
    (ctx.vertical(), ctx.horizontal()) match {
      case (c, null) => OrientationType.Vertical
      case (null, c) => OrientationType.Horizontal
    }

  private def visitBlock(ctx: ISLParser.BlockContext): BlockId =
    visitBlock_id(ctx.block_id())

  private def visitPoint(ctx: ISLParser.PointContext): Point =
    Point(
      x = ctx.NUMBER(0).getText.toInt,
      y = ctx.NUMBER(1).getText.toInt
    )

  private def visitColor(ctx: ISLParser.ColorContext): Color =
    Color(
      r = ctx.NUMBER(0).getText.toInt,
      g = ctx.NUMBER(1).getText.toInt,
      b = ctx.NUMBER(2).getText.toInt,
      a = ctx.NUMBER(3).getText.toInt
    )

  private def visitBlock_id(ctx: ISLParser.Block_idContext): BlockId ={
    val builder = Seq.newBuilder[Int]
    var current = ctx
    while (current != null) {
      builder += current.NUMBER().getText.toInt
      current = current.block_id()
    }
    BlockId(builder.result())
  }
}
