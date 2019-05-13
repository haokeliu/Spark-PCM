Marlin
============

A distributed matrix operations library build on top of [Spark](http://spark.apache.org/). Now, the master branch is in version 0.4-SNAPSHOT.  

##Branches Notice
This branch(spark-marlin) built on a custom version Spark to get better performance for matrix operations, however this branch has not been published out. If you use the official version Spark, please refer to `master` branch or `spark-1.0.x` branch  


##Prerequisites
As Marlin is built on top of Spark, you need to get the Spark installed first.  If you are not clear how to setup Spark, please refer to the guidelines [here](http://spark.apache.org/docs/latest/). Currently, Marlin is developed on the APIs of Spark 1.4.0 version.

##Compile Marlin
We use Maven to build our project currently, you can just type `mvn package -DskipTests` to get the jar package. Moreover, you can assign profile e.g. `spark-1.3`, `spark-1.2`, `hadoop-2.4`,  to build Marlin according to your environment.

As the API changes in Breeze, we have specially created a new branch named spark-1.0.x which means it is compatible with Spark version 1.0.x, while the master branch mainly focus on the later newest versions of Spark

##Run Marlin
We have already offered some examples in `edu.nju.pasalab.marlin.examples` to show how to use the APIs in the project. For example, if you want to run two large matrices multiplication, use spark-submit method, and type in command
 
	$./bin/spark-submit \
	 --class edu.nju.pasalab.marlin.examples.MatrixMultiply
	 --master <master-url> \
	 --executor-memory <memory> \
	 marlin_2.10-0.2-SNAPSHOT.jar \
	 <matrix A rows> <martrix A columns> \
	 <martrix B columns> <cores cross the cluster>

**Note:** Because the pre-built Spark-assembly jar doesn't have any files about netlib-java native compontent, which means you cannot use the native linear algebra library e.g BLAS to accelerate the computing, but have to use pure java to perform the small block matrix multiply in every worker. We have done some experiments and find it has a significant performance difference between the native BLAS computing and the pure java one, here you can find more info about the [performance comparison](https://github.com/PasaLab/marlin/wiki/Performance-comparison-on-matrices-multiply) and [how to load native library](https://github.com/PasaLab/marlin/wiki/How-to-load-native-linear-algebra-library).

**Note:** this example use `MTUtils.randomDenVecMatrix` to generate distributed random matrix in-memory without reading data from files.

**Note:** `<cores cross the cluster>` is the num of cores across the cluster you want to use. 


##Martix Operations API in Marlin
Currently, we have finished some APIs, you can find documentation in this [page](https://github.com/PasaLab/marlin/wiki/Linear-Algebra-Cheat-Sheet).


##Algorithms and Performance Evaluation
The details of the matrix multiplication algorithm is [here](https://github.com/PasaLab/marlin/wiki/Matrix-multiply-algorithm).

###Performance Evaluation
We have done some performance evaluation of Marlin. It can be seen [here](https://github.com/PasaLab/marlin/wiki/Performance-comparison-on-matrices-multiply).

##Contact
gurongwalker at gmail dot com

myasuka at live dot com

马林
============

分布式矩阵操作库建立在[Spark]（http://spark.apache.org/）之上。现在，主分支在版本0.4-SNAPSHOT中。

##分支机构通知
这个分支（spark-marlin）建立在一个自定义的Spark版本上，以获得更好的矩阵操作性能，但是这个分支尚未发布。如果您使用Spark的正式版本，请参阅`master`分支或`spark-1.0.x`分支


##先决条件
由于Marlin建立在Spark之上，您需要首先安装Spark。如果你不清楚如何设置Spark，请参考[这里]（http://spark.apache.org/docs/latest/）。目前，Marlin是在Spark 1.4.0版本的API上开发的。

##编译Marlin
我们目前使用Maven构建我们的项目，您只需键入`mvn package -DskipTests`来获取jar包。此外，您可以指定配置文件`spark-1.3`，`spark-1.2`，`hadoop-2.4`，根据你的环境建立Marlin。

随着Breeze中API的变化，我们特别创建了一个名为spark-1.0.x的新分支，这意味着它与Spark版本1.0.x兼容，而主分支主要关注最新版本的Spark

##运行马林
我们已经在`edu.nju.pasalab.marlin.examples`中提供了一些示例来展示如何在项目中使用API??。例如，如果要运行两个大型矩阵乘法，请使用spark-submit方法，然后键入命令
?
$。/ bin / spark-submit \
--class edu.nju.pasalab.marlin.examples.MatrixMultiply
--master <master-url> \
--executor-memory <内存> \
marlin_2.10-0.2-SNAPSHOT.jar \
<矩阵A行> <martrix A列> \
<martrix B列> <内核跨越群集>

**注意：**因为预先构建的Spark-assembly jar没有关于netlib-java本地组件的任何文件，这意味着您不能使用本地线性代数库，例如BLAS来加速计算，但必须使用pure java在每个worker中执行小块矩阵乘法。我们已经做了一些实验，发现它在原生BLAS计算和纯java之间有着显着的性能差异，在这里你可以找到更多关于[性能比较]的信息（https://github.com/PasaLab/marlin/wiki / Performance-comparison-on-matrices-multiply）和[如何加载本地库]（https://github.com/PasaLab/marlin/wiki/How-to-load-native-linear-algebra-library）。

**注意：**这个例子使用`MTUtils.randomDenVecMatrix`在内存中生成分布式随机矩阵，而不从文件中读取数据。

**注意：**`<内核跨越群集>`是您要使用的群集中的内核数量。


Marlin的Martix Operations API
目前，我们已经完成了一些API，你可以在这个页面找到文档（https://github.com/PasaLab/marlin/wiki/Linear-Algebra-Cheat-Sheet）。


##算法和性能评估
矩阵乘法算法的细节是[here]（https://github.com/PasaLab/marlin/wiki/Matrix-multiply-algorithm）。

###性能评估
我们已经做了一些Marlin的绩效评估。可以看到[这里]（https://github.com/PasaLab/marlin/wiki/Performance-comparison-on-matrices-multiply）。

＃＃联系
gmail点com上的gurongwalker

myasuka at live dot com