package isl

sealed trait ISL

case class BlockId(ids: Seq[Int]) extends ISL

case class Color(r: Int, g: Int, b: Int, a: Int) extends ISL

case class Point(x: Int, y: Int) extends ISL

case class Program(moves: Seq[Move]) extends ISL

enum ProgramLine extends ISL:
  case MoveProgramLine(move: Move)
  case Comment

enum Move extends ISL:
  case PcutMove(id: BlockId, point: Point)
  case LcutMove(id: BlockId, orientationType: OrientationType, lineNumber: Int)
  case ColorMove(id: BlockId, color: Color)
  case SwapMove(id0: BlockId, id1: BlockId)
  case MergeMove(id0: BlockId, id1: BlockId)

enum OrientationType extends ISL:
  case Vertical
  case Horizontal
