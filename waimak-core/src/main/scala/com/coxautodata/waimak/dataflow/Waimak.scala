/**
  * Copyright 2018 Cox Automotive UK Limited
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package com.coxautodata.waimak.dataflow

import com.coxautodata.waimak.dataflow.DFExecutorPriorityStrategies._
import com.coxautodata.waimak.dataflow.spark.{SparkDataFlow, SparkFlowReporter}
import org.apache.hadoop.fs.Path
import org.apache.spark.sql.SparkSession

/**
  * Defines factory functions for creating and running Waimak data flows.
  *
  * Create by Alexei Perelighin on 2018/02/27
  */
object Waimak {

  /**
    * Creates an empty spark data flow.
    *
    * @param sparkSession
    * @return
    */
  def sparkFlow(sparkSession: SparkSession): SparkDataFlow = SparkDataFlow.empty(sparkSession)

  /**
    * Creates an empty spark data flow. With temporary folder.
    *
    * @param sparkSession
    * @param tempFolder
    * @return
    */
  def sparkFlow(sparkSession: SparkSession, tempFolder: String): SparkDataFlow = SparkDataFlow.empty(sparkSession, new Path(tempFolder))

  /**
    * Creates a spark data flow executor.
    *
    * @param maxParallelActions defines how maximum number of actions can be run in parallel, default is 20
    * @param priorityStrategy   a function that decides which actions are to be scheduled first
    * @return
    */
  def sparkExecutor(maxParallelActions: Int = 20, priorityStrategy: priorityStrategy = DFExecutorPriorityStrategies.defaultPriorityStrategy): DataFlowExecutor =
    ParallelDataFlowExecutor(SparkFlowReporter, maxParallelActions, priorityStrategy)

}