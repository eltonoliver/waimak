package com.coxautodata.waimak.dataflow.spark.dataquality.deequ.prefabchecks

import com.amazon.deequ.checks.{Check, CheckLevel}
import com.amazon.deequ.{VerificationRunBuilder, VerificationRunBuilderWithRepository}
import com.coxautodata.waimak.dataflow.spark.dataquality.deequ.DeequPrefabCheck


/**
  * Checks the uniqueness of a combination of columns of a dataset against warning and critical thresholds.
  * If thresholds are not configured, by default it will generate a warning alert if the combination of columns is not fully unique.
  * N.B doesn't seem to quite work as expected when nulls form part of the column combination
  */
class UniquenessCheck extends DeequPrefabCheck[UniquenessCheckConfig] {
  override protected def checks(conf: UniquenessCheckConfig): Option[VerificationRunBuilder => VerificationRunBuilder] = {
    val warningChecks = generateChecks(conf.warningThreshold, CheckLevel.Warning, conf.columns, "warning_checks")
    val criticalChecks = generateChecks(conf.criticalThreshold, CheckLevel.Error, conf.columns, "critical_checks")
    Some((b: VerificationRunBuilder) => b.addChecks(warningChecks ++ criticalChecks))
  }

  private def generateChecks(maybeThreshold: Option[Double], level: CheckLevel.Value, columns: List[String], description: String): Seq[Check] = {
    maybeThreshold.toSeq.map {
      threshold =>
        Check(level, description)
          .hasUniqueness(columns, (fraction: Double) => fraction >= threshold, Some(s"${columns.mkString(",")} was not ${threshold * 100}% unique."))
    }
  }

  override protected def anomalyChecks(conf: UniquenessCheckConfig): Option[VerificationRunBuilderWithRepository => VerificationRunBuilderWithRepository] = None

  override def checkName: String = "uniquenessCheck"
}

case class UniquenessCheckConfig(columns: List[String], warningThreshold: Option[Double] = Some(1.0), criticalThreshold: Option[Double] = None)