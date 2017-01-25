package io.torchbearer.turkservice.filters

import io.torchbearer.ServiceCore.tyoes.Rectangle

/**
  * Created by fredvollmer on 1/22/17.
  */
class RectangleOverlapFilter(rects: List[Rectangle]) extends Filter {
  def getLargestCluster(): List[Rectangle] = {
    rects.
  }


  override def runFilter(): List[Rectangle] = getLargestCluster()
}
