package com.lucianomolinari.http4splayground.catsio

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import cats.data.EitherT

object MockedProgram extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {

    // val a1 = for {
    //   s <- EitherT.right(IO(1))
    //   // b <- EitherT.
    // } yield s

    type Lists = (List[Either[String, Deal]], List[Either[String, TrackRelease]], List[Either[String, Release]])

    val res = for {
      (dealsEithers, trsEither, releasesEithers) <-
        EitherT.right[String]((IO(readDeals), IO(readTrackReleases), IO(readReleases)).parTupled) // run the in parallel
      (dErrors, deals)                            = splitErrosAndValues(dealsEithers)
      _                                           = println(s"dErrors: $dErrors - deals: $deals")
      (trErrors, trs)                             = splitErrosAndValues(trsEither)
      _                                           = println(s"trErrors: $trErrors - trs: $trs")
      (rErrors, releases)                         = splitErrosAndValues(releasesEithers)
      _                                           = println(s"rErrors: $rErrors - releases: $releases")
      _                                          <- EitherT(writeData(deals, trs, releases))

      // list <- EitherT.rightT[IO, String]((IO(readDeals), IO(readTrackReleases), IO(readReleases)))
      // a: IO[Lists] = list.parTupled
      // b <- EitherT.right(a)

      // c = b
      // _ =
      // (dErrors, deals)                            = splitErrosAndValues(b)

      // e <- EitherT.rightT(1)
      // (dealsEithers, trsEither, releasesEithers) <- // EitherT((IO(readDeals), IO(readTrackReleases), IO(readReleases)).parTupled)
      // (dErrors, deals)                            = splitErrosAndValues(dealsEithers)
      // _                                          <- IO(println(s"dErrors: $dErrors - deals: $deals"))
      // (trErrors, trs)                             = splitErrosAndValues(trsEither)
      // _                                           = println(s"trErrors: $trErrors - trs: $trs")
      // (rErrors, releases)                         = splitErrosAndValues(releasesEithers)
      // _                                           = println(s"rErrors: $rErrors - releases: $releases")
      // _                                          <- writeData(deals, trs, releases)
    } yield ()

    res.value.flatMap(_ match {
      case Left(value)  => IO(println(s"Error!! $value")).map(_ => ExitCode.Success)
      case Right(value) => IO(println("good!")).map(_ => ExitCode.Error)
    })

    //res *> IO(ExitCode.Success)
    // IO(ExitCode.Success)
  }

  case class Deal(id: Int)
  case class TrackRelease(id: Int)
  case class Release(id: Int)

  private def writeData(ld: List[Deal], lTr: List[TrackRelease], lR: List[Release]): IO[Either[String, Unit]] = {
    val writes = List(IO(write("deals", ld)), IO(write("trackReleases", lTr)), IO(write("Releases", lR))).parSequence
    writes.map(_.sequence_)
  }

  private def write[T](name: String, l: List[T]): Either[String, Unit] =
    Right(println(s"Saving list $name"))
    //if (name == "Releases") Left("Ops!") else Right(println(s"Saving list $name"))

  private def splitErrosAndValues[T](l: List[Either[String, T]]): (List[String], List[T]) = {
    l.separate
  }

  private def readDeals: List[Either[String, Deal]] = List(
    Right(Deal(1)),
    Left("Error deal 2"),
    Right(Deal(2))
  )

  private def readTrackReleases: List[Either[String, TrackRelease]] = List(
    Right(TrackRelease(1)),
    Right(TrackRelease(2)),
    Left("Error 3")
  )

  private def readReleases: List[Either[String, Release]] = List(
    Left("Error 1"),
    Right(Release(2)),
    Right(Release(3))
  )

}
