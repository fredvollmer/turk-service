package io.torchbearer.turkservice.filters

import io.torchbearer.ServiceCore.tyoes.Rectangle

/**
  * Created by fredvollmer on 1/22/17.
  */

trait Filter {
  def runFilter(): List[Rectangle]
}
