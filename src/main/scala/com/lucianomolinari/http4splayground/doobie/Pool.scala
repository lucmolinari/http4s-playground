package com.lucianomolinari.http4splayground.doobie

import cats._
import cats.data._
import cats.effect._
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import fs2.Stream
import Fragments.{in, whereAndOpt}
import doobie.postgres._

object Pool {

  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:world",
    "postgres",
    "123456",
    Blocker.liftExecutionContext(ExecutionContexts.synchronous)
  )  

  def main(args: Array[String]): Unit = {

  }
  
}
