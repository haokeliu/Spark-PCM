package main

import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.util.AccumulatorV2

class VectorSum extends AccumulatorV2 [Vector,Vector]{
  private var res = Vectors.zeros(15000)

  override def isZero: Boolean = {
    val a = res.toArray
    var b = false
    for (i <- 0 until(a.length)){
      if (a(i) == 0)
        b = true
    }
    b
  }

  override def copy(): AccumulatorV2[Vector, Vector] = {
    val newAcc = new VectorSum
    newAcc.res = this.res
    newAcc
  }

  override def reset(): Unit = null

  override def add(v: Vector): Unit = {

    var a = v.toArray
    val b = res.toArray
    for (i <- 0 until(a.size)){
      a(i) = a(i) + b(i)
    }
    res = Vectors.dense(a)
  }

  override def merge(other: AccumulatorV2[Vector, Vector]): Unit = {
    var a = this.res.toArray
    val b = other.value.toArray
    for (i <- 0 until(other.value.size)){
      a(i) = a(i)+b(i)
    }
    res
  }

  override def value: Vector = res
}