package com.lucianomolinari.http4splayground.io_book.chap5

import java.util.concurrent.Executors

import cats.effect.{ExitCode, IO, IOApp, _}
import cats.implicits._
import com.lucianomolinari.http4splayground.io_book.util.debug._

import scala.concurrent.ExecutionContext

object ShiftingMultiple extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    (ec("1"), ec("2")) match {
      case (ec1, ec2) =>
        for {
          _ <- IO("one").debug
          _ <- IO.shift(ec1)
          _ <- IO("two").debug
          _ <- IO.shift(ec2)
          _ <- IO("three").debug
        } yield ExitCode.Success
    }

  def ec(name: String): ExecutionContext =
    ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor { r =>
      val t = new Thread(r, s"pool-$name-thread-1")
      t.setDaemon(true)
      t
    })

}
