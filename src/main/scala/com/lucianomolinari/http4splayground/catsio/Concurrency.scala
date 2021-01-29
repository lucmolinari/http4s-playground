package com.lucianomolinari.http4splayground.catsio

import cats.effect._
import cats.implicits._

object Concurrency extends IOApp {

  def doSomething(id: Int): IO[Int] = IO {
    println(s"Starting process for $id")
    Thread.sleep(3000)
    println(s"Process for $id completed")
    id * 100
  }

  def sum(id1: Int, id2: Int): Int = id1 + id2

  def doEither(id: Int): Either[String, Int] = {
    println(s"Running for id: $id")
    Thread.sleep(id * 1000)
    if (id == 2) {
      Left("Ops, it's 2")
    } else {
      Right(id)
    }
  }

  def run(args: List[String]): IO[ExitCode] = {
    val ioEither1 = IO(doEither(1))
    val ioEither2 = IO(doEither(3))

    val a = List(ioEither1, ioEither2).parSequence
    // val b1 = ioEither1.parProduct(ioEither2)
    val b: IO[Either[String, Unit]] = a.map(_.sequence_)

    b.map( _ match {
      case Right(value) => 
        println("Finished good")
        ExitCode.Success
      case Left(value) => 
        println(value)
        ExitCode.Error
    })
  }

  def runOld(args: List[String]): IO[ExitCode] = {
    // val io1       = doSomething(1)
    // val io2       = doSomething(2)
    // val processed = for {
    //   res1 <- io1
    //   res2 <- io2
    // } yield (res1, res2)
    // val processed = (io1, io2).tupled

    val blocker = Blocker[IO]
    // val io1 = blocker.use(_.blockOn(doSomething(1)))
    // val io2 = blocker.use(_.blockOn(doSomething(2)))

    val processed = blocker.use { bl =>
      val io1 = bl.blockOn(doSomething(1))
      val io2 = bl.blockOn(doSomething(2))
      (io1, io2).parTupled
        .map { case (r1, r2) =>
          println(s"Results: $r1 - $r2")
          (r1, r2)
        }
        .map { case (r1, r2) => sum(r1, r2) }
    }

    // val processed = for {
    //   io1 = blocker.use(_.blockOn(doSomething(1)))
    //   io2 = blocker.use(_.blockOn(doSomething(2)))
    // } yield (io1, io2)

    // val processed = (io1, io2).parTupled

    processed
      .map(r => println(s"Sum: $r"))
      //.map { case (r1, r2) => println(s"Results: $r1 - $r2") }
      .as(ExitCode.Success)
  }

}
