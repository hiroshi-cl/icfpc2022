package isl

object ISLInterpreter {
  def execute(initialState: State, moves: Seq[Move]): State =
    moves.foldLeft(initialState)(go)

  def go(state: State, move: Move): State =
    move match {
      case Move.PcutMove(id, point) =>
        val block = state.blocks(id)
        val cost = state.cost + calcCost(10.0, state, block)
        val newBlocks = block match {
          case Block.SimpleBlock(bottomLeft, topRight, color) =>
            Seq(
              BlockId(id.ids :+ 0) -> Block.SimpleBlock(bottomLeft, point, color),
              BlockId(id.ids :+ 1) -> Block.SimpleBlock(Point(point.x, bottomLeft.y), Point(topRight.x, point.y), color),
              BlockId(id.ids :+ 2) -> Block.SimpleBlock(point, topRight, color),
              BlockId(id.ids :+ 3) -> Block.SimpleBlock(Point(bottomLeft.x, point.y), Point(point.x, topRight.y), color)
            )
          case Block.ComplexBlock(bottomLeft, topRight, children) =>
            Seq(
              BlockId(id.ids :+ 0) ->
                Block.ComplexBlock(bottomLeft, point,
                  children
                    .filter(b => b.bottomLeft.x < point.x && b.bottomLeft.y < point.y)
                    .map(b => Block.SimpleBlock(
                      b.bottomLeft,
                      Point(b.topRight.x.min(point.x), b.topRight.y.min(point.y)),
                      b.color
                    ))
                ),
              BlockId(id.ids :+ 1) ->
                Block.ComplexBlock(Point(point.x, bottomLeft.y), Point(topRight.x, point.y),
                  children
                    .filter(b => b.topRight.x > point.x && b.bottomLeft.y < point.y)
                    .map(b => Block.SimpleBlock(
                      Point(b.bottomLeft.x.max(point.x), b.bottomLeft.y),
                      Point(b.topRight.x, b.topRight.y.min(point.y)),
                      b.color
                    ))
                ),
              BlockId(id.ids :+ 2) ->
                Block.ComplexBlock(point, topRight,
                  children.filter(b => b.topRight.x > point.x && b.topRight.y > point.y)
                    .map(b => Block.SimpleBlock(
                      Point(b.topRight.x.max(point.x), b.topRight.y.max(point.y)),
                      b.topRight,
                      b.color
                    ))),
              BlockId(id.ids :+ 3) ->
                Block.ComplexBlock(Point(bottomLeft.x, point.y), Point(point.x, topRight.y),
                  children
                    .filter(b => b.bottomLeft.x < point.x && b.topRight.y > point.y)
                    .map(b => Block.SimpleBlock(
                      Point(b.bottomLeft.x, b.topRight.y.max(point.y)),
                      Point(b.topRight.x.min(point.x), b.topRight.y),
                      b.color
                    ))
                )
            )
        }

        state.copy(cost = cost, blocks = state.blocks - id ++ newBlocks)

      case Move.LcutMove(id, OrientationType.Vertical, lineNumber) =>
        val block = state.blocks(id)
        val cost = state.cost + calcCost(7.0, state, block)
        val newBlocks = block match {
          case Block.SimpleBlock(bottomLeft, topRight, color) =>
            Seq(
              BlockId(id.ids :+ 0) -> Block.SimpleBlock(bottomLeft, Point(lineNumber, topRight.y), color),
              BlockId(id.ids :+ 1) -> Block.SimpleBlock(Point(lineNumber, bottomLeft.y), topRight, color)
            )
          case Block.ComplexBlock(bottomLeft, topRight, children) =>
            Seq(
              BlockId(id.ids :+ 0) ->
                Block.ComplexBlock(bottomLeft, Point(lineNumber, topRight.y),
                  children
                    .filter(b => b.bottomLeft.x < lineNumber)
                    .map(b => Block.SimpleBlock(
                      b.bottomLeft,
                      Point(b.topRight.x.min(lineNumber), b.topRight.y),
                      b.color
                    ))
                ),
              BlockId(id.ids :+ 1) ->
                Block.ComplexBlock(Point(lineNumber, bottomLeft.y), topRight,
                  children
                    .filter(b => b.topRight.x > lineNumber)
                    .map(b => Block.SimpleBlock(
                      Point(b.bottomLeft.x.max(lineNumber), b.bottomLeft.y),
                      b.topRight,
                      b.color
                    ))
                )
            )
        }

        state.copy(cost = cost, blocks = state.blocks - id ++ newBlocks)

      case Move.LcutMove(id, OrientationType.Horizontal, lineNumber) =>
        val block = state.blocks(id)
        val cost = state.cost + calcCost(7.0, state, block)
        val newBlocks = block match {
          case Block.SimpleBlock(bottomLeft, topRight, color) =>
            Seq(
              BlockId(id.ids :+ 0) -> Block.SimpleBlock(bottomLeft, Point(topRight.x, lineNumber), color),
              BlockId(id.ids :+ 1) -> Block.SimpleBlock(Point(bottomLeft.x, lineNumber), topRight, color)
            )
          case Block.ComplexBlock(bottomLeft, topRight, children) =>
            Seq(
              BlockId(id.ids :+ 0) ->
                Block.ComplexBlock(bottomLeft, Point(topRight.x, lineNumber),
                  children
                    .filter(b => b.bottomLeft.y < lineNumber)
                    .map(b => Block.SimpleBlock(
                      b.bottomLeft,
                      Point(b.topRight.x, b.topRight.y.min(lineNumber)),
                      b.color
                    ))
                ),
              BlockId(id.ids :+ 1) ->
                Block.ComplexBlock(Point(bottomLeft.x, lineNumber), topRight,
                  children
                    .filter(b => b.topRight.y > lineNumber)
                    .map(b => Block.SimpleBlock(
                      Point(b.bottomLeft.x, b.bottomLeft.y.max(lineNumber)),
                      b.topRight,
                      b.color
                    ))
                )
            )
        }

        state.copy(cost = cost, blocks = state.blocks - id ++ newBlocks)

      case Move.ColorMove(id, color) =>
        val block = state.blocks(id)
        val cost = state.cost + calcCost(5.0, state, block)
        val newBlock = block match {
          case b@Block.SimpleBlock(_, _, _) => b.copy(color = color)
          case Block.ComplexBlock(bottomLeft, topRight, children) =>
            Block.SimpleBlock(bottomLeft, topRight, color)
        }

        state.copy(cost = cost, blocks = state.blocks.updated(id, newBlock))

      case Move.SwapMove(id0, id1) =>
        val block0 = state.blocks(id0)
        val block1 = state.blocks(id1)
        val bottomLeft = Point(block0.bottomLeft.x.min(block1.bottomLeft.x), block0.bottomLeft.y.min(block1.bottomLeft.y))
        val topRight = Point(block0.bottomLeft.x.max(block1.bottomLeft.x), block0.bottomLeft.y.max(block1.bottomLeft.y))
        val cost = state.cost + calcCost(3.0, state, Block.SimpleBlock(bottomLeft, topRight, Color(0, 0, 0, 0)))
        val move =
          if (block0.bottomLeft == bottomLeft)
            Point(block1.bottomLeft.x - bottomLeft.x, block1.bottomLeft.y - bottomLeft.y)
          else
            Point(bottomLeft.x - block0.bottomLeft.x, bottomLeft.y - block0.bottomLeft.y)
        val newBlock0 =
          block0 match {
            case Block.SimpleBlock(bottomLeft, topRight, color) =>
              Block.SimpleBlock(
                Point(bottomLeft.x + move.x, bottomLeft.y + move.y),
                Point(topRight.x + move.x, topRight.y + move.y),
                color
              )
            case Block.ComplexBlock(bottomLeft, topRight, children) =>
              Block.ComplexBlock(
                Point(bottomLeft.x + move.x, bottomLeft.y + move.y),
                Point(topRight.x + move.x, topRight.y + move.y),
                children.map(b => Block.SimpleBlock(
                  Point(b.bottomLeft.x + move.x, b.bottomLeft.y + move.y),
                  Point(b.topRight.x + move.x, b.topRight.y + move.y),
                  b.color
                ))
              )
          }
        val newBlock1 =
          block1 match {
            case Block.SimpleBlock(bottomLeft, topRight, color) =>
              Block.SimpleBlock(
                Point(bottomLeft.x - move.x, bottomLeft.y - move.y),
                Point(topRight.x - move.x, topRight.y - move.y),
                color
              )
            case Block.ComplexBlock(bottomLeft, topRight, children) =>
              Block.ComplexBlock(
                Point(bottomLeft.x - move.x, bottomLeft.y - move.y),
                Point(topRight.x - move.x, topRight.y - move.y),
                children.map(b => Block.SimpleBlock(
                  Point(b.bottomLeft.x - move.x, b.bottomLeft.y - move.y),
                  Point(b.topRight.x - move.x, b.topRight.y - move.y),
                  b.color
                ))
              )
          }
        state.copy(cost = cost, blocks = state.blocks.updated(id0, newBlock0).updated(id1, newBlock1))

      case Move.MergeMove(id0, id1) =>
        val block0 = state.blocks(id0)
        val block1 = state.blocks(id1)
        val bottomLeft = Point(block0.bottomLeft.x.min(block1.bottomLeft.x), block0.bottomLeft.y.min(block1.bottomLeft.y))
        val topRight = Point(block0.bottomLeft.x.max(block1.bottomLeft.x), block0.bottomLeft.y.max(block1.bottomLeft.y))
        val newBlock = Block.ComplexBlock(bottomLeft, topRight, simpleBlocks(block0) ++ simpleBlocks(block1))
        val cost = state.cost + calcCost(1.0, state, newBlock)
        state.copy(
          cost = cost,
          globalCounter = state.globalCounter + 1,
          blocks = state.blocks - id0 - id1 + (BlockId(Seq(state.globalCounter)) -> newBlock)
        )
    }

  private def simpleBlocks(block: Block): Seq[Block.SimpleBlock] =
    block match {
      case b@Block.SimpleBlock(_, _, _) => Seq(b)
      case Block.ComplexBlock(_, _, children) => children
    }

  private def calcCost(baseCost: Double, state: State, block: Block): Long =
    Math.round(
      baseCost
        * state.area.x * state.area.y
        / (block.topRight.x - block.bottomLeft.x) * (block.topRight.y - block.bottomLeft.y)
    ).toLong
}

enum Block:
  def bottomLeft: Point

  def topRight: Point

  case SimpleBlock(bottomLeft: Point, topRight: Point, color: Color) extends Block
  case ComplexBlock(bottomLeft: Point, topRight: Point, children: Seq[SimpleBlock]) extends Block

case class State(cost: Long, globalCounter: Int, blocks: Map[BlockId, Block], area: Point)
