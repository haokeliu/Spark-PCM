package main

import java.util.Random

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.linalg.{Vector, _}
import org.apache.spark.mllib.linalg.distributed._

import scala.collection.mutable.ArrayBuffer
import scala.math.pow
import scala.util.control.Breaks

object fcm  {

  //---------------------------IndexedRowMatrix次幂函数---------------------------

  def distributedPow(a: IndexedRowMatrix,m : Double): IndexedRowMatrix = {

    def vectorOp(e:  IndexedRow): Array[Double] = {
      var v1 = e.vector.toArray

      for (i <- 0 until (v1.size)) {
        v1(i) = pow(v1(i),m)
      }
      v1
    }

    val ttt = a.rows.map(u => {
      val a = vectorOp(u)
      val b :Vector = Vectors.dense(a)
      new IndexedRow(u.index, b)
    })
    val bbbb = new IndexedRowMatrix(ttt)
    bbbb
  }

  //---------------------------IndexedRowMatrix除法函数---------------------------

  def distributedDevVector(a: IndexedRowMatrix, b: Vector): IndexedRowMatrix = {

    val bRows = b.toArray

    def vectorOp(e: IndexedRow): Vector = {
      var v1 = e.vector.toArray
      for (i <- 0 until (v1.size)) {
        v1(i) = v1(i) / b(i)
      }
      Vectors.dense(v1)
    }

    val ttt = a.rows.map(u=> {
      val mm = vectorOp(u)
      new IndexedRow(u.index,mm)
    })
    val bbbb = new IndexedRowMatrix(ttt)
    bbbb
  }


  //---------------------------IndexedRowMatrix点乘函数---------------------------

  def distributedDot(a: IndexedRowMatrix, b: IndexedRowMatrix): IndexedRowMatrix = {
    val aRows = a.rows.map((iV) => (iV.index, iV.vector))
    val bRows = b.rows.map((iV) => (iV.index, iV.vector))
    val joint = aRows.join(bRows)

    def vectorOp(e: (Long, (Vector, Vector))): Array[Double] = {
      var v1 = e._2._1.toArray
      val v2 = e._2._2.toArray
      for (i <- 0 until (v1.size)) {
        v1(i) = v1(i) * v2(i)
      }
      v1
    }

    val ttt = joint.map(u => {
      val a = vectorOp(u)
      val b: Vector = Vectors.dense(a)
      new IndexedRow(u._1, b)
    })
    val bbbb = new IndexedRowMatrix(ttt)
    bbbb
  }


  //---------------------------IndexedRowMatrix除法函数---------------------------

  def distributedDev(a: IndexedRowMatrix, b: IndexedRowMatrix): IndexedRowMatrix = {
    val aRows = a.rows.map((iV) => (iV.index, iV.vector))
    val bRows = b.rows.map((iV) => (iV.index, iV.vector))
    val joint = aRows.join(bRows)

    def vectorOp(e: (Long, (Vector, Vector))): Array[Double] = {
      var v1 = e._2._1.toArray
      val v2 = e._2._2.toArray
      for (i <- 0 until (v1.size)) {
        v1(i) = v1(i) / v2(i)
      }
      v1
    }

    val ttt = joint.map(u => {
      val a = vectorOp(u)
      val b: Vector = Vectors.dense(a)
      new IndexedRow(u._1, b)
    })
    val bbbb = new IndexedRowMatrix(ttt)
    bbbb
  }


  //---------------------------IndexedRowMatrix转置函数---------------------------

  def transposeIndexdRowMatrix(m:IndexedRowMatrix):IndexedRowMatrix ={
    val transposedRowsRDD = m.rows.map(x => rowToTransposedTriplet(x.vector,x.index))
      .flatMap(x => x) // now we have triplets (newRowIndex, (newColIndex, value))
      .groupByKey
      .sortByKey() // sort rows and remove row indexes
      .map(v => new IndexedRow(v._1,buildRow(v._2)))
    new IndexedRowMatrix(transposedRowsRDD)
  }

  def rowToTransposedTriplet(row: Vector, rowIndex: Long): Array[(Long, (Long, Double))] = {
    val indexedRow = row.toArray.zipWithIndex
    indexedRow.map{case (value, colIndex) => (colIndex.toLong, (rowIndex, value))}
  }

  def buildRow(rowWithIndexes: Iterable[(Long, Double)]): Vector = {
    val resArr = new Array[Double](rowWithIndexes.size)
    rowWithIndexes.foreach{case (index, value) =>
      resArr(index.toInt) = value
    }
    Vectors.dense(resArr)
  }
  //---------------------------转置函数结束---------------------------


  def main(args: Array[String]): Unit = {
    //---------------------------读取数据---------------------------

    val conf = new SparkConf().setAppName("S_FCM").setMaster("spark://192.168.1.102:7077").setJars(List("/home/zhr/IdeaProjects/final/out/artifacts/final_jar/final.jar"))
    val sc = new SparkContext(conf)

    //--------------------------- Matrix => IndexedRowMatrix ---------------------------

    def toIndexRowMatrix(m: Matrix): IndexedRowMatrix = {
      val columns = m.toArray.grouped(m.numRows)
      val rows = columns.toSeq.transpose // Skip this if you want a column-major RDD.
      val vectors = rows.map(row => new DenseVector(row.toArray))
      new IndexedRowMatrix(sc.parallelize(vectors).zipWithIndex().map((vd) => new IndexedRow(vd._2, Vectors.dense(vd._1.toArray))))
    }

    //--------------------------- End ---------------------------

    //---------------------------转化为行索引矩阵---------------------------

    val res = sc.textFile("file:///opt/iri10").map(_.split(',') //按“ ”分割
      .map(_.toDouble)) //转成Double类型
      .zipWithIndex()
      //.map(line => Vectors.dense(line)) //转化成向量存储
      .map((vd) => new IndexedRow(vd._2, Vectors.dense(vd._1))) //转化格式
    val irm = new IndexedRowMatrix(res) //建立索引行矩阵实例
    var irm_Block = irm.toBlockMatrix(2,2)

    //----------------------定义参数----------------------

    val cluster_n:Int=3;    //聚类中心数
    val data_n = irm.numRows().toInt
    val cols = irm.numCols().toInt
    val m = 2.5
    val max_iter = 300
    val min_impro = 1e-5

    var obj_f : Array[Double] = new Array[Double](max_iter)

    val raw_u  = DenseMatrix.rand(cluster_n,data_n, new Random())  // 3*150
    val raw = toIndexRowMatrix(raw_u)

    //----------------------定义累加器----------------------

    val rawSum = new VectorSum
    sc.register(rawSum,"raw_u - - Sum")

    raw.rows.foreach(e => rawSum.add(e.vector))
    val sum_raw :Vector= rawSum.value              // 3*1
    var u = distributedDevVector(raw,sum_raw)
  //  println(u.rows.collect().foreach(println(_)))      // 打印行索引矩阵方法
    var U = sc.broadcast(u)

    val loop = new Breaks
    loop.breakable{
      for (i <- 0 until max_iter){
        val mf = distributedPow(U.value,m)
        val c1_1 = mf.toBlockMatrix(2,2).multiply(irm.toBlockMatrix(2,2)).toIndexedRowMatrix()

        val mf_t_Sum = new VectorSum
        sc.register(mf_t_Sum,"mf_t - - Sum")
        val c1_21 = mf.toBlockMatrix(2,2).transpose
        c1_21.toIndexedRowMatrix().rows.foreach(e => mf_t_Sum.add(e.vector))
        val c2_1 :Array[Double]= mf_t_Sum.value.toArray

        var num = new ArrayBuffer[Double]()
        for (i <- 0 until(cols))
          for (j <- 0 until(cluster_n)){
            num += c2_1(j)
          }
        val c1_2_1 = new DenseMatrix(cluster_n,cols,num.toArray)
        val c1_2 = toIndexRowMatrix(c1_2_1)
        val center = distributedDev(c1_1,c1_2)

        val b = center.toBlockMatrix().toLocalMatrix()
        val bb = center.rows.collect()
        val b1 = DenseMatrix.ones(data_n,1)

        var oo = new ArrayBuffer[Vector]()

        for (k <-0 until(cluster_n)){
          var b2 = bb(k).vector
          var b4 = new DenseMatrix(1,cols,b2.toArray)
          var b3 = toIndexRowMatrix(b1.multiply(b4)).toBlockMatrix(2,2)
          var b5 = irm_Block.subtract(b3).toIndexedRowMatrix()
          var b6 = distributedPow(b5,2)
          var b7 = b6.toBlockMatrix().transpose.toIndexedRowMatrix()

          val abcSum = new VectorSum
          sc.register(abcSum,"abc - - Sum")
          b7.rows.foreach(e => abcSum.add(e.vector))
          val sum_abc :Vector= abcSum.value
          oo += sum_abc
        }
        var o3 = new ArrayBuffer[Double]()
        for (x <- 0 until (data_n))
          for(y <-0 until(cluster_n)){
            o3 += Math.sqrt(oo(y).toArray(x))
          }
        val o4 = new DenseMatrix(cluster_n,data_n,o3.toArray)
        val o5 = toIndexRowMatrix(o4)      //out
        val o6 = distributedDot(o5,mf)

        val o5Sum = new VectorSum
        sc.register(o5Sum,"raw_u - - Sum")

        o6.rows.foreach(e => o5Sum.add(e.vector))
        var sum_o = 0.0
        val o7 =  o5Sum.value.toArray.sum
        obj_f(i) =o7
        var tmp = distributedPow(o5,(-2/(m-1)))

        val tmpSum = new VectorSum
        sc.register(tmpSum,"raw_u - - Sum")

        tmp.rows.foreach(e => tmpSum.add(e.vector))
        val sum_tmp = tmpSum.value
        val n_U = distributedDevVector(tmp,sum_tmp)
        U.unpersist()
        U = sc.broadcast(n_U)

        println("Iteration count = " +i+" obj. fcn = " +obj_f(i))

        if (i>1){
          val o3 = obj_f(i)-obj_f(i-1)
          if (Math.abs(o3)<min_impro){
            U.value.rows.collect().foreach(println(_))
            loop.break()
          }
        }

      }
    }
    val uu = U.value.toBlockMatrix().toLocalMatrix()
    var uacc :Array[Int] = new Array[Int](15000)
    var unum :Array[Int] = new Array[Int](3)

    for (i <- 0 until(uu.numCols)){
      if (uu(0,i)>uu(1,i) && uu(0,i)>uu(2,i)){
        unum(0) = unum(0)+1
        uacc(i) = 0
      }
      if (uu(1,i)>uu(0,i) && uu(1,i)>uu(2,i)){
        unum(1) = unum(1)+1
        uacc(i) = 1
      }
      if (uu(2,i)>uu(0,i) && uu(2,i)>uu(1,i)){
        unum(2) = unum(2)+1
        uacc(i) = 2
      }
    }
    println(unum(0)+"   "+unum(1)+"   "+unum(2))

    for (nz <- 0 until(15000)){
      print(uacc(nz)+"   ")
      if (nz%50 ==0){
        println("  ")
      }
      if (nz %150 ==0){
        println("----------------------150 啦----------------------")
      }
    }
    println("-*-*-*-*-*-*-*-* END -*-*-*-*-*-*-*-*")
    sc.stop()
  }
}
