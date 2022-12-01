package org.kunicki.monad_transformers

import cats.data.OptionT
import cats.{Applicative, FlatMap, Functor}

import scala.concurrent.{ExecutionContext, Future}

case class User(id: Long, name: String)

case class Company(id: Long, name: String)

class FromScratch:

  type Effect[A] = OptionT[Future, A]

  private def findUserById(id: Long): Effect[User] = ???

  private def findCompanyByUser(user: User): Effect[Company] = ???

  def findCompanyByUserId(id: Long)(using ExecutionContext): Effect[Company] =
    for {
      user <- findUserById(id)
      company <- findCompanyByUser(user)
    } yield company

case class OptionInsideFuture[A](value: Future[Option[A]]):

  def map[B](f: A => B)(using ExecutionContext): OptionInsideFuture[B] =
    OptionInsideFuture(value.map(_.map(f)))

  def flatMap[B](f: A => OptionInsideFuture[B])(using ExecutionContext): OptionInsideFuture[B] =
    OptionInsideFuture(
      value.flatMap {
        case Some(a) => f(a).value
        case None => Future.successful(None)
      }
    )

case class OptionInsideList[A](value: List[Option[A]]):

  def map[B](f: A => B): OptionInsideList[B] =
    OptionInsideList(value.map(_.map(f)))

  def flatMap[B](f: A => OptionInsideList[B]): OptionInsideList[B] =
    OptionInsideList(
      value.flatMap {
        case Some(a) => f(a).value
        case None => List(None)
      }
    )

case class OptionInsideX[A, X[_]](value: X[Option[A]]):

  def map[B](f: A => B)(using fx: Functor[X]): OptionInsideX[B, X] =
    OptionInsideX(fx.map(value)(_.map(f)))

  def flatMap[B](f: A => OptionInsideX[B, X])(using fmx: FlatMap[X], ax: Applicative[X]): OptionInsideX[B, X] =
    OptionInsideX(
      fmx.flatMap(value) {
        case Some(a) => f(a).value
        case None => ax.pure(None)
      }
    )
