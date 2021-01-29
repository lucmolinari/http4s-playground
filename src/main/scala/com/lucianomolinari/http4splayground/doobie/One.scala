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

object One {

  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:world",
    "postgres",
    "123456",
    Blocker.liftExecutionContext(ExecutionContexts.synchronous)
  )

  val y = xa.yolo
  import y._

  case class Country(code: String, name: String, pop: Int, gnp: Option[Double])

  case class Person(id: Long, name: String, age: Option[Short])

  type PersonInfo = (String, Option[Short])

  case class Info(name: String, code: String, population: Int)

  case class Person1(id: Int, name: String)

  def main(args: Array[String]): Unit = {
    // connectingToDB()
    // selectingData()
    // parameterized()
    // updating()
    // fragments()
    errorHandling()
  }

  def errorHandling(): Unit = {
    List(
      sql"DROP TABLE IF EXISTS person",
      sql"""CREATE TABLE person (
          id    SERIAL,
          name  VARCHAR NOT NULL UNIQUE
        )"""
    ).traverse(_.update.quick).void.unsafeRunSync()

    def insert(s: String): ConnectionIO[Person1] = {
      sql"INSERT INTO person(name) VALUES($s)".update
        .withUniqueGeneratedKeys("id", "name")
    }

    def safeInsert(s: String): ConnectionIO[Either[String, Person1]] = 
      insert(s).attemptSomeSqlState {
        case sqlstate.class23.UNIQUE_VIOLATION => "Oops!"
      }

    safeInsert("bob").quick.unsafeRunSync()

    safeInsert("bob").quick.unsafeRunSync()

    safeInsert("steve").quick.unsafeRunSync()


    // try {
    //   insert("bob").quick.unsafeRunSync()
    // } catch {
    //   case e: java.sql.SQLException =>
    //     println(e.getMessage)
    //     println(e.getSQLState)
    // }

  }

  def fragments(): Unit = {
    def select(name: Option[String], pop: Option[Int], codes: List[String], limit: Long) = {
      val f1 = name.map(s => fr"name LIKE $s")
      val f2 = pop.map(n => fr"population > $n")
      val f3 = codes.toNel.map(cs => in(fr"code", cs))

      val q: Fragment =
        fr"SELECT name, code, population FROM country" ++
          whereAndOpt(f1, f2, f3) ++
          fr"LIMIT $limit"

      q.query[Info]
    }

    select(None, None, Nil, 10).check.unsafeRunSync()
    select(Some("U%"), None, Nil, 10).check.unsafeRunSync()
    select(Some("U%"), Some(12345), List("FRA", "GBR"), 10).check.unsafeRunSync()
  }

  def updating(): Unit = {
    // val drop = sql"DROP TABLE IF EXISTS person".update.run
    // val create = sql"""
    //   CREATE TABLE person (
    //     id    SERIAL,
    //     name  VARCHAR NOT NULL UNIQUE,
    //     age SMALLINT
    //   )
    //   """.update.run

    // println((drop, create).mapN(_ + _).transact(xa).unsafeRunSync())

    // def insert1(name: String, age: Option[Short]): Update0 =
    //   sql"INSERT INTO person(name, age) VALUES($name, $age)".update

    // insert1("Alice", Some(12)).run.transact(xa).unsafeRunSync()
    // insert1("Bob", None).run.transact(xa).unsafeRunSync()

    // sql"UPDATE person SET age = 15 WHERE name = 'Alice'".update.quick.unsafeRunSync()

    // sql"SELECT id, name, age FROM person".query[Person].quick.unsafeRunSync()

    // def insert2(name: String, age: Option[Short]): ConnectionIO[Person] =
    //   for {
    //     _  <- sql"INSERT INTO person(name, age) VALUES($name, $age)".update.run
    //     id <- sql"SELECT lastval()".query[Long].unique
    //     p  <- sql"SELECT id, name, age FROM person WHERE id = $id".query[Person].unique
    //   } yield p

    // def insert3(name: String, age: Option[Short]): ConnectionIO[Person] =
    //   sql"INSERT INTO person(name, age) VALUES($name, $age)"
    //     .update
    //     .withUniqueGeneratedKeys("id", "name", "age")

    // insert3("Elvis", None).quick.unsafeRunSync()

    // val up = {
    //   sql"UPDATE person SET age = age + 1 WHERE age IS NOT NULL".update
    //     .withGeneratedKeys[Person]("id", "name", "age")
    // }
    // up.quick.unsafeRunSync()
    // up.quick.unsafeRunSync()

    // def insertMany(ps: List[PersonInfo]): ConnectionIO[Int] = {
    //   val sql = "INSERT INTO person(name, age) VALUES(?, ?)"
    //   Update[PersonInfo](sql).updateMany(ps)
    // }

    // val data = List[PersonInfo](
    //   ("Frank", Some(12)),
    //   ("Daddy", None)
    // )
    // insertMany(data).quick.unsafeRunSync()

    def insertMany2(ps: List[PersonInfo]): Stream[ConnectionIO, Person] = {
      val sql = "INSERT INTO person(name, age) VALUES(?, ?)"
      Update[PersonInfo](sql).updateManyWithGeneratedKeys[Person]("id", "name", "age")(ps)
    }
    val data2 = List[PersonInfo](("Banjo", Some(39)), ("Skeeter", None), ("Jim-Bob", Some(12)))
    insertMany2(data2).quick.unsafeRunSync()
  }

  def parameterized(): Unit = {
    // sql"SELECT code, name, population, gnp FROM country"
    //   .query[Country]
    //   .stream
    //   .take(5)
    //   .quick
    //   .unsafeRunSync()
    def biggetThan(minPop: Short) = sql"""
      SELECT code, name, population, gnp
      FROM country
      WHERE population > $minPop
      """.query[Country]

    // biggetThan(150000000).quick.unsafeRunSync()
    biggetThan(0).check.unsafeRunSync()

    def populationIn(range: Range, codes: NonEmptyList[String]) = {
      val q = fr"""
        SELECT code, name, population, gnp
        FROM country
        WHERE population > ${range.min}
        AND population < ${range.max}
        AND """ ++ Fragments.in(fr"code", codes)
      q.query[Country]
    }
    // populationIn(100000000 to 300000000, NonEmptyList.of("USA", "BRA", "PAK", "GBR")).quick.unsafeRunSync()
  }

  def selectingData(): Unit = {
    // val prog1 = sql"SELECT name FROM country".query[String].to[List]
    // prog1.transact(xa).unsafeRunSync().take(5).foreach(println)

    // This still filters 5 elements on the client side...but it's better than prog1
    // val prog2 = sql"SELECT name FROM country".query[String].stream.take(5).compile.toList
    // prog2.quick.unsafeRunSync()
    //prog2.transact(xa).unsafeRunSync().foreach(println)

    // sql"SELECT code, name, population, gnp FROM country"
    //   .query[(String, String, Int, Option[Double])]
    //   .stream
    //   .take(5)
    //   .quick
    //   .unsafeRunSync()

    // type Rec = Record.`'code -> String, 'name -> String, 'pop -> Int, 'gnp -> Option[Double]`.T

    sql"SELECT code, name, population, gnp FROM country"
      // .query[String :: String :: Int :: Option[Double] :: HNil]
      // .query[Rec]
      .query[Country]
      .stream
      .take(5)
      .quick
      .unsafeRunSync()
  }

  def connectingToDB(): Unit = {

    // val program1 = 42.pure[ConnectionIO]
    // program1.transact(xa).unsafeRunSync()

    // val program2 = sql"SELECT 42".query[Int].unique
    // println(program2.transact(xa).unsafeRunSync())

    // val program3: ConnectionIO[(Int, Double)] =
    //   for {
    //     a <- sql"SELECT 42".query[Int].unique
    //     b <- sql"SELECT RANDOM()".query[Double].unique
    //   } yield (a, b)
    // println(program3.transact(xa).unsafeRunSync())

    val program3a = {
      val a: ConnectionIO[Int]    = sql"SELECT 42".query[Int].unique
      val b: ConnectionIO[Double] = sql"SELECT RANDOM()".query[Double].unique
      (a, b).tupled
    }
    // println(program3a.transact(xa).unsafeRunSync())
    println(program3a.replicateA(4).transact(xa).unsafeRunSync())
  }

}
