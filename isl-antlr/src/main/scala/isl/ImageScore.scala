package isl

import java.awt.image.BufferedImage

object ImageScore {
  def calc(blocks: Seq[Block], image: BufferedImage): Long =
    Math.round(blocks.map {
      case Block.SimpleBlock(bottomLeft, topRight, color) => {
        for
          x <- bottomLeft.x until topRight.x
          y <- bottomLeft.y until topRight.y
        yield
          val argb = image.getRGB(x, y)
          val a = color.a - (argb >> 24)
          val r = color.r - ((argb >> 16) & 255)
          val g = color.g - ((argb >> 8) & 255)
          val b = color.b - (argb & 255)
          Math.hypot(Math.hypot(a, r), Math.hypot(g, b))
      }.sum
      case Block.ComplexBlock(_, _, children) =>
        children.map {
          case Block.SimpleBlock(bottomLeft, topRight, color) => {
            for
              x <- bottomLeft.x until topRight.x
              y <- bottomLeft.y until topRight.y
            yield
              val argb = image.getRGB(x, y)
              val a = color.a - (argb >> 24)
              val r = color.r - ((argb >> 16) & 255)
              val g = color.g - ((argb >> 8) & 255)
              val b = color.b - (argb & 255)
              Math.hypot(Math.hypot(a, r), Math.hypot(g, b))
          }.sum
        }.sum
    }.sum * 0.005).toLong
}
