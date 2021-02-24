package com.lucianomolinari.http4splayground.io_book.chap2

import java.util.concurrent.TimeUnit

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.lucianomolinari.http4splayground.io_book.util.debug._

import scala.concurrent.duration.FiniteDuration

object Chap2 extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    // (IO(12), IO("hi")).mapN((i, s) => s"$s: $i").map(println) >> IO(ExitCode.Success)
    tickingClock.as(ExitCode.Success)
  }

  val tickingClock: IO[Unit] =
    IO(println(System.currentTimeMillis())).debug >> (IO.sleep(FiniteDuration(1, TimeUnit.SECONDS))) >> (tickingClock)

}
