package isl

object ISLPrinter {
  def print(program: Program): String = {
    val sb = new StringBuilder
    for
      move <- program.moves
    do
      if (!sb.isEmpty)
        sb.append('\n')

      sb.append(move match {
        case Move.PcutMove(id, point) => s"cut[${printBlockId(id)}][${point.x},${point.y}]"
        case Move.LcutMove(id, orientationType, lineNumber) => s"cut[${printBlockId(id)}][${
          if (orientationType == OrientationType.Vertical) 'X' else 'Y'
        }][$lineNumber]"
        case Move.ColorMove(id, color) => s"color[${printBlockId(id)}][${color.r},${color.g},${color.b},${color.a}]"
        case Move.SwapMove(id0, id1) => s"swap[${printBlockId(id0)}][${printBlockId(id1)}]"
        case Move.MergeMove(id0, id1) => s"merge[${printBlockId(id0)}][${printBlockId(id1)}]"
      })

    sb.toString()
  }

  private def printBlockId(blockId: BlockId): String =
    blockId.ids.mkString(".")
}
