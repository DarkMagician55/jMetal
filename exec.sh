#!/bin/bash
java -cp ./jmetal-exec/target/jmetal-exec-5.1-SNAPSHOT.jar:./jmetal-algorithm/target/jmetal-algorithm-5.1-SNAPSHOT.jar:./jmetal-core/target/jmetal-core-5.1-SNAPSHOT.jar:./jmetal-problem/target/jmetal-problem-5.1-SNAPSHOT.jar org.uma.jmetal.runner.singleobjective.WindFLODERunner 1 00 00 2000000000 >>./result/run.log
