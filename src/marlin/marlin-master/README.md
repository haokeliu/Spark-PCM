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

����
============

�ֲ�ʽ��������⽨����[Spark]��http://spark.apache.org/��֮�ϡ����ڣ�����֧�ڰ汾0.4-SNAPSHOT�С�

##��֧����֪ͨ
�����֧��spark-marlin��������һ���Զ����Spark�汾�ϣ��Ի�ø��õľ���������ܣ����������֧��δ�����������ʹ��Spark����ʽ�汾�������`master`��֧��`spark-1.0.x`��֧


##�Ⱦ�����
����Marlin������Spark֮�ϣ�����Ҫ���Ȱ�װSpark������㲻����������Spark����ο�[����]��http://spark.apache.org/docs/latest/����Ŀǰ��Marlin����Spark 1.4.0�汾��API�Ͽ����ġ�

##����Marlin
����Ŀǰʹ��Maven�������ǵ���Ŀ����ֻ�����`mvn package -DskipTests`����ȡjar�������⣬������ָ�������ļ�`spark-1.3`��`spark-1.2`��`hadoop-2.4`��������Ļ�������Marlin��

����Breeze��API�ı仯�������ر𴴽���һ����Ϊspark-1.0.x���·�֧������ζ������Spark�汾1.0.x���ݣ�������֧��Ҫ��ע���°汾��Spark

##��������
�����Ѿ���`edu.nju.pasalab.marlin.examples`���ṩ��һЩʾ����չʾ�������Ŀ��ʹ��API??�����磬���Ҫ�����������;���˷�����ʹ��spark-submit������Ȼ���������
?
$��/ bin / spark-submit \
--class edu.nju.pasalab.marlin.examples.MatrixMultiply
--master <master-url> \
--executor-memory <�ڴ�> \
marlin_2.10-0.2-SNAPSHOT.jar \
<����A��> <martrix A��> \
<martrix B��> <�ں˿�ԽȺ��>

**ע�⣺**��ΪԤ�ȹ�����Spark-assembly jarû�й���netlib-java����������κ��ļ�������ζ��������ʹ�ñ������Դ����⣬����BLAS�����ټ��㣬������ʹ��pure java��ÿ��worker��ִ��С�����˷��������Ѿ�����һЩʵ�飬��������ԭ��BLAS����ʹ�java֮���������ŵ����ܲ��죬������������ҵ��������[���ܱȽ�]����Ϣ��https://github.com/PasaLab/marlin/wiki / Performance-comparison-on-matrices-multiply����[��μ��ر��ؿ�]��https://github.com/PasaLab/marlin/wiki/How-to-load-native-linear-algebra-library����

**ע�⣺**�������ʹ��`MTUtils.randomDenVecMatrix`���ڴ������ɷֲ�ʽ������󣬶������ļ��ж�ȡ���ݡ�

**ע�⣺**`<�ں˿�ԽȺ��>`����Ҫʹ�õ�Ⱥ���е��ں�������


Marlin��Martix Operations API
Ŀǰ�������Ѿ������һЩAPI������������ҳ���ҵ��ĵ���https://github.com/PasaLab/marlin/wiki/Linear-Algebra-Cheat-Sheet����


##�㷨����������
����˷��㷨��ϸ����[here]��https://github.com/PasaLab/marlin/wiki/Matrix-multiply-algorithm����

###��������
�����Ѿ�����һЩMarlin�ļ�Ч���������Կ���[����]��https://github.com/PasaLab/marlin/wiki/Performance-comparison-on-matrices-multiply����

������ϵ
gmail��com�ϵ�gurongwalker

myasuka at live dot com